import javax.swing.*;
import java.sql.*;

public class LoginForm extends JFrame {

    JTextField txtUsername;
    JPasswordField txtPassword;
    JButton btnLogin, btnRegister;

    public LoginForm() {
        setTitle("User Login");
        setSize(400, 250);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel lblUser = new JLabel("Username:");
        lblUser.setBounds(50, 50, 100, 30);
        add(lblUser);

        txtUsername = new JTextField();
        txtUsername.setBounds(150, 50, 180, 30);
        add(txtUsername);

        JLabel lblPass = new JLabel("Password:");
        lblPass.setBounds(50, 100, 100, 30);
        add(lblPass);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(150, 100, 180, 30);
        add(txtPassword);

        btnLogin = new JButton("Login");
        btnLogin.setBounds(70, 160, 100, 30);
        add(btnLogin);

        btnRegister = new JButton("Register");
        btnRegister.setBounds(220, 160, 100, 30);
        add(btnRegister);

        btnLogin.addActionListener(e -> loginUser());
        btnRegister.addActionListener(e -> {
            dispose();
            new RegisterForm();
        });

        setVisible(true);
    }

    void loginUser() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {

            // Hash the entered password before checking
            String hashed = PasswordUtil.hashPassword(password);

            // Use correct column names for your table
            String sql = "SELECT USER_ID, USERNAME, ROLE FROM USERS WHERE USERNAME = ? AND PASSWORD = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, username);
            pst.setString(2, hashed);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("USER_ID");
                String role = rs.getString("ROLE");

                JOptionPane.showMessageDialog(this, "Login Successful as " + role.toUpperCase() + "!");
                dispose();

                if (role != null && role.equalsIgnoreCase("admin")) {
                    new AdminDashboard();
                } else {
                    new CustomerDashboard(userId, username);
                }

            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password!");
            }

        } catch (SQLException sqle) {
            JOptionPane.showMessageDialog(this, "SQL Error: " + sqle.getMessage());
            sqle.printStackTrace();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new LoginForm();
    }
}
