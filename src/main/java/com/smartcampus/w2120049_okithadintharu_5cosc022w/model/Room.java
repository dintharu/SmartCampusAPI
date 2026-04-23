package com.smartcampus.w2120049_okithadintharu_5cosc022w.model;

import java.util.ArrayList;
import java.util.List;

public class Room {

    private String id;

    private String name;

    private int capacity;

    private List<String> sensorIds = new ArrayList<>();

    public Room() {
    }

    public Room(String id, String name, int capacity) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public List<String> getSensorIds() {
        return sensorIds;
    }

    public void setSensorIds(List<String> sensorIds) {
        this.sensorIds = sensorIds;
    }

    @Override
    public String toString() {
        return "Room{"
                + "id='" + id + '\''
                + ", name='" + name + '\''
                + ", capacity=" + capacity
                + ", sensorIds=" + sensorIds
                + '}';
    }

}
