package ru.mail.jira.plugins.commons;


import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import io.sentry.Sentry;
import io.sentry.event.UserBuilder;

public class SentryClient {
    private static JiraAuthenticationContext jiraAuthenticationContext;

    public static void init(JiraAuthenticationContext jiraAuthenticationContext, String dsn) {
        if (SentryClient.jiraAuthenticationContext == null) {
            SentryClient.jiraAuthenticationContext = jiraAuthenticationContext;
            Sentry.init(dsn);
        }
    }

    public static void capture(Throwable throwable) {
        try {
            setContextUser();
            Sentry.capture(throwable);
        } catch (Throwable e) {
        } finally {
            Sentry.clearContext();
        }
    }

    public static void capture(String message) {
        try {
            setContextUser();
            Sentry.capture(message);
        } catch (Throwable e) {
        } finally {
            Sentry.clearContext();
        }
    }

    public static void close() {
        Sentry.close();
    }

    private static void setContextUser() {
        if (jiraAuthenticationContext != null && jiraAuthenticationContext.isLoggedInUser()) {
            ApplicationUser user = jiraAuthenticationContext.getLoggedInUser();
            Sentry.setUser(new UserBuilder()
                                   .setId(user.getKey())
                                   .setEmail(user.getEmailAddress())
                                   .setUsername(user.getDisplayName())
                                   .build());
        }
    }
}
