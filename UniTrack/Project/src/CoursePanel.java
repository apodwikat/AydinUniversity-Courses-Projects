package pack1;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class CoursePanel extends JFrame {
    JTextField txtCourseName, txtCredit;
    JComboBox<String> cmbInstructors;
    JTable table;
    DefaultTableModel model;

    public CoursePanel() {
        setTitle("Course Management");
        setSize(650, 500);
        getContentPane().setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(new Color(255, 230, 250)); // pastel pembe 🎀

        Font labelFont = new Font("Segoe UI", Font.BOLD, 13);
        Font buttonFont = new Font("Segoe UI", Font.BOLD, 12);
        Color buttonPink = new Color(255, 182, 193);

        JLabel lblName = new JLabel("Course Name:");
        lblName.setBounds(30, 30, 100, 25);
        lblName.setFont(labelFont);
        getContentPane().add(lblName);

        txtCourseName = new JTextField();
        txtCourseName.setBounds(140, 30, 150, 25);
        getContentPane().add(txtCourseName);

        JLabel lblCredit = new JLabel("Credit:");
        lblCredit.setBounds(30, 70, 100, 25);
        lblCredit.setFont(labelFont);
        getContentPane().add(lblCredit);

        txtCredit = new JTextField();
        txtCredit.setBounds(140, 70, 150, 25);
        getContentPane().add(txtCredit);

        JLabel lblInstructor = new JLabel("Instructor:");
        lblInstructor.setBounds(30, 110, 100, 25);
        lblInstructor.setFont(labelFont);
        getContentPane().add(lblInstructor);

        cmbInstructors = new JComboBox<>();
        cmbInstructors.setBounds(140, 110, 200, 25);
        getContentPane().add(cmbInstructors);

        JButton btnAdd = new JButton("Add");
        btnAdd.setBounds(380, 30, 100, 30);
        style(btnAdd, buttonPink, buttonFont);
        getContentPane().add(btnAdd);

        JButton btnDelete = new JButton("Delete");
        btnDelete.setForeground(new Color(254, 160, 255));
        btnDelete.setBounds(380, 70, 100, 30);
        style(btnDelete, new Color(255, 105, 180), buttonFont);
        getContentPane().add(btnDelete);

        model = new DefaultTableModel(new String[]{"ID", "Course Name", "Credit", "Instructor"}, 0);
        table = new JTable(model);
        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(30, 170, 570, 250);
        getContentPane().add(sp);

        btnAdd.addActionListener(e -> {
            try {
                Connection conn = DBConnection.connect();
                int instructorId = getInstructorId((String) cmbInstructors.getSelectedItem());
                PreparedStatement ps = conn.prepareStatement("INSERT INTO courses (course_name, credit, instructor_id) VALUES (?, ?, ?)");
                ps.setString(1, txtCourseName.getText());
                ps.setInt(2, Integer.parseInt(txtCredit.getText()));
                ps.setInt(3, instructorId);
                ps.executeUpdate();
                loadTable();
                JOptionPane.showMessageDialog(this, "Course added.");
                txtCourseName.setText("");
                txtCredit.setText("");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error while adding.");
            }
        });

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a course to delete.");
                return;
            }
            int id = (int) model.getValueAt(row, 0);
            try {
                Connection conn = DBConnection.connect();
                PreparedStatement ps = conn.prepareStatement("DELETE FROM courses WHERE id = ?");
                ps.setInt(1, id);
                ps.executeUpdate();
                loadTable();
                JOptionPane.showMessageDialog(this, "Course deleted.");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error while deleting.");
            }
        });

        loadInstructors();
        loadTable();
    }

    private void style(JButton btn, Color bg, Font font) {
        btn.setBackground(Color.WHITE);
        btn.setForeground(bg);
        btn.setFont(font);
    }

    private void loadInstructors() {
        cmbInstructors.removeAllItems();
        try {
            Connection conn = DBConnection.connect();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT name FROM instructors");
            while (rs.next()) {
                cmbInstructors.addItem(rs.getString("name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getInstructorId(String name) throws SQLException {
        Connection conn = DBConnection.connect();
        PreparedStatement ps = conn.prepareStatement("SELECT id FROM instructors WHERE name = ?");
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
            ResultSet rs = st.executeQuery("SELECT c.id, c.course_name, c.credit, i.name AS instructor FROM courses c JOIN instructors i ON c.instructor_id = i.id");
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("course_name"),
                        rs.getInt("credit"),
                        rs.getString("instructor")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
