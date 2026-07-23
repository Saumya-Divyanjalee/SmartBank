package lk.saumiz.banking.dao;

import lk.saumiz.banking.entity.User;
import java.sql.Connection;
import java.sql.SQLException;

public interface UserDAO {
    User findByUsername(Connection con, String username) throws SQLException;
}
