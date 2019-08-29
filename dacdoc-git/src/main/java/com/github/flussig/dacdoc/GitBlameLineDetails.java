package com.github.flussig.dacdoc;

import com.github.flussig.dacdoc.util.Strings;

import java.util.List;

/**
 * Info associated with one line of file under git blame
 * */
public class GitBlameLineDetails {
    // gets line details from strings that form the block for 1 line of blamed file
    // assumption is that input is from 'git blame --line-porcelain' command
    // porcelain format description: https://git-scm.com/docs/git-blame
    public static GitBlameLineDetails fromBlock(List<String> strings) {
        GitBlameLineDetails result = new GitBlameLineDetails();

        for(int i = 0; i < strings.size(); i++) {
            String currentLine = strings.get(i);

            if(Strings.isNullOrEmpty(currentLine) && currentLine.startsWith("\t")) {
                continue;
            }

            String[] lineParts = currentLine.split(" ");

            if(lineParts.length < 2) {
                continue;
            }

            // extract header
            if(i == 0) {
                result.setCommitId(lineParts[0]);
                result.setLineNumber(Integer.valueOf(lineParts[2]));
            }

            // extract user that made last modification
            if(lineParts[0].equals("author")) {
                result.setUser(lineParts[1]);
            }

            // extract email of the user that made las modification
            if(lineParts[0].equals("author-mail")) {
                result.setUserEmail(lineParts[1]);
            }

            // extract author epoch second
            if(lineParts[0].equals("author-time")) {
                result.setEpochSecond(Long.valueOf(lineParts[1]));
            }
        }

        return result;
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
