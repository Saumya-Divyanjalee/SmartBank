package lk.saumiz.banking.dao.impl;

import lk.saumiz.banking.dao.AccountDAO;
import lk.saumiz.banking.entity.Account;
import lk.saumiz.banking.util.IdGenerator;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDAOImpl implements AccountDAO {

    @Override
    public boolean save(Connection con, Account a) throws SQLException {
        String sql = "INSERT INTO accounts (account_no, customer_id, branch_id, account_type, balance, status) " +
                "VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, a.getAccountNo());
            ps.setString(2, a.getCustomerId());
            ps.setString(3, a.getBranchId());
            ps.setString(4, a.getAccountType());
            ps.setBigDecimal(5, a.getBalance());
            ps.setString(6, a.getStatus());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean update(Connection con, Account a) throws SQLException {
        String sql = "UPDATE accounts SET account_type=?, status=? WHERE account_no=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, a.getAccountType());
            ps.setString(2, a.getStatus());
            ps.setString(3, a.getAccountNo());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(Connection con, String accountNo) throws SQLException {
        String sql = "DELETE FROM accounts WHERE account_no=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, accountNo);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Account findByAccountNo(Connection con, String accountNo) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE account_no=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, accountNo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    /**
     * Locks the account row until the enclosing transaction commits/rolls back.
     * Must be called INSIDE a transaction (con.setAutoCommit(false)) — this is what
     * prevents two simultaneous transfers from both reading the same stale balance.
     */
    @Override
    public Account findByAccountNoForUpdate(Connection con, String accountNo) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE account_no=? FOR UPDATE";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, accountNo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    @Override
    public List<Account> findByCustomerId(Connection con, String customerId) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE customer_id=?";
        List<Account> list = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    @Override
    public List<Account> findAll(Connection con) throws SQLException {
        String sql = "SELECT * FROM accounts ORDER BY created_date DESC";
        List<Account> list = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    /**
     * Atomic, race-safe withdraw: the WHERE clause re-checks balance >= amount
     * in the SAME statement as the update, so even without row locking, MySQL's
     * row-level locking on the UPDATE prevents an overdraw. Returns false if the
     * balance was insufficient (0 rows affected) rather than throwing.
     */
    @Override
    public boolean withdrawAtomic(Connection con, String accountNo, BigDecimal amount) throws SQLException {
        String sql = "UPDATE accounts SET balance = balance - ?, version = version + 1 " +
                "WHERE account_no = ? AND balance >= ? AND status = 'ACTIVE'";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBigDecimal(1, amount);
            ps.setString(2, accountNo);
            ps.setBigDecimal(3, amount);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean depositAtomic(Connection con, String accountNo, BigDecimal amount) throws SQLException {
        String sql = "UPDATE accounts SET balance = balance + ?, version = version + 1 " +
                "WHERE account_no = ? AND status = 'ACTIVE'";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBigDecimal(1, amount);
            ps.setString(2, accountNo);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public String generateNextAccountNo(Connection con) throws SQLException {
        return IdGenerator.generate(con, "accounts", "account_no", "ACC", 4);
    }

    @Override
    public int countAllAccounts(Connection con) throws SQLException {
        String sql = "SELECT COUNT(*) FROM accounts";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    @Override
    public BigDecimal getTotalBankBalance(Connection con) throws SQLException {
        String sql = "SELECT COALESCE(SUM(balance),0) FROM accounts WHERE status != 'CLOSED'";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getBigDecimal(1) : BigDecimal.ZERO;
        }
    }

    private Account map(ResultSet rs) throws SQLException {
        Account a = new Account();
        a.setAccountNo(rs.getString("account_no"));
        a.setCustomerId(rs.getString("customer_id"));
        a.setBranchId(rs.getString("branch_id"));
        a.setAccountType(rs.getString("account_type"));
        a.setBalance(rs.getBigDecimal("balance"));
        a.setStatus(rs.getString("status"));
        a.setVersion(rs.getInt("version"));
        Timestamp ts = rs.getTimestamp("created_date");
        if (ts != null) a.setCreatedDate(ts.toLocalDateTime());
        return a;
    }
}
