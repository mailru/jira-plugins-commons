package ru.mail.jira.plugins.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;

@SuppressWarnings("UnusedDeclaration")
public abstract class RestExecutor<T> {
    private static final String FIELD_HEADER = "X-Atlassian-Rest-Exception-Field";

    private static final Logger log = LoggerFactory.getLogger(RestExecutor.class);

    protected abstract T doAction() throws Exception;

    public Response getResponse() {
        return getResponse(Response.Status.OK);
    }

    public Response getResponse(Response.Status successStatus) {
        try {
            T actionResult = doAction();
            Response.ResponseBuilder responseBuilder = Response.status(successStatus).entity(actionResult);

            if (actionResult instanceof byte[])
                responseBuilder = responseBuilder.type("application/force-download")
                        .header("Content-Transfer-Encoding", "binary")
                        .header("charset", "UTF-8");
            else if(actionResult instanceof StreamRestResult)
                responseBuilder = responseBuilder.entity(((StreamRestResult) actionResult).getInputStream())
                        .type(((StreamRestResult) actionResult).getContentType());
            return responseBuilder.build();
        } catch (SecurityException e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
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
