package dao;

import dto.AttendanceDTO;
import util.DBConnection;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class AttendanceDAO {

    public List<AttendanceDTO> getByEmp(Integer empId) {

        List<AttendanceDTO> list = new ArrayList<>();

        String sql = "SELECT * FROM attendance WHERE emp_id=? ORDER BY work_date DESC";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, empId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Time in = rs.getTime("check_in");
                Time out = rs.getTime("check_out");

                list.add(new AttendanceDTO(
                        rs.getInt("emp_id"),
                        rs.getDate("work_date").toLocalDate(),
                        in != null ? in.toLocalTime() : null,
                        out != null ? out.toLocalTime() : null
                ));
            }

        } catch (Exception e) {
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