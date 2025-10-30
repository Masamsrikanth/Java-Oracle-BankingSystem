import javax.swing.*;
import java.sql.*;

public class TransferMoney extends JFrame {

    int fromAcc;

    public TransferMoney(int fromAcc) {
        this.fromAcc = fromAcc;

        setTitle("Transfer Money");
        setSize(400, 250);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel lblTo = new JLabel("To Account No:");
        lblTo.setBounds(40, 30, 140, 30);
        add(lblTo);

        JTextField txtTo = new JTextField();
        txtTo.setBounds(160, 30, 150, 30);
        add(txtTo);

        JLabel lblAmount = new JLabel("Amount:");
        lblAmount.setBounds(40, 80, 140, 30);
        add(lblAmount);

        JTextField txtAmount = new JTextField();
        txtAmount.setBounds(160, 80, 150, 30);
        add(txtAmount);

        JButton btnSend = new JButton("Send");
        btnSend.setBounds(140, 140, 100, 40);
        add(btnSend);

        btnSend.addActionListener(e -> {
            int toAcc = Integer.parseInt(txtTo.getText());
            double amount = Double.parseDouble(txtAmount.getText());
            transferMoney(fromAcc, toAcc, amount);
        });

        setVisible(true);
    }

    void transferMoney(int fromAcc, int toAcc, double amount) {
        try (Connection con = DBConnection.getConnection()) {

            // 1 Check balance
            PreparedStatement pst1 = con.prepareStatement(
                "SELECT balance FROM accounts WHERE acc_no=?"
            );
            pst1.setInt(1, fromAcc);
            ResultSet rs = pst1.executeQuery();

            if (!rs.next() || rs.getDouble(1) < amount) {
                JOptionPane.showMessageDialog(this,
                    "⚠️ Insufficient Balance!");
                return;
            }

            con.setAutoCommit(false);

            //  Deduct from sender
            PreparedStatement pst2 = con.prepareStatement(
                "UPDATE accounts SET balance = balance - ? WHERE acc_no=?"
            );
            pst2.setDouble(1, amount);
            pst2.setInt(2, fromAcc);
            pst2.executeUpdate();

            //  Add to receiver
            PreparedStatement pst3 = con.prepareStatement(
                "UPDATE accounts SET balance = balance + ? WHERE acc_no=?"
            );
            pst3.setDouble(1, amount);
            pst3.setInt(2, toAcc);
            pst3.executeUpdate();

            //  Insert transaction record
            PreparedStatement pst4 = con.prepareStatement(
                "INSERT INTO transactions(from_acc, to_acc, amount) VALUES (?, ?, ?)"
            );
            pst4.setInt(1, fromAcc);
            pst4.setInt(2, toAcc);
            pst4.setDouble(3, amount);
            pst4.executeUpdate();

            con.commit();
            con.setAutoCommit(true);

            JOptionPane.showMessageDialog(this,
                " Money Transferred Successfully!");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error: " + e.getMessage());
        }
    }
}
