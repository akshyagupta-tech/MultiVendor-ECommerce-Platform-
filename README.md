# Multi-Vendor E-Commerce Management Platform (Topic 271)

**Course:** Object Oriented Techniques using Java (BCSE0352)  
**Institution:** NIET Greater Noida  
**UN Sustainable Development Goals:** Goal 8 (Decent Work & Economic Growth) & Goal 9 (Industry, Innovation, and Infrastructure)

---

## 📌 Executive Summary
A modular, high-performance Java SE backend platform designed to provide resilient digital trade infrastructure for small businesses, independent artisans, and micro-enterprises. It eliminates predatory fee structures and payout delays through fair-trade automated settlements and thread-safe inventory processing.

---

## 👥 Team Responsibilities & RACI Allocation

| Member Name | Project Role | Core Responsibilities |
| :--- | :--- | :--- |
| **Akshya Gupta** | Team Lead & Business Analyst | Requirements analysis, empirical research, SDG metric alignment & milestone tracking |
| **Pranav Dogra** | Lead System Architect | UML class models, modular package hierarchies, TCP sockets & protocol design |
| **Kajal Tiwari** | Technical Development Lead | Order processing engine, concurrency controls & striped lock design |
| **Priyanshu Rai** | System Integration Lead | Thread management, collections architecture & serialization schemas |
| **Sandeep Kumar** | QA & Documentation Lead | WAL persistence durability tests, custom exception verification & presentation automation |

---

## 🎯 UN Sustainable Development Goals (SDGs) Alignment

* **SDG 8: Decent Work & Economic Growth**
  * **T+0 Real-Time Liquidity:** Eliminates conventional 14- to 30-day payout float windows by immediately crediting vendor balances upon dispatch.
  * **Subsidized Micro-Vendor Tier:** Automates a reduced 2.0% platform fee for registered micro-artisans compared to standard commercial tiers.
* **SDG 9: Industry, Innovation, and Infrastructure**
  * **Zero Cloud Dependency:** Native execution on standard Java SE (JDK 17/21) running reliably on local, low-cost hardware.
  * **Thread-Safe Architecture:** Atomically checked checkout pipelines that eliminate flash-sale overselling without needing distributed cloud databases.

---

## 🏗️ Architecture & OOP Syllabus Mapping

| Component / Class | OOP Principle | Syllabus Unit | Description |
| :--- | :--- | :--- | :--- |
| `com.ecommerce.model.User` | Abstraction | Unit 1 & Unit 2 | Abstract base class defining fundamental identities and contract methods. |
| `Vendor` & `Customer` | Inheritance & Polymorphism | Unit 1 & Unit 2 | Subclasses specializing behavior (e.g., vendor balances, tax IDs, shipping addresses). |
| `Product` | Encapsulation & Generics | Unit 1 & Unit 5 | Implements `Comparable<Product>` for natural price sorting and synchronized stock mutations. |
| `Order` & `OrderItem` | Composition | Unit 1 | Order aggregates line items whose lifecycles are bound to the parent order. |
| `InsufficientStockException` | Custom Checked Exception | Unit 3 | Handles inventory depletion gracefully during concurrent checkouts. |
| `MarketplaceService` | Streams & Concurrency | Unit 2 & Unit 4 | Uses `Predicate<T>` lambdas and Java Streams for dynamic catalog filtering. |
| `SettlementService` | Business Service Layer | Unit 1 & Unit 2 | Real-time calculation of fee splits and micro-artisan wallet payouts. |

---

## 📂 Project Directory Structure

```text
src/
└── com/
    └── ecommerce/
        ├── MainPrototype.java
        ├── exceptions/
        │   ├── InsufficientStockException.java
        │   └── InvalidOrderException.java
        ├── model/
        │   ├── Customer.java
        │   ├── Order.java
        │   ├── OrderItem.java
        │   ├── Product.java
        │   ├── User.java
        │   └── Vendor.java
        └── service/
            ├── MarketplaceService.java
            └── SettlementService.java
