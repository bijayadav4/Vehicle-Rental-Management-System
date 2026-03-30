package com.rental.vehicle_rental.service;

import com.rental.vehicle_rental.dto.ApiResponse;
import com.rental.vehicle_rental.dto.ProfileRequest;
import com.rental.vehicle_rental.dto.RatingRequest;
import com.rental.vehicle_rental.dto.WalletRequest;
import com.rental.vehicle_rental.model.Rating;
import com.rental.vehicle_rental.model.User;
import com.rental.vehicle_rental.model.Vehicle;
import com.rental.vehicle_rental.repository.RatingRepository;
import com.rental.vehicle_rental.repository.UserRepository;
import com.rental.vehicle_rental.repository.VehicleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository    userRepository;
    private final VehicleRepository vehicleRepository;
    private final RatingRepository  ratingRepository;
    private final PasswordEncoder   passwordEncoder;

    public UserService(UserRepository userRepository,
                       VehicleRepository vehicleRepository,
                       RatingRepository ratingRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository    = userRepository;
        this.vehicleRepository = vehicleRepository;
        this.ratingRepository  = ratingRepository;
        this.passwordEncoder   = passwordEncoder;
    }

    public User getProfile(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public ApiResponse updateProfile(String email, ProfileRequest request) {
        Optional<User> opt = userRepository.findByEmail(email);
        if (opt.isEmpty()) return new ApiResponse(false, "User not found.");
        User user = opt.get();
        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getPhone()    != null) user.setPhone(request.getPhone());
        if (request.getNewPassword() != null && request.getCurrentPassword() != null) {
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                return new ApiResponse(false, "Current password is incorrect.");
            }
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }
        userRepository.save(user);
        return new ApiResponse(true, "Profile updated successfully.");
    }

    public ApiResponse topUpWallet(String email, WalletRequest request) {
        Optional<User> opt = userRepository.findByEmail(email);
        if (opt.isEmpty()) return new ApiResponse(false, "User not found.");
        if (request.getAmount() <= 0) return new ApiResponse(false, "Invalid amount.");
        User user = opt.get();
        user.setSecurityBalance(user.getSecurityBalance() + request.getAmount());
        userRepository.save(user);
        return new ApiResponse(true,
                "Rs." + request.getAmount() + " added. New balance: Rs." + user.getSecurityBalance());
    }

    public ApiResponse submitRating(String email, RatingRequest request) {
        Optional<User> optUser = userRepository.findByEmail(email);
        if (optUser.isEmpty()) return new ApiResponse(false, "User not found.");
        Optional<Vehicle> optVehicle = vehicleRepository.findById(request.getVehicleId());
        if (optVehicle.isEmpty()) return new ApiResponse(false, "Vehicle not found.");
        if (request.getStars() < 1 || request.getStars() > 5) {
            return new ApiResponse(false, "Stars must be between 1 and 5.");
        }
        Rating rating = new Rating(optUser.get(), optVehicle.get(),
                request.getStars(), request.getReview());
        ratingRepository.save(rating);
        return new ApiResponse(true, "Thank you for your review!");
    }

    public List<Rating> getVehicleRatings(Long vehicleId) {
        Optional<Vehicle> opt = vehicleRepository.findById(vehicleId);
        return opt.map(ratingRepository::findByVehicle).orElse(List.of());
    }

    public Double getAverageRating(Long vehicleId) {
        Double avg = ratingRepository.avgStarsByVehicleId(vehicleId);
        return avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0;
    }
}
