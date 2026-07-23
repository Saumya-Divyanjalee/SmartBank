package lk.saumiz.banking.bo;

import lk.saumiz.banking.entity.User;

public interface UserBO {
    /** Returns the authenticated User if credentials + active status are valid, else null. */
    User login(String username, String plainPassword) throws Exception;
}
