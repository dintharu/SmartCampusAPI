package com.smartcampus.w2120049_okithadintharu_5cosc022w.exception.mapper;

import com.smartcampus.w2120049_okithadintharu_5cosc022w.exception.RoomNotEmptyException;
import com.smartcampus.w2120049_okithadintharu_5cosc022w.model.ErrorMessage;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class RoomNotEmptyExceptionMapper
        implements ExceptionMapper<RoomNotEmptyException> {

    @Override
    public Response toResponse(RoomNotEmptyException ex) {
        ErrorMessage body = new ErrorMessage(409, "Conflict", ex.getMessage());
        return Response.status(Response.Status.CONFLICT)
                .entity(body)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}