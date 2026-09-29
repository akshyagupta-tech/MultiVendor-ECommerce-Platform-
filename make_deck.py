import sys

content = """
# Slide 1: Project Title
Multi-Tier Artisans E-Commerce Platform
From In-Memory Prototype to Distributed Persistent System (SDG 8 & 9)

# Slide 2: The Big Picture (Architecture)
1. Presentation Tier: Java Swing Desktop Client (Interactive UI)
2. Network Tier: Multi-Threaded TCP Sockets (Port 8080)
3. Business Tier: Atomic Stock Decrement & Custom Exceptions
4. Persistence Tier: Thread-Safe Flat-File CSV Storage

# Slide 3: Network & Concurrency (Phase 2)
- MarketplaceServer accepts multiple simultaneous clients via ExecutorService thread pool.
- Sockets transmit text-delimited protocol packets (CATALOG, ORDER, BALANCES).
- Synchronized locks stop race conditions during high-speed flash sales.

# Slide 4: Data Durability (CSV Engine)
- Eliminates volatile RAM loss.
- products.csv preserves live inventory numbers across restarts.
- wallets.csv tracks verified vendor earnings instantly (T+0 settlement).

# Slide 5: Swing GUI & Real-Time UX (Phase 3)
- JTable dynamically displays live server catalog.
- One-click ordering with instant feedback alerts.
- Built-in audit log and instant vendor balance viewer.

# Slide 6: SDG 8 & 9 Impact
- SDG 8: Fair 2% platform fee empowers local handmade artisans.
- SDG 9: Lightweight Java SE stack runs reliably without heavy database servers.
"""

print("Updated slide outline ready for presentation deck export!")
