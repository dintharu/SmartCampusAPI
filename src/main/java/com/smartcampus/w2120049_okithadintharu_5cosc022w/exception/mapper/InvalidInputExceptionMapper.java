package com.smartcampus.w2120049_okithadintharu_5cosc022w.exception.mapper;

import com.smartcampus.w2120049_okithadintharu_5cosc022w.exception.InvalidInputException;
import com.smartcampus.w2120049_okithadintharu_5cosc022w.model.ErrorMessage;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


@Provider
public class InvalidInputExceptionMapper
        implements ExceptionMapper<InvalidInputException> {

    @Override
    public Response toResponse(InvalidInputException ex) {
        ErrorMessage body = new ErrorMessage(400, "Bad Request", ex.getMessage());
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(body)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}