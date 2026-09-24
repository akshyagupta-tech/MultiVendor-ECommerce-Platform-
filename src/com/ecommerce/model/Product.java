package com.ecommerce.model;

import com.ecommerce.exceptions.InsufficientStockException;

public class Product implements Comparable<Product> {
    private String sku;
    private String title;
    private double price;
    private int stock;
    private String vendorId;

    public Product(String sku, String title, double price, int stock, String vendorId) {
        this.sku = sku;
        this.title = title;
        this.price = price;
        this.stock = stock;
        this.vendorId = vendorId;
    }

    public String getSku() { return sku; }
    public String getTitle() { return title; }
    public double getPrice() { return price; }
    public synchronized int getStock() { return stock; }
    public String getVendorId() { return vendorId; }

    public synchronized void decrementStock(int qty) throws InsufficientStockException {
        if (qty <= 0 || qty > stock) {
            throw new InsufficientStockException("Insufficient stock for " + sku
                    + ": requested " + qty + ", available " + stock + ".");
        }
        this.stock -= qty;
    }
    public synchronized void incrementStock(int qty) { this.stock += qty; }

    @Override
    public int compareTo(Product other) {
        return Double.compare(this.price, other.price);
    }

    @Override
    public String toString() {
        return String.format("SKU: %-8s | Title: %-24s | Price: $%7.2f | Stock: %-4d | Vendor: %s",
                sku, title, price, stock, vendorId);
    }
}
