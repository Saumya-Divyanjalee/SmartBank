package lk.saumiz.banking.util;

/**
 * Run this once to get a real BCrypt hash for your admin seed user.
 * Usage: mvn compile exec:java -Dexec.mainClass="lk.saumiz.banking.util.PasswordHashGenerator" -Dexec.args="admin123"
 * (Or just run it as a normal Java main from IntelliJ with a Program Argument.)
 */
public class PasswordHashGenerator {
    public static void main(String[] args) {
        String plainPassword = (args.length > 0) ? args[0] : "admin123";
        String hash = PasswordUtil.hash(plainPassword);
        System.out.println("Plain password : " + plainPassword);
        System.out.println("BCrypt hash    : " + hash);
        System.out.println("\nPaste the hash above into schema.sql, replacing <PASTE_BCRYPT_HASH_HERE>");
    }
}
