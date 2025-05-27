package pack1;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class DepartmentPanel extends JFrame {
    JTextField txtDeptName;
    JTable table;
    DefaultTableModel model;

    public DepartmentPanel() {
        setTitle("Department Management");
        setSize(500, 400);
        getContentPane().setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(new Color(255, 230, 250)); // pastel pembe

        Font labelFont = new Font("Segoe UI", Font.BOLD, 13);
        Font buttonFont = new Font("Segoe UI", Font.BOLD, 12);
        Color buttonPink = new Color(255, 182, 193);

        JLabel lblName = new JLabel("Department Name:");
        lblName.setBounds(30, 30, 130, 25);
        lblName.setFont(labelFont);
        getContentPane().add(lblName);

        txtDeptName = new JTextField();
        txtDeptName.setBounds(160, 30, 150, 25);
        getContentPane().add(txtDeptName);

        JButton btnAdd = new JButton("Add");
        btnAdd.setBounds(330, 30, 100, 25);
        btnAdd.setBackground(buttonPink);
        btnAdd.setForeground(new Color(254, 160, 255));
        btnAdd.setFont(buttonFont);
        getContentPane().add(btnAdd);

        JButton btnDelete = new JButton("Delete");
        btnDelete.setBounds(330, 65, 100, 25);
        btnDelete.setBackground(new Color(255, 105, 180));
        btnDelete.setForeground(new Color(0, 0, 0));
        btnDelete.setFont(buttonFont);
        getContentPane().add(btnDelete);

        model = new DefaultTableModel(new String[]{"ID", "Name"}, 0);
        table = new JTable(model);
        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(30, 110, 420, 200);
        getContentPane().add(sp);

        btnAdd.addActionListener(e -> {
            try {
                Connection conn = DBConnection.connect();
                PreparedStatement ps = conn.prepareStatement("INSERT INTO departments (name) VALUES (?)");
                ps.setString(1, txtDeptName.getText());
                ps.executeUpdate();
                loadTable();
                txtDeptName.setText("");
                JOptionPane.showMessageDialog(this, "Department added.");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error adding department.");
            }
        });

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a department to delete.");
                return;
            }
            int id = (int) model.getValueAt(row, 0);
            try {
                Connection conn = DBConnection.connect();
                PreparedStatement ps = conn.prepareStatement("DELETE FROM departments WHERE id = ?");
                ps.setInt(1, id);
                ps.executeUpdate();
                loadTable();
                JOptionPane.showMessageDialog(this, "Department deleted.");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting department.");
            }
        });

        loadTable();
    }

    private void loadTable() {
        model.setRowCount(0);
        try {
            Connection conn = DBConnection.connect();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM departments");
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
