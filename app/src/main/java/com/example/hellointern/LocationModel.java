package com.example.hellointern;

public class LocationModel {
    private double latitude;
    private double longitude;
    private String address; // New field for address

    public LocationModel(double latitude, double longitude, String address) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address; // Initialize address
    }

    // Getters for latitude, longitude, and address
    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getAddress() {
        return address; // Getter for address
    }
}
