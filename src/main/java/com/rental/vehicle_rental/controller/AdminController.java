package com.rental.vehicle_rental.controller;

import com.rental.vehicle_rental.dto.ApiResponse;
import com.rental.vehicle_rental.model.User;
import com.rental.vehicle_rental.service.AdminService;
import com.rental.vehicle_rental.service.VehicleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final AdminService   adminService;
    private final VehicleService vehicleService;

    public AdminController(AdminService adminService, VehicleService vehicleService) {
        this.adminService   = adminService;
        this.vehicleService = vehicleService;
    }

    @GetMapping("/borrowers")
    public ResponseEntity<List<User>> getAllBorrowers() {
        return ResponseEntity.ok(adminService.getAllBorrowers());
    }

    @PutMapping("/borrowers/{id}/block")
    public ResponseEntity<?> blockUser(@PathVariable Long id) {
        ApiResponse res = adminService.blockUser(id);
        return res.isSuccess() ? ResponseEntity.ok(res) : ResponseEntity.badRequest().body(res);
    }

    @PutMapping("/borrowers/{id}/unblock")
    public ResponseEntity<?> unblockUser(@PathVariable Long id) {
        ApiResponse res = adminService.unblockUser(id);
        return res.isSuccess() ? ResponseEntity.ok(res) : ResponseEntity.badRequest().body(res);
    }

    @GetMapping("/revenue")
    public ResponseEntity<Map<String, Object>> getRevenue() {
        return ResponseEntity.ok(adminService.getRevenueReport());
    }

    @PutMapping("/vehicles/{id}/service")
    public ResponseEntity<?> markServiced(@PathVariable Long id) {
        ApiResponse res = vehicleService.markAsServiced(id);
        return res.isSuccess() ? ResponseEntity.ok(res) : ResponseEntity.badRequest().body(res);
    }
}
