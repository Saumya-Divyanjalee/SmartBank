package lk.saumiz.banking.bo.impl;

import lk.saumiz.banking.bo.DashboardBO;
import lk.saumiz.banking.dao.AccountDAO;
import lk.saumiz.banking.dao.CustomerDAO;
import lk.saumiz.banking.dao.TransactionDAO;
import lk.saumiz.banking.dao.custom.DAOFactory;
import lk.saumiz.banking.db.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;

public class DashboardBOImpl implements DashboardBO {

    private final CustomerDAO customerDAO = DAOFactory.getInstance().getCustomerDAO();
    private final AccountDAO accountDAO = DAOFactory.getInstance().getAccountDAO();
    private final TransactionDAO transactionDAO = DAOFactory.getInstance().getTransactionDAO();

    @Override
    public int getTotalCustomers() throws Exception {
        try (Connection con = DBConnection.getInstance().getConnection()) {
            return customerDAO.countAll(con);
        }
    }

    @Override
    public int getTotalAccounts() throws Exception {
        try (Connection con = DBConnection.getInstance().getConnection()) {
            return accountDAO.countAllAccounts(con);
        }
    }

    @Override
    public BigDecimal getTotalBankBalance() throws Exception {
        try (Connection con = DBConnection.getInstance().getConnection()) {
            return accountDAO.getTotalBankBalance(con);
        }
    }

    @Override
    public BigDecimal getTodayDeposits() throws Exception {
        try (Connection con = DBConnection.getInstance().getConnection()) {
            return transactionDAO.sumTodayByType(con, "DEPOSIT");
        }
    }

    @Override
    public BigDecimal getTodayWithdrawals() throws Exception {
        try (Connection con = DBConnection.getInstance().getConnection()) {
            return transactionDAO.sumTodayByType(con, "WITHDRAW");
        }
    }

    @Override
    public BigDecimal getTodayTransfers() throws Exception {
        try (Connection con = DBConnection.getInstance().getConnection()) {
            return transactionDAO.sumTodayByType(con, "TRANSFER");
        }
    }
}
