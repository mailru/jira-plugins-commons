package ru.mail.jira.plugins.commons;

import java.io.InputStream;

public class StreamRestResult {
    private final InputStream inputStream;
    private final String contentType;

    public StreamRestResult(InputStream inputStream, String contentType) {
        this.inputStream = inputStream;
        this.contentType = contentType;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public String getContentType() {
        return contentType;
    }
}
