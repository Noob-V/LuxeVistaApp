package com.example.resort;

import java.io.Serializable;

public class ReservationItem implements Serializable {
    private int userId;
    private int serviceId;
    private String serviceName;
    private String serviceDescription;
    private String reservationDate;
    private String timeSlot;
    private String status;
    private double servicePrice;

    public ReservationItem(int userId, int serviceId, String serviceName, String serviceDescription,
                           String reservationDate, String timeSlot, double servicePrice, String status) {
        this.userId = userId;
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.serviceDescription = serviceDescription;
        this.reservationDate = reservationDate;
        this.timeSlot = timeSlot;
        this.servicePrice = servicePrice;
        this.status = status;
    }

    public String getServiceDescription() {
        return serviceDescription;
    }

    public String getServiceName() {
        return serviceName;
    }

    public double getServicePrice() {
        return servicePrice;
    }

    public int getServiceId() {
        return serviceId;
    }

    public String getReservationDate() {
        return reservationDate;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public String getStatus() {
        return status;
    }

    public int getUserId() {
        return userId;
    }
}
