package com.github.flussig.dacdoc.check;

import java.io.File;

/**
 * Check that executes only once and then stores result to avoid executing multiple times
 */
public abstract class SingleExecutionCheck extends Check {
    private volatile boolean executed = false;
    private CheckResult result;

    public SingleExecutionCheck(String argument, File file) {
        super(argument, file);
    }

    @Override
    public CheckResult execute() {
        if(!executed) {
            init();
        }

        return result;
    }

    /**
     * Initialize value of the result
     */
    private synchronized void init() {
        if(executed)
            return;

        result = performCheck();
    }

    /**
     * actual method to perform result
     */
    public abstract CheckResult performCheck();
}
