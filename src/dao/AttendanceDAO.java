package dao;

import dto.AttendanceDTO;
import util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDAO {

    public List<AttendanceDTO> getByEmp(int empId) {
        List<AttendanceDTO> list = new ArrayList<>();
        // Phải JOIN với bảng employees để lấy full_name
        String sql = "SELECT a.emp_id, e.emp_name AS full_name, a.work_date, a.check_in, a.check_out, a.status " +
                "FROM attendance a " +
                "JOIN employees e ON a.emp_id = e.id " +
                "WHERE a.emp_id = ? " +
                "ORDER BY a.work_date DESC";
        try (Connection conn = util.DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, empId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                AttendanceDTO dto = new AttendanceDTO();
                dto.setEmpId(rs.getInt("emp_id"));
                dto.setEmpName(rs.getString("full_name")); // Đã có tên từ câu SQL JOIN
                dto.setWorkDate(rs.getDate("work_date").toLocalDate());

                Timestamp in = rs.getTimestamp("check_in");
                if (in != null) dto.setCheckIn(in.toLocalDateTime().toLocalTime());

                Timestamp out = rs.getTimestamp("check_out");
                if (out != null) dto.setCheckOut(out.toLocalDateTime().toLocalTime());

                dto.setStatus(rs.getString("status"));
                list.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<AttendanceDTO> getAllAttendance() {
        List<AttendanceDTO> list = new ArrayList<>();
        String sql = "SELECT a.emp_id, e.emp_name AS full_name, a.work_date, a.check_in, a.check_out, a.status " +
                "FROM attendance a " +
                "JOIN employees e ON a.emp_id = e.id " +
                "ORDER BY a.work_date DESC";

        try (Connection conn = util.DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            // --- ĐÂY LÀ ĐOẠN BẠN CẦN DÁN VÀO ---
            while (rs.next()) {
                AttendanceDTO dto = new AttendanceDTO();
                dto.setEmpId(rs.getInt("emp_id"));

                // Lưu ý: Kiểm tra tên cột trong DB là 'full_name' hay 'emp_name' để khớp với SQL trên
                dto.setEmpName(rs.getString("full_name"));

                // 1. Chuyển java.sql.Date thành java.time.LocalDate
                java.sql.Date sqlDate = rs.getDate("work_date");
                if (sqlDate != null) {
                    dto.setWorkDate(sqlDate.toLocalDate());
                }

                // 2. Chuyển java.sql.Timestamp thành java.time.LocalTime (Giờ vào)
                java.sql.Timestamp sqlCheckIn = rs.getTimestamp("check_in");
                if (sqlCheckIn != null) {
                    dto.setCheckIn(sqlCheckIn.toLocalDateTime().toLocalTime());
                }

                // 3. Chuyển java.sql.Timestamp thành java.time.LocalTime (Giờ ra)
                java.sql.Timestamp sqlCheckOut = rs.getTimestamp("check_out");
                if (sqlCheckOut != null) {
                    dto.setCheckOut(sqlCheckOut.toLocalDateTime().toLocalTime());
                }

                dto.setStatus(rs.getString("status"));
                list.add(dto);
            }
            // --- KẾT THÚC ĐOẠN DÁN ---

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


    // ===== CHECK-IN =====
    public boolean checkIn(Integer empId) {

        String checkSql = "SELECT 1 FROM attendance WHERE emp_id=? AND work_date=?";
        String insertSql = "INSERT INTO attendance (emp_id, work_date, check_in, check_out) VALUES (?, ?, ?, ?)";

        try (Connection c = DBConnection.getConnection()) {

            // kiểm tra đã check-in chưa
            try (PreparedStatement check = c.prepareStatement(checkSql)) {
                check.setInt(1, empId);
                check.setDate(2, Date.valueOf(LocalDate.now()));

                ResultSet rs = check.executeQuery();
                if (rs.next()) return false;
            }

            // insert
            try (PreparedStatement ps = c.prepareStatement(insertSql)) {
                ps.setInt(1, empId);
                ps.setDate(2, Date.valueOf(LocalDate.now()));
                ps.setTime(3, Time.valueOf(LocalTime.now()));

                // ✅ set 00:00:00
                ps.setTime(4, Time.valueOf("00:00:00"));

                return ps.executeUpdate() > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<LocalDate> getDistinctWorkDates() {
        List<LocalDate> dates = new ArrayList<>();
        // Phải lấy DISTINCT để không trùng lặp ngày
        String sql = "SELECT DISTINCT work_date FROM attendance ORDER BY work_date DESC";
        try (Connection conn = util.DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                java.sql.Date sqlDate = rs.getDate("work_date");
                if (sqlDate != null) {
                    dates.add(sqlDate.toLocalDate());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dates;
    }

    public List<AttendanceDTO> getAttendanceByDate(java.time.LocalDate date) {
        List<AttendanceDTO> list = new java.util.ArrayList<>();
        // SQL sử dụng JOIN để lấy tên nhân viên và lọc theo work_date
        String sql = "SELECT a.emp_id, e.emp_name AS full_name, a.work_date, a.check_in, a.check_out, a.status " +
                "FROM attendance a " +
                "JOIN employees e ON a.emp_id = e.id " +
                "WHERE a.work_date = ? " +
                "ORDER BY a.check_in ASC";

        try (java.sql.Connection conn = util.DBConnection.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {

            // Thiết lập tham số ngày (chuyển từ LocalDate sang java.sql.Date)
            ps.setDate(1, java.sql.Date.valueOf(date));

            try (java.sql.ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AttendanceDTO dto = new AttendanceDTO();
                    dto.setEmpId(rs.getInt("emp_id"));
                    dto.setEmpName(rs.getString("full_name"));

                    // Xử lý ngày làm việc
                    java.sql.Date sqlDate = rs.getDate("work_date");
                    if (sqlDate != null) {
                        dto.setWorkDate(sqlDate.toLocalDate());
                    }

                    // Xử lý giờ vào (chuyển Timestamp -> LocalTime)
                    java.sql.Timestamp sqlIn = rs.getTimestamp("check_in");
                    if (sqlIn != null) {
                        dto.setCheckIn(sqlIn.toLocalDateTime().toLocalTime());
                    }

                    // Xử lý giờ ra (chuyển Timestamp -> LocalTime)
                    java.sql.Timestamp sqlOut = rs.getTimestamp("check_out");
                    if (sqlOut != null) {
                        dto.setCheckOut(sqlOut.toLocalDateTime().toLocalTime());
                    }

                    dto.setStatus(rs.getString("status"));
                    list.add(dto);
                }
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ===== CHECK-OUT =====
    public boolean checkOut(Integer empId) {

        String sql = "UPDATE attendance SET check_out=? " +
                "WHERE emp_id=? AND work_date=? AND check_out='00:00:00'";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setTime(1, Time.valueOf(LocalTime.now()));
            ps.setInt(2, empId);
            ps.setDate(3, Date.valueOf(LocalDate.now()));

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}