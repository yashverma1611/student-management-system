package com.sms.view;

import com.sms.dao.StudentDAO;
import com.sms.model.Student;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.List;

public class DashboardFrame extends JFrame {
    private final StudentDAO studentDAO = new StudentDAO();
    private final JTable table = new JTable();
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"ID", "Roll No", "Student Name", "Course / Sem", "Email ID", "Contact", "Fee Status"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTextField txtSearch = new JTextField(18);

    public DashboardFrame(String adminUser) {
        setTitle("Student Management System v2.5 - [Apex Institute of Technology]");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(980, 580);
        setLocationRelativeTo(null);

        initComponents(adminUser);
        loadTableData();
    }

    private void initComponents(String adminUser) {
        JPanel contentPane = new JPanel(new BorderLayout(10, 10));
        contentPane.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));

        // 1. Top Header & Toolbar
        JPanel topContainer = new JPanel(new BorderLayout(5, 5));

        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel lblHeader = new JLabel("Academic Administration Console (Logged in as: " + adminUser + ")");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblHeader.setForeground(new Color(30, 41, 59));
        headerPanel.add(lblHeader, BorderLayout.WEST);

        JButton btnLogout = new JButton("Logout");
        btnLogout.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
        headerPanel.add(btnLogout, BorderLayout.EAST);
        topContainer.add(headerPanel, BorderLayout.NORTH);

        // Action Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        toolbar.setBackground(new Color(241, 245, 249));
        toolbar.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)));

        JButton btnAdd = new JButton("+ Add Student");
        btnAdd.setBackground(new Color(37, 99, 235));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);
        btnAdd.addActionListener(e -> openStudentDialog(null));

        JButton btnEdit = new JButton("Edit Record");
        btnEdit.addActionListener(e -> editSelectedStudent());

        JButton btnDelete = new JButton("Delete");
        btnDelete.setBackground(new Color(239, 68, 68));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFocusPainted(false);
        btnDelete.addActionListener(e -> deleteSelectedStudent());

        JButton btnExport = new JButton("Export CSV");
        btnExport.addActionListener(e -> exportRecords());

        toolbar.add(btnAdd);
        toolbar.add(btnEdit);
        toolbar.add(btnDelete);
        toolbar.add(btnExport);

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 8));
        searchPanel.setBackground(new Color(241, 245, 249));
        searchPanel.add(new JLabel("Search Roll / Name:"));
        searchPanel.add(txtSearch);
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterTableData(txtSearch.getText().trim());
            }
        });

        JPanel combinedToolbar = new JPanel(new BorderLayout());
        combinedToolbar.add(toolbar, BorderLayout.WEST);
        combinedToolbar.add(searchPanel, BorderLayout.EAST);
        topContainer.add(combinedToolbar, BorderLayout.SOUTH);

        contentPane.add(topContainer, BorderLayout.NORTH);

        // 2. Data Table
        table.setModel(tableModel);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(table);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        // 3. Status Bar
        JLabel lblStatus = new JLabel("Database Engine: Operational • Apex Institute of Technology Academic Network");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatus.setForeground(Color.GRAY);
        contentPane.add(lblStatus, BorderLayout.SOUTH);

        add(contentPane);
    }

    public void loadTableData() {
        tableModel.setRowCount(0);
        List<Student> students = studentDAO.getAllStudents();
        for (Student s : students) {
            tableModel.addRow(new Object[]{
                    s.getId(),
                    s.getRollNo(),
                    s.getName(),
                    s.getCourse() + " - Sem " + s.getSemester(),
                    s.getEmail(),
                    s.getContact(),
                    s.getFeeStatus()
            });
        }
    }

    private void filterTableData(String query) {
        if (query.isEmpty()) {
            loadTableData();
            return;
        }
        tableModel.setRowCount(0);
        List<Student> students = studentDAO.searchStudents(query);
        for (Student s : students) {
            tableModel.addRow(new Object[]{
                    s.getId(),
                    s.getRollNo(),
                    s.getName(),
                    s.getCourse() + " - Sem " + s.getSemester(),
                    s.getEmail(),
                    s.getContact(),
                    s.getFeeStatus()
            });
        }
    }

    private void openStudentDialog(Student student) {
        StudentDialog dialog = new StudentDialog(this, student);
        dialog.setVisible(true);
        loadTableData();
    }

    private void editSelectedStudent() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student record to edit.", "Selection Required", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String rollNo = (String) tableModel.getValueAt(selectedRow, 1);
        List<Student> list = studentDAO.searchStudents(rollNo);
        if (!list.isEmpty()) {
            openStudentDialog(list.get(0));
        }
    }

    private void deleteSelectedStudent() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student record to delete.", "Selection Required", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String rollNo = (String) tableModel.getValueAt(selectedRow, 1);
        String name = (String) tableModel.getValueAt(selectedRow, 2);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete student: " + name + " (Roll No: " + rollNo + ")?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = studentDAO.deleteStudent(rollNo);
            if (success) {
                JOptionPane.showMessageDialog(this, "Student record deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadTableData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete student record.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportRecords() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("student_roster.csv"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (studentDAO.exportToCSV(file)) {
                JOptionPane.showMessageDialog(this, "Roster exported successfully to:\n" + file.getAbsolutePath(), "Export Complete", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Export failed. Please check file permissions.", "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
