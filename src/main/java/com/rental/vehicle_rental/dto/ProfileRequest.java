package com.rental.vehicle_rental.dto;

public class ProfileRequest {
    private String fullName;
    private String phone;
    private String currentPassword;
    private String newPassword;

    public ProfileRequest() {}

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getCurrentPassword() { return currentPassword; }
    public void setCurrentPassword(String p) { this.currentPassword = p; }
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String p) { this.newPassword = p; }
}
