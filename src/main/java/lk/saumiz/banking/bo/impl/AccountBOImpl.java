package lk.saumiz.banking.bo.impl;

import lk.saumiz.banking.bo.AccountBO;
import lk.saumiz.banking.dao.AccountDAO;
import lk.saumiz.banking.dao.TransactionDAO;
import lk.saumiz.banking.dao.custom.DAOFactory;
import lk.saumiz.banking.db.DBConnection;
import lk.saumiz.banking.dto.AccountDTO;
import lk.saumiz.banking.dto.TransferRequestDTO;
import lk.saumiz.banking.entity.Account;
import lk.saumiz.banking.entity.Transaction;
import lk.saumiz.banking.exception.AccountNotFoundException;
import lk.saumiz.banking.exception.InsufficientBalanceException;
import lk.saumiz.banking.exception.InvalidTransactionException;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class AccountBOImpl implements AccountBO {

    private final AccountDAO accountDAO = DAOFactory.getInstance().getAccountDAO();
    private final TransactionDAO transactionDAO = DAOFactory.getInstance().getTransactionDAO();

    @Override
    public String createAccount(AccountDTO dto) throws Exception {
        try (Connection con = DBConnection.getInstance().getConnection()) {
            String newAccNo = accountDAO.generateNextAccountNo(con);
            Account account = new Account(newAccNo, dto.getCustomerId(), dto.getBranchId(),
                    dto.getAccountType(), dto.getBalance() == null ? BigDecimal.ZERO : dto.getBalance(),
                    "ACTIVE");
            boolean saved = accountDAO.save(con, account);
            return saved ? newAccNo : null;
        }
    }

    @Override
    public Account getAccount(String accountNo) throws Exception {
        try (Connection con = DBConnection.getInstance().getConnection()) {
            return accountDAO.findByAccountNo(con, accountNo);
        }
    }

    @Override
    public List<Account> getAccountsForCustomer(String customerId) throws Exception {
        try (Connection con = DBConnection.getInstance().getConnection()) {
            return accountDAO.findByCustomerId(con, customerId);
        }
    }

    @Override
    public List<Account> getAllAccounts() throws Exception {
        try (Connection con = DBConnection.getInstance().getConnection()) {
            return accountDAO.findAll(con);
        }
    }

    /**
     * DEPOSIT workflow: find account -> add balance -> save transaction -> commit.
     * Runs as one JDBC transaction so the balance update and the history row
     * either both happen or neither does.
     */
    @Override
    public boolean deposit(String accountNo, BigDecimal amount, String remarks) throws Exception {
        validateAmount(amount);
        Connection con = null;
        try {
            con = DBConnection.getInstance().getConnection();
            con.setAutoCommit(false);

            Account account = accountDAO.findByAccountNoForUpdate(con, accountNo);
            if (account == null) throw new AccountNotFoundException("Account not found: " + accountNo);
            if (!"ACTIVE".equals(account.getStatus()))
                throw new InvalidTransactionException("Account is not active: " + accountNo);

            boolean updated = accountDAO.depositAtomic(con, accountNo, amount);
            if (!updated) throw new InvalidTransactionException("Deposit failed for account: " + accountNo);

            BigDecimal newBalance = account.getBalance().add(amount);
            String txnId = transactionDAO.generateNextId(con);
            Transaction txn = new Transaction(txnId, null, accountNo, amount, "DEPOSIT", newBalance, remarks);
            transactionDAO.save(con, txn);

            con.commit();
            return true;
        } catch (Exception e) {
            rollbackQuietly(con);
            throw e;
        } finally {
            closeQuietly(con);
        }
    }

    /**
     * WITHDRAW workflow: check balance -> update balance -> insert transaction -> commit.
     * If balance is insufficient the atomic UPDATE affects 0 rows, we detect that
     * and roll back with a clear exception instead of ever going negative.
     */
    @Override
    public boolean withdraw(String accountNo, BigDecimal amount, String remarks) throws Exception {
        validateAmount(amount);
        Connection con = null;
        try {
            con = DBConnection.getInstance().getConnection();
            con.setAutoCommit(false);

            Account account = accountDAO.findByAccountNoForUpdate(con, accountNo);
            if (account == null) throw new AccountNotFoundException("Account not found: " + accountNo);
            if (!"ACTIVE".equals(account.getStatus()))
                throw new InvalidTransactionException("Account is not active: " + accountNo);
            if (account.getBalance().compareTo(amount) < 0)
                throw new InsufficientBalanceException("Insufficient balance in account: " + accountNo);

            boolean updated = accountDAO.withdrawAtomic(con, accountNo, amount);
            if (!updated) throw new InsufficientBalanceException("Insufficient balance in account: " + accountNo);

            BigDecimal newBalance = account.getBalance().subtract(amount);
            String txnId = transactionDAO.generateNextId(con);
            Transaction txn = new Transaction(txnId, accountNo, null, amount, "WITHDRAW", newBalance, remarks);
            transactionDAO.save(con, txn);

            con.commit();
            return true;
        } catch (Exception e) {
            rollbackQuietly(con);
            throw e;
        } finally {
            closeQuietly(con);
        }
    }

    /**
     * TRANSFER workflow (the star module): withdraw from sender, deposit to receiver,
     * save transaction history, commit. Any failure at any step rolls EVERYTHING back,
     * so money can never vanish or duplicate between the two accounts.
     *
     * Deadlock note: when two transfers run concurrently in opposite directions
     * (A->B and B->A), locking rows in a fixed order (lexicographically by account
     * number) prevents a circular-wait deadlock between the two transactions.
     */
    @Override
    public boolean transfer(TransferRequestDTO request) throws Exception {
        String from = request.getFromAccount();
        String to = request.getToAccount();
        BigDecimal amount = request.getAmount();
        validateAmount(amount);

        if (from == null || to == null || from.equals(to))
            throw new InvalidTransactionException("Sender and receiver accounts must be different, valid accounts");

        // Lock in a consistent order to avoid deadlocks between concurrent opposite-direction transfers
        String first = from.compareTo(to) <= 0 ? from : to;
        String second = from.compareTo(to) <= 0 ? to : from;

        Connection con = null;
        try {
            con = DBConnection.getInstance().getConnection();
            con.setAutoCommit(false);

            Account firstAcc = accountDAO.findByAccountNoForUpdate(con, first);
            Account secondAcc = accountDAO.findByAccountNoForUpdate(con, second);

            Account fromAccount = from.equals(first) ? firstAcc : secondAcc;
            Account toAccount = to.equals(first) ? firstAcc : secondAcc;

            if (fromAccount == null) throw new AccountNotFoundException("Sender account not found: " + from);
            if (toAccount == null) throw new AccountNotFoundException("Receiver account not found: " + to);
            if (!"ACTIVE".equals(fromAccount.getStatus()))
                throw new InvalidTransactionException("Sender account is not active: " + from);
            if (!"ACTIVE".equals(toAccount.getStatus()))
                throw new InvalidTransactionException("Receiver account is not active: " + to);
            if (fromAccount.getBalance().compareTo(amount) < 0)
                throw new InsufficientBalanceException("Insufficient balance in account: " + from);

            // Step 1: withdraw from sender
            boolean withdrawn = accountDAO.withdrawAtomic(con, from, amount);
            if (!withdrawn) throw new InsufficientBalanceException("Insufficient balance in account: " + from);

            // Step 2: deposit to receiver
            boolean deposited = accountDAO.depositAtomic(con, to, amount);
            if (!deposited) throw new InvalidTransactionException("Deposit failed for account: " + to);

            // Step 3: save one transaction row representing the transfer
            BigDecimal senderBalanceAfter = fromAccount.getBalance().subtract(amount);
            String txnId = transactionDAO.generateNextId(con);
            Transaction txn = new Transaction(txnId, from, to, amount, "TRANSFER",
                    senderBalanceAfter, request.getRemarks());
            transactionDAO.save(con, txn);

            // Step 4: commit - if we got here, both balance changes and the history row are durable together
            con.commit();
            return true;
        } catch (Exception e) {
            // Any failure above (insufficient balance, closed account, DB error) rolls back
            // BOTH balance changes so no money is ever lost or duplicated.
            rollbackQuietly(con);
            throw e;
        } finally {
            closeQuietly(con);
        }
    }

    @Override
    public List<Transaction> getTransactionHistory(String accountNo) throws Exception {
        try (Connection con = DBConnection.getInstance().getConnection()) {
            return transactionDAO.findByAccountNo(con, accountNo);
        }
    }

    private void validateAmount(BigDecimal amount) throws InvalidTransactionException {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new InvalidTransactionException("Amount must be greater than zero");
    }

    private void rollbackQuietly(Connection con) {
        if (con != null) {
            try {
                con.rollback();
            } catch (SQLException ignored) {
                // logging only - we're already handling the original exception
            }
        }
    }

    private void closeQuietly(Connection con) {
        if (con != null) {
            try {
                con.setAutoCommit(true);
                con.close();
            } catch (SQLException ignored) {
            }
        }
    }
}
