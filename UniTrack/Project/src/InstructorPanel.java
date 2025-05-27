package pack1;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class InstructorPanel extends JFrame {
    JTextField txtInstructorName;
    JComboBox<String> cmbDepartment;
    JTable table;
    DefaultTableModel model;

    public InstructorPanel() {
        setTitle("Instructor Management");
        setSize(600, 500);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(new Color(255, 230, 250));

        Font labelFont = new Font("Segoe UI", Font.BOLD, 13);
        Font buttonFont = new Font("Segoe UI", Font.BOLD, 12);
        Color buttonPink = new Color(255, 182, 193);

        JLabel lblName = new JLabel("Instructor Name:");
        lblName.setBounds(30, 30, 120, 25);
        lblName.setFont(labelFont);
        add(lblName);

        txtInstructorName = new JTextField();
        txtInstructorName.setBounds(160, 30, 150, 25);
        add(txtInstructorName);

        JLabel lblDept = new JLabel("Department:");
        lblDept.setBounds(30, 70, 120, 25);
        lblDept.setFont(labelFont);
        add(lblDept);

        cmbDepartment = new JComboBox<>();
        cmbDepartment.setBounds(160, 70, 150, 25);
        add(cmbDepartment);

        JButton btnAdd = new JButton("Add");
        btnAdd.setBounds(330, 30, 100, 30);
        style(btnAdd, buttonPink, buttonFont);
        add(btnAdd);

        JButton btnDelete = new JButton("Delete");
        btnDelete.setBounds(330, 70, 100, 30);
        style(btnDelete, new Color(255, 105, 180), buttonFont);
        add(btnDelete);

        model = new DefaultTableModel(new String[]{"ID", "Name", "Department"}, 0);
        table = new JTable(model);
        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(30, 130, 520, 250);
        add(sp);

        btnAdd.addActionListener(e -> {
            try {
                Connection conn = DBConnection.connect();
                int deptId = getDepartmentId((String) cmbDepartment.getSelectedItem());
                PreparedStatement ps = conn.prepareStatement("INSERT INTO instructors (name, department_id) VALUES (?, ?)");
                ps.setString(1, txtInstructorName.getText());
                ps.setInt(2, deptId);
                ps.executeUpdate();
                loadTable();
                txtInstructorName.setText("");
                JOptionPane.showMessageDialog(this, "Instructor added.");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error while adding.");
            }
        });

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select an instructor to delete.");
                return;
            }
            int id = (int) model.getValueAt(row, 0);
            try {
                Connection conn = DBConnection.connect();
                PreparedStatement ps = conn.prepareStatement("DELETE FROM instructors WHERE id = ?");
                ps.setInt(1, id);
                ps.executeUpdate();
                loadTable();
                JOptionPane.showMessageDialog(this, "Instructor deleted.");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error while deleting.");
            }
        });

        loadDepartments();
        loadTable();
    }

    private void style(JButton btn, Color color, Font font) {
        btn.setBackground(Color.WHITE);
        btn.setForeground(color);
        btn.setFont(font);
    }

    private void loadDepartments() {
        cmbDepartment.removeAllItems();
        try {
            Connection conn = DBConnection.connect();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT name FROM departments");
            while (rs.next()) {
                cmbDepartment.addItem(rs.getString("name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getDepartmentId(String name) throws SQLException {
        Connection conn = DBConnection.connect();
        PreparedStatement ps = conn.prepareStatement("SELECT id FROM departments WHERE name = ?");
        ps.setString(1, name);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt("id");
        }
        return 0;
    }

    private void loadTable() {
        model.setRowCount(0);
        try {
            Connection conn = DBConnection.connect();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT i.id, i.name, d.name AS dept FROM instructors i JOIN departments d ON i.department_id = d.id");
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("dept")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
