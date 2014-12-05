package ru.mail.jira.plugins.commons;

import java.io.IOException;

public class HttpSenderException extends IOException {
    private int responseCode;
    private String requestMessage;
    private String responseMessage;

    public HttpSenderException(int responseCode, String requestMessage, String responseMessage) {
        super(String.format("Invalid server response, code: %d, message: %s", responseCode, responseMessage));
        this.responseCode = responseCode;
        this.requestMessage = requestMessage;
        this.responseMessage = responseMessage;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getRequestMessage() {
        return requestMessage;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    @Override
    public String toString() {
        return String.format("SenderException[responseCode=%d, requestMessage=%s, responseMessage=%s]", responseCode, requestMessage, responseMessage);
    }
}
