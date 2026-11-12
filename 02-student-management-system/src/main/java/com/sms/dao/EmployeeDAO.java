package com.sms.dao;

import com.sms.model.Employee;
import com.sms.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {

    public Employee validateLogin(String empId, String password) {
        String sql = "SELECT * FROM employees WHERE emp_id = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, empId);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Employee(
                            rs.getString("emp_id"),
                            rs.getString("name"),
                            rs.getString("department"),
                            rs.getString("password"),
                            rs.getString("role"),
                            rs.getTimestamp("created_at")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Employee> getAllEmployees() {
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT * FROM employees ORDER BY emp_id ASC";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Employee(
                        rs.getString("emp_id"),
                        rs.getString("name"),
                        rs.getString("department"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getTimestamp("created_at")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean addEmployee(Employee emp, String adminOperator) {
        String sql = "INSERT INTO employees (emp_id, name, department, password, role) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, emp.getEmpId());
            ps.setString(2, emp.getName());
            ps.setString(3, emp.getDepartment());
            ps.setString(4, emp.getPassword());
            ps.setString(5, emp.getRole());
            boolean success = ps.executeUpdate() > 0;
            if (success) {
                AuditDAO.logAction(adminOperator, "CREATE_EMPLOYEE", emp.getEmpId(),
                        "Authorized new staff member " + emp.getName() + " in " + emp.getDepartment());
            }
            return success;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteEmployee(String empId, String adminOperator) {
        String sql = "DELETE FROM employees WHERE emp_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, empId);
            boolean success = ps.executeUpdate() > 0;
            if (success) {
                AuditDAO.logAction(adminOperator, "REVOKE_EMPLOYEE", empId, "Revoked employee access credentials");
            }
            return success;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
