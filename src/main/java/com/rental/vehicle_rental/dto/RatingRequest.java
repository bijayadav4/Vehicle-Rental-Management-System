package com.rental.vehicle_rental.dto;

public class RatingRequest {
    private Long vehicleId;
    private Integer stars;
    private String review;

    public RatingRequest() {}

    public Long getVehicleId() { return vehicleId; }
    public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }
    public Integer getStars() { return stars; }
    public void setStars(Integer stars) { this.stars = stars; }
    public String getReview() { return review; }
    public void setReview(String review) { this.review = review; }
}
