/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.w2120049_okithadintharu_5cosc022w.repository;

import com.smartcampus.w2120049_okithadintharu_5cosc022w.model.Room;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author LENOVO
 */
public class RoomRepository {
    
    
    private static final RoomRepository INSTANCE = new RoomRepository();
    
     private final Map<String, Room> rooms = new ConcurrentHashMap<>();

    public RoomRepository() {
    }
     
      public static RoomRepository getInstance() {
        return INSTANCE;
    }
      
      public Room save(Room room) {
        rooms.put(room.getId(), room);
        return room;
    }
      
      public Room findById(String id) {
        return rooms.get(id);
    }
      
      public List<Room> findAll() {
        Collection<Room> values = rooms.values();
        return new ArrayList<>(values);
    }
      
      
       public boolean exists(String id) {
        return rooms.containsKey(id);
    }
       
       public boolean deleteById(String id) {
        return rooms.remove(id) != null;
    }
       
        public int count() {
        return rooms.size();
    }
        
      
      
    
}
