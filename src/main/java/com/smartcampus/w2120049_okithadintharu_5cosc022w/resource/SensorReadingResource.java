package com.smartcampus.w2120049_okithadintharu_5cosc022w.resource;

import com.smartcampus.w2120049_okithadintharu_5cosc022w.exception.InvalidInputException;
import com.smartcampus.w2120049_okithadintharu_5cosc022w.exception.SensorUnavailableException;
import com.smartcampus.w2120049_okithadintharu_5cosc022w.model.Sensor;
import com.smartcampus.w2120049_okithadintharu_5cosc022w.model.SensorReading;
import com.smartcampus.w2120049_okithadintharu_5cosc022w.repository.SensorReadingRepository;
import com.smartcampus.w2120049_okithadintharu_5cosc022w.repository.SensorRepository;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;
import java.util.UUID;


@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private final String sensorId;
    private final SensorRepository sensorRepository = SensorRepository.getInstance();
    private final SensorReadingRepository readingRepository = SensorReadingRepository.getInstance();

   
    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

   
    @GET
    public Response getReadings() {
        List<SensorReading> readings = readingRepository.findBySensorId(sensorId);
        return Response.ok(readings).build();
    }

    @POST
    public Response addReading(SensorReading reading, @Context UriInfo uriInfo) {
        if (reading == null) {
            throw new InvalidInputException("Request body must contain a reading object.");
        }

        Sensor parent = sensorRepository.findById(sensorId);

        // Spec Part 5.3: MAINTENANCE sensors are physically offline and
        // must reject writes. Any other status (ACTIVE, INACTIVE, etc.)
        // accepts readings.
        if ("MAINTENANCE".equalsIgnoreCase(parent.getStatus())) {
            throw new SensorUnavailableException(
                    "Sensor '" + sensorId + "' is in MAINTENANCE and cannot accept readings."
            );
        }

        // Fill in server-side defaults if the client omitted them.
        if (reading.getId() == null || reading.getId().trim().isEmpty()) {
            reading.setId("R-" + UUID.randomUUID().toString().substring(0, 8));
        }
        if (reading.getTimestamp() == 0L) {
            reading.setTimestamp(System.currentTimeMillis());
        }

        // Persist the reading.
        SensorReading saved = readingRepository.add(sensorId, reading);

        // Side-effect required by spec Part 4.2: keep parent sensor's
        // currentValue in sync with the latest reading.
        parent.setCurrentValue(saved.getValue());
        sensorRepository.save(parent);

        URI location = uriInfo.getAbsolutePath();
        return Response.created(location).entity(saved).build();
    }
}