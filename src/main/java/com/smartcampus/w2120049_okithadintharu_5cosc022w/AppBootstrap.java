package com.smartcampus.w2120049_okithadintharu_5cosc022w;

import com.smartcampus.w2120049_okithadintharu_5cosc022w.model.Room;
import com.smartcampus.w2120049_okithadintharu_5cosc022w.model.Sensor;
import com.smartcampus.w2120049_okithadintharu_5cosc022w.model.SensorReading;
import com.smartcampus.w2120049_okithadintharu_5cosc022w.repository.RoomRepository;
import com.smartcampus.w2120049_okithadintharu_5cosc022w.repository.SensorReadingRepository;
import com.smartcampus.w2120049_okithadintharu_5cosc022w.repository.SensorRepository;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;


@WebListener
public class AppBootstrap implements ServletContextListener {

    private static final Logger LOGGER = Logger.getLogger(AppBootstrap.class.getName());

    private final RoomRepository roomRepository = RoomRepository.getInstance();
    private final SensorRepository sensorRepository = SensorRepository.getInstance();
    private final SensorReadingRepository readingRepository = SensorReadingRepository.getInstance();

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            seedRooms();
            seedSensors();
            seedSampleReadings();
            LOGGER.info("SmartCampusAPI seeded successfully: "
                    + roomRepository.count() + " rooms, "
                    + sensorRepository.count() + " sensors.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to seed sample data on startup", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Nothing to clean up — in-memory repositories are GC'd with the JVM.
    }

    /**
     * Creates three sample rooms representing common campus spaces.
     */
    private void seedRooms() {
        Room library = new Room("LIB-301", "Library Quiet Study", 40);
        library.setSensorIds(Arrays.asList("TEMP-001", "OCC-001", "CO2-001"));

        Room lecture = new Room("LEC-12", "Lecture Theatre 12", 120);
        lecture.setSensorIds(Arrays.asList("CO2-002", "OCC-002"));

        Room lab = new Room("LAB-5", "Robotics Lab", 25);

        roomRepository.save(library);
        roomRepository.save(lecture);
        roomRepository.save(lab);
    }

    /**
     * Creates five sample sensors distributed across two of the seeded rooms.
     * The Robotics Lab is intentionally left empty to make the
     * "delete a room with no sensors" test path immediately demonstrable.
     */
    private void seedSensors() {
        sensorRepository.save(new Sensor("TEMP-001", "Temperature", "ACTIVE", 21.5, "LIB-301"));
        sensorRepository.save(new Sensor("OCC-001", "Occupancy", "ACTIVE", 18, "LIB-301"));
        sensorRepository.save(new Sensor("CO2-001", "CO2", "ACTIVE", 612, "LIB-301"));
        sensorRepository.save(new Sensor("CO2-002", "CO2", "MAINTENANCE", 0, "LEC-12"));
        sensorRepository.save(new Sensor("OCC-002", "Occupancy", "ACTIVE", 87, "LEC-12"));
    }

    /**
     * Adds a small history of readings to one sensor so that
     * {@code GET /sensors/TEMP-001/readings} returns a non-empty payload
     * out of the box.
     */
    private void seedSampleReadings() {
        long now = System.currentTimeMillis();
        readingRepository.add("TEMP-001", new SensorReading("R-1001", now - 600_000, 21.1));
        readingRepository.add("TEMP-001", new SensorReading("R-1002", now - 300_000, 21.3));
        readingRepository.add("TEMP-001", new SensorReading("R-1003", now,           21.5));
    }
}