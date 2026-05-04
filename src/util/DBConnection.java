package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:mysql://192.168.1.233:3306/qly_nhansu",
                "duc123",
                "123456a@"
        );
    }
}
