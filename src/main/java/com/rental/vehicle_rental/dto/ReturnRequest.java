package com.rental.vehicle_rental.dto;

public class ReturnRequest {
    private Long bookingId;
    private Integer kmsRidden;
    private String damageLevel;
    private String paymentMethod;

    public ReturnRequest() {}

    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }
    public Integer getKmsRidden() { return kmsRidden; }
    public void setKmsRidden(Integer kmsRidden) { this.kmsRidden = kmsRidden; }
    public String getDamageLevel() { return damageLevel; }
    public void setDamageLevel(String damageLevel) { this.damageLevel = damageLevel; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
}
