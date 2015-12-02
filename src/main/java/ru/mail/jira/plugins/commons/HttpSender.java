package ru.mail.jira.plugins.commons;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("UnusedDeclaration")
public class HttpSender {
    private static final Logger log = Logger.getLogger(HttpSender.class);

    private final String url;
    private String user;
    private String password;
    private final Map<String, String> headers = new HashMap<String, String>();

    public HttpSender(String url, Object... params) {
        this.url = params != null && params.length > 0 ? CommonUtils.formatUrl(url, params) : url;
    }

    public HttpSender setAuthenticationInfo(String user, String password) {
        this.user = user;
        this.password = password;
        return this;
    }

    public HttpSender setHeader(String header, String value) {
        headers.put(header, value);
        return this;
    }

    public HttpSender setHeaders(Map<String, String> headers) {
        this.headers.putAll(headers);
        return this;
    }

    public HttpSender setContentTypeJson() {
        setHeader("Content-Type", "application/json; charset=utf-8");
        return this;
    }

    private String getAuthRealm() {
        return DatatypeConverter.printBase64Binary(user.concat(":").concat(password).getBytes());
    }

    private String send(String method, String body) throws IOException {
        log.info(String.format("Sending HTTP request: %s", url));

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        try {
            connection.setDoInput(true);
            connection.setDoOutput(StringUtils.isNotEmpty(body));
            connection.setAllowUserInteraction(true);
            connection.setRequestMethod(method);
            if (StringUtils.isNotEmpty(user) && StringUtils.isNotEmpty(password))
                connection.setRequestProperty("Authorization", "Basic " + getAuthRealm());
            for (Map.Entry<String, String> entry : headers.entrySet())
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            if (StringUtils.isNotEmpty(body))
                IOUtils.write(body, connection.getOutputStream());

            int rc = connection.getResponseCode();
            if (rc == HttpURLConnection.HTTP_OK) {
                String response = IOUtils.toString(connection.getInputStream(), "UTF-8");
                log.info(String.format("HTTP response body:\n %s", response));
                return response;
            }
            else {
                String response = "";
                if (connection.getErrorStream() != null)
                       response = IOUtils.toString(connection.getErrorStream(), "UTF-8");
                log.info(String.format("HTTP response body:\n %s", response));
                throw new HttpSenderException(rc, body, response);
            }
        } finally {
            connection.disconnect();
        }
    }

    public String sendGet() throws IOException {
        return send("GET", null);
    }

    public String sendGet(String body) throws IOException {
        return send("GET", body);
    }

    public String sendPost(String body) throws IOException {
        return send("POST", body);
    }
}
