package lk.saumiz.banking.bo.impl;

import lk.saumiz.banking.bo.UserBO;
import lk.saumiz.banking.dao.UserDAO;
import lk.saumiz.banking.dao.custom.DAOFactory;
import lk.saumiz.banking.db.DBConnection;
import lk.saumiz.banking.entity.User;
import lk.saumiz.banking.util.PasswordUtil;

import java.sql.Connection;

public class UserBOImpl implements UserBO {

    private final UserDAO userDAO = DAOFactory.getInstance().getUserDAO();

    @Override
    public User login(String username, String plainPassword) throws Exception {
        try (Connection con = DBConnection.getInstance().getConnection()) {
            User user = userDAO.findByUsername(con, username);
            if (user == null) return null;
            if (!"ACTIVE".equals(user.getStatus())) return null;
            if (!PasswordUtil.verify(plainPassword, user.getPassword())) return null;
            return user;
        }
    }
}
