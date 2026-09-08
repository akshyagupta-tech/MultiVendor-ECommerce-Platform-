package com.ecommerce.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Order {
    private String orderId;
    private String customerId;
    private LocalDateTime timestamp;
    private List<OrderItem> items;
    private String status;

    public Order(String orderId, String customerId) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.timestamp = LocalDateTime.now();
        this.items = new ArrayList<>();
        this.status = "CREATED";
    }

    public String getOrderId() { return orderId; }
    public String getCustomerId() { return customerId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public void addItem(OrderItem item) { this.items.add(item); }
    public List<OrderItem> getItems() { return Collections.unmodifiableList(items); }

    public double calculateTotal() {
        return items.stream().mapToDouble(OrderItem::getSubtotal).sum();
    }

    @Override
    public String toString() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Order ID: %s | Customer: %s | Status: %s | Date: %s\n",
                orderId, customerId, status, timestamp.format(dtf)));
        for (OrderItem item : items) {
            sb.append(item.toString()).append("\n");
        }
        sb.append(String.format("Total Order Amount: $%.2f", calculateTotal()));
        return sb.toString();
    }
}
