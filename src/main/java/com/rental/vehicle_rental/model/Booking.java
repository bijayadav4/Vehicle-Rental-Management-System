package com.rental.vehicle_rental.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Link to User — many bookings can belong to one user
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Link to Vehicle — many bookings can be for one vehicle
    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Column(nullable = false)
    private LocalDate rentDate;

    private LocalDate returnDate;

    private Double totalRent = 0.0;

    private Boolean returned = false;

    // Constructors
    public Booking() {}

    public Booking(User user, Vehicle vehicle, LocalDate rentDate) {
        this.user       = user;
        this.vehicle    = vehicle;
        this.rentDate   = rentDate;
        this.returnDate = rentDate;
        this.returned   = false;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }
    public LocalDate getRentDate() { return rentDate; }
    public void setRentDate(LocalDate date) { this.rentDate = date; }
    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate date) { this.returnDate = date; }
    public Double getTotalRent() { return totalRent; }
    public void setTotalRent(Double rent) { this.totalRent = rent; }
    public Boolean getReturned() { return returned; }
    public void setReturned(Boolean returned) { this.returned = returned; }
}