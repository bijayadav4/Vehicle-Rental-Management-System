package com.rental.vehicle_rental.service;

import com.rental.vehicle_rental.dto.ApiResponse;
import com.rental.vehicle_rental.dto.VehicleRequest;
import com.rental.vehicle_rental.model.Vehicle;
import com.rental.vehicle_rental.model.VehicleType;
import com.rental.vehicle_rental.repository.VehicleRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public Vehicle addVehicle(VehicleRequest request) {
        String name = request.getName() != null
            ? request.getName().trim()
            : "";
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Vehicle name is required.");
        }

        String numberPlate = request.getNumberPlate() != null
            ? request.getNumberPlate().trim()
            : "";
        if (numberPlate.isEmpty()) {
            throw new IllegalArgumentException("Number plate is required.");
        }

        if (request.getType() == null || request.getType().isBlank()) {
            throw new IllegalArgumentException("Vehicle type is required.");
        }

        if (request.getRentalPricePerDay() == null || request.getRentalPricePerDay() <= 0) {
            throw new IllegalArgumentException("Rental price must be greater than 0.");
        }

        if (request.getAvailableCount() == null || request.getAvailableCount() <= 0) {
            throw new IllegalArgumentException("Available count must be at least 1.");
        }

        if (request.getSecurityDeposit() == null || request.getSecurityDeposit() < 0) {
            throw new IllegalArgumentException("Security deposit cannot be negative.");
        }

        if (vehicleRepository.existsByNumberPlateIgnoreCase(numberPlate)) {
            throw new DataIntegrityViolationException("Vehicle with this number plate already exists.");
        }

        Vehicle vehicle = new Vehicle(
                name,
            numberPlate,
                VehicleType.valueOf(request.getType().toUpperCase()),
                request.getRentalPricePerDay(),
                request.getAvailableCount(),
                request.getSecurityDeposit()
        );
        return vehicleRepository.save(vehicle);
    }

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public List<Vehicle> getAvailableVehicles() {
        return vehicleRepository.findAvailableForRent();
    }

    public Optional<Vehicle> getVehicleById(Long id) {
        return vehicleRepository.findById(id);
    }

    public List<Vehicle> searchByName(String name) {
        return vehicleRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Vehicle> searchByPlate(String plate) {
        return vehicleRepository.findByNumberPlateContainingIgnoreCase(plate);
    }

    public List<Vehicle> getByType(String type) {
        return vehicleRepository.findByType(VehicleType.valueOf(type.toUpperCase()));
    }

    public List<Vehicle> getAllSortedByPriceAsc() {
        return vehicleRepository.findAllByOrderByRentalPricePerDayAsc();
    }

    public List<Vehicle> getAllSortedByPriceDesc() {
        return vehicleRepository.findAllByOrderByRentalPricePerDayDesc();
    }

    public List<Vehicle> getServiceDueVehicles() {
        return vehicleRepository.findVehiclesDueForService();
    }

    public ApiResponse updateVehicle(Long id, VehicleRequest request) {
        Optional<Vehicle> opt = vehicleRepository.findById(id);
        if (opt.isEmpty()) return new ApiResponse(false, "Vehicle not found.");
        Vehicle vehicle = opt.get();
        if (request.getAvailableCount() != null) vehicle.setAvailableCount(request.getAvailableCount());
        if (request.getSecurityDeposit() != null) vehicle.setSecurityDeposit(request.getSecurityDeposit());
        if (request.getRentalPricePerDay() != null) vehicle.setRentalPricePerDay(request.getRentalPricePerDay());
        vehicleRepository.save(vehicle);
        return new ApiResponse(true, "Vehicle updated successfully.");
    }

    public ApiResponse deleteVehicle(Long id) {
        Optional<Vehicle> opt = vehicleRepository.findById(id);
        if (opt.isEmpty()) return new ApiResponse(false, "Vehicle not found.");
        if (opt.get().getIsRented()) return new ApiResponse(false, "Cannot delete a rented vehicle.");
        vehicleRepository.deleteById(id);
        return new ApiResponse(true, "Vehicle deleted successfully.");
    }

    public ApiResponse updateSecurityDeposit(Long id, Double deposit) {
        Optional<Vehicle> opt = vehicleRepository.findById(id);
        if (opt.isEmpty()) return new ApiResponse(false, "Vehicle not found.");
        opt.get().setSecurityDeposit(deposit);
        vehicleRepository.save(opt.get());
        return new ApiResponse(true, "Security deposit updated to Rs." + deposit);
    }

    public ApiResponse markAsServiced(Long id) {
        Optional<Vehicle> opt = vehicleRepository.findById(id);
        if (opt.isEmpty()) return new ApiResponse(false, "Vehicle not found.");
        Vehicle v = opt.get();
        v.setTotalKmsRun(0);
        vehicleRepository.save(v);
        return new ApiResponse(true, v.getName() + " marked as serviced. KM counter reset to 0.");
    }

    public ApiResponse updateImageUrl(Long id, String imageUrl) {
        Optional<Vehicle> opt = vehicleRepository.findById(id);
        if (opt.isEmpty()) return new ApiResponse(false, "Vehicle not found.");
        Vehicle v = opt.get();
        v.setImageUrl(imageUrl);
        vehicleRepository.save(v);
        return new ApiResponse(true, "Image updated.");
    }
}
