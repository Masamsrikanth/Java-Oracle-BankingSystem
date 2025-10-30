import javax.swing.*;
import java.sql.*;

public class ManageAccounts extends JFrame {

    JComboBox<String> userList;
    JButton btnCreate;
    JTextField txtBalance;

    public ManageAccounts() {
        setTitle("Manage Accounts");
        setSize(450, 300);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel lblUser = new JLabel("Select User:");
        lblUser.setBounds(30, 50, 150, 30);
        add(lblUser);

        userList = new JComboBox<>();
        userList.setBounds(150, 50, 200, 30);
        add(userList);

        JLabel lblBal = new JLabel("Initial Balance:");
        lblBal.setBounds(30, 100, 150, 30);
        add(lblBal);

        txtBalance = new JTextField("0");
        txtBalance.setBounds(150, 100, 200, 30);
        add(txtBalance);

        btnCreate = new JButton("Create Account");
        btnCreate.setBounds(150, 160, 200, 40);
        add(btnCreate);

        btnCreate.addActionListener(e -> createAccount());

        loadUsers();
        setVisible(true);
    }

    void loadUsers() {
        try(Connection con = DBConnection.getConnection()) {
            PreparedStatement pst = con.prepareStatement(
                "SELECT user_id, username FROM users WHERE role='CUSTOMER'"
            );
            ResultSet rs = pst.executeQuery();

            while(rs.next()) {
                userList.addItem(rs.getInt("user_id") + " - " + rs.getString("username"));
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    void createAccount() {
        String selected = (String) userList.getSelectedItem();
        if(selected == null) return;

        int userId = Integer.parseInt(selected.split(" - ")[0]);
        double balance = Double.parseDouble(txtBalance.getText());

        try(Connection con = DBConnection.getConnection()) {
            PreparedStatement pst = con.prepareStatement(
                "INSERT INTO accounts(user_id, balance) VALUES (?, ?)"
            );
            pst.setInt(1, userId);
            pst.setDouble(2, balance);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, " Account Created Successfully!");

        } catch(Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}
