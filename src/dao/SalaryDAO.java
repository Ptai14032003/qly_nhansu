package dao;

import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SalaryDAO {

    // ✅ Tính tổng tiền phạt (Giữ nguyên logic của bạn)
    public double getTotalPenalty(int empId, int month, int year) {
        String sql = """
                    SELECT 
                        COALESCE(l.total_late_penalty, 0) + COALESCE(e.total_early_penalty, 0) AS total_penalty
                    FROM
                    (
                        SELECT SUM(ac.penalty_amount) AS total_late_penalty
                        FROM attendance a
                        LEFT JOIN attendance_config ac
                            ON ac.type = 'LATE'
                            AND a.late_minutes BETWEEN ac.min_minutes AND ac.max_minutes
                        WHERE a.emp_id = ?
                          AND MONTH(a.work_date) = ?
                          AND YEAR(a.work_date) = ?
                    ) l,
                    (
                        SELECT SUM(ac.penalty_amount) AS total_early_penalty
                        FROM attendance a
                        LEFT JOIN attendance_config ac
                            ON ac.type = 'EARLY'
                            AND a.early_minutes BETWEEN ac.min_minutes AND ac.max_minutes
                        WHERE a.emp_id = ?
                          AND MONTH(a.work_date) = ?
                          AND YEAR(a.work_date) = ?
                    ) e
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, empId);
            ps.setInt(2, month);
            ps.setInt(3, year);
            ps.setInt(4, empId);
            ps.setInt(5, month);
            ps.setInt(6, year);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble("total_penalty");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // ✅ Lấy lương chi tiết (Đã kiểm tra tên cột final_amount)
    public dto.SalaryDTO getSalaryByPeriod(int empId, int month, int year) {
        String sql =
                "SELECT s.emp_id, s.month, s.year, " +
                        "e.base_salary, e.allowance, " +
                        "COALESCE(s.bonus,0) AS bonus, " +
                        "COALESCE(s.total_penalty,0) AS total_penalty, " +
                        "s.final_amount " + // Lấy trực tiếp từ DB thay vì tính toán lại trong SQL để đảm bảo tính nhất quán
                        "FROM salaries s " +
                        "JOIN employees e ON s.emp_id = e.id " +
                        "LEFT JOIN positions p ON e.pos_id = p.id " +
                        "WHERE s.emp_id = ? AND s.month = ? AND s.year = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, empId);
            ps.setInt(2, month);
            ps.setInt(3, year);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new dto.SalaryDTO(
                            rs.getInt("emp_id"),
                            rs.getInt("month"),
                            rs.getInt("year"),
                            rs.getDouble("base_salary"),
                            rs.getDouble("allowance"),
                            rs.getDouble("bonus"),
                            rs.getDouble("total_penalty"),
                            rs.getDouble("final_amount") // Sửa tại đây
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ✅ Upsert lương (Sử dụng đúng cột final_amount)
    public boolean upsertSalary(dto.SalaryDTO salary) {
        String sql = """
                    INSERT INTO salaries (emp_id, month, year, bonus, total_penalty, final_amount)
                    VALUES (?, ?, ?, ?, ?, ?)
                    ON DUPLICATE KEY UPDATE
                        bonus = VALUES(bonus),
                        total_penalty = VALUES(total_penalty),
                        final_amount = VALUES(final_amount)
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, salary.getEmpId());
            ps.setInt(2, salary.getMonth());
            ps.setInt(3, salary.getYear());
            ps.setDouble(4, salary.getBonus());
            ps.setDouble(5, salary.getTotalPenalty());
            ps.setDouble(6, salary.getFinalAmount());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ✅ Sửa lỗi Unknown column 's.final_salary'
    public List<Object[]> getSalaryByMonth(int month, int year) {
        List<Object[]> list = new ArrayList<>();
        // 1. Thêm e.allowance vào SELECT
        String sql = "SELECT e.id, e.emp_name, e.base_salary, p.coefficient, e.allowance, " +
                "s.bonus, s.total_penalty, s.final_amount, " +
                "(SELECT COUNT(*) FROM attendance a WHERE a.emp_id = e.id AND MONTH(a.work_date) = ? AND YEAR(a.work_date) = ?) as work_days " +
                "FROM salaries s " +
                "JOIN employees e ON s.emp_id = e.id " +
                "JOIN positions p ON e.pos_id = p.id " +
                "WHERE s.month = ? AND s.year = ? AND e.status = 1";

        try (Connection conn = util.DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, month);
            ps.setInt(2, year);
            ps.setInt(3, month);
            ps.setInt(4, year);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // 2. Sắp xếp lại thứ tự Object để khớp với các cột trên giao diện
                    list.add(new Object[]{
                            rs.getInt("id"),
                            rs.getString("emp_name"),
                            String.format("%,.0f", rs.getDouble("base_salary")),
                            rs.getDouble("coefficient"),
                            String.format("%,.0f", rs.getDouble("allowance")), // Lấy tiền phụ cấp thay vì số 4
                            String.format("%,.0f", rs.getDouble("bonus")),
                            String.format("%,.0f", rs.getDouble("total_penalty")),
                            String.format("%,.0f", rs.getDouble("final_amount"))
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean updateAllSalaryComponents(int month, int year) {
        String sqlUpdate = "UPDATE salaries s " +
                "JOIN employees e ON s.emp_id = e.id " +
                "JOIN positions p ON e.pos_id = p.id " +
                "SET " +
                "  s.total_penalty = ( " +
                "      SELECT COALESCE(SUM( " +
                "          CASE WHEN a.late_minutes BETWEEN 15 AND 30 THEN 50000 " +
                "               WHEN a.late_minutes > 30 THEN 100000 ELSE 0 END + " +
                "          CASE WHEN a.early_minutes > 0 AND a.early_minutes <= 15 THEN 20000 " +
                "               WHEN a.early_minutes > 15 THEN 50000 ELSE 0 END " +
                "      ), 0) " +
                "      FROM attendance a " +
                "      WHERE a.emp_id = s.emp_id AND MONTH(a.work_date) = ? AND YEAR(a.work_date) = ? " +
                "  ), " +
                "  s.bonus = IF(s.total_penalty = 0, 500000, 0), " +
                "  s.final_amount = ROUND(GREATEST(0, " +
                "      ((e.base_salary / 22) * p.coefficient * ( " +
                "          /* TỔNG NGÀY CÔNG = NGÀY ĐI LÀM + NGÀY NGHỈ (END - START + 1) */ " +
                "          (SELECT COUNT(*) FROM attendance a " +
                "           WHERE a.emp_id = s.emp_id AND MONTH(a.work_date) = ? AND YEAR(a.work_date) = ?) " +
                "          + " +
                "          (SELECT COALESCE(SUM(DATEDIFF(l.end_date, l.start_date) + 1), 0) " +
                "           FROM leave_requests l " +
                "           WHERE l.emp_id = s.emp_id AND l.status = 'APPROVED' " +
                "           AND MONTH(l.start_date) = ? AND YEAR(l.start_date) = ?) " +
                "      )) + e.allowance + s.bonus - s.total_penalty " +
                "  ), 0) " +
                "WHERE s.month = ? AND s.year = ?";

        try (Connection conn = util.DBConnection.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(sqlUpdate)) {
                // Thiết lập 8 tham số tương ứng với các dấu ? trong SQL
                ps.setInt(1, month);
                ps.setInt(2, year); // Tính phạt (attendance)
                ps.setInt(3, month);
                ps.setInt(4, year); // Đếm ngày đi làm (attendance)
                ps.setInt(5, month);
                ps.setInt(6, year); // Tính ngày nghỉ (leave_requests)
                ps.setInt(7, month);
                ps.setInt(8, year); // Điều kiện WHERE của bảng salaries

                int affectedRows = ps.executeUpdate();
                return affectedRows >= 0; // Trả về true nếu thực thi không lỗi
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}