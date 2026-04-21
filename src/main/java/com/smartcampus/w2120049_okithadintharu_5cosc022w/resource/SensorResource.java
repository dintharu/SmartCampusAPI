package com.smartcampus.w2120049_okithadintharu_5cosc022w.resource;

import com.smartcampus.w2120049_okithadintharu_5cosc022w.exception.InvalidInputException;
import com.smartcampus.w2120049_okithadintharu_5cosc022w.exception.LinkedResourceNotFoundException;
import com.smartcampus.w2120049_okithadintharu_5cosc022w.exception.ResourceNotFoundException;
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


@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    private final SensorRepository sensorRepository = SensorRepository.getInstance();
    private final RoomRepository roomRepository = RoomRepository.getInstance();

    
    @GET
    public Response listAllSensors() {
        List<Sensor> sensors = sensorRepository.findAll();
        return Response.ok(sensors).build();
    }

    @GET
    @Path("/{sensorId}")
    public Response getSensorById(@PathParam("sensorId") String sensorId) {
        Sensor sensor = sensorRepository.findById(sensorId);
        if (sensor == null) {
            throw new ResourceNotFoundException("Sensor with id '" + sensorId + "' does not exist.");
        }
        return Response.ok(sensor).build();
    }

   
    @POST
    public Response createSensor(Sensor sensor, @Context UriInfo uriInfo) {
        // Basic shape validation
        if (sensor == null || sensor.getId() == null || sensor.getId().trim().isEmpty()) {
            throw new InvalidInputException("Sensor 'id' is required.");
        }
        if (sensor.getType() == null || sensor.getType().trim().isEmpty()) {
            throw new InvalidInputException("Sensor 'type' is required.");
        }
        if (sensor.getStatus() == null || sensor.getStatus().trim().isEmpty()) {
            throw new InvalidInputException("Sensor 'status' is required.");
        }
        if (sensor.getRoomId() == null || sensor.getRoomId().trim().isEmpty()) {
            throw new InvalidInputException("Sensor 'roomId' is required.");
        }

        // Linked-resource integrity: the referenced Room must exist.
        if (!roomRepository.exists(sensor.getRoomId())) {
            throw new LinkedResourceNotFoundException(
                    "Cannot create sensor: referenced room '"
                            + sensor.getRoomId() + "' does not exist."
            );
        }

        Sensor saved = sensorRepository.save(sensor);

        URI location = uriInfo.getAbsolutePathBuilder()
                .path(saved.getId())
                .build();

        return Response.created(location).entity(saved).build();
    }

    
    @DELETE
    @Path("/{sensorId}")
    public Response deleteSensor(@PathParam("sensorId") String sensorId) {
        if (!sensorRepository.exists(sensorId)) {
            throw new ResourceNotFoundException("Sensor with id '" + sensorId + "' does not exist.");
        }
        sensorRepository.deleteById(sensorId);
        return Response.noContent().build();
    }
}