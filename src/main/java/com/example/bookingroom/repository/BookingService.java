package com.example.bookingroom.repository;

import com.example.bookingroom.pojo.DataBooking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class BookingService {
    @Autowired
    private BookingRepository bookingRepository;

    public BookingService(BookingRepository bookingRepository){
        this.bookingRepository = bookingRepository;
    }
    public List<DataBooking> findDataBooking(){
        return bookingRepository.findAll();
    }
    public DataBooking createDataBooking(DataBooking dataBooking){
        return bookingRepository.save(dataBooking);
    }
    public DataBooking updateDataBooking(DataBooking dataBooking){
        return bookingRepository.save(dataBooking);
    }
    public boolean delDataBooking(DataBooking dataBooking) {
        try {
            bookingRepository.delete(dataBooking);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    public DataBooking foundName(String name){
        return bookingRepository.FindByname(name);
    }
    public DataBooking foundroomName(String name){
        return bookingRepository.FindByRoomname(name);
    }
    public DataBooking showdetial(String id){
        return bookingRepository.FindById(id);
    }



}
