package com.rental.vehicle_rental.dto;

public class LoginResponse {

    private String token;
    private String role;
    private String email;
    private Long userId;

    public LoginResponse(String token, String role, String email, Long userId) {
        this.token = token;
        this.role = role;
        this.email = email;
        this.userId = userId;
    }

    public String getToken() { return token; }
    public String getRole() { return role; }
    public String getEmail() { return email; }
    public Long getUserId() { return userId; }
}
