# Smart Banking Management System

A desktop banking application built with **Java 21, JavaFX, and MySQL**, following a clean
layered architecture (Controller → Business Object → DAO → JDBC → MySQL). Supports customer
and account management, deposits, withdrawals, secure fund transfers, transaction history,
a live dashboard, and PDF account statements via JasperReports.

<p align="center">
  <a href="https://github.com/Saumya-Divyanjalee/SmartBank">
    <img alt="Repo" src="https://img.shields.io/badge/GitHub-SmartBank-C2600D?logo=github&logoColor=white">
  </a>
  <img alt="Java" src="https://img.shields.io/badge/Java-21-C2600D?logo=openjdk&logoColor=white">
  <img alt="JavaFX" src="https://img.shields.io/badge/JavaFX-UI-C2600D?logo=java&logoColor=white">
  <img alt="MySQL" src="https://img.shields.io/badge/MySQL-Database-C2600D?logo=mysql&logoColor=white">
</p>

**Author:** [Saumya Divyanjalee](https://github.com/Saumya-Divyanjalee) · **Repository:** [github.com/Saumya-Divyanjalee/SmartBank](https://github.com/Saumya-Divyanjalee/SmartBank)

<p align="center">
  <img src="screenshots/login.png" alt="Login Screen" width="700"/>
</p>

---

## Table of Contents
- [Overview](#overview)
- [Screenshots](#screenshots)
- [Tech Stack](#tech-stack)
- [Features](#features)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Design Patterns](#design-patterns)
- [Getting Started](#getting-started)
- [Security Notes](#security-notes)
- [Common Interview Questions](#common-interview-questions)

---

## Overview

This project simulates the core operations of a retail bank's back-office system: managing
customers and their accounts, processing deposits and withdrawals, and — most importantly —
handling fund transfers safely using JDBC transactions, so money is never lost or duplicated
even if something fails mid-operation.

## Screenshots

> Replace the placeholder images in the `/screenshots` folder with your own — see
> [Adding Your Own Screenshots](#adding-your-own-screenshots) below.

| Login | Dashboard |
|---|---|
| ![Login](screenshots/login.png) | ![Dashboard](screenshots/dashboard.png) |

| Customer Management | Account Management |
|---|---|
| ![Customers](screenshots/customers.png) | ![Accounts](screenshots/accounts.png) |

| Deposit / Withdraw / Transfer | Reports |
|---|---|
| ![Transactions](screenshots/transactions.png) | ![Reports](screenshots/reports.png) |

### Adding Your Own Screenshots
1. Run the application.
2. Take a screenshot of each screen (Windows: `Win + Shift + S`).
3. Save them into a `screenshots/` folder at the project root, using these exact filenames:
   `login.png`, `dashboard.png`, `customers.png`, `accounts.png`, `transactions.png`, `reports.png`
4. Commit the folder along with the README — GitHub will render the images automatically.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| UI | JavaFX (FXML + CSS) |
| Database | MySQL 8+ |
| Data Access | JDBC |
| Connection Pooling | HikariCP |
| Password Hashing | BCrypt (jBCrypt) |
| Reporting | JasperReports (PDF account statements) |
| Build Tool | Maven |
| IDE | IntelliJ IDEA |

## Features

- **Authentication** — role-based login (Admin/Staff), BCrypt-hashed passwords, inactive
  accounts blocked at login
- **Customer Management** — create, update, delete, and search customers
- **Account Management** — open accounts per customer, list and filter by customer
- **Deposit / Withdraw** — atomic balance updates; a withdrawal can never overdraw an
  account, even under concurrent requests
- **Transfer** — the core module. Runs as a single JDBC transaction: withdraw → deposit →
  save history → commit. Any failure at any step rolls back every change, so a transfer
  can never lose or duplicate money. Row locks are acquired in a consistent order to
  prevent deadlocks between simultaneous opposite-direction transfers
- **Transaction History** — full audit trail per account
- **Dashboard** — live totals for customers, accounts, bank balance, and today's activity
- **Reports** — JasperReports-generated PDF account statements

## Architecture

```
JavaFX UI (FXML + CSS)
        │
   Controller Layer      → handles user actions
        │
 Business Object (BO)     → business rules, transaction boundaries
        │
   DAO Layer               → all SQL, isolated from business logic
        │
      JDBC
        │
     MySQL
```

## Project Structure

```
src/main/java/lk/saumiz/banking/
├── Main.java                 # JavaFX entry point
├── controller/                # FXML controllers (Login, Main, Dashboard, Customer, Account, Transaction, Report)
├── bo/                        # Business interfaces + bo/impl + bo/custom (BOFactory)
├── dao/                       # DAO interfaces + dao/impl (JDBC) + dao/custom (DAOFactory)
├── dto/                       # CustomerDTO, AccountDTO, TransferRequestDTO
├── entity/                    # User, Customer, Account, Transaction
├── exception/                 # Custom checked exceptions
├── util/                      # PasswordUtil, IdGenerator, SessionManager, ReportGenerator
└── db/DBConnection.java       # Singleton HikariCP connection pool

src/main/resources/
├── view/                      # FXML screens + bank-theme.css
├── reports/AccountStatement.jrxml
└── db/schema.sql
```

## Design Patterns

- **DAO Pattern** — isolates all SQL from business logic
- **Factory Pattern** — `DAOFactory` and `BOFactory` hand out interface-typed instances
- **Singleton Pattern** — `DBConnection`, `SessionManager`, `DAOFactory`, `BOFactory`

## Getting Started

### 1. Clone the repository
```bash
git clone https://github.com/Saumya-Divyanjalee/SmartBank.git
cd SmartBank
```

### 2. Create the database
```bash
mysql -u root -p < src/main/resources/db/schema.sql
```

### 3. Generate the admin password hash
```bash
mvn compile
mvn exec:java -Dexec.mainClass="lk.saumiz.banking.util.PasswordHashGenerator" -Dexec.args="admin123"
```
Copy the printed BCrypt hash and run in MySQL:
```sql
USE smart_banking_db;
UPDATE users SET password = '<paste_hash_here>' WHERE username = 'admin';
```

### 4. Configure the database connection
Edit `src/main/java/lk/saumiz/banking/db/DBConnection.java` with your MySQL credentials.

### 5. Run
```bash
mvn clean javafx:run
```
Or run `Launcher.java` directly from IntelliJ.

Login with `admin` / the password you chose in step 2.

## Security Notes

- Passwords are never stored in plain text — BCrypt hashing only.
- All SQL uses `PreparedStatement` to prevent SQL injection.
- The seed admin password in `schema.sql` is a placeholder by design — you must generate
  your own hash so you actually know the working credentials.

## Common Interview Questions

**Why layered architecture?**
Separates UI, business rules, and data access so each layer can be tested, maintained, and
replaced independently.

**Why JDBC transactions for transfer?**
A transfer touches two account balances and inserts one history row — three separate
statements. Wrapping them in one transaction with `commit()`/`rollback()` guarantees
all-or-nothing execution, so a failure partway through can never leave money lost or
duplicated.

**Why PreparedStatement everywhere?**
Prevents SQL injection and lets MySQL cache the compiled query plan across executions.

**Why DAO?**
Keeps SQL out of business logic, so swapping the database later only touches the
`dao/impl` package.

---

*Built as a portfolio project demonstrating layered architecture, JDBC transaction
management, and secure banking workflows.*