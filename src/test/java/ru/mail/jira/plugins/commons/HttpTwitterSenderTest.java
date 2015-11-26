package ru.mail.jira.plugins.commons;

import com.atlassian.jira.avatar.SystemAndCustomAvatars;
import com.atlassian.jira.util.json.JSONArray;
import com.atlassian.jira.util.json.JSONObject;

import static org.junit.Assert.assertNotNull;

/**
 * Created by i.mashintsev on 25.11.15.
 */
public class HttpTwitterSenderTest {

    @org.junit.Test
    public void testAuthenticate() throws Exception {
        HttpTwitterSender oAuthSender = new HttpTwitterSender()
                .authenticate("hxxZED7XGgEIZfdZjimVpCA4R", "TCLxQrik1rL6p4AprH1eV8764jmtEHhMJ3JZuNDS3DdbzZmhzV");

        assertNotNull(oAuthSender.getAccessToken());
    }

    @org.junit.Test(expected = AuthenticationException.class)
    public void testFailedAuthenticate() throws Exception {
        HttpTwitterSender oAuthSender = new HttpTwitterSender()
                .authenticate("hxxZED7XGgEIZfdZjimVpCA4R", "TCLxQrik1rL6p4AprH1eV8764jmtEHhMJ3JZuNDS3DdbzZmhz1");
    }

    @org.junit.Test
    public void testSend() throws Exception {
        HttpTwitterSender oAuthSender = new HttpTwitterSender()
                .authenticate("hxxZED7XGgEIZfdZjimVpCA4R", "TCLxQrik1rL6p4AprH1eV8764jmtEHhMJ3JZuNDS3DdbzZmhzV");

        String reponse = oAuthSender.sendGet(CommonUtils.formatUrl("https://api.twitter.com/1.1/search/tweets.json?q=%s", "mail.ru"), null);
        assertNotNull(reponse);
    }
}