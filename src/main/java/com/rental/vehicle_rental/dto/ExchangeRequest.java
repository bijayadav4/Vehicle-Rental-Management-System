package com.rental.vehicle_rental.dto;

public class ExchangeRequest {
    private Long bookingId;
    private Long newVehicleId;
    private Integer kmsRidden;
    private String paymentMethod;

    public ExchangeRequest() {}

    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }
    public Long getNewVehicleId() { return newVehicleId; }
    public void setNewVehicleId(Long newVehicleId) { this.newVehicleId = newVehicleId; }
    public Integer getKmsRidden() { return kmsRidden; }
    public void setKmsRidden(Integer kmsRidden) { this.kmsRidden = kmsRidden; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
}
