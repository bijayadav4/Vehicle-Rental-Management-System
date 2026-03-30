package com.rental.vehicle_rental.controller;

import com.rental.vehicle_rental.dto.ApiResponse;
import com.rental.vehicle_rental.service.FileUploadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = "*")
public class FileUploadController {

    private final FileUploadService fileUploadService;

    public FileUploadController(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

    @PostMapping("/vehicle/{vehicleId}/image")
    public ResponseEntity<?> uploadImage(
            @PathVariable Long vehicleId,
            @RequestParam("file") MultipartFile file) {
        ApiResponse res = fileUploadService.uploadVehicleImage(vehicleId, file);
        return res.isSuccess()
                ? ResponseEntity.ok(res)
                : ResponseEntity.badRequest().body(res);
    }
}
