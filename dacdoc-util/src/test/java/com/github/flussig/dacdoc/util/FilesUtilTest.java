package com.github.flussig.dacdoc.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FilesUtilTest {
    @Test
    public void testFileUtil() {
        String fileName = "README.md";

        assertEquals("README", Files.removeExtension(fileName));
    }
}
