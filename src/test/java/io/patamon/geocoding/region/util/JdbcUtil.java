package io.patamon.geocoding.region.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JdbcUtil {

    private static final String driver_class = "com.mysql.cj.jdbc.Driver"; // com.mysql.jdbc.Driver

    private static final String db_url = "jdbc:mysql://localhost:3306/cnarea";

    private static final String db_userid = "root";

    private static final String db_password = "12345678";

    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName(driver_class);
        } catch (ClassNotFoundException cnfe) {
            OutUtil.err("Exception: JdbcUtil.getConnection driver_class not found");
            return null;
        }
        try {
            conn = DriverManager.getConnection(db_url, db_userid, db_password);
        } catch (SQLException sqle) {
            OutUtil.err("Exception: JdbcUtil.getConnection get connection failed");
            return null;
        }
        return conn;
    }
    
    public static void free(ResultSet rs, Statement stmt) {
        free(stmt);
        free(rs);
    }

    public static void free(ResultSet rs) {
        if (rs == null) return;
        try {
            rs.close();
        } catch (SQLException sqle) {}
    }

    public static void free(Statement stmt) {
        if (stmt == null) return;
        try {
            stmt.close();
        } catch (SQLException sqle) {}
    }

    public static void free(Connection conn) {
        if (conn == null) return;
        try {
            conn.close();
        } catch (SQLException sqle) {}
    }
}