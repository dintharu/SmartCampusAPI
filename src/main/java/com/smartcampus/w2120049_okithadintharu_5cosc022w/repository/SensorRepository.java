package com.smartcampus.w2120049_okithadintharu_5cosc022w.repository;

import com.smartcampus.w2120049_okithadintharu_5cosc022w.model.Sensor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SensorRepository {

    private static final SensorRepository INSTANCE = new SensorRepository();

    private final Map<String, Sensor> sensors = new ConcurrentHashMap<>();

    private SensorRepository() {
    }

    public static SensorRepository getInstance() {
        return INSTANCE;
    }

    public Sensor save(Sensor sensor) {
        sensors.put(sensor.getId(), sensor);
        return sensor;
    }

    public Sensor findById(String id) {
        return sensors.get(id);
    }

    public List<Sensor> findAll() {
        Collection<Sensor> values = sensors.values();
        return new ArrayList<>(values);
    }

    public List<Sensor> findByType(String type) {
        List<Sensor> matches = new ArrayList<>();
        if (type == null) {
            return matches;
        }
        for (Sensor sensor : sensors.values()) {
            if (sensor.getType() != null && sensor.getType().equalsIgnoreCase(type)) {
                matches.add(sensor);
            }
        }
        return matches;
    }

    public List<Sensor> findByRoomId(String roomId) {
        List<Sensor> matches = new ArrayList<>();
        if (roomId == null) {
            return matches;
        }
        for (Sensor sensor : sensors.values()) {
            if (roomId.equals(sensor.getRoomId())) {
                matches.add(sensor);
            }
        }
        return matches;
    }

    public boolean exists(String id) {
        return sensors.containsKey(id);
    }

    public boolean deleteById(String id) {
        return sensors.remove(id) != null;
    }

    public int count() {
        return sensors.size();
    }

}
