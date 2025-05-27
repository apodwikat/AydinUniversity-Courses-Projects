package pack1;

import javax.swing.*;
import java.awt.*;

public class MainPage extends JFrame {
    private JButton btnStudents, btnCourses, btnInstructors, btnDepartments, btnSemesters, btnEnrollments, btnTranscript, btnExit;

    public MainPage() {
        setTitle("UniTrack - Main Page");
        setBounds(100, 100, 500, 404);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);
        getContentPane().setBackground(new Color(255, 230, 250));

        Font font = new Font("Segoe UI", Font.BOLD, 14);

        btnStudents = new JButton("Manage Students");
        btnStudents.setFont(font);
        btnStudents.setBounds(120, 30, 250, 30);
        btnStudents.setBackground(new Color(255, 182, 193));
        btnStudents.setForeground(new Color(0, 0, 0));
        btnStudents.addActionListener(e -> new StudentPanel().setVisible(true));
        getContentPane().add(btnStudents);

        btnCourses = new JButton("Manage Courses");
        btnCourses.setFont(font);
        btnCourses.setBounds(120, 148, 250, 30);
        btnCourses.setBackground(new Color(255, 182, 193));
        btnCourses.setForeground(new Color(0, 0, 0));
        btnCourses.addActionListener(e -> new CoursePanel().setVisible(true));
        getContentPane().add(btnCourses);

        btnInstructors = new JButton("Manage Instructors");
        btnInstructors.setFont(font);
        btnInstructors.setBounds(120, 116, 250, 30);
        btnInstructors.setBackground(new Color(255, 182, 193));
        btnInstructors.setForeground(new Color(0, 0, 0));
        btnInstructors.addActionListener(e -> new InstructorPanel().setVisible(true));
        getContentPane().add(btnInstructors);

        btnDepartments = new JButton("Manage Departments");
        btnDepartments.setFont(font);
        btnDepartments.setBounds(120, 74, 250, 30);
        btnDepartments.setBackground(new Color(255, 182, 193));
        btnDepartments.setForeground(new Color(0, 0, 0));
        btnDepartments.addActionListener(e -> new DepartmentPanel().setVisible(true));
        getContentPane().add(btnDepartments);

        btnSemesters = new JButton("Manage Semesters");
        btnSemesters.setFont(font);
        btnSemesters.setBounds(120, 190, 250, 30);
        btnSemesters.setBackground(new Color(255, 182, 193));
        btnSemesters.setForeground(new Color(0, 0, 0));
        btnSemesters.addActionListener(e -> new SemesterPanel().setVisible(true));
        getContentPane().add(btnSemesters);

        btnEnrollments = new JButton("Manage Enrollments");
        btnEnrollments.setFont(font);
        btnEnrollments.setBounds(120, 230, 250, 30);
        btnEnrollments.setBackground(new Color(255, 182, 193));
        btnEnrollments.setForeground(new Color(0, 0, 0));
        btnEnrollments.addActionListener(e -> new EnrollmentPanel().setVisible(true));
        getContentPane().add(btnEnrollments);

        btnTranscript = new JButton("View Transcripts");
        btnTranscript.setFont(font);
        btnTranscript.setBounds(120, 270, 250, 30);
        btnTranscript.setBackground(new Color(255, 182, 193));
        btnTranscript.setForeground(new Color(0, 0, 0));
        btnTranscript.addActionListener(e -> new TranscriptPage().setVisible(true));
        getContentPane().add(btnTranscript);

        btnExit = new JButton("Exit");
        btnExit.setFont(font);
        btnExit.setBounds(120, 310, 250, 30);
        btnExit.setBackground(new Color(255, 105, 180));
        btnExit.setForeground(new Color(0, 0, 0));
        btnExit.addActionListener(e -> System.exit(0));
        getContentPane().add(btnExit);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainPage().setVisible(true));
    }
}
