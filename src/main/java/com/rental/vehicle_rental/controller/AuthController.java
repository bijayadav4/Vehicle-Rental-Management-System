package com.rental.vehicle_rental.controller;

import com.rental.vehicle_rental.dto.ApiResponse;
import com.rental.vehicle_rental.dto.LoginRequest;
import com.rental.vehicle_rental.dto.LoginResponse;
import com.rental.vehicle_rental.dto.SignupRequest;
import com.rental.vehicle_rental.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        ApiResponse response = authService.signup(request);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        if (response == null) {
            return ResponseEntity.status(401)
                    .body(new ApiResponse(false, "Invalid email or password."));
        }
        return ResponseEntity.ok(response);
    }
}
