package com.smartcampus.w2120049_okithadintharu_5cosc022w.resource;

import com.smartcampus.w2120049_okithadintharu_5cosc022w.exception.InvalidInputException;
import com.smartcampus.w2120049_okithadintharu_5cosc022w.exception.ResourceNotFoundException;
import com.smartcampus.w2120049_okithadintharu_5cosc022w.exception.RoomNotEmptyException;
import com.smartcampus.w2120049_okithadintharu_5cosc022w.model.Room;
import com.smartcampus.w2120049_okithadintharu_5cosc022w.model.Sensor;
import com.smartcampus.w2120049_okithadintharu_5cosc022w.repository.RoomRepository;
import com.smartcampus.w2120049_okithadintharu_5cosc022w.repository.SensorRepository;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    private final RoomRepository roomRepository = RoomRepository.getInstance();
    private final SensorRepository sensorRepository = SensorRepository.getInstance();

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

    @POST
    public Response createRoom(Room room, @Context UriInfo uriInfo) {
        if (room == null || room.getId() == null || room.getId().trim().isEmpty()) {
            throw new InvalidInputException("Room 'id' is required.");
        }
        if (room.getName() == null || room.getName().trim().isEmpty()) {
            throw new InvalidInputException("Room 'name' is required.");
        }

        Room saved = roomRepository.save(room);

        URI location = uriInfo.getAbsolutePathBuilder()
                .path(saved.getId())
                .build();

        return Response.created(location).entity(saved).build();
    }

    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        if (!roomRepository.exists(roomId)) {
            throw new ResourceNotFoundException("Room with id '" + roomId + "' does not exist.");
        }

        List<Sensor> linkedSensors = sensorRepository.findByRoomId(roomId);
        if (!linkedSensors.isEmpty()) {
            throw new RoomNotEmptyException(
                    "Room '" + roomId + "' cannot be deleted because it still has "
                    + linkedSensors.size() + " linked sensor(s). "
                    + "Remove or reassign the sensors before deleting the room."
            );
        }

        roomRepository.deleteById(roomId);
        return Response.noContent().build();
    }
}
