package com.github.flussig.dacdoc.util;

import java.util.HashSet;
import java.util.Set;

/**
 * Another implementation of elementary string checks - done to avoid external dependencies
 * */
public class Strings {
    public static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

    public static boolean isNotNullOrEmpty(String s) {
        return !isNullOrEmpty(s);
    }

    // returns line numbers of the full string where substrings are found
    public static Set<Integer> lineNumbersOfSubstring(String text, String substring) {
        int currentIndex = 0;
        Set<Integer> lineNumbers = new HashSet<>();
        while(currentIndex != -1) {
            currentIndex = text.indexOf('\n', currentIndex);
            lineNumbers.add(lineNumberOfCharacterAtIndex(text, currentIndex));
        }
        return lineNumbers;
    }

    // gives full number of lines in a given text
    public static int numberOfLines(String text) {
        String[] lines = text.split("\n");
        return lines.length;
    }

    // gives line number of a character of a given index in a text
    public static int lineNumberOfCharacterAtIndex(String text, Integer index) {
        int currentIndex = 0;
        int lineNumber = 0;
        while(currentIndex < index && currentIndex != -1) {
            currentIndex = text.indexOf('\n', currentIndex);
            lineNumber++;
        }
        return lineNumber;
    }
}
