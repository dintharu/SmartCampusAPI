package com.smartcampus.w2120049_okithadintharu_5cosc022w.exception.mapper;

import com.smartcampus.w2120049_okithadintharu_5cosc022w.exception.LinkedResourceNotFoundException;
import com.smartcampus.w2120049_okithadintharu_5cosc022w.model.ErrorMessage;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class LinkedResourceNotFoundExceptionMapper
        implements ExceptionMapper<LinkedResourceNotFoundException> {

    @Override
    public Response toResponse(LinkedResourceNotFoundException ex) {
        ErrorMessage body = new ErrorMessage(422, "Unprocessable Entity", ex.getMessage());
        return Response.status(422) // Jersey doesn't have a Status enum for 422
                .entity(body)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
