package com.example.bookingroom.pojo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("BookingRoom")
public class DataBooking {
    @Id
    private String _id;
    private String roomName;
    private Integer participants;
    private String purpose;
    private String name;
    private String bookingDate;
    private String startTime;
    private String endTime;
    private String status;
    private String email;

    public DataBooking(){}
    public DataBooking(String _id, String roomName, Integer participants, String purpose, String name, String bookingDate, String startTime, String endTime, String status, String email){
        this._id = _id;
        this.roomName = roomName;
        this.participants = participants;
        this.purpose = purpose;
        this.name = name;
        this.bookingDate = bookingDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.email = this.email;
    }
}
