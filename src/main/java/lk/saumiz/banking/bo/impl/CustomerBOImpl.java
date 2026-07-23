package lk.saumiz.banking.bo.impl;

import lk.saumiz.banking.bo.CustomerBO;
import lk.saumiz.banking.dao.CustomerDAO;
import lk.saumiz.banking.dao.custom.DAOFactory;
import lk.saumiz.banking.db.DBConnection;
import lk.saumiz.banking.dto.CustomerDTO;
import lk.saumiz.banking.entity.Customer;

import java.sql.Connection;
import java.util.List;

public class CustomerBOImpl implements CustomerBO {

    private final CustomerDAO customerDAO = DAOFactory.getInstance().getCustomerDAO();

    @Override
    public String createCustomer(CustomerDTO dto) throws Exception {
        validate(dto);
        try (Connection con = DBConnection.getInstance().getConnection()) {
            String newId = customerDAO.generateNextId(con);
            Customer customer = new Customer(newId, dto.getName(), dto.getNic(),
                    dto.getPhone(), dto.getAddress(), dto.getEmail());
            boolean saved = customerDAO.save(con, customer);
            return saved ? newId : null;
        }
    }

    @Override
    public boolean updateCustomer(CustomerDTO dto) throws Exception {
        validate(dto);
        if (dto.getCustomerId() == null || dto.getCustomerId().isBlank()) {
            throw new IllegalArgumentException("Customer ID is required for update");
        }
        try (Connection con = DBConnection.getInstance().getConnection()) {
            Customer customer = new Customer(dto.getCustomerId(), dto.getName(), dto.getNic(),
                    dto.getPhone(), dto.getAddress(), dto.getEmail());
            return customerDAO.update(con, customer);
        }
    }

    @Override
    public boolean deleteCustomer(String customerId) throws Exception {
        try (Connection con = DBConnection.getInstance().getConnection()) {
            return customerDAO.delete(con, customerId);
        }
    }

    @Override
    public Customer getCustomer(String customerId) throws Exception {
        try (Connection con = DBConnection.getInstance().getConnection()) {
            return customerDAO.findById(con, customerId);
        }
    }

    @Override
    public List<Customer> getAllCustomers() throws Exception {
        try (Connection con = DBConnection.getInstance().getConnection()) {
            return customerDAO.findAll(con);
        }
    }

    @Override
    public List<Customer> searchCustomers(String keyword) throws Exception {
        try (Connection con = DBConnection.getInstance().getConnection()) {
            return customerDAO.search(con, keyword);
        }
    }

    private void validate(CustomerDTO dto) {
        if (dto.getName() == null || dto.getName().isBlank())
            throw new IllegalArgumentException("Name is required");
        if (dto.getNic() == null || dto.getNic().isBlank())
            throw new IllegalArgumentException("NIC is required");
        if (dto.getPhone() == null || dto.getPhone().isBlank())
            throw new IllegalArgumentException("Phone is required");
    }
}
