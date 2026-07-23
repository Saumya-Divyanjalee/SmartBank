package lk.saumiz.banking.dao;

import lk.saumiz.banking.entity.Account;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface AccountDAO {
    boolean save(Connection con, Account account) throws SQLException;
    boolean update(Connection con, Account account) throws SQLException;
    boolean delete(Connection con, String accountNo) throws SQLException;
    Account findByAccountNo(Connection con, String accountNo) throws SQLException;

    /** Locks the row (SELECT ... FOR UPDATE) so concurrent transfers can't race on the same account. */
    Account findByAccountNoForUpdate(Connection con, String accountNo) throws SQLException;

    List<Account> findByCustomerId(Connection con, String customerId) throws SQLException;
    List<Account> findAll(Connection con) throws SQLException;

    /** Atomic balance update: WHERE clause checks balance >= ? to prevent overdraw races. */
    boolean withdrawAtomic(Connection con, String accountNo, BigDecimal amount) throws SQLException;
    boolean depositAtomic(Connection con, String accountNo, BigDecimal amount) throws SQLException;

    String generateNextAccountNo(Connection con) throws SQLException;

    // Dashboard aggregates
    int countAllAccounts(Connection con) throws SQLException;
    BigDecimal getTotalBankBalance(Connection con) throws SQLException;
}
