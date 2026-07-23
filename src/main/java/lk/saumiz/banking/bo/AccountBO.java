package lk.saumiz.banking.bo;

import lk.saumiz.banking.dto.AccountDTO;
import lk.saumiz.banking.dto.TransferRequestDTO;
import lk.saumiz.banking.entity.Account;
import lk.saumiz.banking.entity.Transaction;

import java.math.BigDecimal;
import java.util.List;

public interface AccountBO {
    String createAccount(AccountDTO dto) throws Exception;
    Account getAccount(String accountNo) throws Exception;
    List<Account> getAccountsForCustomer(String customerId) throws Exception;
    List<Account> getAllAccounts() throws Exception;

    boolean deposit(String accountNo, BigDecimal amount, String remarks) throws Exception;
    boolean withdraw(String accountNo, BigDecimal amount, String remarks) throws Exception;

    /**
     * Transfers money between two accounts inside a single JDBC transaction.
     * On ANY failure (insufficient balance, closed account, DB error) the whole
     * operation rolls back and no partial state is left behind.
     */
    boolean transfer(TransferRequestDTO request) throws Exception;

    List<Transaction> getTransactionHistory(String accountNo) throws Exception;
}
