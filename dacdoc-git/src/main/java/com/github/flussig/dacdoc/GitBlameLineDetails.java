package com.github.flussig.dacdoc;

import com.github.flussig.dacdoc.util.Strings;

import java.util.List;

/**
 * Info associated with one line of file under git blame
 * */
public class GitBlameLineDetails {
    public static final String LINE_CONTENT_INDICATOR = "\t";
    public static final String SEPARATOR = " ";
    private static final String AUTHOR_LINE_INDICATOR = "author";
    private static final String AUTHOR_EMAIL_LINE_INDICATOR = "author-mail";
    private static final String AUTHOR_TIME_INDICATOR = "author-time";

    // gets line details from strings that form the block for 1 line of blamed file
    // assumption is that input is from 'git blame --line-porcelain' command
    // porcelain format description: https://git-scm.com/docs/git-blame
    public static GitBlameLineDetails fromBlock(List<String> strings) {
        GitBlameLineDetails result = new GitBlameLineDetails();

        for(int i = 0; i < strings.size(); i++) {
            String currentLine = strings.get(i);

            if(Strings.isNullOrEmpty(currentLine) && currentLine.startsWith(LINE_CONTENT_INDICATOR)) {
                continue;
            }

            String[] lineParts = currentLine.split(SEPARATOR);

            if(lineParts.length < 2) {
                continue;
            }

            // extract header
            if(i == 0) {
                result.setCommitId(lineParts[0]);
                result.setLineNumber(Integer.valueOf(lineParts[2]));
            }

            // extract user that made last modification
            if(lineParts[0].equals(AUTHOR_LINE_INDICATOR)) {
                result.setUser(joinParts(lineParts, 1, lineParts.length - 1));
            }

            // extract email of the user that made las modification
            if(lineParts[0].equals(AUTHOR_EMAIL_LINE_INDICATOR)) {
                result.setUserEmail(joinParts(lineParts, 1, lineParts.length - 1));
            }

            // extract author epoch second
            if(lineParts[0].equals(AUTHOR_TIME_INDICATOR)) {
                result.setEpochSecond(Long.valueOf(lineParts[1]));
            }
        }

        return result;
    }

    private static String joinParts(String[] parts, int start, int end) {
        StringBuilder sb = new StringBuilder();

        for(int i = start; i <= end; i++) {
            sb.append(parts[i]);
            if(i != end) {
                sb.append(SEPARATOR);
            }
        }

        return sb.toString();
    }

    private GitBlameLineDetails() {}

    private Integer lineNumber;
    private String commitId;
    private String user;
    private String userEmail;
    private Long epochSecond;

    public Integer getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(Integer lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getCommitId() {
        return commitId;
    }

    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Long getEpochSecond() {
        return epochSecond;
    }

    public void setEpochSecond(Long epochSecond) {
        this.epochSecond = epochSecond;
    }
}
