package com.smartcampus.w2120049_okithadintharu_5cosc022w.resource;

import com.smartcampus.w2120049_okithadintharu_5cosc022w.exception.ResourceNotFoundException;
import com.smartcampus.w2120049_okithadintharu_5cosc022w.model.Room;
import com.smartcampus.w2120049_okithadintharu_5cosc022w.repository.RoomRepository;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;


@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    private final RoomRepository roomRepository = RoomRepository.getInstance();

    
    @GET
    public Response listAllRooms() {
        List<Room> rooms = roomRepository.findAll();
        return Response.ok(rooms).build();
    }

    
    @GET
    @Path("/{roomId}")
    public Response getRoomById(@PathParam("roomId") String roomId) {
        Room room = roomRepository.findById(roomId);
        if (room == null) {
            throw new ResourceNotFoundException("Room with id '" + roomId + "' does not exist.");
        }
        return Response.ok(room).build();
    }
}