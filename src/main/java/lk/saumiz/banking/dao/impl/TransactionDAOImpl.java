package lk.saumiz.banking.dao.impl;

import lk.saumiz.banking.dao.TransactionDAO;
import lk.saumiz.banking.entity.Transaction;
import lk.saumiz.banking.util.IdGenerator;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAOImpl implements TransactionDAO {

    @Override
    public boolean save(Connection con, Transaction t) throws SQLException {
        String sql = "INSERT INTO transactions " +
                "(transaction_id, from_account, to_account, amount, transaction_type, balance_after, remarks) " +
                "VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, t.getTransactionId());
            ps.setString(2, t.getFromAccount());
            ps.setString(3, t.getToAccount());
            ps.setBigDecimal(4, t.getAmount());
            ps.setString(5, t.getTransactionType());
            ps.setBigDecimal(6, t.getBalanceAfter());
            ps.setString(7, t.getRemarks());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public List<Transaction> findByAccountNo(Connection con, String accountNo) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE from_account=? OR to_account=? ORDER BY transaction_date DESC";
        List<Transaction> list = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, accountNo);
            ps.setString(2, accountNo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    @Override
    public List<Transaction> findByAccountAndDateRange(Connection con, String accountNo,
                                                         LocalDate from, LocalDate to) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE (from_account=? OR to_account=?) " +
                "AND transaction_date BETWEEN ? AND ? ORDER BY transaction_date";
        List<Transaction> list = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, accountNo);
            ps.setString(2, accountNo);
            ps.setTimestamp(3, Timestamp.valueOf(from.atStartOfDay()));
            ps.setTimestamp(4, Timestamp.valueOf(to.atTime(23, 59, 59)));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    @Override
    public String generateNextId(Connection con) throws SQLException {
        return IdGenerator.generate(con, "transactions", "transaction_id", "TXN", 6);
    }

    @Override
    public BigDecimal sumTodayByType(Connection con, String type) throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount),0) FROM transactions " +
                "WHERE transaction_type=? AND DATE(transaction_date) = CURDATE()";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, type);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getBigDecimal(1) : BigDecimal.ZERO;
            }
        }
    }

    private Transaction map(ResultSet rs) throws SQLException {
        Transaction t = new Transaction();
        t.setTransactionId(rs.getString("transaction_id"));
        t.setFromAccount(rs.getString("from_account"));
        t.setToAccount(rs.getString("to_account"));
        t.setAmount(rs.getBigDecimal("amount"));
        t.setTransactionType(rs.getString("transaction_type"));
        t.setBalanceAfter(rs.getBigDecimal("balance_after"));
        t.setRemarks(rs.getString("remarks"));
        Timestamp ts = rs.getTimestamp("transaction_date");
        if (ts != null) t.setTransactionDate(ts.toLocalDateTime());
        return t;
    }
}
