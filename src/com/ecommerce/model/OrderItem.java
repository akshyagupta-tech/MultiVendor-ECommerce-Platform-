package com.ecommerce.model;

public class OrderItem {
    private String sku;
    private String title;
    private int quantity;
    private double unitPrice;
    private String vendorId;

    public OrderItem(String sku, String title, int quantity, double unitPrice, String vendorId) {
        this.sku = sku;
        this.title = title;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.vendorId = vendorId;
    }

    public String getSku() { return sku; }
    public String getTitle() { return title; }
    public int getQuantity() { return quantity; }
    public double getUnitPrice() { return unitPrice; }
    public String getVendorId() { return vendorId; }
    public double getSubtotal() { return quantity * unitPrice; }

    @Override
    public String toString() {
        return String.format("  -> SKU: %-8s | %-22s | Qty: %-3d | Unit: $%.2f | Subtotal: $%.2f",
                sku, title, quantity, unitPrice, getSubtotal());
    }
}
