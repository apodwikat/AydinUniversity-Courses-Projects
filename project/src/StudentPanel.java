package pack1;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class StudentPanel extends JFrame {
    JTextField txtName, txtNumber, txtDept;
    JTable table;
    DefaultTableModel model;

    public StudentPanel() {
        setTitle("Student Management");
        setSize(600, 450);
        getContentPane().setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(new Color(255, 230, 250)); // pastel pembe 🎀

        Font labelFont = new Font("Segoe UI", Font.BOLD, 13);
        Font buttonFont = new Font("Segoe UI", Font.BOLD, 12);
        Color buttonPink = new Color(255, 182, 193);

        JLabel lblName = new JLabel("Name:");
        lblName.setBounds(30, 30, 100, 25);
        lblName.setFont(labelFont);
        getContentPane().add(lblName);

        txtName = new JTextField();
        txtName.setBounds(120, 30, 150, 25);
        getContentPane().add(txtName);

        JLabel lblNumber = new JLabel("Student No:");
        lblNumber.setBounds(30, 70, 100, 25);
        lblNumber.setFont(labelFont);
        getContentPane().add(lblNumber);

        txtNumber = new JTextField();
        txtNumber.setBounds(120, 70, 150, 25);
        getContentPane().add(txtNumber);

        JLabel lblDept = new JLabel("Department:");
        lblDept.setBounds(30, 110, 100, 25);
        lblDept.setFont(labelFont);
        getContentPane().add(lblDept);

        txtDept = new JTextField();
        txtDept.setBounds(120, 110, 150, 25);
        getContentPane().add(txtDept);

        JButton btnAdd = new JButton("Add");
        btnAdd.setBounds(300, 30, 100, 25);
        btnAdd.setBackground(buttonPink);
        btnAdd.setForeground(new Color(254, 160, 255));
        btnAdd.setFont(buttonFont);
        getContentPane().add(btnAdd);

        JButton btnDelete = new JButton("Delete");
        btnDelete.setBounds(300, 70, 100, 25);
        btnDelete.setBackground(new Color(255, 105, 180));
        btnDelete.setForeground(new Color(254, 160, 255));
        btnDelete.setFont(buttonFont);
        getContentPane().add(btnDelete);

        model = new DefaultTableModel(new String[]{"ID", "Name", "Number", "Department"}, 0);
        table = new JTable(model);
        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(30, 170, 520, 200);
        getContentPane().add(sp);

        btnAdd.addActionListener(e -> {
            try {
                Connection conn = DBConnection.connect();
                PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO students (name, student_number, department) VALUES (?, ?, ?)");
                ps.setString(1, txtName.getText());
                ps.setString(2, txtNumber.getText());
                ps.setString(3, txtDept.getText());
                ps.executeUpdate();
                loadTable();
                JOptionPane.showMessageDialog(this, "Student added.");
                txtName.setText(""); txtNumber.setText(""); txtDept.setText("");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error while adding.");
            }
        });

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a student to delete.");
                return;
            }
            int id = (int) model.getValueAt(row, 0);
            try {
                Connection conn = DBConnection.connect();
                PreparedStatement ps = conn.prepareStatement("DELETE FROM students WHERE id = ?");
                ps.setInt(1, id);
                ps.executeUpdate();
                loadTable();
                JOptionPane.showMessageDialog(this, "Student deleted.");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error while deleting.");
            }
        });

        loadTable();
    }

    private void loadTable() {
        model.setRowCount(0);
        try {
            Connection conn = DBConnection.connect();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM students");
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("student_number"),
                    rs.getString("department")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
