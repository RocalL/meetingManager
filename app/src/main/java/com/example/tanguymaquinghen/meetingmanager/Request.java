package com.example.tanguymaquinghen.meetingmanager;

public class Request {

    private String phoneNumber;
    private String contactName;
    private double latitude;
    private double longitude;

    public Request(String phoneNumber, String contactName, double longitude, double latitude) {
        this.phoneNumber = phoneNumber;
        this.contactName = contactName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Request(String phoneNumber){
        this.phoneNumber = phoneNumber;
    }

    public Request(){
        this.phoneNumber = "";
        this.contactName = "";
        this.longitude = 0.0;
        this.latitude = 0.0;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }
}
