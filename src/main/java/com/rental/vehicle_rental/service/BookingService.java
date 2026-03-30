package com.rental.vehicle_rental.service;

import com.rental.vehicle_rental.dto.ApiResponse;
import com.rental.vehicle_rental.dto.BookingRequest;
import com.rental.vehicle_rental.dto.ExchangeRequest;
import com.rental.vehicle_rental.dto.ExtendRequest;
import com.rental.vehicle_rental.dto.ReturnRequest;
import com.rental.vehicle_rental.dto.ReturnResponse;
import com.rental.vehicle_rental.model.Booking;
import com.rental.vehicle_rental.model.User;
import com.rental.vehicle_rental.model.Vehicle;
import com.rental.vehicle_rental.model.VehicleType;
import com.rental.vehicle_rental.repository.BookingRepository;
import com.rental.vehicle_rental.repository.UserRepository;
import com.rental.vehicle_rental.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final VehicleRepository vehicleRepository;
    private final UserRepository    userRepository;
    private final EmailService      emailService;

    public BookingService(BookingRepository bookingRepository,
                          VehicleRepository vehicleRepository,
                          UserRepository userRepository,
                          EmailService emailService) {
        this.bookingRepository = bookingRepository;
        this.vehicleRepository = vehicleRepository;
        this.userRepository    = userRepository;
        this.emailService      = emailService;
    }

    public ApiResponse rentVehicle(String email, BookingRequest request) {
        Optional<User> optUser = userRepository.findByEmail(email);
        if (optUser.isEmpty()) return new ApiResponse(false, "User not found.");
        User user = optUser.get();

        if (Boolean.TRUE.equals(user.getIsBlocked())) {
            return new ApiResponse(false, "Your account is blocked. Contact admin.");
        }

        Optional<Vehicle> optVehicle = vehicleRepository.findById(request.getVehicleId());
        if (optVehicle.isEmpty()) return new ApiResponse(false, "Vehicle not found.");
        Vehicle vehicle = optVehicle.get();

        if (vehicle.needsService()) {
            return new ApiResponse(false, "Vehicle is due for service.");
        }
        if (vehicle.getAvailableCount() <= 0) {
            return new ApiResponse(false, "No units available.");
        }

        if (vehicle.getType() == VehicleType.CAR) {
            if (user.getRentedCarId() != null) {
                return new ApiResponse(false, "You already have a car rented.");
            }
            if (user.getSecurityBalance() < 10000) {
                return new ApiResponse(false,
                        "Minimum Rs.10,000 required. Balance: Rs." +
                        user.getSecurityBalance());
            }
        } else {
            if (user.getRentedBikeId() != null) {
                return new ApiResponse(false, "You already have a bike rented.");
            }
            if (user.getSecurityBalance() < 3000) {
                return new ApiResponse(false,
                        "Minimum Rs.3,000 required. Balance: Rs." +
                        user.getSecurityBalance());
            }
        }

        Booking booking = new Booking(user, vehicle, LocalDate.now());
        booking.setTotalRent(vehicle.getRentalPricePerDay());
        bookingRepository.save(booking);

        vehicle.setAvailableCount(vehicle.getAvailableCount() - 1);
        vehicle.setIsRented(true);
        vehicleRepository.save(vehicle);

        if (vehicle.getType() == VehicleType.CAR) {
            user.setRentedCarId(vehicle.getId());
        } else {
            user.setRentedBikeId(vehicle.getId());
        }
        userRepository.save(user);

        emailService.sendRentConfirmationEmail(booking);

        return new ApiResponse(true,
                "Vehicle rented! Booking ID: " + booking.getId() +
                " | Confirmation email sent.");
    }

    public ReturnResponse returnVehicle(String email, ReturnRequest request) {
        ReturnResponse response = new ReturnResponse();

        Optional<User> optUser = userRepository.findByEmail(email);
        if (optUser.isEmpty()) {
            response.setSuccess(false);
            response.setMessage("User not found.");
            return response;
        }
        User user = optUser.get();

        Optional<Booking> optBooking = bookingRepository.findById(request.getBookingId());
        if (optBooking.isEmpty() || optBooking.get().getReturned()) {
            response.setSuccess(false);
            response.setMessage("Active booking not found.");
            return response;
        }
        Booking booking = optBooking.get();
        Vehicle vehicle  = booking.getVehicle();

        long days = ChronoUnit.DAYS.between(booking.getRentDate(), LocalDate.now());
        if (days < 1) days = 1;
        double baseRent    = days * vehicle.getRentalPricePerDay();
        double totalCharge = baseRent;
        double kmCharge    = 0;
        double damageFine  = 0;

        int kms = request.getKmsRidden() != null ? request.getKmsRidden() : 0;
        if (kms > 500) {
            kmCharge    = baseRent * 0.15;
            totalCharge += kmCharge;
        }

        if (vehicle.getType() == VehicleType.CAR && request.getDamageLevel() != null) {
            String dmg = request.getDamageLevel().toUpperCase();
            if (dmg.equals("LOW"))    damageFine = baseRent * 0.20;
            else if (dmg.equals("MEDIUM")) damageFine = baseRent * 0.50;
            else if (dmg.equals("HIGH"))   damageFine = baseRent * 0.75;
            totalCharge += damageFine;
        }

        if ("DEPOSIT".equalsIgnoreCase(request.getPaymentMethod())) {
            user.setSecurityBalance(
                    Math.max(0, user.getSecurityBalance() - totalCharge));
        }

        vehicle.setTotalKmsRun(vehicle.getTotalKmsRun() + kms);
        vehicle.setAvailableCount(vehicle.getAvailableCount() + 1);
        vehicle.setIsRented(false);
        vehicleRepository.save(vehicle);

        booking.setReturnDate(LocalDate.now());
        booking.setTotalRent(totalCharge);
        booking.setReturned(true);
        bookingRepository.save(booking);

        if (vehicle.getType() == VehicleType.CAR) {
            user.setRentedCarId(null);
        } else {
            user.setRentedBikeId(null);
        }
        user.setExtensionCount(0);
        userRepository.save(user);

        emailService.sendReturnConfirmationEmail(booking, totalCharge);

        response.setSuccess(true);
        response.setMessage("Vehicle returned! Confirmation email sent." +
                (vehicle.needsService() ? " Vehicle now due for service." : ""));
        response.setBaseRent(baseRent);
        response.setKmCharge(kmCharge);
        response.setDamageFine(damageFine);
        response.setTotalCharge(totalCharge);
        response.setRemainingBalance(user.getSecurityBalance());
        return response;
    }

    public ApiResponse extendBooking(String email, ExtendRequest request) {
        Optional<User> optUser = userRepository.findByEmail(email);
        if (optUser.isEmpty()) return new ApiResponse(false, "User not found.");
        User user = optUser.get();

        if (user.getExtensionCount() >= 2) {
            return new ApiResponse(false, "Maximum 2 extensions already used.");
        }

        Optional<Booking> optBooking = bookingRepository.findById(request.getBookingId());
        if (optBooking.isEmpty() || optBooking.get().getReturned()) {
            return new ApiResponse(false, "Active booking not found.");
        }
        Booking booking = optBooking.get();
        Vehicle vehicle  = booking.getVehicle();

        LocalDate newReturn = booking.getReturnDate().plusDays(1);
        booking.setReturnDate(newReturn);
        booking.setTotalRent(booking.getTotalRent() + vehicle.getRentalPricePerDay());
        bookingRepository.save(booking);

        user.setExtensionCount(user.getExtensionCount() + 1);
        userRepository.save(user);

        return new ApiResponse(true,
                "Extended to " + newReturn +
                " | Extensions used: " + user.getExtensionCount() + "/2");
    }

    public ApiResponse exchangeVehicle(String email, ExchangeRequest request) {
        Optional<User> optUser = userRepository.findByEmail(email);
        if (optUser.isEmpty()) return new ApiResponse(false, "User not found.");
        User user = optUser.get();

        Optional<Booking> optBooking = bookingRepository.findById(request.getBookingId());
        if (optBooking.isEmpty() || optBooking.get().getReturned()) {
            return new ApiResponse(false, "Active booking not found.");
        }
        Booking oldBooking = optBooking.get();
        Vehicle oldVehicle  = oldBooking.getVehicle();

        Optional<Vehicle> optNew = vehicleRepository.findById(request.getNewVehicleId());
        if (optNew.isEmpty()) return new ApiResponse(false, "New vehicle not found.");
        Vehicle newVehicle = optNew.get();

        if (newVehicle.needsService() || newVehicle.getAvailableCount() <= 0) {
            return new ApiResponse(false, "Selected vehicle not available.");
        }
        if (newVehicle.getType() != oldVehicle.getType()) {
            return new ApiResponse(false, "Can only exchange same vehicle type.");
        }

        int kms  = request.getKmsRidden() != null ? request.getKmsRidden() : 0;
        long days = ChronoUnit.DAYS.between(oldBooking.getRentDate(), LocalDate.now());
        if (days < 1) days = 1;
        double partialRent = days * oldVehicle.getRentalPricePerDay();

        if ("DEPOSIT".equalsIgnoreCase(request.getPaymentMethod())) {
            user.setSecurityBalance(
                    Math.max(0, user.getSecurityBalance() - partialRent));
        }

        oldVehicle.setTotalKmsRun(oldVehicle.getTotalKmsRun() + kms);
        oldVehicle.setAvailableCount(oldVehicle.getAvailableCount() + 1);
        oldVehicle.setIsRented(false);
        vehicleRepository.save(oldVehicle);

        oldBooking.setReturned(true);
        oldBooking.setTotalRent(partialRent);
        bookingRepository.save(oldBooking);

        Booking newBooking = new Booking(user, newVehicle, LocalDate.now());
        newBooking.setTotalRent(newVehicle.getRentalPricePerDay());
        bookingRepository.save(newBooking);

        newVehicle.setAvailableCount(newVehicle.getAvailableCount() - 1);
        newVehicle.setIsRented(true);
        vehicleRepository.save(newVehicle);

        if (newVehicle.getType() == VehicleType.CAR) {
            user.setRentedCarId(newVehicle.getId());
        } else {
            user.setRentedBikeId(newVehicle.getId());
        }
        user.setExtensionCount(0);
        userRepository.save(user);

        emailService.sendRentConfirmationEmail(newBooking);

        return new ApiResponse(true,
                "Exchanged to " + newVehicle.getName() +
                " | New Booking: #" + newBooking.getId());
    }

    public ApiResponse markAsLost(String email, Long bookingId) {
        Optional<User> optUser = userRepository.findByEmail(email);
        if (optUser.isEmpty()) return new ApiResponse(false, "User not found.");
        User user = optUser.get();

        Optional<Booking> optBooking = bookingRepository.findById(bookingId);
        if (optBooking.isEmpty() || optBooking.get().getReturned()) {
            return new ApiResponse(false, "Active booking not found.");
        }
        Booking booking = optBooking.get();
        Vehicle vehicle  = booking.getVehicle();

        double forfeited = user.getSecurityBalance();
        user.setSecurityBalance(0.0);
        booking.setReturned(true);
        booking.setTotalRent(forfeited);
        bookingRepository.save(booking);
        vehicleRepository.delete(vehicle);

        if (vehicle.getType() == VehicleType.CAR) {
            user.setRentedCarId(null);
        } else {
            user.setRentedBikeId(null);
        }
        user.setExtensionCount(0);
        userRepository.save(user);

        return new ApiResponse(true,
                "Vehicle marked as lost. Rs." + forfeited + " forfeited.");
    }

    public List<Booking> getMyBookings(String email) {
        Optional<User> optUser = userRepository.findByEmail(email);
        if (optUser.isEmpty()) return new ArrayList<>();
        return bookingRepository.findByUser(optUser.get());
    }

    public List<Booking> getMyActiveBookings(String email) {
        Optional<User> optUser = userRepository.findByEmail(email);
        if (optUser.isEmpty()) return new ArrayList<>();
        return bookingRepository.findByUserAndReturned(optUser.get(), false);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }
}
