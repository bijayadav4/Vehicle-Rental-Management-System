package com.rental.vehicle_rental;

import com.rental.vehicle_rental.model.Role;
import com.rental.vehicle_rental.model.User;
import com.rental.vehicle_rental.model.Vehicle;
import com.rental.vehicle_rental.model.VehicleType;
import com.rental.vehicle_rental.repository.UserRepository;
import com.rental.vehicle_rental.repository.VehicleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository    userRepository;
    private final VehicleRepository vehicleRepository;
    private final PasswordEncoder   passwordEncoder;

    public DataSeeder(UserRepository userRepo,
                      VehicleRepository vehicleRepo,
                      PasswordEncoder encoder) {
        this.userRepository    = userRepo;
        this.vehicleRepository = vehicleRepo;
        this.passwordEncoder   = encoder;
    }

    @Override
    public void run(String... args) {

        // Create default admin if not exists
        if (!userRepository.existsByEmail("admin@rental.com")) {
            User admin = new User(
                    "admin@rental.com",
                    passwordEncoder.encode("admin123"),
                    Role.ADMIN);
            userRepository.save(admin);
            System.out.println("✅ Default admin created.");
        }

        // Create sample borrower
        if (!userRepository.existsByEmail("user@rental.com")) {
            User borrower = new User(
                    "user@rental.com",
                    passwordEncoder.encode("user123"),
                    Role.BORROWER);
            userRepository.save(borrower);
            System.out.println("✅ Sample borrower created.");
        }

        // Create sample vehicles
        if (vehicleRepository.count() == 0) {
            vehicleRepository.save(new Vehicle(
                    "Honda City", "TN01AB1234",
                    VehicleType.CAR, 1500.0, 3, 10000.0));
            vehicleRepository.save(new Vehicle(
                    "Maruti Swift", "TN02CD5678",
                    VehicleType.CAR, 1200.0, 2, 10000.0));
            vehicleRepository.save(new Vehicle(
                    "Royal Enfield", "TN03EF9012",
                    VehicleType.BIKE, 600.0, 4, 3000.0));
            vehicleRepository.save(new Vehicle(
                    "Honda Activa", "TN04GH3456",
                    VehicleType.BIKE, 400.0, 5, 3000.0));
            System.out.println("✅ Sample vehicles created.");
        }
    }
}