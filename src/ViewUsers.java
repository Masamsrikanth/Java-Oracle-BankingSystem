import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class ViewUsers extends JFrame {

    JTable table;
    DefaultTableModel model;

    public ViewUsers() {
        setTitle("Users List");
        setSize(600, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new java.awt.BorderLayout());

        model = new DefaultTableModel();
        table = new JTable(model);

        model.addColumn("User ID");
        model.addColumn("Username");
        model.addColumn("Role");

        fetchUsers();

        add(new JScrollPane(table), java.awt.BorderLayout.CENTER);

        setVisible(true);
    }

    void fetchUsers() {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT user_id, username, role FROM users ORDER BY user_id";
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            model.setRowCount(0);

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("role")
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
}
