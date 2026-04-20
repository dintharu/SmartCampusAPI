/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.w2120049_okithadintharu_5cosc022w.model;

/**
 *
 * @author LENOVO
 */
public class SensorReading {
    
     private String id;
     
      private long timestamp;
      
       private double value;

    public SensorReading() {
    }

    public SensorReading(String id, long timestamp, double value) {
        this.id = id;
        this.timestamp = timestamp;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
           return "SensorReading{"
                + "id='" + id + '\''
                + ", timestamp=" + timestamp
                + ", value=" + value
                + '}';
    }
       
       
    
}
