package pack1;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class EnrollmentPanel extends JFrame {
    JComboBox<String> cmbStudent, cmbCourse, cmbSemester;
    JTextField txtGrade;
    JTable table;
    DefaultTableModel model;

    public EnrollmentPanel() {
        setTitle("Enrollments");
        setSize(700, 500);
        getContentPane().setLayout(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(255, 230, 250));

        Font font = new Font("Segoe UI", Font.BOLD, 13);
        Color pink = new Color(255, 182, 193);

        JLabel lblStudent = new JLabel("Student:");
        lblStudent.setBounds(30, 30, 100, 25);
        lblStudent.setFont(font);
        getContentPane().add(lblStudent);

        cmbStudent = new JComboBox<>();
        cmbStudent.setBounds(140, 30, 200, 25);
        getContentPane().add(cmbStudent);

        JLabel lblCourse = new JLabel("Course:");
        lblCourse.setBounds(30, 70, 100, 25);
        lblCourse.setFont(font);
        getContentPane().add(lblCourse);

        cmbCourse = new JComboBox<>();
        cmbCourse.setBounds(140, 70, 200, 25);
        getContentPane().add(cmbCourse);

        JLabel lblSemester = new JLabel("Semester:");
        lblSemester.setBounds(30, 110, 100, 25);
        lblSemester.setFont(font);
        getContentPane().add(lblSemester);

        cmbSemester = new JComboBox<>();
        cmbSemester.setBounds(140, 110, 200, 25);
        getContentPane().add(cmbSemester);

        JLabel lblGrade = new JLabel("Grade:");
        lblGrade.setBounds(30, 150, 100, 25);
        lblGrade.setFont(font);
        getContentPane().add(lblGrade);

        txtGrade = new JTextField();
        txtGrade.setBounds(140, 150, 200, 25);
        getContentPane().add(txtGrade);

        JButton btnAdd = new JButton("Add");
        btnAdd.setBounds(370, 60, 120, 30);
        btnAdd.setBackground(pink);
        btnAdd.setForeground(new Color(232, 178, 255));
        btnAdd.setFont(font);
        getContentPane().add(btnAdd);

        JButton btnDelete = new JButton("Delete");
        btnDelete.setBounds(370, 100, 120, 30);
        btnDelete.setBackground(new Color(255, 105, 180));
        btnDelete.setForeground(new Color(0, 0, 0));
        btnDelete.setFont(font);
        getContentPane().add(btnDelete);

        model = new DefaultTableModel(new String[]{"ID", "Student", "Course", "Semester", "Grade"}, 0);
        table = new JTable(model);
        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(30, 200, 620, 220);
        getContentPane().add(sp);

        btnAdd.addActionListener(e -> {
            try {
                Connection conn = DBConnection.connect();
                int studentId = getId("students", (String) cmbStudent.getSelectedItem());
                int courseId = getId("courses", (String) cmbCourse.getSelectedItem());
                int semesterId = getSemesterId((String) cmbSemester.getSelectedItem());
                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO enrollments (student_id, course_id, semester_id, grade) VALUES (?, ?, ?, ?)");
                ps.setInt(1, studentId);
                ps.setInt(2, courseId);
                ps.setInt(3, semesterId);
                ps.setString(4, txtGrade.getText());
                ps.executeUpdate();
                loadTable();
                txtGrade.setText("");
                JOptionPane.showMessageDialog(this, "Enrollment added.");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error while enrolling.\n" + ex.getMessage());
            }
        });

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select an enrollment to delete.");
                return;
            }
            int id = (int) model.getValueAt(row, 0);
            try {
                Connection conn = DBConnection.connect();
                PreparedStatement ps = conn.prepareStatement("DELETE FROM enrollments WHERE id = ?");
                ps.setInt(1, id);
                ps.executeUpdate();
                loadTable();
                JOptionPane.showMessageDialog(this, "Enrollment deleted.");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting enrollment.");
            }
        });

        loadCombos();
        loadTable();
    }

    private void loadCombos() {
        cmbStudent.removeAllItems();
        cmbCourse.removeAllItems();
        cmbSemester.removeAllItems();
        try {
            Connection conn = DBConnection.connect();
            Statement st = conn.createStatement();

            ResultSet rs = st.executeQuery("SELECT name FROM students");
            while (rs.next()) cmbStudent.addItem(rs.getString("name"));

            rs = st.executeQuery("SELECT course_name FROM courses");
            while (rs.next()) cmbCourse.addItem(rs.getString("course_name"));

            rs = st.executeQuery("SELECT CONCAT(semester_name, ' ', year) AS sem FROM semester");
            while (rs.next()) cmbSemester.addItem(rs.getString("sem"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getId(String table, String name) throws SQLException {
        Connection conn = DBConnection.connect();
        String column = switch (table) {
            case "students", "instructors", "departments" -> "name";
            case "courses" -> "course_name";
            case "semester" -> "semester_name";
            default -> "name";
        };
        PreparedStatement ps = conn.prepareStatement("SELECT id FROM " + table + " WHERE " + column + " = ?");
        ps.setString(1, name);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return rs.getInt("id");
        return 0;
    }

    private int getSemesterId(String fullName) throws SQLException {
        String[] parts = fullName.split(" ");
        String name = parts[0];
        String year = parts[1];
        Connection conn = DBConnection.connect();
        PreparedStatement ps = conn.prepareStatement(
                "SELECT id FROM semester WHERE semester_name = ? AND year = ?");
        ps.setString(1, name);
        ps.setString(2, year);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return rs.getInt("id");
        return 0;
    }

    private void loadTable() {
        model.setRowCount(0);
        try {
            Connection conn = DBConnection.connect();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(
                    "SELECT e.id, s.name AS student, c.course_name, CONCAT(se.semester_name, ' ', se.year) AS semester, e.grade " +
                            "FROM enrollments e " +
                            "JOIN students s ON e.student_id = s.id " +
                            "JOIN courses c ON e.course_id = c.id " +
                            "JOIN semester se ON e.semester_id = se.id");
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("student"),
                        rs.getString("course_name"),
                        rs.getString("semester"),
                        rs.getString("grade")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
