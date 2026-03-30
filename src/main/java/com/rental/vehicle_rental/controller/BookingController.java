package com.rental.vehicle_rental.controller;

import com.rental.vehicle_rental.dto.ApiResponse;
import com.rental.vehicle_rental.dto.BookingRequest;
import com.rental.vehicle_rental.dto.ExchangeRequest;
import com.rental.vehicle_rental.dto.ExtendRequest;
import com.rental.vehicle_rental.dto.ReturnRequest;
import com.rental.vehicle_rental.dto.ReturnResponse;
import com.rental.vehicle_rental.model.Booking;
import com.rental.vehicle_rental.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/rent")
    public ResponseEntity<?> rentVehicle(
            Authentication auth,
            @RequestBody BookingRequest request) {
        ApiResponse response = bookingService.rentVehicle(auth.getName(), request);
        if (response.isSuccess()) return ResponseEntity.ok(response);
        return ResponseEntity.badRequest().body(response);
    }

    @PostMapping("/return")
    public ResponseEntity<?> returnVehicle(
            Authentication auth,
            @RequestBody ReturnRequest request) {
        ReturnResponse response = bookingService.returnVehicle(auth.getName(), request);
        if (response.isSuccess()) return ResponseEntity.ok(response);
        return ResponseEntity.badRequest().body(response);
    }

    @PostMapping("/extend")
    public ResponseEntity<?> extendBooking(
            Authentication auth,
            @RequestBody ExtendRequest request) {
        ApiResponse response = bookingService.extendBooking(auth.getName(), request);
        if (response.isSuccess()) return ResponseEntity.ok(response);
        return ResponseEntity.badRequest().body(response);
    }

    @PostMapping("/exchange")
    public ResponseEntity<?> exchangeVehicle(
            Authentication auth,
            @RequestBody ExchangeRequest request) {
        ApiResponse response = bookingService.exchangeVehicle(auth.getName(), request);
        if (response.isSuccess()) return ResponseEntity.ok(response);
        return ResponseEntity.badRequest().body(response);
    }

    @PostMapping("/lost/{bookingId}")
    public ResponseEntity<?> markAsLost(
            Authentication auth,
            @PathVariable Long bookingId) {
        ApiResponse response = bookingService.markAsLost(auth.getName(), bookingId);
        if (response.isSuccess()) return ResponseEntity.ok(response);
        return ResponseEntity.badRequest().body(response);
    }

    @GetMapping("/my")
    public ResponseEntity<List<Booking>> getMyBookings(Authentication auth) {
        return ResponseEntity.ok(bookingService.getMyBookings(auth.getName()));
    }

    @GetMapping("/my/active")
    public ResponseEntity<List<Booking>> getMyActiveBookings(Authentication auth) {
        return ResponseEntity.ok(bookingService.getMyActiveBookings(auth.getName()));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Booking>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }
}
