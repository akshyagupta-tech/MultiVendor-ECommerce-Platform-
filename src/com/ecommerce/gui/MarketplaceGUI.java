package com.ecommerce.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class MarketplaceGUI extends JFrame {
    private static final String HOST = "localhost";
    private static final int PORT = 8080;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private DefaultTableModel tableModel;
    private JTable productTable;
    private JTextField txtSku, txtQty;
    private JTextField txtNewSku, txtNewTitle, txtNewPrice, txtNewStock, txtNewVendor;
    private JTextArea txtLog;
    private JLabel lblStatus;

    public MarketplaceGUI() {
        super("Multi-Vendor Artisans E-Commerce Platform (SDG 8 & 9)");
        setSize(950, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        initNetwork();
        buildUI();
        refreshCatalog();
    }

    private void initNetwork() {
        try {
            socket = new Socket(HOST, PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            in.readLine(); // initial greeting
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "Could not connect to MarketplaceServer on port " + PORT + ".\nMake sure server is running!", 
                "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buildUI() {
        // --- Top Header ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(30, 41, 59));
        JLabel title = new JLabel("  Handmade Artisans Marketplace — Multi-Tier Portal", JLabel.LEFT);
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setForeground(Color.WHITE);
        title.setPreferredSize(new Dimension(500, 45));
        topPanel.add(title, BorderLayout.WEST);

        lblStatus = new JLabel("Status: Connected to " + HOST + ":" + PORT + "   ", JLabel.RIGHT);
        lblStatus.setForeground(new Color(74, 222, 128));
        topPanel.add(lblStatus, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // --- Tabbed Pane ---
        JTabbedPane tabbedPane = new JTabbedPane();

        // TAB 1: Customer View (Catalog + Ordering)
        JPanel customerTab = new JPanel(new BorderLayout(8, 8));

        String[] columns = {"SKU", "Title", "Unit Price (Rs.)", "Stock Left", "Vendor ID"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        productTable = new JTable(tableModel);
        productTable.setRowHeight(24);
        productTable.getSelectionModel().addListSelectionListener(e -> {
            int row = productTable.getSelectedRow();
            if (row != -1) txtSku.setText(tableModel.getValueAt(row, 0).toString());
        });
        customerTab.add(new JScrollPane(productTable), BorderLayout.CENTER);

        JPanel orderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        orderPanel.setBorder(BorderFactory.createTitledBorder("Place Order (Atomic Stock Decrement)"));
        orderPanel.add(new JLabel("Selected SKU:"));
        txtSku = new JTextField(8);
        orderPanel.add(txtSku);
        orderPanel.add(new JLabel("Quantity:"));
        txtQty = new JTextField("1", 5);
        orderPanel.add(txtQty);

        JButton btnOrder = new JButton("Submit Order");
        btnOrder.setBackground(new Color(16, 185, 129));
        btnOrder.addActionListener(e -> handleOrder());
        orderPanel.add(btnOrder);

        JButton btnRefresh = new JButton("Refresh Catalog");
        btnRefresh.addActionListener(e -> refreshCatalog());
        orderPanel.add(btnRefresh);

        JButton btnWallets = new JButton("View Wallets (SDG 8)");
        btnWallets.addActionListener(e -> fetchWallets());
        orderPanel.add(btnWallets);

        customerTab.add(orderPanel, BorderLayout.SOUTH);
        tabbedPane.addTab("🛒 Customer Catalog & Checkout", customerTab);

        // TAB 2: Vendor Portal (Upload New Product)
        JPanel vendorTab = new JPanel(new GridBagLayout());
        vendorTab.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 8, 8, 8);
        g.fill = GridBagConstraints.HORIZONTAL;

        g.gridx = 0; g.gridy = 0; vendorTab.add(new JLabel("Product SKU (e.g. SKU-105):"), g);
        g.gridx = 1; txtNewSku = new JTextField(15); vendorTab.add(txtNewSku, g);

        g.gridx = 0; g.gridy = 1; vendorTab.add(new JLabel("Product Title:"), g);
        g.gridx = 1; txtNewTitle = new JTextField(15); vendorTab.add(txtNewTitle, g);

        g.gridx = 0; g.gridy = 2; vendorTab.add(new JLabel("Unit Price (Rs.):"), g);
        g.gridx = 1; txtNewPrice = new JTextField(15); vendorTab.add(txtNewPrice, g);

        g.gridx = 0; g.gridy = 3; vendorTab.add(new JLabel("Stock Quantity:"), g);
        g.gridx = 1; txtNewStock = new JTextField(15); vendorTab.add(txtNewStock, g);

        g.gridx = 0; g.gridy = 4; vendorTab.add(new JLabel("Vendor ID (e.g. VEND-001):"), g);
        g.gridx = 1; txtNewVendor = new JTextField(15); vendorTab.add(txtNewVendor, g);

        JButton btnUpload = new JButton("Upload Product to Live Network");
        btnUpload.setBackground(new Color(59, 130, 246));
        btnUpload.addActionListener(e -> handleAddProduct());
        g.gridx = 0; g.gridy = 5; g.gridwidth = 2;
        vendorTab.add(btnUpload, g);

        tabbedPane.addTab("📦 Vendor Inventory Manager", vendorTab);
        add(tabbedPane, BorderLayout.CENTER);

        // --- Bottom Activity Audit Log ---
        txtLog = new JTextArea(5, 50);
        txtLog.setEditable(false);
        txtLog.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtLog.setBackground(new Color(248, 250, 252));
        JScrollPane logScroll = new JScrollPane(txtLog);
        logScroll.setBorder(BorderFactory.createTitledBorder("Transaction Audit Log"));
        add(logScroll, BorderLayout.SOUTH);
    }

    private synchronized void refreshCatalog() {
        if (out == null || in == null) return;
        try {
            out.println("LIST");
            String response = in.readLine();
            if (response != null && response.startsWith("CATALOG|")) {
                tableModel.setRowCount(0);
                String data = response.substring(8);
                String[] items = data.split(";");
                for (String item : items) {
                    if (item.trim().isEmpty()) continue;
                    String[] fields = item.split(":");
                    if (fields.length >= 5) tableModel.addRow(fields);
                }
                log("Catalog refreshed from server.");
            }
        } catch (IOException e) {
            log("Error refreshing catalog: " + e.getMessage());
        }
    }

    private synchronized void handleOrder() {
        String sku = txtSku.getText().trim();
        String qtyStr = txtQty.getText().trim();
        if (sku.isEmpty() || qtyStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter SKU and Quantity.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            int qty = Integer.parseInt(qtyStr);
            out.println("ORDER|" + sku + "|" + qty);
            String response = in.readLine();
            log(response);
            if (response != null && response.startsWith("SUCCESS|")) {
                JOptionPane.showMessageDialog(this, response.substring(8), "Order Success", JOptionPane.INFORMATION_MESSAGE);
                refreshCatalog();
            } else if (response != null && response.startsWith("ERROR|")) {
                JOptionPane.showMessageDialog(this, response.substring(6), "Order Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid quantity: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private synchronized void handleAddProduct() {
        String sku = txtNewSku.getText().trim();
        String title = txtNewTitle.getText().trim();
        String price = txtNewPrice.getText().trim();
        String stock = txtNewStock.getText().trim();
        String vendor = txtNewVendor.getText().trim();

        if (sku.isEmpty() || title.isEmpty() || price.isEmpty() || stock.isEmpty() || vendor.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all product fields.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Double.parseDouble(price);
            Integer.parseInt(stock);
            out.println("ADD_PRODUCT|" + sku + "|" + title + "|" + price + "|" + stock + "|" + vendor);
            String response = in.readLine();
            log(response);

            if (response != null && response.startsWith("SUCCESS|")) {
                JOptionPane.showMessageDialog(this, response.substring(8), "Product Added", JOptionPane.INFORMATION_MESSAGE);
                txtNewSku.setText("");
                txtNewTitle.setText("");
                txtNewPrice.setText("");
                txtNewStock.setText("");
                txtNewVendor.setText("");
                refreshCatalog();
            } else if (response != null && response.startsWith("ERROR|")) {
                JOptionPane.showMessageDialog(this, response.substring(6), "Upload Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Price and Stock must be numeric.", "Input Error", JOptionPane.WARNING_MESSAGE);
        } catch (IOException e) {
            log("Network error: " + e.getMessage());
        }
    }

    private synchronized void fetchWallets() {
        if (out == null || in == null) return;
        try {
            out.println("BALANCES");
            String response = in.readLine();
            if (response != null && response.startsWith("BALANCES|")) {
                String payload = response.substring(9).replace(";", "\n");
                JOptionPane.showMessageDialog(this, payload, "Vendor Balances (SDG 8 Payouts)", JOptionPane.INFORMATION_MESSAGE);
                log("Checked vendor wallets: " + payload.replace("\n", ", "));
            }
        } catch (IOException e) {
            log("Error fetching balances: " + e.getMessage());
        }
    }

    private void log(String msg) {
        txtLog.append(msg + "\n");
        txtLog.setCaretPosition(txtLog.getDocument().getLength());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MarketplaceGUI().setVisible(true));
    }
}
