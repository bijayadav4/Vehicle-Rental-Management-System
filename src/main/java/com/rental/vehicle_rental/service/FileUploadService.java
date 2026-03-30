package com.rental.vehicle_rental.service;

import com.rental.vehicle_rental.dto.ApiResponse;
import com.rental.vehicle_rental.model.Vehicle;
import com.rental.vehicle_rental.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Service
public class FileUploadService {

    private final VehicleRepository vehicleRepository;

    @Value("${upload.dir}")
    private String uploadDir;

    public FileUploadService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public ApiResponse uploadVehicleImage(Long vehicleId, MultipartFile file) {
        Optional<Vehicle> opt = vehicleRepository.findById(vehicleId);
        if (opt.isEmpty()) return new ApiResponse(false, "Vehicle not found.");

        if (file.isEmpty()) return new ApiResponse(false, "No file selected.");

        String originalName = file.getOriginalFilename();
        if (originalName == null) return new ApiResponse(false, "Invalid file.");
        int dotIndex = originalName.lastIndexOf('.');
        if (dotIndex < 0) return new ApiResponse(false, "File extension is missing.");

        String ext = originalName.substring(dotIndex).toLowerCase(Locale.ROOT);
        if (!ext.matches("\\.(jpg|jpeg|png|webp)")) {
            return new ApiResponse(false, "Only JPG, PNG, WEBP allowed.");
        }

        String fileName = "vehicle_" + vehicleId + "_" +
                UUID.randomUUID().toString().substring(0, 8) + ext;

        try {
            Path dirPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(dirPath);

            Path destPath = dirPath.resolve(fileName);
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, destPath, StandardCopyOption.REPLACE_EXISTING);
            }

            String imageUrl = "/uploads/" + fileName;
            Vehicle vehicle = opt.get();
            vehicle.setImageUrl(imageUrl);
            vehicleRepository.save(vehicle);

            return new ApiResponse(true, "Image uploaded. URL: " + imageUrl);

        } catch (IOException e) {
            return new ApiResponse(false, "Upload failed: " + e.getMessage());
        }
    }
}
