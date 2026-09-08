package com.ecommerce.model;

public class Customer extends User {
    private String deliveryAddress;

    public Customer(String id, String name, String email, String deliveryAddress) {
        super(id, name, email, "CUSTOMER");
        this.deliveryAddress = deliveryAddress;
    }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    @Override
    public String getRoleDetails() {
        return "Address: " + deliveryAddress;
    }
}
