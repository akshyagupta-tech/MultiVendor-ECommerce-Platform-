package com.ecommerce.service;

import com.ecommerce.model.Order;
import com.ecommerce.model.OrderItem;
import com.ecommerce.model.Vendor;
import java.util.Map;

public class SettlementService {
    public void settleOrder(Order order, Map<String, Vendor> vendors) {
        System.out.println("\n--- [SDG 8] Real-Time Vendor Settlement Ledger ---");
        for (OrderItem item : order.getItems()) {
            Vendor vendor = vendors.get(item.getVendorId());
            if (vendor != null) {
                double subtotal = item.getSubtotal();
                double commissionFee = subtotal * vendor.getCommissionRate();
                double netVendorPayout = subtotal - commissionFee;

                vendor.creditBalance(netVendorPayout);

                System.out.printf("Vendor: %-16s | Subtotal: $%-7.2f | Platform Cut (%.1f%%): $%-5.2f | Payout: $%.2f\n",
                        vendor.getName(), subtotal, vendor.getCommissionRate() * 100, commissionFee, netVendorPayout);
            }
        }
        System.out.println("--------------------------------------------------");
    }
}
