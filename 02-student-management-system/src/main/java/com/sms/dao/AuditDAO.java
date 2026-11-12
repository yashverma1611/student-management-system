package com.sms.dao;

import com.sms.model.AuditLog;
import com.sms.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuditDAO {

    public static boolean logAction(String operator, String actionType, String targetEntity, String details) {
        String sql = "INSERT INTO audit_logs (operator, action_type, target_entity, details) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, operator);
            ps.setString(2, actionType);
            ps.setString(3, targetEntity);
            ps.setString(4, details);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Audit log write error: " + e.getMessage());
            return false;
        }
    }

    public List<AuditLog> getAllAuditLogs() {
        List<AuditLog> list = new ArrayList<>();
        String sql = "SELECT * FROM audit_logs ORDER BY id DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new AuditLog(
                        rs.getInt("id"),
                        rs.getTimestamp("timestamp"),
                        rs.getString("operator"),
                        rs.getString("action_type"),
                        rs.getString("target_entity"),
                        rs.getString("details")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
