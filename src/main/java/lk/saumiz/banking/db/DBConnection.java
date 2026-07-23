package lk.saumiz.banking.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Singleton database connection manager.
 * Uses HikariCP for pooling so DAO calls don't open/close a raw connection every time.
 */
public class DBConnection {

    private static DBConnection instance;
    private final HikariDataSource dataSource;

    // --- EDIT THESE TO MATCH YOUR LOCAL MYSQL SETUP ---
    private static final String URL = "jdbc:mysql://localhost:3306/smart_banking_db?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";

    private DBConnection() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(URL);
        config.setUsername(DB_USER);
        config.setPassword(DB_PASSWORD);
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setPoolName("SmartBankingPool");
        this.dataSource = new HikariDataSource(config);
    }

    public static synchronized DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
