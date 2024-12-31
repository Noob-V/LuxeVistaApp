package com.example.resort;

import android.graphics.Bitmap;

public class AttractionItem {
    private String name;
    private String description;
    private Bitmap image;
    private String type;
    private String offerDetails;
    private String availableFrom;
    private String availableTo;

    public AttractionItem(String name, String description, Bitmap image, String type,
                          String offerDetails, String availableFrom, String availableTo) {
        this.name = name;
        this.description = description;
        this.image = image;
        this.type = type;
        this.offerDetails = offerDetails;
        this.availableFrom = availableFrom;
        this.availableTo = availableTo;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Bitmap getImage() {
        return image;
    }

    public String getType() {
        return type;
    }

    public String getOfferDetails() {
        return offerDetails;
    }

    public String getAvailableFrom() {
        return availableFrom;
    }

    public String getAvailableTo() {
        return availableTo;
    }
}
