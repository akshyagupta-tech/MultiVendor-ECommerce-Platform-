package com.ecommerce.service;

import com.ecommerce.model.Product;

import java.io.*;
import java.util.*;

public class FileStorageService {
    private static final String PRODUCTS_FILE = "data/products.csv";
    private static final String WALLETS_FILE = "data/wallets.csv";

    public static synchronized List<Product> loadProducts() {
        List<Product> list = new ArrayList<>();
        File file = new File(PRODUCTS_FILE);
        if (!file.exists()) return list;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split(",");
                if (p.length >= 5) {
                    list.add(new Product(p[0].trim(), p[1].trim(), 
                                         Double.parseDouble(p[2].trim()), 
                                         Integer.parseInt(p[3].trim()), 
                                         p[4].trim()));
                }
            }
        } catch (IOException e) {
            System.err.println("[STORAGE ERROR] Failed loading products: " + e.getMessage());
        }
        return list;
    }

    public static synchronized void saveProducts(List<Product> products) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(PRODUCTS_FILE))) {
            bw.write("SKU,Title,Price,Stock,VendorId");
            bw.newLine();
            for (Product p : products) {
                bw.write(p.getSku() + "," + p.getTitle() + "," + p.getPrice() + "," + p.getStock() + "," + p.getVendorId());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("[STORAGE ERROR] Failed saving products: " + e.getMessage());
        }
    }

    public static synchronized Map<String, Double> loadWallets() {
        Map<String, Double> map = new HashMap<>();
        File file = new File(WALLETS_FILE);
        if (!file.exists()) return map;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split(",");
                if (p.length >= 2) {
                    map.put(p[0].trim(), Double.parseDouble(p[1].trim()));
                }
            }
        } catch (IOException e) {
            System.err.println("[STORAGE ERROR] Failed loading wallets: " + e.getMessage());
        }
        return map;
    }

    public static synchronized void saveWallets(Map<String, Double> wallets) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(WALLETS_FILE))) {
            bw.write("VendorId,Balance");
            bw.newLine();
            for (Map.Entry<String, Double> entry : wallets.entrySet()) {
                bw.write(entry.getKey() + "," + String.format(Locale.US, "%.2f", entry.getValue()));
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("[STORAGE ERROR] Failed saving wallets: " + e.getMessage());
        }
    }
}
