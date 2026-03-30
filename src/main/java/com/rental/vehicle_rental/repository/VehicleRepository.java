package com.rental.vehicle_rental.repository;

import com.rental.vehicle_rental.model.Vehicle;
import com.rental.vehicle_rental.model.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByType(VehicleType type);
    List<Vehicle> findByNameContainingIgnoreCase(String name);
    List<Vehicle> findByNumberPlateContainingIgnoreCase(String plate);
    boolean existsByNumberPlateIgnoreCase(String numberPlate);

    @Query("SELECT v FROM Vehicle v WHERE v.availableCount > 0 AND ((v.type = 'CAR' AND v.totalKmsRun < 3000) OR (v.type = 'BIKE' AND v.totalKmsRun < 1500))")
    List<Vehicle> findAvailableForRent();

    @Query("SELECT v FROM Vehicle v WHERE (v.type = 'CAR' AND v.totalKmsRun >= 3000) OR (v.type = 'BIKE' AND v.totalKmsRun >= 1500)")
    List<Vehicle> findVehiclesDueForService();

    List<Vehicle> findAllByOrderByRentalPricePerDayAsc();
    List<Vehicle> findAllByOrderByRentalPricePerDayDesc();
}
