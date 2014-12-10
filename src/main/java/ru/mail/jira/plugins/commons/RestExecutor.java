package ru.mail.jira.plugins.commons;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.util.I18nHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;

@SuppressWarnings("UnusedDeclaration")
public abstract class RestExecutor<T> {
    private static final String FIELD_HEADER = "X-Atlassian-Rest-Exception-Field";

    private static final Logger log = LoggerFactory.getLogger(RestExecutor.class);

    private final I18nHelper i18n = ComponentAccessor.getJiraAuthenticationContext().getI18nHelper();

    protected abstract T doAction();

    public Response getResponse() {
        return getResponse(Response.Status.OK);
    }

    public Response getResponse(Response.Status successStatus) {
        try {
            return Response.status(successStatus).entity(doAction()).build();
        } catch (SecurityException e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(i18n.getText("login.error.communication")).build();
        } catch (IllegalArgumentException e) {
            Response.ResponseBuilder responseBuilder = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage());
            if (e instanceof RestFieldException)
                responseBuilder = responseBuilder.header(FIELD_HEADER, ((RestFieldException) e).getField());
            return responseBuilder.build();
        } catch (Exception e) {
            log.error("REST Exception", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}
