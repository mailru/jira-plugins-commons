package ru.mail.jira.plugins.commons;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.mail.Email;
import com.atlassian.jira.mail.builder.EmailBuilder;
import com.atlassian.jira.notification.NotificationRecipient;
import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserUtil;
import com.atlassian.mail.queue.MailQueueItem;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;

@SuppressWarnings("UnusedDeclaration")
public class CommonUtils {
    private final static Logger log = LoggerFactory.getLogger(CommonUtils.class);

    public static String getXmlTagContent(String xml, String tagName) {
        String startTag = String.format("<%s>", tagName);
        String endTag = String.format("</%s>", tagName);
        int startPos = xml.indexOf(startTag);
        int endPos = xml.indexOf(endTag);
        return (startPos >= 0 && endPos >= 0) ? xml.substring(startPos + startTag.length(), endPos) : "";
    }

    public static String join(List<String> stringList) {
        if (stringList == null)
            return "";
        return StringUtils.join(stringList, ", ");
    }

    public static List<String> split(String joinedString) {
        List<String> stringList = new LinkedList<String>();
        if (StringUtils.isNotBlank(joinedString))
            for (String s : joinedString.trim().split("\\s*,\\s*"))
                if (StringUtils.isNotEmpty(s))
                    stringList.add(s);
        return stringList;
    }

    public static String convertUserKeysToJoinedString(List<String> userKeys) {
        List<String> userNames = new LinkedList<String>();
        if (userKeys != null)
            for (String userKey : userKeys) {
                ApplicationUser user = ComponentAccessor.getUserManager().getUserByKey(userKey);
                if (user != null)
                    userNames.add(user.getName());
            }
        return join(userNames);
    }

    public static List<String> convertJoinedStringToUserKeys(String joinedString) {
        List<String> userKeys = new LinkedList<String>();
        for (String userName : split(joinedString)) {
            ApplicationUser user = ComponentAccessor.getUserManager().getUserByName(userName);
            if (user == null)
                throw new IllegalArgumentException(ComponentAccessor.getJiraAuthenticationContext().getI18nHelper().getText("user.picker.errors.usernotfound", userName));
            userKeys.add(user.getKey());
        }
        return userKeys;
    }

    public static CustomField getCustomField(long customFieldId) {
        CustomField customField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(customFieldId);
        if (customField == null)
            throw new IllegalStateException(String.format("Custom field (%d) is not found.", customFieldId));
        return customField;
    }

    public static String getCustomFieldStringValue(Issue issue, long customFieldId) {
        CustomField customField = getCustomField(customFieldId);
        Object customFieldValue = issue.getCustomFieldValue(customField);
        if (customFieldValue != null)
            return customFieldValue.toString().trim();
        else
            return "";
    }

    public static boolean isUserInGroups(ApplicationUser user, Collection<String> groupNames) {
        if (user == null)
            return false;

        GroupManager groupManager = ComponentAccessor.getGroupManager();
        for (String groupName : groupNames)
            if (groupManager.isUserInGroup(user.getName(), groupName))
                return true;
        return false;
    }

    public static void sendEmail(List<String> recipientKeys, String subject, String message) {
        UserUtil userUtil = ComponentAccessor.getUserUtil();
        for (String key : recipientKeys) {
            ApplicationUser user = userUtil.getUserByKey(key);
            if (user != null)
                sendEmail(user, message, subject);
            else
                throw new IllegalArgumentException("Bad user key => " + key);
        }
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

    public static String getStackTrace(Throwable e) {
        Writer stackTrace = new StringWriter();
        e.printStackTrace(new PrintWriter(stackTrace));
        return stackTrace.toString();
    }
}
