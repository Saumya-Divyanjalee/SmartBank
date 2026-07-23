package lk.saumiz.banking.dao;

import lk.saumiz.banking.entity.Transaction;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface TransactionDAO {
    boolean save(Connection con, Transaction transaction) throws SQLException;
    List<Transaction> findByAccountNo(Connection con, String accountNo) throws SQLException;
    List<Transaction> findByAccountAndDateRange(Connection con, String accountNo,
                                                 LocalDate from, LocalDate to) throws SQLException;
    String generateNextId(Connection con) throws SQLException;

    // Dashboard aggregates (today's activity)
    BigDecimal sumTodayByType(Connection con, String type) throws SQLException;
}
