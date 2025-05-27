package pack1;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class SemesterPanel extends JFrame {
    private JTextField txtSemesterName, txtYear;
    private JTable table;
    private DefaultTableModel model;

    public SemesterPanel() {
        setTitle("Semester Management");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(new Color(255, 230, 250));

        JLabel lblName = new JLabel("Semester:");
        lblName.setBounds(30, 30, 100, 25);
        add(lblName);

        txtSemesterName = new JTextField();
        txtSemesterName.setBounds(130, 30, 150, 25);
        add(txtSemesterName);

        JLabel lblYear = new JLabel("Year:");
        lblYear.setBounds(30, 70, 100, 25);
        add(lblYear);

        txtYear = new JTextField();
        txtYear.setBounds(130, 70, 150, 25);
        add(txtYear);

        JButton btnAdd = new JButton("Add");
        btnAdd.setBounds(300, 30, 100, 25);
        btnAdd.addActionListener(e -> addSemester());
        add(btnAdd);

        JButton btnDelete = new JButton("Delete");
        btnDelete.setBounds(300, 70, 100, 25);
        btnDelete.addActionListener(e -> deleteSemester());
        add(btnDelete);

        model = new DefaultTableModel(new String[]{"ID", "Semester", "Year"}, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(30, 120, 420, 200);
        add(scrollPane);

        loadTable();
    }

    private void loadTable() {
        model.setRowCount(0);
        try (Connection conn = DBConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM semester")) {
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("semester_name"),
                        rs.getString("year")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addSemester() {
        String name = txtSemesterName.getText();
        String year = txtYear.getText();
        if (name.isEmpty() || year.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }
        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO semester (semester_name, year) VALUES (?, ?)");) {
            ps.setString(1, name);
            ps.setString(2, year);
            ps.executeUpdate();
            loadTable();
            txtSemesterName.setText("");
            txtYear.setText("");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error while adding semester.");
        }
    }

    private void deleteSemester() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a semester to delete.");
            return;
        }
        int id = Integer.parseInt(model.getValueAt(row, 0).toString());
        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM semester WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
            loadTable();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error while deleting semester.");
        }
    }
} 
