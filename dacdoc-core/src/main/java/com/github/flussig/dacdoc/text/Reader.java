package com.github.flussig.dacdoc.text;

import com.github.flussig.dacdoc.Constants;
import com.github.flussig.dacdoc.GitBlameLineDetails;
import com.github.flussig.dacdoc.GitUtil;
import com.github.flussig.dacdoc.check.Check;
import com.github.flussig.dacdoc.check.CheckRegistry;
import com.github.flussig.dacdoc.check.CheckResult;
import com.github.flussig.dacdoc.check.CompositeCheck;
import com.github.flussig.dacdoc.exception.DacDocException;
import com.github.flussig.dacdoc.exception.DacDocParseException;
import com.github.flussig.dacdoc.util.Strings;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Reader accepts File handlers for given project and extracts all DacDoc anchors (placeholders)
 */
public class Reader {
    private static Pattern anchorPlaceholderPattern = Pattern.compile(String.format(
            "%s%s((.|\\n|\\r)*?)%s",
            Constants.ANCHOR_FRAMING,
            Constants.ANCHOR_KEYWORD,
            Constants.ANCHOR_FRAMING));


    /**
     * Get all markdown files in given directory
     */
    public static Set<File> findMarkdownFiles(Path path) throws DacDocException {
        Set<File> result = new HashSet<>();

        try {
            Files.walk(path)
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .filter(file -> !file.getName().startsWith("_"))
                    .filter(file -> file.getName().endsWith(".md"))
                    .forEach(result::add);
        } catch(Exception e) {
            throw new DacDocException(
                    String.format(
                            "traversing root folder %s throws exception", path), e);
        }

        return result;
    }

    /**
     * Parse files and extract anchors
     */
    public static Map<File, Set<Anchor>> parseFiles(Set<File> files) throws IOException, DacDocParseException {
        Map<File, Set<Anchor>> result = new HashMap<>();

        for(File f: files) {
            Set<Anchor> anchors = new HashSet<>();
            result.put(f, anchors);

            String content = new String(Files.readAllBytes(f.toPath()));

            // extract all DACDOC placeholders
            Matcher dacdocPlaceholderMatcher = anchorPlaceholderPattern.matcher(content);

            while(dacdocPlaceholderMatcher.find()) {
                String dacdocAnchorFullText = dacdocPlaceholderMatcher.group();

                Anchor anchor = Anchor.from(dacdocAnchorFullText);

                anchors.add(anchor);
            }
        }

        attachChecks(result);

        return result;
    }

    /**
     * loops through anchor-check map and replace anchors with results in files
     */
    public static Map<File, String> getTransformedFiles(Map<File, Set<Anchor>> checkMap, Path dacdocResourceFirectory) throws DacDocParseException {
        Set<File> files = checkMap.keySet();

        // map file and its initial content
        Map<File, String> fileContents = files.stream()
                .collect(Collectors.toMap(f -> f, f -> {
                    try {
                        return new String(Files.readAllBytes(f.toPath()));
                    } catch(IOException e) {
                        return null;
                    }
                }));

        files.parallelStream().forEach(file -> {
            transformFile(checkMap, dacdocResourceFirectory, fileContents, file);
        });

        return fileContents;
    }

    private static void transformFile(
        Map<File, Set<Anchor>> checkMap,
        Path dacdocResourceFirectory,
        Map<File, String> fileContents,
        File file) {
        String newFileContent = fileContents.get(file);

        // map each anchor for the file to latest git blame detail
        Map<Anchor, Set<GitBlameLineDetails>> anchorToLatestGitBlame =
            createAnchorToGitBlameMap(checkMap, fileContents, file);

        // get check results in parallel
        Map<Anchor, CheckResult> anchorCheckResults = checkMap.get(file)
            .parallelStream()
            .collect(Collectors.toMap(anchor -> anchor, anchor -> anchor.getCheck().execute()));

        // replace each anchor with new content after checks
        for(Anchor anchor: anchorCheckResults.keySet()) {
            CheckResult checkResult = anchorCheckResults.get(anchor);

            Set<GitBlameLineDetails> gitBlames = anchorToLatestGitBlame.get(anchor);

            for(GitBlameLineDetails gitBlame: gitBlames) {
                // replace given anchor with test result
                newFileContent = Strings.replaceFirst(
                    newFileContent,
                    anchor.getFullText(),
                    anchor.getTransformedText(checkResult, gitBlame, dacdocResourceFirectory, file));

            }
        }

        // add aggregate check for the file to the top of the file
        List<Check> fileChecks = checkMap.get(file).stream().map(Anchor::getCheck).collect(
            Collectors.toList());
        Check aggregateFileCheck = new CompositeCheck(fileChecks);
        GitBlameLineDetails latestGitBlameForFile = anchorToLatestGitBlame.entrySet().stream()
            .flatMap(kv -> kv.getValue().stream())
            .filter(Objects::nonNull)
            .max(Comparator.comparing(GitBlameLineDetails::getEpochSecond))
            .orElse(null);

        String fileCheckImageString = Anchor.getCheckImage(
            aggregateFileCheck.execute(),
            latestGitBlameForFile,
            dacdocResourceFirectory,
            file,
            file.getName());

        newFileContent = String.format("%s\n\n%s", fileCheckImageString, newFileContent);

        fileContents.replace(file, newFileContent);
    }

    private static Map<Anchor, Set<GitBlameLineDetails>> createAnchorToGitBlameMap(Map<File, Set<Anchor>> checkMap, Map<File, String> fileContents, File file) {
        // git blame details before modifying the text
        List<GitBlameLineDetails> gitBlameLineDetails = GitUtil.getBlameDetails(file);

        // get line numbers for all anchors in the file before modifying the text
        Map<Anchor, Set<Integer>> anchorToLineNumbers = checkMap.get(file).stream()
                .collect(Collectors.toMap(
                        a -> a,
                        a -> Strings.lineNumbersOfSubstring(fileContents.get(file), a.getFullText())));

        // map from anchor to blame info for latest changed line in anchor
        Map<Anchor, Set<GitBlameLineDetails>> anchorToLatestGitBlame = new HashMap<>();

        for(Map.Entry<Anchor, Set<Integer>> kv: anchorToLineNumbers.entrySet()) {
            Anchor anchor = kv.getKey();
            int anchorNumOfLines = Strings.numberOfLines(anchor.getFullText());

            Set<GitBlameLineDetails> latestGitBlameDetails = new HashSet<>();

            for(Integer lineNumber: kv.getValue()) {
                GitBlameLineDetails latestGitBlameDetail = gitBlameLineDetails.stream()
                        .filter(gitBlameLineDetail ->
                                gitBlameLineDetail.getLineNumber() >= lineNumber && gitBlameLineDetail.getLineNumber() < lineNumber + anchorNumOfLines)
                        .max(Comparator.comparing(GitBlameLineDetails::getEpochSecond))
                        .orElse(null);

                latestGitBlameDetails.add(latestGitBlameDetail);
            }

            anchorToLatestGitBlame.put(anchor, latestGitBlameDetails);
        }

        return anchorToLatestGitBlame;
    }

    /**
     * Map file-anchor tuple to checks
     */
    private static void attachChecks(Map<File, Set<Anchor>> fileAnchorMap) {
        // convert fileAnchorMap to set of tuples
        Set<FileAnchorTuple> tuples = fileAnchorMap.entrySet().stream()
                .flatMap(kv -> kv.getValue().stream().map(anchor -> new FileAnchorTuple(kv.getKey(), anchor)))
                .collect(Collectors.toSet());

        // first loop through anchors: assign all checks
        fillChecksInitial(tuples);

        // second loop through anchors: put values into composite checks
        fillChecksComposite(tuples);
    }

    // TODO: avoid circular dependencies for composite checks
    private static void fillChecksComposite(Set<FileAnchorTuple> tuples) {
        for(FileAnchorTuple fileAnchorTuple: tuples.stream().filter(t -> t.getAnchor().getAnchorType() == AnchorType.COMPOSITE).collect(Collectors.toSet())) {
            CompositeCheck compositeCheck = (CompositeCheck)fileAnchorTuple.getAnchor().getCheck();

            Collection<String> ids = fileAnchorTuple.getAnchor().getIds();

            // find checks for all ids and attach to composite check
            for(String id: ids) {
                Check subCheck;

                Optional<FileAnchorTuple> subTuple = tuples.stream().filter(t -> t.getAnchor().getId().equals(id)).findFirst();

                if(!subTuple.isPresent()) {
                    subCheck = Check.unknownCheck;
                } else {
                    subCheck = subTuple.get().getAnchor().getCheck();
                }

                compositeCheck.getChecks().add(subCheck);
            }
        }
    }

    private static void fillChecksInitial(Set<FileAnchorTuple> tuples) {
        for(FileAnchorTuple fileAnchorTuple: tuples) {
            Anchor anchor = fileAnchorTuple.getAnchor();
            File file = fileAnchorTuple.getFile();
            anchor.setCheck(generateCheck(anchor, file));
        }
    }

    private static Check generateCheck(Anchor anchor, File file) {
        Check check;
        if(anchor.getAnchorType() == AnchorType.COMPOSITE) {
            // for composite type: put empty composite check
            check = new CompositeCheck(new ArrayList());
        } else {
            // for primitive type: define type of check and add it
            if(CheckRegistry.checkRegistry.containsKey(anchor.getTestId())) {
                Class<? extends Check> checkClass = CheckRegistry.checkRegistry.get(anchor.getTestId());
                try {
                    check = checkClass.getDeclaredConstructor(String.class, File.class).newInstance(anchor.getArgument(), file);
                } catch (Exception ex) {
                    check = Check.unknownCheck;
                    // TODO: throw specific error
                }
            } else {
                check = Check.unknownCheck;
            }
        }
        return check;
    }

    private static class FileAnchorTuple {
        private File file;
        private Anchor anchor;

        public FileAnchorTuple(File file, Anchor anchor) {
            this.anchor = anchor;
            this.file = file;
        }

        public File getFile() {
            return file;
        }

        public Anchor getAnchor() {
            return anchor;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            FileAnchorTuple that = (FileAnchorTuple) o;
            return Objects.equals(file, that.file) &&
                    Objects.equals(anchor, that.anchor);
        }

        @Override
        public int hashCode() {
            return Objects.hash(file, anchor);
        }
    }
}
