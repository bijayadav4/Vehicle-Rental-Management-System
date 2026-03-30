package com.rental.vehicle_rental.controller;

import com.rental.vehicle_rental.dto.ApiResponse;
import com.rental.vehicle_rental.dto.VehicleRequest;
import com.rental.vehicle_rental.model.Vehicle;
import com.rental.vehicle_rental.service.VehicleService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/vehicles")
@CrossOrigin(origins = "*")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @PostMapping
    public ResponseEntity<?> addVehicle(@RequestBody VehicleRequest request) {
        try {
            Vehicle vehicle = vehicleService.addVehicle(request);
            return ResponseEntity.ok(vehicle);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage()));
        } catch (DataIntegrityViolationException ex) {
            return ResponseEntity.badRequest().body(
                    new ApiResponse(false, "Vehicle with this number plate already exists."));
        }
    }

    @GetMapping
    public ResponseEntity<List<Vehicle>> getAllVehicles(
            @RequestParam(required = false) String sort) {
        List<Vehicle> vehicles;
        if ("price_asc".equals(sort)) {
            vehicles = vehicleService.getAllSortedByPriceAsc();
        } else if ("price_desc".equals(sort)) {
            vehicles = vehicleService.getAllSortedByPriceDesc();
        } else {
            vehicles = vehicleService.getAllVehicles();
        }
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/available")
    public ResponseEntity<List<Vehicle>> getAvailableVehicles() {
        return ResponseEntity.ok(vehicleService.getAvailableVehicles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getVehicleById(@PathVariable Long id) {
        Optional<Vehicle> vehicle = vehicleService.getVehicleById(id);
        if (vehicle.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(vehicle.get());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Vehicle>> searchVehicles(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String plate,
            @RequestParam(required = false) String type) {
        if (name != null) {
            return ResponseEntity.ok(vehicleService.searchByName(name));
        }
        if (plate != null) {
            return ResponseEntity.ok(vehicleService.searchByPlate(plate));
        }
        if (type != null) {
            return ResponseEntity.ok(vehicleService.getByType(type));
        }
        return ResponseEntity.ok(vehicleService.getAllVehicles());
    }

    @GetMapping("/service-due")
    public ResponseEntity<List<Vehicle>> getServiceDue() {
        return ResponseEntity.ok(vehicleService.getServiceDueVehicles());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateVehicle(
            @PathVariable Long id,
            @RequestBody VehicleRequest request) {
        ApiResponse response = vehicleService.updateVehicle(id, request);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    @PutMapping("/{id}/deposit")
    public ResponseEntity<?> updateDeposit(
            @PathVariable Long id,
            @RequestBody Map<String, Double> body) {
        Double deposit = body.get("securityDeposit");
        ApiResponse response = vehicleService.updateSecurityDeposit(id, deposit);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVehicle(@PathVariable Long id) {
        ApiResponse response = vehicleService.deleteVehicle(id);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }
}
