package com.github.flussig.dacdoc.exception;

/**
 * DacDoc exception that appears when parsing readme files
 */
public class DacDocParseException extends DacDocException {
    public DacDocParseException(String message) {
        super(message);
    }
    public DacDocParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
