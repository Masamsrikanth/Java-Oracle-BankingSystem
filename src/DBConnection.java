import java.sql.*;

public class DBConnection {
    private static final String URL = "jdbc:oracle:thin:@localhost:1521/orcl";
    private static final String USER = "sbms";
    private static final String PASS = "sbms123";

    public static Connection getConnection() throws Exception {
        Class.forName("oracle.jdbc.OracleDriver");
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
