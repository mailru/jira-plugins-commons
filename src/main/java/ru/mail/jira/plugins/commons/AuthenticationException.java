package ru.mail.jira.plugins.commons;

import java.io.IOException;

public class AuthenticationException extends IOException {
    public AuthenticationException() {
        super();
    }

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
