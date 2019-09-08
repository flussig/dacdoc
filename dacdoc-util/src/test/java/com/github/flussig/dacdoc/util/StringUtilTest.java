package com.github.flussig.dacdoc.util;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StringUtilTest {
    @Test
    public void testLineNumberOfSubstring() throws IOException {
        File readme = new File(getClass()
                .getClassLoader()
                .getResource("README.md")
                .getFile());

        String readmeContent = new String(Files.readAllBytes(readme.toPath()));

        Set<Integer> linesForSubstringMultipleEntry = Strings.lineNumbersOfSubstring(readmeContent, "line1");
        Set<Integer> linesForSubstringSingleEntry = Strings.lineNumbersOfSubstring(readmeContent, "line2");
        Set<Integer> linesForSubstringNoEntry = Strings.lineNumbersOfSubstring(readmeContent, "line9");

        assertEquals(2, linesForSubstringMultipleEntry.size());
        assertTrue(linesForSubstringMultipleEntry.containsAll(Arrays.asList(1,7)));

        assertEquals(1, linesForSubstringSingleEntry.size());
        assertTrue(linesForSubstringSingleEntry.containsAll(Arrays.asList(2)));

        assertEquals(0, linesForSubstringNoEntry.size());
    }
}
