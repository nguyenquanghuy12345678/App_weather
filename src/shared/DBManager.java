package shared;

import java.sql.*;

/**
 * Simple SQLite database manager. Requires sqlite-jdbc on classpath.
 * Provides a single Connection instance (not pooled) for lightweight desktop use.
 */
public class DBManager {
    private static final String DB_FILE = "weather.db"; // stored in working directory
    private static final String JDBC_URL = "jdbc:sqlite:" + DB_FILE;
    private static Connection connection;

    /**
     * Get (and lazily create) a SQLite connection.
     */
    public static synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("org.sqlite.JDBC");
            } catch (ClassNotFoundException e) {
                throw new SQLException("SQLite JDBC driver not found. Add sqlite-jdbc JAR to classpath.", e);
            }
            connection = DriverManager.getConnection(JDBC_URL);
            initSchema(connection);
        }
        return connection;
    }

    private static void initSchema(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS search_history (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "location TEXT UNIQUE NOT NULL, " +
                    "latitude REAL NOT NULL, " +
                    "longitude REAL NOT NULL, " +
                    "last_access TIMESTAMP DEFAULT CURRENT_TIMESTAMP)"
            );
            st.executeUpdate("CREATE TABLE IF NOT EXISTS favorites (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "location TEXT UNIQUE NOT NULL, " +
                    "latitude REAL NOT NULL, " +
                    "longitude REAL NOT NULL, " +
                    "added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)"
            );
            st.executeUpdate("CREATE TABLE IF NOT EXISTS community_reports (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "location TEXT NOT NULL, " +
                    "accuracy INTEGER NOT NULL, " +
                    "comment TEXT, " +
                    "username TEXT NOT NULL, " +
                    "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP)"
            );
        }
    }

    /** Close connection (optional on app exit). */
    public static synchronized void close() {
        if (connection != null) {
            try { connection.close(); } catch (SQLException ignored) {}
            connection = null;
        }
    }
}
