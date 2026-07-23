package lk.saumiz.banking.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Generates the next sequential ID for a table, e.g. C001 -> C002, ACC0001 -> ACC0002.
 * Looks at the MAX existing id with the given prefix and increments the numeric suffix.
 */
public class IdGenerator {

    private IdGenerator() {}

    public static String generate(Connection con, String table, String idColumn,
                                   String prefix, int numDigits) throws SQLException {
        String sql = "SELECT " + idColumn + " FROM " + table +
                " WHERE " + idColumn + " LIKE ? ORDER BY " + idColumn + " DESC LIMIT 1";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, prefix + "%");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String lastId = rs.getString(1);
                    String numberPart = lastId.substring(prefix.length());
                    int next = Integer.parseInt(numberPart) + 1;
                    return prefix + String.format("%0" + numDigits + "d", next);
                } else {
                    return prefix + String.format("%0" + numDigits + "d", 1);
                }
            }
        }
    }
}
