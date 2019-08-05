package com.github.flussig.check;

import com.github.flussig.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Registers check classes (both defined in this library and in user's code)
 */
public class CheckRegistry {
    public static Map<String, Class<? extends Check>> checkRegistry = new HashMap<>();

    static {
        checkRegistry.put(Constants.DEFAULT_TEST_ID, UrlCheck.class);
    }
}
