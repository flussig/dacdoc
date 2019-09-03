package com.github.flussig.dacdoc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.io.File;
import java.util.List;

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
    }
}
