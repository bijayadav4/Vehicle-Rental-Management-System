package com.rental.vehicle_rental.controller;

import com.rental.vehicle_rental.dto.ApiResponse;
import com.rental.vehicle_rental.dto.ProfileRequest;
import com.rental.vehicle_rental.dto.RatingRequest;
import com.rental.vehicle_rental.dto.WalletRequest;
import com.rental.vehicle_rental.model.Rating;
import com.rental.vehicle_rental.model.User;
import com.rental.vehicle_rental.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(Authentication auth) {
        return ResponseEntity.ok(userService.getProfile(auth.getName()));
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(Authentication auth,
                                           @RequestBody ProfileRequest request) {
        ApiResponse res = userService.updateProfile(auth.getName(), request);
        return res.isSuccess() ? ResponseEntity.ok(res) : ResponseEntity.badRequest().body(res);
    }

    @PostMapping("/wallet/topup")
    public ResponseEntity<?> topUp(Authentication auth,
                                   @RequestBody WalletRequest request) {
        ApiResponse res = userService.topUpWallet(auth.getName(), request);
        return res.isSuccess() ? ResponseEntity.ok(res) : ResponseEntity.badRequest().body(res);
    }

    @PostMapping("/ratings")
    public ResponseEntity<?> submitRating(Authentication auth,
                                          @RequestBody RatingRequest request) {
        ApiResponse res = userService.submitRating(auth.getName(), request);
        return res.isSuccess() ? ResponseEntity.ok(res) : ResponseEntity.badRequest().body(res);
    }

    @GetMapping("/ratings/{vehicleId}")
    public ResponseEntity<List<Rating>> getVehicleRatings(@PathVariable Long vehicleId) {
        return ResponseEntity.ok(userService.getVehicleRatings(vehicleId));
    }

    @GetMapping("/ratings/{vehicleId}/average")
    public ResponseEntity<?> getAvgRating(@PathVariable Long vehicleId) {
        return ResponseEntity.ok(
                java.util.Map.of("average", userService.getAverageRating(vehicleId),
                                 "vehicleId", vehicleId));
    }
}
