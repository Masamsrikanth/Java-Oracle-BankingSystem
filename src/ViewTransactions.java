import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class ViewTransactions extends JFrame {

    JTable table;
    DefaultTableModel model;
    int accountNumber;
    JComboBox<String> filterBox;

    public ViewTransactions(int accountNumber) {
        this.accountNumber = accountNumber;

        setTitle("Transaction History - Account " + accountNumber);
        setSize(700, 450);
        setLayout(new java.awt.BorderLayout());
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel topPanel = new JPanel();
        JLabel lbl = new JLabel("Filter:");
        filterBox = new JComboBox<>(new String[]{"All", "Sent", "Received"});
        topPanel.add(lbl);
        topPanel.add(filterBox);

        JButton btnRefresh = new JButton("Apply");
        topPanel.add(btnRefresh);
        add(topPanel, java.awt.BorderLayout.NORTH);

        model = new DefaultTableModel();
        table = new JTable(model);
        model.addColumn("Transaction ID");
        model.addColumn("From Account");
        model.addColumn("To Account");
        model.addColumn("Amount (₹)");
        model.addColumn("Date & Time");

        fetchTransactions("All");

        JScrollPane scroll = new JScrollPane(table);
        add(scroll, java.awt.BorderLayout.CENTER);

        btnRefresh.addActionListener(e -> {
            String type = (String) filterBox.getSelectedItem();
            fetchTransactions(type);
        });

        setVisible(true);
    }

    void fetchTransactions(String type) {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT trans_id, from_acc, to_acc, amount, trans_date FROM transactions ";
            if (type.equals("Sent")) {
                sql += "WHERE from_acc=? ";
            } else if (type.equals("Received")) {
                sql += "WHERE to_acc=? ";
            } else {
                sql += "WHERE from_acc=? OR to_acc=? ";
            }
            sql += "ORDER BY trans_date DESC";

            PreparedStatement pst = con.prepareStatement(sql);
            if (type.equals("Sent") || type.equals("Received")) {
                pst.setInt(1, accountNumber);
            } else {
                pst.setInt(1, accountNumber);
                pst.setInt(2, accountNumber);
            }

            ResultSet rs = pst.executeQuery();
            model.setRowCount(0);
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("trans_id"),
                    rs.getInt("from_acc"),
                    rs.getInt("to_acc"),
                    rs.getDouble("amount"),
                    rs.getTimestamp("trans_date")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}
