import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class RegisterForm extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnRegister, btnLogin;

    public RegisterForm() {
        setTitle("Register - SOUMS");
        setSize(400, 300);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel lblTitle = new JLabel("User Registration", SwingConstants.CENTER);
        lblTitle.setBounds(50, 20, 300, 30);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        add(lblTitle);

        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setBounds(50, 80, 100, 25);
        add(lblUsername);

        txtUsername = new JTextField();
        txtUsername.setBounds(150, 80, 180, 25);
        add(txtUsername);

        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setBounds(50, 120, 100, 25);
        add(lblPassword);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(150, 120, 180, 25);
        add(txtPassword);

        btnRegister = new JButton("Register");
        btnRegister.setBounds(80, 180, 100, 30);
        add(btnRegister);

        btnLogin = new JButton("Back to Login");
        btnLogin.setBounds(200, 180, 130, 30);
        add(btnLogin);

        // Register button action
        btnRegister.addActionListener(e -> registerUser());

        // Go back to login
        btnLogin.addActionListener(e -> {
            dispose();
            new LoginForm();
        });

        setVisible(true);
    }

    private void registerUser() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password!");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            //  1. Hash password before saving
            String hashed = PasswordUtil.hashPassword(password);

            //  2. Insert into users table
            String sql = "INSERT INTO users (username, password, role, status) VALUES (?, ?, 'customer', 'ACTIVE')";
            PreparedStatement pst = con.prepareStatement(sql, new String[]{"user_id"});
            pst.setString(1, username);
            pst.setString(2, hashed);
            pst.executeUpdate();

            //  3. Get the user_id of the newly inserted user
            ResultSet rs = pst.getGeneratedKeys();
            int userId = -1;
            if (rs.next()) {
                userId = rs.getInt(1);
            }

            //  4. Create an account for this user
            if (userId != -1) {
                PreparedStatement pst2 = con.prepareStatement(
                    "INSERT INTO accounts (acc_no, user_id, balance, status) VALUES (account_seq.NEXTVAL, ?, 0, 'ACTIVE')"
                );
                pst2.setInt(1, userId);
                pst2.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Registration successful! Account created automatically.");
            dispose();
            new LoginForm();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new RegisterForm();
    }
}
