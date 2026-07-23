package lk.saumiz.banking.dao.custom;

import lk.saumiz.banking.dao.AccountDAO;
import lk.saumiz.banking.dao.CustomerDAO;
import lk.saumiz.banking.dao.TransactionDAO;
import lk.saumiz.banking.dao.UserDAO;
import lk.saumiz.banking.dao.impl.AccountDAOImpl;
import lk.saumiz.banking.dao.impl.CustomerDAOImpl;
import lk.saumiz.banking.dao.impl.TransactionDAOImpl;
import lk.saumiz.banking.dao.impl.UserDAOImpl;

/**
 * Factory pattern: hands out DAO instances so the rest of the app depends only
 * on the DAO interfaces, never the concrete impl classes. Singleton itself,
 * since there's no reason to have more than one factory.
 */
public class DAOFactory {

    public enum DAOType { USER, CUSTOMER, ACCOUNT, TRANSACTION }

    private static final DAOFactory INSTANCE = new DAOFactory();

    private DAOFactory() {}

    public static DAOFactory getInstance() {
        return INSTANCE;
    }

    public UserDAO getUserDAO() {
        return new UserDAOImpl();
    }

    public CustomerDAO getCustomerDAO() {
        return new CustomerDAOImpl();
    }

    public AccountDAO getAccountDAO() {
        return new AccountDAOImpl();
    }

    public TransactionDAO getTransactionDAO() {
        return new TransactionDAOImpl();
    }
}
