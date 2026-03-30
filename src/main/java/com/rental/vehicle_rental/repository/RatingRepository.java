package com.rental.vehicle_rental.repository;

import com.rental.vehicle_rental.model.Rating;
import com.rental.vehicle_rental.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByVehicle(Vehicle vehicle);

    @Query("SELECT AVG(r.stars) FROM Rating r WHERE r.vehicle.id = :vehicleId")
    Double avgStarsByVehicleId(Long vehicleId);
}
