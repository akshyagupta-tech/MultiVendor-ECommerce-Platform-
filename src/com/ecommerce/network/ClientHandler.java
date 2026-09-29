package com.ecommerce.network;

import com.ecommerce.model.Product;
import com.ecommerce.exceptions.InsufficientStockException;
import com.ecommerce.service.FileStorageService;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Map;

public class ClientHandler implements Runnable {
    private Socket socket;
    private List<Product> catalog;
    private Map<String, Double> vendorBalances;

    public ClientHandler(Socket socket, List<Product> catalog, Map<String, Double> vendorBalances) {
        this.socket = socket;
        this.catalog = catalog;
        this.vendorBalances = vendorBalances;
    }

    @Override
    public void run() {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            out.println("CONNECTED|Welcome to Multi-Vendor E-Commerce Platform");

            String line;
            while ((line = in.readLine()) != null) {
                line = line.trim();
                if (line.equalsIgnoreCase("EXIT")) {
                    out.println("DISCONNECTED|Session closed.");
                    break;
                }

                String[] parts = line.split("\\|");
                String action = parts[0].toUpperCase();

                switch (action) {
                    case "LIST":
                        StringBuilder sb = new StringBuilder("CATALOG|");
                        synchronized (catalog) {
                            for (Product p : catalog) {
                                sb.append(p.getSku()).append(":")
                                  .append(p.getTitle()).append(":")
                                  .append(p.getPrice()).append(":")
                                  .append(p.getStock()).append(":")
                                  .append(p.getVendorId()).append(";");
                            }
                        }
                        out.println(sb.toString());
                        break;

                    case "ORDER":
                        if (parts.length < 3) {
                            out.println("ERROR|Usage: ORDER|<SKU>|<QTY>");
                            break;
                        }
                        String sku = parts[1];
                        int qty = Integer.parseInt(parts[2]);

                        Product found = null;
                        synchronized (catalog) {
                            for (Product p : catalog) {
                                if (p.getSku().equalsIgnoreCase(sku)) {
                                    found = p;
                                    break;
                                }
                            }
                        }

                        if (found == null) {
                            out.println("ERROR|Product not found.");
                            break;
                        }

                        try {
                            found.decrementStock(qty);
                            double total = found.getPrice() * qty;
                            double fee = total * 0.02;
                            double vendorNet = total - fee;

                            synchronized (vendorBalances) {
                                vendorBalances.put(found.getVendorId(), 
                                    vendorBalances.getOrDefault(found.getVendorId(), 0.0) + vendorNet);
                                FileStorageService.saveWallets(vendorBalances);
                            }

                            synchronized (catalog) {
                                FileStorageService.saveProducts(catalog);
                            }

                            out.println("SUCCESS|Ordered " + qty + " units of " + found.getTitle() + 
                                        ". Settled Rs." + String.format("%.2f", vendorNet) + " to " + found.getVendorId() + " (Persisted to CSV)");
                        } catch (InsufficientStockException e) {
                            out.println("ERROR|" + e.getMessage());
                        }
                        break;

                    case "BALANCES":
                        StringBuilder bal = new StringBuilder("BALANCES|");
                        synchronized (vendorBalances) {
                            for (Map.Entry<String, Double> entry : vendorBalances.entrySet()) {
                                bal.append(entry.getKey()).append("=Rs.").append(String.format("%.2f", entry.getValue())).append(";");
                            }
                        }
                        out.println(bal.toString());
                        break;

                    default:
                        out.println("ERROR|Unknown command. Supported: LIST, ORDER|<SKU>|<QTY>, BALANCES, EXIT");
                }
            }
        } catch (IOException e) {
            System.out.println("[SERVER] Client disconnected: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {}
        }
    }
}
