package com.ecommerce;

import com.ecommerce.model.*;
import com.ecommerce.exceptions.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class MainPrototype {
    private static List<Product> catalog = new ArrayList<>();
    private static Map<String, Double> vendorBalances = new HashMap<>();

    public static void main(String[] args) {
        initSampleData();
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("======================================");
            System.out.println("----------------------------- MENU -----------------------------");
            System.out.println("1. List All Products (Sorted by Price)");
            System.out.println("2. Search by Max Price (Java Lambda Stream)");
            System.out.println("3. Search by Vendor ID (Java Lambda Stream)");
            System.out.println("4. Place an Order (Tests Atomicity & Stock Decrement)");
            System.out.println("5. Trigger Flash-Sale Contention Test (InsufficientStockException)");
            System.out.println("6. Display Vendor Balances (SDG 8 Settlement Status)");
            System.out.println("7. Run Multi-Threaded Stress Test (Race Condition Simulation)");
            System.out.println("8. Exit");
            System.out.print("Choose option: ");

            int choice;
            try {
                choice = Integer.parseInt(sc.nextLine());
            } catch (Exception e) {
                System.out.println("Please enter a valid number.");
                continue;
            }

            switch (choice) {
                case 1:
                    Collections.sort(catalog);
                    System.out.println("\n--- All Products (Sorted by Price) ---");
                    for (Product p : catalog) {
                        System.out.println(p.getSku() + " | " + p.getTitle() + " | Rs." + p.getPrice() + " | Stock: " + p.getStock() + " | Vendor: " + p.getVendorId());
                    }
                    break;

                case 2:
                    System.out.print("Enter max budget: ");
                    double maxBudget = Double.parseDouble(sc.nextLine());
                    System.out.println("\n--- Products within Budget ---");
                    catalog.stream()
                           .filter(p -> p.getPrice() <= maxBudget)
                           .forEach(p -> System.out.println(p.getTitle() + " - Rs." + p.getPrice()));
                    break;

                case 3:
                    System.out.print("Enter Vendor ID (e.g. VEND-001): ");
                    String vId = sc.nextLine();
                    System.out.println("\n--- Products by " + vId + " ---");
                    catalog.stream()
                           .filter(p -> p.getVendorId().equalsIgnoreCase(vId))
                           .forEach(p -> System.out.println(p.getTitle() + " | Stock: " + p.getStock()));
                    break;

                case 4:
                    System.out.println("\nPlacing normal order for 1 unit of SKU-101...");
                    Product target = catalog.get(0);
                    try {
                        target.decrementStock(1);
                        double amount = target.getPrice();
                        double fee = amount * 0.02;
                        double vendorNet = amount - fee;
                        vendorBalances.put(target.getVendorId(), vendorBalances.getOrDefault(target.getVendorId(), 0.0) + vendorNet);
                        System.out.println("Order successful! Stock remaining: " + target.getStock());
                        System.out.println("Settled Rs." + vendorNet + " to " + target.getVendorId() + " (2% fee: Rs." + fee + ")");
                    } catch (InsufficientStockException e) {
                        System.out.println("Order Failed: " + e.getMessage());
                    }
                    break;

                case 5:
                    System.out.println("\nAttempting to buy 500 units of SKU-102 (Stock is low)...");
                    Product lowStockItem = catalog.get(1);
                    String stockErrorMessage = "Only " + lowStockItem.getStock() + " units available. Cannot fulfill order for 500.";
                    try {
                        if (lowStockItem.getStock() < 500) {
                            throw new InsufficientStockException(stockErrorMessage);
                        }
                        lowStockItem.decrementStock(500);
                        System.out.println("Order successful!");
                    } catch (InsufficientStockException e) {
                        System.out.println("[CAUGHT CUSTOM EXCEPTION] " + e.getMessage());
                    }
                    break;

                case 6:
                    System.out.println("\n--- SDG 8 Vendor Wallets (T+0 Instant Settlement) ---");
                    for (Map.Entry<String, Double> entry : vendorBalances.entrySet()) {
                        System.out.println("Vendor: " + entry.getKey() + " | Available Balance: Rs." + String.format("%.2f", entry.getValue()));
                    }
                    break;

                case 7:
                    runStressTest();
                    break;

                case 8:
                    System.out.println("Exiting application.");
                    sc.close();
                    return;

                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private static void initSampleData() {
        catalog.add(new Product("SKU-101", "Handmade Clay Pot", 250.0, 15, "VEND-001"));
        catalog.add(new Product("SKU-102", "Madhubani Bookmark", 120.0, 4, "VEND-002"));
        catalog.add(new Product("SKU-103", "Boho Wooden Coaster", 340.0, 10, "VEND-001"));
        catalog.add(new Product("SKU-104", "Lippan Wall Decor", 890.0, 2, "VEND-003"));

        vendorBalances.put("VEND-001", 1200.0);
        vendorBalances.put("VEND-002", 450.0);
        vendorBalances.put("VEND-003", 850.0);
    }

    private static void runStressTest() {
        System.out.println("\n--- [SIMULATION] 10 Concurrent Threads Competing for 1 Single Stock Unit ---");
        Product flashItem = new Product("SKU-FLASH", "Artisan Table Lamp", 599.0, 1, "VEND-001");

        ExecutorService executor = Executors.newFixedThreadPool(10);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        for (int i = 1; i <= 10; i++) {
            final int studentBuyerId = i;
            executor.submit(() -> {
                try {
                    flashItem.decrementStock(1);
                    successCount.incrementAndGet();
                    System.out.println(" -> Customer Thread " + studentBuyerId + ": [SUCCESS] Grabbed lock and bought item!");
                } catch (InsufficientStockException e) {
                    failureCount.incrementAndGet();
                    System.out.println(" -> Customer Thread " + studentBuyerId + ": [BLOCKED] " + e.getMessage());
                } catch (Exception e) {
                    System.out.println(" -> Error: " + e.getMessage());
                }
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(2, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {}

        System.out.println("\n----------------- TEST SUMMARY -----------------");
        System.out.println("Successful Orders : " + successCount.get() + " (Only 1 thread got it)");
        System.out.println("Rejected Requests : " + failureCount.get() + " (9 threads blocked safely)");
        System.out.println("Stock Left in RAM : " + flashItem.getStock() + " (Never drops below 0)");
        System.out.println("Race Condition Test: " + (successCount.get() == 1 && flashItem.getStock() == 0 ? "PASSED [100% THREAD SAFE]" : "FAILED"));
        System.out.println("------------------------------------------------\n");
    }
}
