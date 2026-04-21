package com.smartcampus.w2120049_okithadintharu_5cosc022w.exception.mapper;

import com.smartcampus.w2120049_okithadintharu_5cosc022w.model.ErrorMessage;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.logging.Level;
import java.util.logging.Logger;


@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOGGER = Logger.getLogger(GlobalExceptionMapper.class.getName());

    @Override
    public Response toResponse(Throwable ex) {

        // Let Jersey's own WebApplicationException (404 route-not-found,
        // 405 method-not-allowed, 415 unsupported-media-type, etc.)
        // propagate with their intended status.
        if (ex instanceof WebApplicationException) {
            WebApplicationException wae = (WebApplicationException) ex;
            Response existing = wae.getResponse();
            int status = existing.getStatus();
            ErrorMessage body = new ErrorMessage(
                    status,
                    Response.Status.fromStatusCode(status) != null
                            ? Response.Status.fromStatusCode(status).getReasonPhrase()
                            : "Error",
                    ex.getMessage() != null ? ex.getMessage() : "Request could not be processed."
            );
            return Response.status(status)
                    .entity(body)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        // Anything else is a server bug. Log the trace for ourselves,
        // return a generic message to the client.
        LOGGER.log(Level.SEVERE, "Unhandled exception while processing request", ex);
        ErrorMessage body = new ErrorMessage(
                500,
                "Internal Server Error",
                "An unexpected error occurred. Please contact the system administrator."
        );
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(body)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}