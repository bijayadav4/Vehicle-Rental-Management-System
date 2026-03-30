package com.rental.vehicle_rental.dto;

public class VehicleRequest {
    private String name;
    private String numberPlate;
    private String type;
    private Double rentalPricePerDay;
    private Integer availableCount;
    private Double securityDeposit;

    public VehicleRequest() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getNumberPlate() { return numberPlate; }
    public void setNumberPlate(String numberPlate) { this.numberPlate = numberPlate; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Double getRentalPricePerDay() { return rentalPricePerDay; }
    public void setRentalPricePerDay(Double rentalPricePerDay) { this.rentalPricePerDay = rentalPricePerDay; }
    public Integer getAvailableCount() { return availableCount; }
    public void setAvailableCount(Integer availableCount) { this.availableCount = availableCount; }
    public Double getSecurityDeposit() { return securityDeposit; }
    public void setSecurityDeposit(Double securityDeposit) { this.securityDeposit = securityDeposit; }
}
