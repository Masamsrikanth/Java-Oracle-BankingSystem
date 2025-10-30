import java.sql.*;

public class DBConnectionTest {
    public static void main(String[] args) {
        try {
            Class.forName("oracle.jdbc.OracleDriver");

            Connection con = DriverManager.getConnection(
                "jdbc:oracle:thin:@localhost:1521/orcl",
                "sbms",
                "sbms123"
            );

            System.out.println(" Database Connected Successfully!");
            con.close();

        } catch (Exception e) {
            System.out.println(" Connection Failed!");
            System.out.println(e);
        }
    }
}
