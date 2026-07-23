package lk.saumiz.banking.dao.impl;

import lk.saumiz.banking.dao.CustomerDAO;
import lk.saumiz.banking.entity.Customer;
import lk.saumiz.banking.util.IdGenerator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAOImpl implements CustomerDAO {

    @Override
    public boolean save(Connection con, Customer c) throws SQLException {
        String sql = "INSERT INTO customers (customer_id, name, nic, phone, address, email) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getCustomerId());
            ps.setString(2, c.getName());
            ps.setString(3, c.getNic());
            ps.setString(4, c.getPhone());
            ps.setString(5, c.getAddress());
            ps.setString(6, c.getEmail());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean update(Connection con, Customer c) throws SQLException {
        String sql = "UPDATE customers SET name=?, nic=?, phone=?, address=?, email=? WHERE customer_id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getName());
            ps.setString(2, c.getNic());
            ps.setString(3, c.getPhone());
            ps.setString(4, c.getAddress());
            ps.setString(5, c.getEmail());
            ps.setString(6, c.getCustomerId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(Connection con, String customerId) throws SQLException {
        String sql = "DELETE FROM customers WHERE customer_id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, customerId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Customer findById(Connection con, String customerId) throws SQLException {
        String sql = "SELECT * FROM customers WHERE customer_id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    @Override
    public List<Customer> findAll(Connection con) throws SQLException {
        String sql = "SELECT * FROM customers ORDER BY created_date DESC";
        List<Customer> list = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    @Override
    public List<Customer> search(Connection con, String keyword) throws SQLException {
        String sql = "SELECT * FROM customers WHERE name LIKE ? OR nic LIKE ? OR phone LIKE ? OR customer_id LIKE ?";
        List<Customer> list = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            String like = "%" + keyword + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            ps.setString(4, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    @Override
    public String generateNextId(Connection con) throws SQLException {
        return IdGenerator.generate(con, "customers", "customer_id", "C", 3);
    }

    @Override
    public int countAll(Connection con) throws SQLException {
        String sql = "SELECT COUNT(*) FROM customers";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private Customer map(ResultSet rs) throws SQLException {
        Customer c = new Customer();
        c.setCustomerId(rs.getString("customer_id"));
        c.setName(rs.getString("name"));
        c.setNic(rs.getString("nic"));
        c.setPhone(rs.getString("phone"));
        c.setAddress(rs.getString("address"));
        c.setEmail(rs.getString("email"));
        Timestamp ts = rs.getTimestamp("created_date");
        if (ts != null) c.setCreatedDate(ts.toLocalDateTime());
        return c;
    }
}
