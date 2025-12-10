package com.moments.optimizer.model;

public class ModelClientException extends Exception {
    private final String errorCode;
    private final String rawResponseSnippet;

    public ModelClientException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.rawResponseSnippet = null;
    }

    public ModelClientException(String errorCode, String message, String rawResponseSnippet) {
        super(message);
        this.errorCode = errorCode;
        this.rawResponseSnippet = rawResponseSnippet;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getRawResponseSnippet() {
        return rawResponseSnippet;
    }
}
