package com.smartcampus.w2120049_okithadintharu_5cosc022w.exception.mapper;

import com.smartcampus.w2120049_okithadintharu_5cosc022w.exception.SensorUnavailableException;
import com.smartcampus.w2120049_okithadintharu_5cosc022w.model.ErrorMessage;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class SensorUnavailableExceptionMapper
        implements ExceptionMapper<SensorUnavailableException> {

    @Override
    public Response toResponse(SensorUnavailableException ex) {
        ErrorMessage body = new ErrorMessage(403, "Forbidden", ex.getMessage());
        return Response.status(Response.Status.FORBIDDEN)
                .entity(body)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}