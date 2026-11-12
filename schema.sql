-- =======================================================
-- Student Management System (SMS v2.5 Enterprise) Database Schema
-- Apex Institute of Technology • Session 2026-2027
-- Full Tri-Portal RBAC (Admin, Employee, Student) + Audit Logging + UPI Fees
-- =======================================================

CREATE DATABASE IF NOT EXISTS student_management_db;
USE student_management_db;

-- 1. Academic Departments
CREATE TABLE IF NOT EXISTS departments (
    dept_id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    head_of_department VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. System Employees / Staff (Authorized by Admin)
CREATE TABLE IF NOT EXISTS employees (
    emp_id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    department VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(30) DEFAULT 'STAFF', -- STAFF, COUNSELOR, ACCOUNTANT
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. Super Administrator Credentials & 2FA Configuration
CREATE TABLE IF NOT EXISTS admin_credentials (
    username VARCHAR(50) PRIMARY KEY,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    two_factor_enabled BOOLEAN DEFAULT TRUE,
    last_login TIMESTAMP NULL
);

-- 4. Master Student Records (Each with immutable Login ID & Password)
CREATE TABLE IF NOT EXISTS students (
    id INT AUTO_INCREMENT PRIMARY KEY,
    roll_no VARCHAR(50) UNIQUE NOT NULL,      -- Used as Student Login ID
    password VARCHAR(100) NOT NULL DEFAULT 'pass123', -- Immutable by student; viewable/editable by staff
    name VARCHAR(100) NOT NULL,
    department VARCHAR(100) NOT NULL,
    course VARCHAR(50) NOT NULL,
    semester INT NOT NULL,
    email VARCHAR(100),
    contact VARCHAR(15),
    address TEXT,
    fee_status ENUM('PAID', 'DUE') DEFAULT 'DUE',
    fee_paid DECIMAL(10,2) DEFAULT 0.00,
    fee_due DECIMAL(10,2) DEFAULT 25000.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 5. Live Database Modification Audit Trail (Admin-Only Visibility)
CREATE TABLE IF NOT EXISTS audit_logs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    operator VARCHAR(50) NOT NULL,           -- Employee ID or Admin who made the change
    action_type VARCHAR(50) NOT NULL,         -- INSERT_STUDENT, UPDATE_STUDENT, FEE_COLLECT, CREATE_STAFF, REVOKE_STAFF
    target_entity VARCHAR(100) NOT NULL,      -- e.g. BCA2026-001 or EMP-103
    details TEXT NOT NULL                     -- Payload details and change description
);

-- 6. Tuition Fee UPI Transactions Table
CREATE TABLE IF NOT EXISTS fee_transactions (
    txn_id INT AUTO_INCREMENT PRIMARY KEY,
    student_roll VARCHAR(50) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    fee_head VARCHAR(50) NOT NULL,            -- Tuition Fee, Examination Fee, Lab Fee
    payment_mode VARCHAR(20) DEFAULT 'UPI_QR',
    upi_reference VARCHAR(100),
    collected_by VARCHAR(50) NOT NULL,        -- 'STUDENT_SELF' or Employee ID (e.g., EMP-101)
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_roll) REFERENCES students(roll_no) ON DELETE CASCADE
);

-- =======================================================
-- Initial Data Seeding
-- =======================================================

-- Departments
INSERT INTO departments (dept_id, name, head_of_department) VALUES
('DEPT-CA', 'Computer Applications', 'Dr. Alok Mathur'),
('DEPT-MS', 'Management Studies', 'Dr. Shweta Rastogi'),
('DEPT-COM', 'Commerce & Accounts', 'Prof. R. K. Gupta'),
('DEPT-IT', 'Information Technology', 'Prof. David Miller')
ON DUPLICATE KEY UPDATE name=name;

-- Super Admin Account (Stealth gateway via Staff Portal + 2FA Email OTP)
INSERT INTO admin_credentials (username, password, email, two_factor_enabled) VALUES
('admin', 'admin123', 'admin@apextech.edu', TRUE)
ON DUPLICATE KEY UPDATE email=email;

-- Authorized Staff / Employees (Managed by Admin)
INSERT INTO employees (emp_id, name, department, password, role) VALUES
('EMP-101', 'Rajesh Verma', 'Computer Applications', 'staff123', 'COUNSELOR'),
('EMP-102', 'Sunita Awasthi', 'Academic Office', 'staff123', 'ACCOUNTANT')
ON DUPLICATE KEY UPDATE name=name;

-- Enrolled Students with Credentials & Fees (Divided by Department)
INSERT INTO students (roll_no, password, name, department, course, semester, email, contact, address, fee_status, fee_paid, fee_due) VALUES
('BCA2026-001', 'pass123', 'Aarav Sharma', 'Computer Applications', 'BCA', 3, 'aarav.sharma@apextech.edu', '9876543210', 'Flat 102, Gomti Nagar, Lucknow', 'PAID', 50000.00, 0.00),
('BCA2026-002', 'pass123', 'Rohan Verma', 'Computer Applications', 'BCA', 3, 'rohan.verma@apextech.edu', '9123456789', 'Aliganj Sector C, Lucknow', 'DUE', 25000.00, 25000.00),
('BBA2026-015', 'pass123', 'Kunal Patel', 'Management Studies', 'BBA', 2, 'kunal.patel@apextech.edu', '9988776655', 'Indira Nagar, Lucknow', 'DUE', 10000.00, 40000.00),
('BCA2026-042', 'pass123', 'Ananya Singh', 'Computer Applications', 'BCA', 3, 'ananya.singh@apextech.edu', '9453193268', 'Tedhi Puliya, Ring Road, Lucknow', 'PAID', 50000.00, 0.00),
('BCOM2026-008', 'pass123', 'Pooja Gupta', 'Commerce & Accounts', 'B.Com', 1, 'pooja.gupta@apextech.edu', '9839943482', 'Faizabad Road, Lucknow', 'PAID', 30000.00, 0.00)
ON DUPLICATE KEY UPDATE roll_no=roll_no;

-- Initial Audit Log Entries (Showing which employee performed which action)
INSERT INTO audit_logs (timestamp, operator, action_type, target_entity, details) VALUES
('2026-11-12 09:15:22', 'EMP-101', 'STUDENT_REGISTER', 'BCA2026-001', 'Initial enrollment of student Aarav Sharma in BCA Sem 3'),
('2026-11-12 09:30:10', 'EMP-102', 'FEE_COLLECTION', 'BCA2026-001', 'Settled tuition fee ₹25,000 via UPI QR code transaction'),
('2026-11-12 10:04:45', 'EMP-101', 'STUDENT_UPDATE', 'BCA2026-002', 'Updated phone number and semester address for Rohan Verma'),
('2026-11-12 11:20:00', 'admin', 'EMPLOYEE_AUTHORIZED', 'EMP-102', 'Authorized new staff account Sunita Awasthi under Academic Office');
