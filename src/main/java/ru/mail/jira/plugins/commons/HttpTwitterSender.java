package ru.mail.jira.plugins.commons;

import com.atlassian.jira.util.json.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

/**
 * Created by i.mashintsev on 25.11.15.
 */
public class HttpTwitterSender {

    private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }

    public HttpTwitterSender authenticate(String key, String secret) throws AuthenticationException {
        try {
            HttpSender sender = new HttpSender("https://api.twitter.com/oauth2/token")
                    .setAuthenticationInfo(key, secret)
                    .setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            JSONObject response = new JSONObject(sender.sendGet("grant_type=client_credentials"));
            accessToken = response.getString("access_token");
            if (StringUtils.isEmpty(accessToken)) {
                throw new AuthenticationException(String.format("There isn't access token. Authorization failed with params %s %s", key, secret));
            }
        } catch (Throwable t) {
            throw new AuthenticationException(String.format("Authorization failed with params %s %s", key, secret));
        }
        return this;
    }

    public String sendGet(String url, String body) throws IOException {
        HttpSender sender = new HttpSender(url)
                .setHeader("Authorization", "Bearer " + accessToken);

        return sender.sendGet(body);
    }
}
