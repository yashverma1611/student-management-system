package com.sms.view;

import com.sms.dao.StudentDAO;
import com.sms.model.Student;
import javax.swing.*;
import java.awt.*;

public class StudentDialog extends JDialog {
    private final JTextField txtRollNo = new JTextField(15);
    private final JTextField txtName = new JTextField(15);
    private final JComboBox<String> cmbCourse = new JComboBox<>(new String[]{"BCA", "BBA", "B.COM", "MCA"});
    private final JComboBox<Integer> cmbSemester = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5, 6});
    private final JTextField txtEmail = new JTextField(15);
    private final JTextField txtContact = new JTextField(15);
    private final JTextField txtAddress = new JTextField(15);
    private final JComboBox<String> cmbFeeStatus = new JComboBox<>(new String[]{"PAID", "DUE"});

    private final Student existingStudent;
    private final StudentDAO studentDAO = new StudentDAO();

    public StudentDialog(Frame parent, Student student) {
        super(parent, (student == null ? "Register New Student" : "Edit Student Details"), true);
        this.existingStudent = student;

        setSize(460, 460);
        setLocationRelativeTo(parent);
        setResizable(false);

        initComponents();
    }

    private void initComponents() {
        JPanel contentPane = new JPanel(new BorderLayout(15, 15));
        contentPane.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        contentPane.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel(existingStudent == null ? "New Student Registration Form" : "Update Student Profile", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(new Color(15, 23, 42));
        contentPane.add(lblTitle, BorderLayout.NORTH);

        JPanel formGrid = new JPanel(new GridLayout(8, 2, 10, 10));
        formGrid.setBackground(Color.WHITE);

        formGrid.add(new JLabel("University Roll No:"));
        formGrid.add(txtRollNo);

        formGrid.add(new JLabel("Full Name:"));
        formGrid.add(txtName);

        formGrid.add(new JLabel("Degree Program:"));
        formGrid.add(cmbCourse);

        formGrid.add(new JLabel("Current Semester:"));
        formGrid.add(cmbSemester);

        formGrid.add(new JLabel("Email Address:"));
        formGrid.add(txtEmail);

        formGrid.add(new JLabel("Contact Number:"));
        formGrid.add(txtContact);

        formGrid.add(new JLabel("Postal Address:"));
        formGrid.add(txtAddress);

        formGrid.add(new JLabel("Tuition Fee Status:"));
        formGrid.add(cmbFeeStatus);

        contentPane.add(formGrid, BorderLayout.CENTER);

        // Prepopulate if editing
        if (existingStudent != null) {
            txtRollNo.setText(existingStudent.getRollNo());
            txtRollNo.setEditable(false);
            txtName.setText(existingStudent.getName());
            cmbCourse.setSelectedItem(existingStudent.getCourse());
            cmbSemester.setSelectedItem(existingStudent.getSemester());
            txtEmail.setText(existingStudent.getEmail());
            txtContact.setText(existingStudent.getContact());
            txtAddress.setText(existingStudent.getAddress());
            cmbFeeStatus.setSelectedItem(existingStudent.getFeeStatus());
        }

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setBackground(Color.WHITE);

        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(e -> dispose());

        JButton btnSave = new JButton("Save Record");
        btnSave.setBackground(new Color(37, 99, 235));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        btnSave.addActionListener(e -> saveStudent());

        btnPanel.add(btnCancel);
        btnPanel.add(btnSave);
        contentPane.add(btnPanel, BorderLayout.SOUTH);

        add(contentPane);
    }

    private void saveStudent() {
        String roll = txtRollNo.getText().trim();
        String name = txtName.getText().trim();
        String course = (String) cmbCourse.getSelectedItem();
        int sem = (Integer) cmbSemester.getSelectedItem();
        String email = txtEmail.getText().trim();
        String contact = txtContact.getText().trim();
        String address = txtAddress.getText().trim();
        String fee = (String) cmbFeeStatus.getSelectedItem();

        if (roll.isEmpty() || name.isEmpty() || email.isEmpty() || contact.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Roll No, Name, Email, and Contact are mandatory.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Strict Regex Validation (Page 21 Report specification)
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(this, "Invalid email format! Example: student@email.com", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 10-digit mobile contact validation
        String digitsOnly = contact.replaceAll("[^0-9]", "");
        if (digitsOnly.length() < 10) {
            JOptionPane.showMessageDialog(this, "Contact number must contain at least 10 valid digits.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (existingStudent == null) {
            Student newStudent = new Student(roll, name, course, sem, email, contact, fee);
            newStudent.setAddress(address.isEmpty() ? "Lucknow" : address);
            if (studentDAO.addStudent(newStudent)) {
                JOptionPane.showMessageDialog(this, "Student registered successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to register student. Roll number may already exist.", "Duplicate Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            existingStudent.setName(name);
            existingStudent.setCourse(course);
            existingStudent.setSemester(sem);
            existingStudent.setEmail(email);
            existingStudent.setContact(contact);
            existingStudent.setAddress(address);
            existingStudent.setFeeStatus(fee);

            if (studentDAO.updateStudent(existingStudent)) {
                JOptionPane.showMessageDialog(this, "Student profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update record.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
