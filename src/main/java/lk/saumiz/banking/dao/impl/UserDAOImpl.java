package lk.saumiz.banking.dao.impl;

import lk.saumiz.banking.dao.UserDAO;
import lk.saumiz.banking.entity.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class UserDAOImpl implements UserDAO {

    @Override
    public User findByUsername(Connection con, String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                User u = new User();
                u.setUserId(rs.getString("user_id"));
                u.setUsername(rs.getString("username"));
                u.setPassword(rs.getString("password"));
                u.setRole(rs.getString("role"));
                u.setStatus(rs.getString("status"));
                Timestamp ts = rs.getTimestamp("created_date");
                if (ts != null) u.setCreatedDate(ts.toLocalDateTime());
                return u;
            }
        }
    }
}
