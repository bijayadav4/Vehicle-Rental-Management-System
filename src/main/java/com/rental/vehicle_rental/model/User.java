package com.rental.vehicle_rental.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private Double  securityBalance;
    private Long    rentedCarId;
    private Long    rentedBikeId;
    private Integer extensionCount;
    private Boolean isBlocked = false;
    private String  phone;
    private String  fullName;

    public User() {}

    public User(String email, String password, Role role) {
        this.email    = email;
        this.password = password;
        this.role     = role;
        if (role == Role.BORROWER) {
            this.securityBalance = 30000.0;
            this.extensionCount  = 0;
            this.isBlocked       = false;
        }
    }

    public Long    getId()               { return id; }
    public String  getEmail()            { return email; }
    public void    setEmail(String e)    { this.email = e; }
    public String  getPassword()         { return password; }
    public void    setPassword(String p) { this.password = p; }
    public Role    getRole()             { return role; }
    public void    setRole(Role r)       { this.role = r; }
    public Double  getSecurityBalance()  { return securityBalance; }
    public void    setSecurityBalance(Double b) { this.securityBalance = b; }
    public Long    getRentedCarId()      { return rentedCarId; }
    public void    setRentedCarId(Long id)  { this.rentedCarId = id; }
    public Long    getRentedBikeId()     { return rentedBikeId; }
    public void    setRentedBikeId(Long id) { this.rentedBikeId = id; }
    public Integer getExtensionCount()   { return extensionCount; }
    public void    setExtensionCount(Integer c) { this.extensionCount = c; }
    public Boolean getIsBlocked()        { return isBlocked; }
    public void    setIsBlocked(Boolean b)  { this.isBlocked = b; }
    public String  getPhone()            { return phone; }
    public void    setPhone(String phone) { this.phone = phone; }
    public String  getFullName()         { return fullName; }
    public void    setFullName(String n) { this.fullName = n; }
}
