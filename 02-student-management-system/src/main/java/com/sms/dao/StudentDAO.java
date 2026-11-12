package com.sms.dao;

import com.sms.model.Student;
import com.sms.util.DBConnection;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    // 1. CREATE: Insert new student record (invoked by Employee or Admin)
    public boolean addStudent(Student s, String operator) {
        String sql = "INSERT INTO students (roll_no, password, name, department, course, semester, email, contact, address, fee_status, fee_paid, fee_due) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getRollNo());
            ps.setString(2, s.getPassword() != null ? s.getPassword() : "pass123");
            ps.setString(3, s.getName());
            ps.setString(4, s.getDepartment());
            ps.setString(5, s.getCourse());
            ps.setInt(6, s.getSemester());
            ps.setString(7, s.getEmail());
            ps.setString(8, s.getContact());
            ps.setString(9, s.getAddress());
            ps.setString(10, s.getFeeStatus());
            ps.setDouble(11, s.getFeePaid());
            ps.setDouble(12, s.getFeeDue());

            boolean success = ps.executeUpdate() > 0;
            if (success) {
                AuditDAO.logAction(operator, "INSERT_STUDENT", s.getRollNo(),
                        "Registered student " + s.getName() + " under " + s.getDepartment() + " (Sem " + s.getSemester() + ")");
            }
            return success;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 2. READ: Retrieve all student records (for Employee & Admin visibility)
    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students ORDER BY id DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRowToStudent(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 3. READ: Filter by Department
    public List<Student> getStudentsByDepartment(String dept) {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE department = ? ORDER BY id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dept);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToStudent(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 4. READ: Student Private View (Validates credentials & returns isolated single record)
    public Student validateStudentLogin(String rollNo, String password) {
        String sql = "SELECT * FROM students WHERE roll_no = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rollNo);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToStudent(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 5. UPDATE: Update student profile and credentials (invoked by Staff/Admin)
    public boolean updateStudent(Student s, String operator) {
        String sql = "UPDATE students SET name = ?, department = ?, course = ?, semester = ?, password = ?, email = ?, contact = ?, address = ?, fee_status = ? WHERE roll_no = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getName());
            ps.setString(2, s.getDepartment());
            ps.setString(3, s.getCourse());
            ps.setInt(4, s.getSemester());
            ps.setString(5, s.getPassword());
            ps.setString(6, s.getEmail());
            ps.setString(7, s.getContact());
            ps.setString(8, s.getAddress());
            ps.setString(9, s.getFeeStatus());
            ps.setString(10, s.getRollNo());

            boolean success = ps.executeUpdate() > 0;
            if (success) {
                AuditDAO.logAction(operator, "UPDATE_STUDENT", s.getRollNo(),
                        "Updated profile/credentials for " + s.getName() + " (" + s.getDepartment() + ")");
            }
            return success;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 6. FEE PAYMENT: Submit fees with dynamic amount & update due balance
    public boolean submitFeePayment(String rollNo, double amount, String feeHead, String operator) {
        String updateFeeSql = "UPDATE students SET fee_paid = fee_paid + ?, fee_due = GREATEST(0, fee_due - ?), "
                            + "fee_status = CASE WHEN fee_due - ? <= 0 THEN 'PAID' ELSE 'DUE' END WHERE roll_no = ?";
        String insertTxnSql = "INSERT INTO fee_transactions (student_roll, amount, fee_head, payment_mode, collected_by) VALUES (?, ?, ?, 'UPI_QR', ?)";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps1 = conn.prepareStatement(updateFeeSql);
                 PreparedStatement ps2 = conn.prepareStatement(insertTxnSql)) {
                
                ps1.setDouble(1, amount);
                ps1.setDouble(2, amount);
                ps1.setDouble(3, amount);
                ps1.setString(4, rollNo);
                ps1.executeUpdate();

                ps2.setString(1, rollNo);
                ps2.setDouble(2, amount);
                ps2.setString(3, feeHead);
                ps2.setString(4, operator);
                ps2.executeUpdate();

                conn.commit();
                AuditDAO.logAction(operator, "FEE_COLLECTION", rollNo,
                        "Settled " + feeHead + " of ₹" + amount + " via dynamic UPI QR code");
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 7. DELETE: Remove record by Roll Number
    public boolean deleteStudent(String rollNo, String operator) {
        String sql = "DELETE FROM students WHERE roll_no = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rollNo);
            boolean success = ps.executeUpdate() > 0;
            if (success) {
                AuditDAO.logAction(operator, "DELETE_STUDENT", rollNo, "Removed student from university database");
            }
            return success;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 8. SEARCH: Filter by Roll Number or Name
    public List<Student> searchStudents(String query) {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE roll_no LIKE ? OR name LIKE ? ORDER BY id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + query + "%");
            ps.setString(2, "%" + query + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToStudent(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 9. EXPORT: Dump records to CSV
    public boolean exportToCSV(File file) {
        List<Student> students = getAllStudents();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("ID,Roll Number,Password,Name,Department,Course,Semester,Email,Contact,Address,Fee Status,Fee Paid,Fee Due\n");
            for (Student s : students) {
                writer.write(String.format("%d,%s,%s,\"%s\",\"%s\",%s,%d,%s,%s,\"%s\",%s,%.2f,%.2f\n",
                        s.getId(), s.getRollNo(), s.getPassword(), s.getName(), s.getDepartment(), s.getCourse(),
                        s.getSemester(), s.getEmail(), s.getContact(), s.getAddress(), s.getFeeStatus(),
                        s.getFeePaid(), s.getFeeDue()));
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Student mapRowToStudent(ResultSet rs) throws SQLException {
        return new Student(
                rs.getInt("id"),
                rs.getString("roll_no"),
                rs.getString("password"),
                rs.getString("name"),
                rs.getString("department"),
                rs.getString("course"),
                rs.getInt("semester"),
                rs.getString("email"),
                rs.getString("contact"),
                rs.getString("address"),
                rs.getString("fee_status"),
                rs.getDouble("fee_paid"),
                rs.getDouble("fee_due")
        );
    }
}
