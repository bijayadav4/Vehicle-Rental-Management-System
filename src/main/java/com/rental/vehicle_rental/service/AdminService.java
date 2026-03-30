package com.rental.vehicle_rental.service;

import com.rental.vehicle_rental.dto.ApiResponse;
import com.rental.vehicle_rental.model.Booking;
import com.rental.vehicle_rental.model.Role;
import com.rental.vehicle_rental.model.User;
import com.rental.vehicle_rental.repository.BookingRepository;
import com.rental.vehicle_rental.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final UserRepository    userRepository;
    private final BookingRepository bookingRepository;
    private final EmailService      emailService;

    public AdminService(UserRepository userRepository,
                        BookingRepository bookingRepository,
                        EmailService emailService) {
        this.userRepository    = userRepository;
        this.bookingRepository = bookingRepository;
        this.emailService      = emailService;
    }

    public List<User> getAllBorrowers() {
        return userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.BORROWER)
                .collect(Collectors.toList());
    }

    public ApiResponse blockUser(Long id) {
        Optional<User> opt = userRepository.findById(id);
        if (opt.isEmpty()) return new ApiResponse(false, "User not found.");
        User user = opt.get();
        user.setIsBlocked(true);
        userRepository.save(user);
        emailService.sendBlockedEmail(user);
        return new ApiResponse(true, user.getEmail() + " has been blocked.");
    }

    public ApiResponse unblockUser(Long id) {
        Optional<User> opt = userRepository.findById(id);
        if (opt.isEmpty()) return new ApiResponse(false, "User not found.");
        User user = opt.get();
        user.setIsBlocked(false);
        userRepository.save(user);
        return new ApiResponse(true, user.getEmail() + " has been unblocked.");
    }

    public Map<String, Object> getRevenueReport() {
        List<Booking> all      = bookingRepository.findAll();
        List<Booking> returned = all.stream()
                .filter(Booking::getReturned).collect(Collectors.toList());

        double totalRevenue  = returned.stream().mapToDouble(Booking::getTotalRent).sum();
        long completedCount  = returned.size();
        double avgRevenue    = completedCount > 0 ? totalRevenue / completedCount : 0;

        Map<String, Object> report = new HashMap<>();
        report.put("totalRevenue",      Math.round(totalRevenue * 100.0) / 100.0);
        report.put("totalBookings",     all.size());
        report.put("activeBookings",    all.stream().filter(b -> !b.getReturned()).count());
        report.put("completedBookings", completedCount);
        report.put("averagePerBooking", Math.round(avgRevenue * 100.0) / 100.0);
        return report;
    }
}
