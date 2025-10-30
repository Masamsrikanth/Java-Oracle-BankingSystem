import javax.swing.*;
import java.awt.event.*;

public class AdminDashboard extends JFrame {

    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setSize(500, 400);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton btnUsers = new JButton("View All Users");
        btnUsers.setBounds(150, 80, 200, 40);
        add(btnUsers);

        JButton btnAccounts = new JButton("Manage Accounts");
        btnAccounts.setBounds(150, 140, 200, 40);
        add(btnAccounts);

        JButton btnLogout = new JButton("Logout");
        btnLogout.setBounds(150, 200, 200, 40);
        add(btnLogout);

        btnUsers.addActionListener(e -> new ViewUsers());
        btnAccounts.addActionListener(e -> new ManageAccounts());
        btnLogout.addActionListener(e -> {
            dispose();
            new LoginForm();
        });

        setVisible(true);
    }
}
