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

 

| Login | Dashboard |
|---|---|
| <img width="1127" height="797" alt="Screenshot 2026-07-23 134214" src="https://github.com/user-attachments/assets/1eb73bd7-0b0a-4e97-a6e8-67975ba35a98" />
|  <img width="1378" height="900" alt="Screenshot 2026-07-23 134226" src="https://github.com/user-attachments/assets/3c1ee2e3-886d-4790-8d79-96c6098949cc" />
 

 

| Deposit / Withdraw / Transfer | Reports |
|---|---|
|  <img width="1367" height="908" alt="Screenshot 2026-07-23 134412" src="https://github.com/user-attachments/assets/9717e1fe-17c9-4323-9380-c05fbbc1b133" />
|<img width="1918" height="1017" alt="Screenshot 2026-07-23 134957" src="https://github.com/user-attachments/assets/bd66b1bc-c53a-491e-96eb-ed97e0a141e1" />
   

 
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

 

*Built as a portfolio project demonstrating layered architecture, JDBC transaction
management, and secure banking workflows.*
