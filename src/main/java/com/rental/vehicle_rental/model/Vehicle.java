package com.rental.vehicle_rental.model;

import jakarta.persistence.*;

@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String numberPlate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleType type;

    @Column(nullable = false)
    private Double rentalPricePerDay;

    @Column(nullable = false)
    private Integer availableCount;

    @Column(nullable = false)
    private Double securityDeposit;

    private Integer totalKmsRun = 0;
    private Boolean isRented    = false;
    private String  imageUrl;

    public Vehicle() {}

    public Vehicle(String name, String numberPlate, VehicleType type,
                   Double rentalPricePerDay, Integer availableCount, Double securityDeposit) {
        this.name              = name;
        this.numberPlate       = numberPlate;
        this.type              = type;
        this.rentalPricePerDay = rentalPricePerDay;
        this.availableCount    = availableCount;
        this.securityDeposit   = securityDeposit;
        this.totalKmsRun       = 0;
        this.isRented          = false;
    }

    public int getServiceIntervalKms() { return type == VehicleType.CAR ? 3000 : 1500; }
    public boolean needsService() { return totalKmsRun >= getServiceIntervalKms(); }

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getNumberPlate() { return numberPlate; }
    public void setNumberPlate(String p) { this.numberPlate = p; }
    public VehicleType getType() { return type; }
    public void setType(VehicleType type) { this.type = type; }
    public Double getRentalPricePerDay() { return rentalPricePerDay; }
    public void setRentalPricePerDay(Double price) { this.rentalPricePerDay = price; }
    public Integer getAvailableCount() { return availableCount; }
    public void setAvailableCount(Integer count) { this.availableCount = count; }
    public Double getSecurityDeposit() { return securityDeposit; }
    public void setSecurityDeposit(Double deposit) { this.securityDeposit = deposit; }
    public Integer getTotalKmsRun() { return totalKmsRun; }
    public void setTotalKmsRun(Integer kms) { this.totalKmsRun = kms; }
    public Boolean getIsRented() { return isRented; }
    public void setIsRented(Boolean rented) { this.isRented = rented; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
