import javax.swing.*;
import java.sql.*;

public class CustomerDashboard extends JFrame {

    int userId;
    String username;
    int accountNumber;

    JButton btnBalance, btnDeposit, btnWithdraw, btnTransfer, btnTransactions;

    public CustomerDashboard(int userId, String username) {
        this.userId = userId;
        this.username = username;

        System.out.println("DEBUG: CustomerDashboard opened for userId = " + userId);

        setTitle("Customer Dashboard - " + username);
        setSize(500, 400);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        btnBalance = new JButton("Balance Enquiry");
        btnBalance.setBounds(150, 50, 200, 40);
        add(btnBalance);

        btnDeposit = new JButton("Deposit");
        btnDeposit.setBounds(150, 110, 200, 40);
        add(btnDeposit);

        btnWithdraw = new JButton("Withdraw");
        btnWithdraw.setBounds(150, 170, 200, 40);
        add(btnWithdraw);

        btnTransfer = new JButton("Transfer Money");
        btnTransfer.setBounds(150, 230, 200, 40);
        add(btnTransfer);

        btnTransactions = new JButton("View Transactions");
        btnTransactions.setBounds(150, 290, 200, 40);
        add(btnTransactions);

        // Disable buttons until account is verified
        setButtonsEnabled(false);

        // Add actions
        btnBalance.addActionListener(e -> checkBalance());
        btnDeposit.addActionListener(e -> depositMoney());
        btnWithdraw.addActionListener(e -> withdrawMoney());
        btnTransfer.addActionListener(e -> new TransferMoney(accountNumber));
        btnTransactions.addActionListener(e -> new ViewTransactions(accountNumber));

        // Load account details
        findAccount();

        setVisible(true);
    }

    /** Enable or disable all transaction buttons */
    void setButtonsEnabled(boolean enabled) {
        btnBalance.setEnabled(enabled);
        btnDeposit.setEnabled(enabled);
        btnWithdraw.setEnabled(enabled);
        btnTransfer.setEnabled(enabled);
        btnTransactions.setEnabled(enabled);
    }

    /** Find account by user_id */
    void findAccount() {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT ACC_NO FROM ACCOUNTS WHERE USER_ID = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                accountNumber = rs.getInt("ACC_NO");
                System.out.println("DEBUG: Account found for userId = " + userId + ", acc_no = " + accountNumber);
                setButtonsEnabled(true);
            } else {
                JOptionPane.showMessageDialog(this,
                    "⚠️ No bank account found for user ID: " + userId + 
                    ". Please contact admin to create one.");
                setButtonsEnabled(false);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error finding account: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /** Balance Enquiry */
    void checkBalance() {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT BALANCE FROM ACCOUNTS WHERE ACC_NO = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, accountNumber);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                double balance = rs.getDouble("BALANCE");
                JOptionPane.showMessageDialog(this, "💰 Current Balance: ₹" + balance);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error checking balance: " + e.getMessage());
        }
    }

    /** Deposit Money */
    void depositMoney() {
        String amt = JOptionPane.showInputDialog(this, "Enter amount to deposit:");
        if (amt != null && !amt.isEmpty()) {
            double amount = Double.parseDouble(amt);
            try (Connection con = DBConnection.getConnection()) {
                String sql = "UPDATE ACCOUNTS SET BALANCE = BALANCE + ? WHERE ACC_NO = ?";
                PreparedStatement pst = con.prepareStatement(sql);
                pst.setDouble(1, amount);
                pst.setInt(2, accountNumber);
                int updated = pst.executeUpdate();

                if (updated > 0)
                    JOptionPane.showMessageDialog(this, " ₹" + amount + " deposited successfully!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error during deposit: " + e.getMessage());
            }
        }
    }

    /** Withdraw Money */
    void withdrawMoney() {
        String amt = JOptionPane.showInputDialog(this, "Enter amount to withdraw:");
        if (amt != null && !amt.isEmpty()) {
            double amount = Double.parseDouble(amt);
            try (Connection con = DBConnection.getConnection()) {
                // Check balance
                String checkSql = "SELECT BALANCE FROM ACCOUNTS WHERE ACC_NO = ?";
                PreparedStatement check = con.prepareStatement(checkSql);
                check.setInt(1, accountNumber);
                ResultSet rs = check.executeQuery();

                if (rs.next()) {
                    double balance = rs.getDouble("BALANCE");
                    if (balance < amount) {
                        JOptionPane.showMessageDialog(this, " Insufficient balance!");
                        return;
                    }
                }

                // Deduct
                String sql = "UPDATE ACCOUNTS SET BALANCE = BALANCE - ? WHERE ACC_NO = ?";
                PreparedStatement pst = con.prepareStatement(sql);
                pst.setDouble(1, amount);
                pst.setInt(2, accountNumber);
                pst.executeUpdate();

                JOptionPane.showMessageDialog(this, " ₹" + amount + " withdrawn successfully!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error during withdrawal: " + e.getMessage());
            }
        }
    }
}
