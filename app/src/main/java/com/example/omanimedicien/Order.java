package com.example.omanimedicien;

public class Order {
    private int id;
    private String userEmail;
    private String medicineName;
    private int quantity;
    private String address;
    private String phone;
    private String paymentMode;
    private String paymentStatus;
    private String status;
    private String date;

    public Order(int id, String userEmail, String medicineName, int quantity, String address, String phone, String paymentMode, String paymentStatus, String status, String date) {
        this.id = id;
        this.userEmail = userEmail;
        this.medicineName = medicineName;
        this.quantity = quantity;
        this.address = address;
        this.phone = phone;
        this.paymentMode = paymentMode;
        this.paymentStatus = paymentStatus;
        this.status = status;
        this.date = date;
    }

    public int getId() { return id; }
    public String getUserEmail() { return userEmail; }
    public String getMedicineName() { return medicineName; }
    public int getQuantity() { return quantity; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public String getPaymentMode() { return paymentMode; }
    public String getPaymentStatus() { return paymentStatus; }
    public String getStatus() { return status; }
    public String getDate() { return date; }
}
