package com.github.flussig.dacdoc.check;

import java.util.HashMap;
import java.util.Map;

/**
 * Registers check classes (both defined in this library and in user's code)
 */
public class CheckRegistry {
    public final static String DEFAULT_TEST_ID = "dacdoc-url";

    public static Map<String, Class<? extends Check>> checkRegistry = new HashMap<>();

    static {
        checkRegistry.put(DEFAULT_TEST_ID, UrlCheck.class);
    }
}
