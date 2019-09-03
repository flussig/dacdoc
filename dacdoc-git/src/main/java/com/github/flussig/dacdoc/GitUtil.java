package com.github.flussig.dacdoc;

import org.apache.commons.collections.ArrayStack;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Util class that provides access to git functionality
 * */
public class GitUtil {
    public static List<GitBlameLineDetails> getBlameDetails(File file) {
        // run git blame and read file content
        List<GitBlameLineDetails> result = new ArrayList<>();

        String gitBlameCommand = String.format("git blame -w --line-porcelain \"%s\"", file.getAbsolutePath());

        try {
            Process gitBlameProcess = Runtime.getRuntime().exec(gitBlameCommand);

            List<String> buffer = new ArrayList<>();

            try(BufferedReader in = new BufferedReader(new InputStreamReader(gitBlameProcess.getInputStream()))) {
                String line;
                while ((line = in.readLine()) != null) {
                    buffer.add(line);

                    if(line.startsWith("\t")) {
                        GitBlameLineDetails lineDetails = GitBlameLineDetails.fromBlock(buffer);
                        result.add(lineDetails);
                        buffer.clear();
                    }
                }
            }

            gitBlameProcess.waitFor();

            return result;
        } catch(Exception e) {
            // TODO: log
            return new ArrayList<>();
        }
    }
}
