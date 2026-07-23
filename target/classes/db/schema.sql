-- ============================================================
-- Smart Banking Management System - Database Schema
-- Engine: MySQL 8+
-- ============================================================

DROP DATABASE IF EXISTS smart_banking_db;
CREATE DATABASE smart_banking_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE smart_banking_db;

-- ---------------------------------------------------------
-- 1. USERS (system login - admin / staff)
-- ---------------------------------------------------------
CREATE TABLE users (
    user_id     VARCHAR(20)  PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,          -- BCrypt hash, never plain text
    role        ENUM('ADMIN','STAFF') NOT NULL DEFAULT 'STAFF',
    status      ENUM('ACTIVE','INACTIVE') NOT NULL DEFAULT 'ACTIVE',
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ---------------------------------------------------------
-- 2. BRANCHES
-- ---------------------------------------------------------
CREATE TABLE branches (
    branch_id   VARCHAR(20) PRIMARY KEY,
    branch_name VARCHAR(100) NOT NULL,
    address     VARCHAR(255),
    phone       VARCHAR(20)
);

-- ---------------------------------------------------------
-- 3. CUSTOMERS
-- ---------------------------------------------------------
CREATE TABLE customers (
    customer_id  VARCHAR(20) PRIMARY KEY,
    name         VARCHAR(100) NOT NULL,
    nic          VARCHAR(20)  NOT NULL UNIQUE,
    phone        VARCHAR(20)  NOT NULL,
    address      VARCHAR(255),
    email        VARCHAR(100),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ---------------------------------------------------------
-- 4. ACCOUNTS  (1 customer -> many accounts)
-- ---------------------------------------------------------
CREATE TABLE accounts (
    account_no   VARCHAR(20) PRIMARY KEY,
    customer_id  VARCHAR(20) NOT NULL,
    branch_id    VARCHAR(20),
    account_type ENUM('SAVINGS','CURRENT','FIXED_DEPOSIT') NOT NULL DEFAULT 'SAVINGS',
    balance      DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    status       ENUM('ACTIVE','FROZEN','CLOSED') NOT NULL DEFAULT 'ACTIVE',
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version      INT NOT NULL DEFAULT 0,           -- optimistic locking column
    CONSTRAINT fk_account_customer FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE,
    CONSTRAINT fk_account_branch   FOREIGN KEY (branch_id)   REFERENCES branches(branch_id) ON DELETE SET NULL,
    CONSTRAINT chk_balance_non_negative CHECK (balance >= 0)
);

CREATE INDEX idx_accounts_customer ON accounts(customer_id);

-- ---------------------------------------------------------
-- 5. TRANSACTIONS
-- ---------------------------------------------------------
CREATE TABLE transactions (
    transaction_id   VARCHAR(30) PRIMARY KEY,
    from_account     VARCHAR(20),
    to_account       VARCHAR(20),
    amount           DECIMAL(15,2) NOT NULL,
    transaction_type ENUM('DEPOSIT','WITHDRAW','TRANSFER') NOT NULL,
    balance_after    DECIMAL(15,2),
    remarks          VARCHAR(255),
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_txn_from FOREIGN KEY (from_account) REFERENCES accounts(account_no) ON DELETE SET NULL,
    CONSTRAINT fk_txn_to   FOREIGN KEY (to_account)   REFERENCES accounts(account_no) ON DELETE SET NULL
);

CREATE INDEX idx_txn_from_date ON transactions(from_account, transaction_date);
CREATE INDEX idx_txn_to_date   ON transactions(to_account, transaction_date);

-- ---------------------------------------------------------
-- 6. LOANS (kept simple - for future extension)
-- ---------------------------------------------------------
CREATE TABLE loan (
    loan_id       VARCHAR(20) PRIMARY KEY,
    customer_id   VARCHAR(20) NOT NULL,
    amount        DECIMAL(15,2) NOT NULL,
    interest_rate DECIMAL(5,2) NOT NULL,
    status        ENUM('PENDING','APPROVED','REJECTED','CLOSED') DEFAULT 'PENDING',
    issued_date   DATE,
    CONSTRAINT fk_loan_customer FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE
);

-- ---------------------------------------------------------
-- 7. CARDS
-- ---------------------------------------------------------
CREATE TABLE cards (
    card_id      VARCHAR(20) PRIMARY KEY,
    account_no   VARCHAR(20) NOT NULL,
    card_type    ENUM('DEBIT','CREDIT') NOT NULL,
    card_number  VARCHAR(20) NOT NULL UNIQUE,
    expiry_date  DATE,
    status       ENUM('ACTIVE','BLOCKED','EXPIRED') DEFAULT 'ACTIVE',
    CONSTRAINT fk_card_account FOREIGN KEY (account_no) REFERENCES accounts(account_no) ON DELETE CASCADE
);

-- ---------------------------------------------------------
-- 8. AUDIT LOGS
-- ---------------------------------------------------------
CREATE TABLE audit_logs (
    log_id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     VARCHAR(20),
    action      VARCHAR(100) NOT NULL,
    details     VARCHAR(500),
    log_date    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_audit_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL
);

-- ============================================================
-- SEED DATA
-- ============================================================

-- Default branch
INSERT INTO branches (branch_id, branch_name, address, phone)
VALUES ('BR001', 'Main Branch - Colombo', '123 Galle Road, Colombo', '0112345678');

-- Default admin user
-- IMPORTANT: password must be a real BCrypt hash - do NOT paste a fake one.
-- Step 1: run lk.saumiz.banking.util.PasswordHashGenerator (see README) with your chosen
--         password, e.g. "admin123", to print a genuine BCrypt hash.
-- Step 2: paste that hash below, replacing <PASTE_BCRYPT_HASH_HERE>, then run this script.
INSERT INTO users (user_id, username, password, role, status)
VALUES ('U001', 'admin', '<PASTE_BCRYPT_HASH_HERE>', 'ADMIN', 'ACTIVE');

-- Sample customer + account for quick testing
INSERT INTO customers (customer_id, name, nic, phone, address, email)
VALUES ('C001', 'Saumya Perera', '200012345678', '0771234567', 'Panadura, Sri Lanka', 'saumya@example.com');

INSERT INTO accounts (account_no, customer_id, branch_id, account_type, balance, status)
VALUES ('ACC0001', 'C001', 'BR001', 'SAVINGS', 10000.00, 'ACTIVE');
