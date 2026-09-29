package com.ecommerce.network;

import com.ecommerce.model.Product;
import com.ecommerce.service.FileStorageService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MarketplaceServer {
    private static final int PORT = 8080;
    private static final int MAX_THREADS = 20;

    private static List<Product> catalog = Collections.synchronizedList(new ArrayList<>());
    private static Map<String, Double> vendorBalances = Collections.synchronizedMap(new HashMap<>());

    public static void main(String[] args) {
        // Load initial state from disk
        catalog.addAll(FileStorageService.loadProducts());
        vendorBalances.putAll(FileStorageService.loadWallets());

        ExecutorService threadPool = Executors.newFixedThreadPool(MAX_THREADS);

        System.out.println("==================================================");
        System.out.println("  E-COMMERCE SERVER WITH CSV STORAGE RUNNING      ");
        System.out.println("  Port: " + PORT + " | Products Loaded: " + catalog.size() + " | Wallets: " + vendorBalances.size());
        System.out.println("==================================================");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("[SERVER] Connection accepted from: " + clientSocket.getRemoteSocketAddress());
                threadPool.submit(new ClientHandler(clientSocket, catalog, vendorBalances));
            }
        } catch (IOException e) {
            System.err.println("[SERVER FATAL] " + e.getMessage());
        } finally {
            threadPool.shutdown();
        }
    }
}
