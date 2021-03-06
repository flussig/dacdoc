package com.github.flussig.dacdoc.text;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Result for validation of internal consistency of DacDoc placeholders/texts
 */
public class ValidationResult {
    private Collection<String> issues = new ArrayList<>();

    public Collection<String> getIssues() {
        return issues;
    }

    public void setIssues(Collection<String> issues) {
        this.issues = issues;
    }
}
