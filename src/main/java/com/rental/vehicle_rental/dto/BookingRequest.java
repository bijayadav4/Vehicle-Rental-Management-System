package com.rental.vehicle_rental.dto;

public class BookingRequest {
    private Long vehicleId;

    public BookingRequest() {}

    public Long getVehicleId() { return vehicleId; }
    public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }
}
