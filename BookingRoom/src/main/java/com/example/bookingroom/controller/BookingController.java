package com.example.bookingroom.controller;

import com.example.bookingroom.pojo.DataBooking;
import com.example.bookingroom.repository.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;


import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class BookingController {
    @Autowired
    private BookingService bookingService;

    @RequestMapping(value = "/Booking", method = RequestMethod.GET)
    public ResponseEntity<?> showData(){
        List<DataBooking> BookingList = bookingService.findDataBooking();
        BookingList.sort(
                Comparator.comparing(DataBooking::getBookingDate)
                        .thenComparing(DataBooking::getStartTime)
        );
        return ResponseEntity.ok(BookingList);
    }

    @RequestMapping("/BookingBy/{id}")
    public ResponseEntity<?> Getdetail(@PathVariable String id){
        DataBooking book = bookingService.showdetial(id);
        if (book != null) {
            return ResponseEntity.ok(book);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @RequestMapping(value = "/BookingByName", method = RequestMethod.GET)
    public ResponseEntity<List<Map<String, String>>> showData(@RequestParam String name) {
        // Assuming you have a data source with DataBooking objects
        List<DataBooking> bookingList = bookingService.findDataBooking();

        // Filter and sort the data based on bookingDate
        List<Map<String, String>> entriesWithSameName = bookingList.stream()
                .filter(booking -> name.equals(booking.getName()))
                .sorted(Comparator.comparing(DataBooking::getBookingDate))
                .map(booking -> {
                    Map<String, String> entry = new HashMap<>();
                    entry.put("_id", booking.get_id());
                    entry.put("roomName", booking.getRoomName());
                    entry.put("participants", String.valueOf(booking.getParticipants()));
                    entry.put("purpose", booking.getPurpose());
                    entry.put("name", booking.getName());
                    entry.put("bookingDate", booking.getBookingDate());
                    entry.put("startTime", booking.getStartTime());
                    entry.put("endTime", booking.getEndTime());
                    entry.put("status", booking.getStatus());
                    entry.put("email", booking.getEmail());
                    return entry;
                })
                .collect(Collectors.toList());

        // Return the sorted list of entries with the same name
        return ResponseEntity.ok(entriesWithSameName);
    }



    @RequestMapping(value = "/addBooking", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createData(@RequestBody DataBooking requestBody) {
        String roomName = requestBody.getRoomName();
        String bookingDate = requestBody.getBookingDate();
        String startTime = requestBody.getStartTime();
        String endTime = requestBody.getEndTime();

        // Check if the start time is before the end time
        LocalTime newLocalStartTime = LocalTime.parse(startTime, DateTimeFormatter.ofPattern("HH:mm"));
        LocalTime newLocalEndTime = LocalTime.parse(endTime, DateTimeFormatter.ofPattern("HH:mm"));

        if (!newLocalStartTime.isBefore(newLocalEndTime)) {
            return ResponseEntity.badRequest().body("{\"error\": \"Start time should be before end time.\"}");
        }

        // Fetch all existing bookings from the database
        List<DataBooking> existingBookings = bookingService.findDataBooking();

        for (DataBooking existingBooking : existingBookings) {
            // Check for room and date overlap
            if (roomName.equals(existingBooking.getRoomName()) && bookingDate.equals(existingBooking.getBookingDate())) {
                LocalTime existingLocalStartTime = LocalTime.parse(existingBooking.getStartTime(), DateTimeFormatter.ofPattern("HH:mm"));
                LocalTime existingLocalEndTime = LocalTime.parse(existingBooking.getEndTime(), DateTimeFormatter.ofPattern("HH:mm"));

                // Check if the time ranges overlap
                if (!(newLocalEndTime.isBefore(existingLocalStartTime) || newLocalStartTime.isAfter(existingLocalEndTime))) {
                    return ResponseEntity.badRequest().body("A booking for this room already exists during the specified date and time.");
                }
            }
        }

        // If no overlap is found and start time is before end time, create the new booking
        DataBooking dataBooking = bookingService.createDataBooking(requestBody);

        return ResponseEntity.ok(dataBooking);
    }




    @RequestMapping(value = "/updateBooking", method = RequestMethod.POST)
    public boolean updateData(@RequestBody MultiValueMap<String, String> b) {
        Map<String, String> m = b.toSingleValueMap();
        String id = m.get("_id");
        String roomName = m.get("roomName");
        String bookingDate = m.get("bookingDate");
        String startTime = m.get("startTime");
        String endTime = m.get("endTime");

        List<DataBooking> existingBookings = bookingService.findDataBooking();

        for (DataBooking existingBooking : existingBookings) {
            // Check if the existing booking has the same _id
            if (existingBooking.get_id().equals(id)) {
                // Convert the new booking times to LocalTime
                LocalTime newLocalStartTime = LocalTime.parse(startTime, DateTimeFormatter.ofPattern("HH:mm"));
                LocalTime newLocalEndTime = LocalTime.parse(endTime, DateTimeFormatter.ofPattern("HH:mm"));

                // Check if only start and end times are being updated
                if (!existingBooking.getStartTime().equals(startTime) || !existingBooking.getEndTime().equals(endTime)) {
                    // Check for room, date, and time overlap with other bookings
                    for (DataBooking otherBooking : existingBookings) {
                        if (!otherBooking.get_id().equals(id)) {
                            LocalTime otherLocalStartTime = LocalTime.parse(otherBooking.getStartTime(), DateTimeFormatter.ofPattern("HH:mm"));
                            LocalTime otherLocalEndTime = LocalTime.parse(otherBooking.getEndTime(), DateTimeFormatter.ofPattern("HH:mm"));

                            // Check for time overlap with other bookings
                            if (!(newLocalEndTime.isBefore(otherLocalStartTime) || newLocalStartTime.isAfter(otherLocalEndTime)) && otherBooking.getRoomName().equals(roomName) && otherBooking.getBookingDate().equals(bookingDate)) {
                                // There is an overlap in time with another booking for the same room and date, return false
                                return false;
                            }
                        }
                    }
                }

                // Update the booking
                bookingService.updateDataBooking(new DataBooking(
                        id, m.get("roomName"), Integer.parseInt(m.get("participants")), m.get("purpose"), m.get("name"),
                        m.get("bookingDate"), m.get("startTime"), m.get("endTime"), m.get("status"), m.get("email")
                ));

                return true;
            }
        }
        return false;
    }



    @RequestMapping(value = "/deleteBooking", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean deleteData(@RequestBody Map<String, String> requestBody) {
        String id = requestBody.get("_id");

        List<DataBooking> existingBookings = bookingService.findDataBooking();
        for (DataBooking existingBooking : existingBookings) {
            if (existingBooking.get_id().equals(id)) {
                bookingService.delDataBooking(existingBooking);
                return true;
            }
        }
        return false;
    }



}



