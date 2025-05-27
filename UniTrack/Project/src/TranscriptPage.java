package pack1;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class TranscriptPage extends JFrame {
    JTextField txtStudentId;
    JTable table;
    DefaultTableModel model;

    public TranscriptPage() {
        setTitle("Transcript");
        setSize(650, 450);
        getContentPane().setLayout(null);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(255, 230, 250)); // Tatlı pembe zemin 🎀
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Font labelFont = new Font("Segoe UI", Font.BOLD, 13);
        Font buttonFont = new Font("Segoe UI", Font.BOLD, 12);
        Color buttonPink = new Color(255, 182, 193);

        JLabel lblId = new JLabel("Student ID:");
        lblId.setBounds(30, 30, 100, 25);
        lblId.setFont(labelFont);
        getContentPane().add(lblId);

        txtStudentId = new JTextField();
        txtStudentId.setBounds(120, 30, 150, 25);
        getContentPane().add(txtStudentId);

        JButton btnLoad = new JButton("Load Transcript");
        btnLoad.setBounds(300, 30, 150, 25);
        btnLoad.setBackground(buttonPink);
        btnLoad.setForeground(new Color(254, 160, 255));
        btnLoad.setFont(buttonFont);
        getContentPane().add(btnLoad);

        model = new DefaultTableModel(new String[]{"Course", "Grade"}, 0);
        table = new JTable(model);
        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(30, 80, 570, 250);
        getContentPane().add(sp);

        btnLoad.addActionListener(e -> loadTranscript());
    }

    private void loadTranscript() {
        model.setRowCount(0);
        try {
            int studentId = Integer.parseInt(txtStudentId.getText());
            Connection conn = DBConnection.connect();
            PreparedStatement ps = conn.prepareStatement(
                "SELECT c.course_name, e.grade " +
                "FROM enrollments e JOIN courses c ON e.course_id = c.id " +
                "WHERE e.student_id = ?"
            );
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();

            double total = 0;
            int count = 0;
            while (rs.next()) {
                String course = rs.getString("course_name");
                double grade = rs.getDouble("grade");
                total += grade;
                count++;
                model.addRow(new Object[]{course, grade});
            }

            if (count > 0) {
                double gpa = total / count;
                JOptionPane.showMessageDialog(this, "GPA: " + String.format("%.2f", gpa));
            } else {
                JOptionPane.showMessageDialog(this, "No grades found.");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading transcript.");
            e.printStackTrace();
        }
    }
}
