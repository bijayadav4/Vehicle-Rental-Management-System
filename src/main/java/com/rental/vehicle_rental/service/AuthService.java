package com.rental.vehicle_rental.service;

import com.rental.vehicle_rental.dto.ApiResponse;
import com.rental.vehicle_rental.dto.LoginRequest;
import com.rental.vehicle_rental.dto.LoginResponse;
import com.rental.vehicle_rental.dto.SignupRequest;
import com.rental.vehicle_rental.model.Role;
import com.rental.vehicle_rental.model.User;
import com.rental.vehicle_rental.repository.UserRepository;
import com.rental.vehicle_rental.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository  userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil         jwtUtil;
    private final EmailService    emailService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       EmailService emailService) {
        this.userRepository  = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil         = jwtUtil;
        this.emailService    = emailService;
    }

    public ApiResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return new ApiResponse(false, "Email already registered.");
        }
        User user = new User(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                Role.BORROWER
        );
        userRepository.save(user);
        emailService.sendWelcomeEmail(user);
        return new ApiResponse(true, "Account created! Security balance: Rs.30,000");
    }

    public LoginResponse login(LoginRequest request) {
        Optional<User> optUser = userRepository.findByEmail(request.getEmail());
        if (optUser.isEmpty()) return null;
        User user = optUser.get();
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) return null;
        if (Boolean.TRUE.equals(user.getIsBlocked())) return null;
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return new LoginResponse(token, user.getRole().name(),
                user.getEmail(), user.getId());
    }
}
