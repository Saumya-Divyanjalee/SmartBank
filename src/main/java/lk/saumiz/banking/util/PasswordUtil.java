package lk.saumiz.banking.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    private PasswordUtil() {}

    /** Hash a plain-text password before storing it in the DB. */
    public static String hash(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    }

    /** Verify a login attempt's plain password against the stored hash. */
    public static boolean verify(String plainPassword, String storedHash) {
        if (plainPassword == null || storedHash == null) return false;
        try {
            return BCrypt.checkpw(plainPassword, storedHash);
        } catch (IllegalArgumentException e) {
            // stored value wasn't a valid bcrypt hash (e.g. placeholder text)
            return false;
        }
    }
}
