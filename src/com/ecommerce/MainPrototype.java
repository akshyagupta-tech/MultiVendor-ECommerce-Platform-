package com.ecommerce;

import com.ecommerce.exceptions.InsufficientStockException;
import com.ecommerce.exceptions.InvalidOrderException;
import com.ecommerce.model.Customer;
import com.ecommerce.model.Product;
import com.ecommerce.model.Vendor;
import com.ecommerce.service.MarketplaceService;
import com.ecommerce.service.SettlementService;

import java.util.*;

public class MainPrototype {
    private static MarketplaceService marketplace;
    private static Customer sampleCustomer;
    private static int orderCounter = 1001;

    public static void main(String[] args) {
        initSampleData();

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println("==================================================================");
        System.out.println("  MULTI-VENDOR E-COMMERCE PLATFORM (Topic 271 | SDG 8 & SDG 9)   ");
        System.out.println("==================================================================");

        while (running) {
            System.out.println("\n----------------------------- MENU -----------------------------");
            System.out.println("1. List All Products (Sorted by Price)");
            System.out.println("2. Search by Max Price (Java Lambda Stream)");
            System.out.println("3. Search by Vendor ID (Java Lambda Stream)");
            System.out.println("4. Place an Order (Tests Atomicity & Stock Decrement)");
            System.out.println("5. Trigger Flash-Sale Contention Test (InsufficientStockException)");
            System.out.println("6. Display Vendor Balances (SDG 8 Settlement Status)");
            System.out.println("7. Exit");
            System.out.print("Enter choice [1-7]: ");
            System.out.flush();

            if (!scanner.hasNextLine()) break;
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1" -> displayProducts(marketplace.getAllProducts());
                case "2" -> {
                    System.out.print("Enter budget limit ($): ");
                    try {
                        double budget = Double.parseDouble(scanner.nextLine().trim());
                        List<Product> matches = marketplace.getProductsByMaxPrice(budget);
                        displayProducts(matches);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid number format.");
                    }
                }
                case "3" -> {
                    System.out.print("Enter Vendor ID (e.g., VEND-01, VEND-02): ");
                    String vId = scanner.nextLine().trim();
                    List<Product> matches = marketplace.getProductsByVendor(vId);
                    displayProducts(matches);
                }
                case "4" -> {
                    System.out.print("Enter SKU to purchase: ");
                    String sku = scanner.nextLine().trim().toUpperCase();
                    System.out.print("Enter Quantity: ");
                    try {
                        int qty = Integer.parseInt(scanner.nextLine().trim());
                        List<AbstractMap.SimpleEntry<String, Integer>> cart = List.of(
                                new AbstractMap.SimpleEntry<>(sku, qty)
                        );
                        String orderId = "ORD-" + (orderCounter++);
                        var order = marketplace.checkout(orderId, sampleCustomer.getId(), cart);
                        System.out.println("\n[SUCCESS] Transaction committed successfully!");
                        System.out.println(order);
                    } catch (InsufficientStockException e) {
                        System.out.println("\n[CHECKOUT REJECTED - OUT OF STOCK] " + e.getMessage());
                    } catch (InvalidOrderException e) {
                        System.out.println("\n[CHECKOUT REJECTED - INVALID ORDER] " + e.getMessage());
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid numeric input.");
                    }
                }
                case "5" -> {
                    System.out.println("\n========================================================");
                    System.out.println("  FLASH-SALE CONTENTION SIMULATION (CONCURRENCY TEST)   ");
                    System.out.println("========================================================");
                    System.out.println("[ACTION] Client attempting to order 999 units of SKU-101...");
                    try {
                        List<AbstractMap.SimpleEntry<String, Integer>> excessiveCart = List.of(
                                new AbstractMap.SimpleEntry<>("SKU-101", 999)
                        );
                        marketplace.checkout("ORD-FAIL-01", sampleCustomer.getId(), excessiveCart);
                    } catch (InsufficientStockException e) {
                        System.out.println("\n>>> [EXCEPTION CAUGHT CLEANLY] <<<");
                        System.out.println("Status: InsufficientStockException handled as expected!");
                        System.out.println("Details: " + e.getMessage());
                        System.out.println("Faulty SKU     : " + e.getSku());
                        System.out.println("Requested Qty  : " + e.getRequested());
                        System.out.println("Available Stock: " + e.getAvailable());
                        System.out.println("Result         : Rollback executed. Zero inventory corrupted.");
                    } catch (InvalidOrderException e) {
                        System.out.println("[INVALID ORDER] " + e.getMessage());
                    }
                    System.out.println("========================================================\n");
                }
                case "6" -> {
                    System.out.println("\n--- Current Vendor Ledger Records ---");
                    marketplace.getVendorRegistry().values().forEach(v ->
                            System.out.println(v.getName() + " -> " + v.getRoleDetails())
                    );
                }
                case "7" -> {
                    System.out.println("Terminating session. Core Prototype shutdown.");
                    running = false;
                }
                default -> System.out.println("Invalid menu choice. Please select 1-7.");
            }
        }
        scanner.close();
    }

    private static void displayProducts(List<Product> products) {
        if (products.isEmpty()) {
            System.out.println("No matching products found.");
            return;
        }
        System.out.println("\n--------------------------------------------------------------------------------------");
        products.forEach(System.out::println);
        System.out.println("--------------------------------------------------------------------------------------");
    }

    private static void initSampleData() {
        SettlementService settlement = new SettlementService();
        marketplace = new MarketplaceService(settlement);

        Vendor artisanVendor = new Vendor("VEND-01", "Kala Handicrafts", "kala@crafts.in", "MSME-DL-2026-09", 0.02);
        Vendor techVendor = new Vendor("VEND-02", "Apex Electronics", "contact@apex.com", "REG-UP-98442", 0.10);

        marketplace.registerVendor(artisanVendor);
        marketplace.registerVendor(techVendor);

        marketplace.addProduct(new Product("SKU-101", "Handmade Clay Teapot", 24.50, 8, "VEND-01"));
        marketplace.addProduct(new Product("SKU-102", "Embroidered Silk Scarf", 35.00, 15, "VEND-01"));
        marketplace.addProduct(new Product("SKU-103", "Wood Carved Coaster Set", 12.00, 20, "VEND-01"));
        marketplace.addProduct(new Product("SKU-201", "USB-C Fast Charging Hub", 45.00, 10, "VEND-02"));
        marketplace.addProduct(new Product("SKU-202", "Wireless Optical Mouse", 18.50, 25, "VEND-02"));

        sampleCustomer = new Customer("CUST-501", "Aarav Sharma", "aarav@gmail.com", "404 Sector 62, Noida");
    }
}
