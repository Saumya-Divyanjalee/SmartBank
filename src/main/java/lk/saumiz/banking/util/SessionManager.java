package lk.saumiz.banking.util;

import lk.saumiz.banking.entity.User;

/** Holds the currently logged-in user for the lifetime of the running application. */
public class SessionManager {

    private static SessionManager instance;
    private User loggedInUser;

    private SessionManager() {}

    public static synchronized SessionManager getInstance() {
        if (instance == null) instance = new SessionManager();
        return instance;
    }

    public User getLoggedInUser() { return loggedInUser; }
    public void setLoggedInUser(User user) { this.loggedInUser = user; }
    public void clear() { this.loggedInUser = null; }
    public boolean isAdmin() {
        return loggedInUser != null && "ADMIN".equals(loggedInUser.getRole());
    }
}
