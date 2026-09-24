package com.ecommerce.service;

import com.ecommerce.exceptions.InsufficientStockException;
import com.ecommerce.exceptions.InvalidOrderException;
import com.ecommerce.model.Order;
import com.ecommerce.model.OrderItem;
import com.ecommerce.model.Product;
import com.ecommerce.model.Vendor;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MarketplaceService {
    private final Map<String, Product> productCatalog;
    private final Map<String, Vendor> vendorRegistry;
    private final List<Order> transactionHistory;
    private final SettlementService settlementService;

    public MarketplaceService(SettlementService settlementService) {
        this.productCatalog = new HashMap<>();
        this.vendorRegistry = new HashMap<>();
        this.transactionHistory = new ArrayList<>();
        this.settlementService = settlementService;
    }

    public void registerVendor(Vendor vendor) {
        vendorRegistry.put(vendor.getId(), vendor);
    }

    public void addProduct(Product product) {
        productCatalog.put(product.getSku(), product);
    }

    public Map<String, Vendor> getVendorRegistry() {
        return Collections.unmodifiableMap(vendorRegistry);
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(productCatalog.values());
    }

    public List<Product> filterProducts(Predicate<Product> predicate) {
        return productCatalog.values().stream()
                .filter(predicate)
                .sorted()
                .collect(Collectors.toList());
    }

    public List<Product> getProductsByMaxPrice(double maxPrice) {
        return filterProducts(p -> p.getPrice() <= maxPrice);
    }

    public List<Product> getProductsByVendor(String vendorId) {
        return filterProducts(p -> p.getVendorId().equalsIgnoreCase(vendorId));
    }

    public synchronized Order checkout(String orderId, String customerId, List<AbstractMap.SimpleEntry<String, Integer>> cartItems)
            throws InsufficientStockException, InvalidOrderException {

        if (cartItems == null || cartItems.isEmpty()) {
            throw new InvalidOrderException("Cannot checkout an empty cart.");
        }

        for (var entry : cartItems) {
            String sku = entry.getKey();
            int requestedQty = entry.getValue();

            Product product = productCatalog.get(sku);
            if (product == null) {
                throw new InvalidOrderException("Product SKU not found: " + sku);
            }
            if (product.getStock() < requestedQty) {
                throw new InsufficientStockException("Insufficient stock for " + sku
                        + ": requested " + requestedQty + ", available " + product.getStock() + ".");
            }
        }

        Order order = new Order(orderId, customerId);
        for (var entry : cartItems) {
            String sku = entry.getKey();
            int requestedQty = entry.getValue();

            Product product = productCatalog.get(sku);
            product.decrementStock(requestedQty);

            OrderItem item = new OrderItem(product.getSku(), product.getTitle(), requestedQty, product.getPrice(), product.getVendorId());
            order.addItem(item);
        }

        order.setStatus("COMMITTED");
        transactionHistory.add(order);
        settlementService.settleOrder(order, vendorRegistry);
        return order;
    }
}
