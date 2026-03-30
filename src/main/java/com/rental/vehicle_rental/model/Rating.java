package com.rental.vehicle_rental.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "ratings")
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @Column(name = "rating", nullable = false)
    private Integer stars;
    private String  review;
    private LocalDate ratedOn;

    public Rating() {}

    public Rating(User user, Vehicle vehicle, Integer stars, String review) {
        this.user     = user;
        this.vehicle  = vehicle;
        this.stars    = stars;
        this.review   = review;
        this.ratedOn  = LocalDate.now();
    }

    public Long      getId()      { return id; }
    public User      getUser()    { return user; }
    public Vehicle   getVehicle() { return vehicle; }
    public Integer   getStars()   { return stars; }
    public void      setStars(Integer s)  { this.stars = s; }
    public String    getReview()  { return review; }
    public void      setReview(String r) { this.review = r; }
    public LocalDate getRatedOn() { return ratedOn; }
}
