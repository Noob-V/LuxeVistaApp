package com.example.resort;

import java.io.Serializable;

public class ServiceItem implements Serializable {

    private String serviceName;
    private String serviceDescription;
    private double servicePrice;
    private int serviceImageResourceId;
    private int serviceId;

    public ServiceItem(int serviceId, String serviceName, String serviceDescription, double servicePrice, int serviceImageResourceId) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.serviceDescription = serviceDescription;
        this.servicePrice = servicePrice;
        this.serviceImageResourceId = serviceImageResourceId;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceDescription() {
        return serviceDescription;
    }

    public void setServiceDescription(String serviceDescription) {
        this.serviceDescription = serviceDescription;
    }

    public double getServicePrice() {
        return servicePrice;
    }

    public void setServicePrice(double servicePrice) {
        this.servicePrice = servicePrice;
    }

    public int getServiceImageResourceId() {
        return serviceImageResourceId;
    }

    public void setServiceImageResourceId(int serviceImageResourceId) {
        this.serviceImageResourceId = serviceImageResourceId;
    }
}
