package com.smartcampus.w2120049_okithadintharu_5cosc022w.repository;

import com.smartcampus.w2120049_okithadintharu_5cosc022w.model.SensorReading;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class SensorReadingRepository {

    private static final SensorReadingRepository INSTANCE = new SensorReadingRepository();

    private final Map<String, List<SensorReading>> readingsBySensor = new ConcurrentHashMap<>();

    private SensorReadingRepository() {
    }

    public static SensorReadingRepository getInstance() {
        return INSTANCE;
    }

    public SensorReading add(String sensorId, SensorReading reading) {
        readingsBySensor
                .computeIfAbsent(sensorId, k -> new CopyOnWriteArrayList<>())
                .add(reading);
        return reading;
    }

    public List<SensorReading> findBySensorId(String sensorId) {
        List<SensorReading> readings = readingsBySensor.get(sensorId);
        if (readings == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(new ArrayList<>(readings));
    }

    public int countForSensor(String sensorId) {
        List<SensorReading> readings = readingsBySensor.get(sensorId);
        return (readings == null) ? 0 : readings.size();
    }

    public void deleteAllForSensor(String sensorId) {
        readingsBySensor.remove(sensorId);
    }

}
