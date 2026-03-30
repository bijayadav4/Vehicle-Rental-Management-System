package com.rental.vehicle_rental.repository;

import com.rental.vehicle_rental.model.Booking;
import com.rental.vehicle_rental.model.User;
import com.rental.vehicle_rental.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUser(User user);
    List<Booking> findByUserAndReturned(User user, Boolean returned);
    Optional<Booking> findByUserAndVehicleAndReturned(User user, Vehicle vehicle, Boolean returned);
    List<Booking> findByReturned(Boolean returned);
}
