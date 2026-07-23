package lk.saumiz.banking.dao;

import lk.saumiz.banking.entity.Customer;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface CustomerDAO {
    boolean save(Connection con, Customer customer) throws SQLException;
    boolean update(Connection con, Customer customer) throws SQLException;
    boolean delete(Connection con, String customerId) throws SQLException;
    Customer findById(Connection con, String customerId) throws SQLException;
    List<Customer> findAll(Connection con) throws SQLException;
    List<Customer> search(Connection con, String keyword) throws SQLException;
    String generateNextId(Connection con) throws SQLException;
    int countAll(Connection con) throws SQLException;
}
