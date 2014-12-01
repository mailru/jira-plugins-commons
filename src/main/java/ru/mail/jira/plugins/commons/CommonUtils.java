package ru.mail.jira.plugins.commons;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.mail.Email;
import com.atlassian.jira.mail.builder.EmailBuilder;
import com.atlassian.jira.notification.NotificationRecipient;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.mail.queue.MailQueueItem;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

@SuppressWarnings("UnusedDeclaration")
public class CommonUtils {
    public static String getXmlTagContent(String xml, String tagName) {
        String startTag = String.format("<%s>", tagName);
        String endTag = String.format("</%s>", tagName);
        int startPos = xml.indexOf(startTag);
        int endPos = xml.indexOf(endTag);
        return (startPos >= 0 && endPos >= 0) ? xml.substring(startPos + startTag.length(), endPos) : "";
    }

    public static String convertUserKeysToJoinedString(List<String> userKeys) {
        if (userKeys == null)
            return "";

        StringBuilder sb = new StringBuilder();
        for (String userKey : userKeys) {
            ApplicationUser user = ComponentAccessor.getUserManager().getUserByKey(userKey);
            if (user != null) {
                if (sb.length() > 0)
                    sb.append(", ");
                sb.append(user.getName());
            }
        }
        return sb.toString();
    }

    public static List<String> convertJoinedStringToUserKeys(String s) {
        if (StringUtils.isEmpty(s))
            return Collections.emptyList();
        String[] userNames = s.trim().split("\\s*,\\s*");

        List<String> userKeys = new ArrayList<String>(userNames.length);
        for (String userName : userNames) {
            ApplicationUser user = ComponentAccessor.getUserManager().getUserByName(userName);
            if (user == null)
                throw new IllegalArgumentException(ComponentAccessor.getJiraAuthenticationContext().getI18nHelper().getText("user.picker.errors.usernotfound", userName));
            userKeys.add(user.getKey());
        }
        return userKeys;
    }

    public static void sendEmail(ApplicationUser recipient, String subject, String message) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("subject", subject);
        params.put("message", message);

        Email email = new Email(recipient.getEmailAddress());

        NotificationRecipient notificationRecipient = new NotificationRecipient(recipient);
        boolean isHtmlFormat = !NotificationRecipient.MIMETYPE_TEXT.equals(notificationRecipient.getFormat());

        MailQueueItem item = new EmailBuilder(email, notificationRecipient)
                .withSubject(subject)
                .withBodyFromFile(isHtmlFormat ? "ru/mail/jira/plugins/commons/email-html.vm" : "ru/mail/jira/plugins/commons/email-text.vm")
                .addParameters(params)
                .renderLater();
        ComponentAccessor.getMailQueue().addItem(item);
    }
}
