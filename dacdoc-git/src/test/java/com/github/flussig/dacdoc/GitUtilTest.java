package com.github.flussig.dacdoc;

import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Unit check for simple GitUtil.
 */
public class GitUtilTest
{
    // test that git util can read file and produce correct git blame  result
    @Test
    public void testGitUtilBlame() {
        File readmeFile = new File(getClass()
                .getClassLoader()
                .getResource("test-readme.md")
                .getFile());

        List<GitBlameLineDetails> gitBlameLineDetails = GitUtil.getBlameDetails(readmeFile);

        assertEquals(3, gitBlameLineDetails.size());

        // not sure if asserting concrete values for git blame output is a good idea here (may change with later commits), so non-null checks are made
        assertTrue(gitBlameLineDetails.stream().noneMatch(line -> line.getCommitId() == null));
        assertTrue(gitBlameLineDetails.stream().noneMatch(line -> line.getUser() == null));
        assertTrue(gitBlameLineDetails.stream().noneMatch(line -> line.getUserEmail() == null));
        assertTrue(gitBlameLineDetails.stream().noneMatch(line -> line.getEpochSecond() == null));
        assertTrue(gitBlameLineDetails.stream().noneMatch(line -> line.getLineNumber() == null));

        // check git line numbers
        Set<Integer> gitLineNumbers = gitBlameLineDetails.stream()
                .map(GitBlameLineDetails::getLineNumber)
                .collect(Collectors.toSet());
        List<Integer> expectedLineNumbers = Arrays.asList(1,2,3);
        assertEquals(3, gitLineNumbers.size());
        assertTrue(gitLineNumbers.containsAll(expectedLineNumbers));
    }
}
