package com.rental.vehicle_rental.dto;

public class ReturnResponse {
    private boolean success;
    private String message;
    private Double baseRent;
    private Double kmCharge;
    private Double damageFine;
    private Double totalCharge;
    private Double remainingBalance;

    public ReturnResponse() {}

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Double getBaseRent() { return baseRent; }
    public void setBaseRent(Double baseRent) { this.baseRent = baseRent; }
    public Double getKmCharge() { return kmCharge; }
    public void setKmCharge(Double kmCharge) { this.kmCharge = kmCharge; }
    public Double getDamageFine() { return damageFine; }
    public void setDamageFine(Double damageFine) { this.damageFine = damageFine; }
    public Double getTotalCharge() { return totalCharge; }
    public void setTotalCharge(Double totalCharge) { this.totalCharge = totalCharge; }
    public Double getRemainingBalance() { return remainingBalance; }
    public void setRemainingBalance(Double remainingBalance) { this.remainingBalance = remainingBalance; }
}
