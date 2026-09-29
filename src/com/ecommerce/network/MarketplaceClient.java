package com.ecommerce.network;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class MarketplaceClient {
    private static final String HOST = "localhost";
    private static final int PORT = 8080;

    public static void main(String[] args) {
        System.out.println("Connecting to Marketplace Server at " + HOST + ":" + PORT + "...");

        try (
            Socket socket = new Socket(HOST, PORT);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Scanner scanner = new Scanner(System.in)
        ) {
            System.out.println("[SERVER RESPONSE] " + in.readLine());
            System.out.println("\nCommands available: LIST | ORDER|<SKU>|<QTY> | BALANCES | EXIT\n");

            while (true) {
                System.out.print("Client> ");
                String command = scanner.nextLine();
                if (command == null || command.trim().isEmpty()) continue;

                out.println(command);
                String response = in.readLine();
                System.out.println("Server> " + response);

                if (command.equalsIgnoreCase("EXIT")) {
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Connection failed: " + e.getMessage());
        }
    }
}
