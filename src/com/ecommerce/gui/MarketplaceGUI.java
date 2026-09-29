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
    private JTextField txtSku;
    private JTextField txtQty;
    private JTextArea txtLog;
    private JLabel lblStatus;

    public MarketplaceGUI() {
        super("Multi-Vendor Artisans E-Commerce Platform (SDG 8 & 9)");
        setSize(900, 600);
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
            String initial = in.readLine();
            System.out.println("[GUI NET] " + initial);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "Could not connect to MarketplaceServer on port " + PORT + ".\nMake sure the server is running!", 
                "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buildUI() {
        // --- Top Banner ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(30, 41, 59));
        JLabel title = new JLabel("  Handmade Artisans Marketplace — Client Portal", JLabel.LEFT);
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setForeground(Color.WHITE);
        title.setPreferredSize(new Dimension(500, 45));
        topPanel.add(title, BorderLayout.WEST);

        lblStatus = new JLabel("Status: Connected to " + HOST + ":" + PORT + "   ", JLabel.RIGHT);
        lblStatus.setForeground(new Color(74, 222, 128));
        topPanel.add(lblStatus, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // --- Center Catalog Table ---
        String[] columns = {"SKU", "Title", "Unit Price (Rs.)", "Stock Left", "Vendor ID"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        productTable = new JTable(tableModel);
        productTable.setRowHeight(24);
        productTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        productTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        productTable.getSelectionModel().addListSelectionListener(e -> {
            int row = productTable.getSelectedRow();
            if (row != -1) {
                txtSku.setText(tableModel.getValueAt(row, 0).toString());
            }
        });

        JScrollPane tableScroll = new JScrollPane(productTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Live Marketplace Catalog"));

        // --- South Panel: Order Controls & Logs ---
        JPanel southPanel = new JPanel(new GridLayout(1, 2, 8, 8));
        southPanel.setPreferredSize(new Dimension(900, 220));

        // Order Action Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Place Order (Atomic Stock Decrement)"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Selected SKU:"), gbc);
        gbc.gridx = 1;
        txtSku = new JTextField(10);
        formPanel.add(txtSku, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1;
        txtQty = new JTextField("1", 10);
        formPanel.add(txtQty, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        JButton btnOrder = new JButton("Submit Order");
        btnOrder.setBackground(new Color(16, 185, 129));
        btnOrder.setForeground(Color.BLACK);
        btnOrder.addActionListener(e -> handleOrder());
        btnPanel.add(btnOrder);

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> refreshCatalog());
        btnPanel.add(btnRefresh);

        JButton btnWallets = new JButton("View Wallets (SDG 8)");
        btnWallets.addActionListener(e -> fetchWallets());
        btnPanel.add(btnWallets);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        formPanel.add(btnPanel, gbc);

        southPanel.add(formPanel);

        // Activity Log Console
        txtLog = new JTextArea();
        txtLog.setEditable(false);
        txtLog.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtLog.setBackground(new Color(248, 250, 252));
        JScrollPane logScroll = new JScrollPane(txtLog);
        logScroll.setBorder(BorderFactory.createTitledBorder("Transaction Audit Log"));
        southPanel.add(logScroll);

        add(tableScroll, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
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
                    if (fields.length >= 5) {
                        tableModel.addRow(fields);
                    }
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
            JOptionPane.showMessageDialog(this, "Please enter SKU and Quantity.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int qty = Integer.parseInt(qtyStr);
            if (qty <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity must be greater than 0.", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            out.println("ORDER|" + sku + "|" + qty);
            String response = in.readLine();
            log(response);

            if (response != null && response.startsWith("SUCCESS|")) {
                JOptionPane.showMessageDialog(this, response.substring(8), "Order Success", JOptionPane.INFORMATION_MESSAGE);
                refreshCatalog();
            } else if (response != null && response.startsWith("ERROR|")) {
                JOptionPane.showMessageDialog(this, response.substring(6), "Order Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Quantity must be a valid integer.", "Input Error", JOptionPane.WARNING_MESSAGE);
        } catch (IOException e) {
            log("Order communication failed: " + e.getMessage());
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
        SwingUtilities.invokeLater(() -> {
            new MarketplaceGUI().setVisible(true);
        });
    }
}
