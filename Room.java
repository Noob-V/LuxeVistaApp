package com.example.resort;

public class Room {
    private String title;
    private String description;
    private String price;
    private String availability;
    private int imageResourceId;

    public Room(String title, String description, String price, String availability, int imageResourceId) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.availability = availability;
        this.imageResourceId = imageResourceId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }

    public String getAvailability() {
        return availability;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }
}
