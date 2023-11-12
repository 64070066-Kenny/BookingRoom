package com.example.bookingroom.repository;

import com.example.bookingroom.pojo.DataBooking;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends MongoRepository<DataBooking, String> {
    @Query(value = "{name:  '?0'}")
    public DataBooking FindByname(String name);
    @Query(value = "{roomName:  '?0'}")
    public DataBooking FindByRoomname(String roomName);
    @Query(value = "{_id:  '?0'}")
    public DataBooking FindById(String id);

}
