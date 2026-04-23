package com.smartcampus.w2120049_okithadintharu_5cosc022w.filter;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.logging.Logger;

@Provider
public class LoggingFilter
        implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger LOGGER = Logger.getLogger(LoggingFilter.class.getName());

    /**
     * Context property key used to shuttle the request start time to the
     * response phase.
     */
    private static final String START_TIME_PROPERTY = "smartcampus.requestStartTime";

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        long startTime = System.currentTimeMillis();
        requestContext.setProperty(START_TIME_PROPERTY, startTime);

        LOGGER.info(String.format(
                "--> %s %s",
                requestContext.getMethod(),
                requestContext.getUriInfo().getPath()
        ));
    }

    @Override
    public void filter(ContainerRequestContext requestContext,
            ContainerResponseContext responseContext) throws IOException {
        Object startTimeProperty = requestContext.getProperty(START_TIME_PROPERTY);
        long elapsedMs = 0L;
        if (startTimeProperty instanceof Long) {
            elapsedMs = System.currentTimeMillis() - (Long) startTimeProperty;
        }

        LOGGER.info(String.format(
                "<-- %s %s  [status=%d, elapsed=%d ms]",
                requestContext.getMethod(),
                requestContext.getUriInfo().getPath(),
                responseContext.getStatus(),
                elapsedMs
        ));
    }
}
