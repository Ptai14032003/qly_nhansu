package dao;

import dto.EmployeeDTO;
import util.DBConnection;
import view.AddEmployeeDialog.ComboboxItem;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class EmployeeDAO {
    private static final Logger LOGGER = Logger.getLogger(EmployeeDAO.class.getName());
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final String PHONE_REGEX = "^\\d{10,11}$";

    // --- 1. LẤY TẤT CẢ NHÂN VIÊN (Để hiện thị lên JTable) ---
    public List<EmployeeDTO> getAllEmployees(int page, int pageSize) {
        List<EmployeeDTO> list = new ArrayList<>();
        // Lấy coefficient từ bảng p (positions) thay vì bảng s
        String sql = "SELECT e.*, d.dept_name, p.pos_name, p.coefficient, " +
                "ed.birthday, ed.gender, ed.address, ed.avatar, ed.id_card, ed.education, ed.experience, " +
                "s.base_salary, s.allowance, s.bonus " +
                "FROM employees e " +
                "LEFT JOIN departments d ON e.dept_id = d.id " +
                "LEFT JOIN positions p ON e.pos_id = p.id " +
                "LEFT JOIN employee_details ed ON e.id = ed.emp_id " +
                "LEFT JOIN salaries s ON e.id = s.emp_id " + // Đảm bảo có dấu cách ở cuối này
                "WHERE e.status = 1"; // Hoặc có dấu cách ở đầu chữ WHERE

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapEmployee(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getAllEmployees", e);
        }
        return searchEmployees("", 0, "Mặc định", page, pageSize);
    }
    //
    public List<EmployeeDTO> getAllEmployeeNames() {
        List<EmployeeDTO> list = new ArrayList<>();
        // Chỉ select những gì cần thiết để tối ưu tốc độ
        String sql = "SELECT id, emp_name FROM employees WHERE status = 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                EmployeeDTO emp = new EmployeeDTO();
                emp.setId(rs.getInt("id"));
                emp.setEmpName(rs.getString("emp_name"));
                list.add(emp);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi lấy danh sách tên nhân viên", e);
        }
        return list; // Trả về đúng danh sách vừa lấy
    }
    // --- 2. LẤY CHI TIẾT NHÂN VIÊN (Có tính toán lương trực tiếp) ---
    public EmployeeDTO getEmployeeById(int id) {
        // THÊM "WHERE e.id = ?" VÀO CUỐI CÂU SQL
        String sql = "SELECT e.*, d.dept_name, p.pos_name, p.coefficient, " +
                "ed.birthday, ed.gender, ed.address, ed.avatar, ed.id_card, ed.education, ed.experience, " +
                "s.base_salary, s.allowance, s.bonus " +
                "FROM employees e " +
                "LEFT JOIN departments d ON e.dept_id = d.id " +
                "LEFT JOIN positions p ON e.pos_id = p.id " +
                "LEFT JOIN employee_details ed ON e.id = ed.emp_id " +
                "LEFT JOIN salaries s ON e.id = s.emp_id " +
                "WHERE e.id = ?"; // <--- BẮT BUỘC PHẢI CÓ DẤU ? Ở ĐÂY

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id); // Bây giờ tham số index 1 mới có chỗ để gán vào

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapEmployee(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi SQL getEmployeeById", e);
        }
        return null;
    }

    // --- 3. THÊM MỚI NHÂN VIÊN (Transaction 2 bảng) ---
    public boolean addEmployee(EmployeeDTO emp) {
        // Chặn nhanh nếu đối tượng truyền vào bị null
        if (emp == null || emp.getEmpName() == null) return false;

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu Transaction giống phần Update

            // --- BƯỚC 1: INSERT VÀO BẢNG employees ---
            String sqlEmp = "INSERT INTO employees (emp_name, email, phone, hire_date, dept_id, pos_id, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement psEmp = conn.prepareStatement(sqlEmp, Statement.RETURN_GENERATED_KEYS);
            psEmp.setString(1, emp.getEmpName());
            psEmp.setString(2, emp.getEmail());
            psEmp.setString(3, emp.getPhone());
            psEmp.setDate(4, emp.getHireDate() != null ? new java.sql.Date(emp.getHireDate().getTime()) : null);

            // Xử lý ID phòng ban và chức vụ (nếu không chọn thì set NULL)
            if (emp.getDeptId() > 0) psEmp.setInt(5, emp.getDeptId());
            else psEmp.setNull(5, java.sql.Types.INTEGER);

            if (emp.getPosId() > 0) psEmp.setInt(6, emp.getPosId());
            else psEmp.setNull(6, java.sql.Types.INTEGER);

            psEmp.setInt(7, 1); // Mặc định trạng thái là 1 (Đang làm việc)
            psEmp.executeUpdate();

            // Lấy ID vừa tự động sinh ra để làm khóa ngoại cho các bảng sau[cite: 1]
            ResultSet rs = psEmp.getGeneratedKeys();
            int newId = rs.next() ? rs.getInt(1) : 0;

            if (newId > 0) {
                // --- BƯỚC 2: INSERT VÀO BẢNG employee_details ---[cite: 1]
                String sqlDetail = "INSERT INTO employee_details (emp_id, birthday, gender, id_card, address, education, experience, avatar) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement psDetail = conn.prepareStatement(sqlDetail);
                psDetail.setInt(1, newId);
                psDetail.setDate(2, emp.getBirthday() != null ? new java.sql.Date(emp.getBirthday().getTime()) : null);
                psDetail.setInt(3, emp.getGender());
                psDetail.setString(4, emp.getIdCard());
                psDetail.setString(5, emp.getAddress());
                psDetail.setString(6, emp.getEducation());
                psDetail.setString(7, emp.getExperience());
                psDetail.setString(8, emp.getAvatar());
                psDetail.executeUpdate();

                // --- BƯỚC 3: INSERT VÀO BẢNG salaries ---[cite: 1]
                String sqlSalary = "INSERT INTO salaries (emp_id, base_salary, allowance, bonus) VALUES (?, ?, ?, ?)";
                PreparedStatement psSalary = conn.prepareStatement(sqlSalary);
                psSalary.setInt(1, newId);
                psSalary.setDouble(2, emp.getBaseSalary());
                psSalary.setDouble(3, emp.getAllowance());
                psSalary.setDouble(4, 0.0); // Thêm mới thì thưởng mặc định là 0
                psSalary.executeUpdate();
            }

            conn.commit(); // Lưu tất cả thay đổi[cite: 1]
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Nếu có lỗi ở bất kỳ bước nào, thu hồi toàn bộ dữ liệu[cite: 1]
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.close(); // Đóng kết nối để giải phóng tài nguyên
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // --- 4. CẬP NHẬT NHÂN VIÊN (Transaction 2 bảng) ---
    public boolean updateEmployee(EmployeeDTO emp) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu Transaction để đảm bảo an toàn dữ liệu

            // 1. Cập nhật bảng 'employees' (Thông tin cơ bản)
            String sqlEmp = "UPDATE employees SET emp_name=?, email=?, phone=?, hire_date=?, dept_id=?, pos_id=? WHERE id=?";
            PreparedStatement psEmp = conn.prepareStatement(sqlEmp);
            psEmp.setString(1, emp.getEmpName());
            psEmp.setString(2, emp.getEmail());
            psEmp.setString(3, emp.getPhone());
            psEmp.setDate(4, emp.getHireDate() != null ? new java.sql.Date(emp.getHireDate().getTime()) : null);
            psEmp.setInt(5, emp.getDeptId());
            psEmp.setInt(6, emp.getPosId());
            psEmp.setInt(7, emp.getId());
            psEmp.executeUpdate();

            // 2. Cập nhật bảng 'employee_details' (Hồ sơ chi tiết)
            String sqlDetail = "UPDATE employee_details SET birthday=?, gender=?, id_card=?, address=?, education=?, experience=?, avatar=? WHERE emp_id=?";
            PreparedStatement psDetail = conn.prepareStatement(sqlDetail);
            psDetail.setDate(1, emp.getBirthday() != null ? new java.sql.Date(emp.getBirthday().getTime()) : null);
            psDetail.setInt(2, emp.getGender());
            psDetail.setString(3, emp.getIdCard());
            psDetail.setString(4, emp.getAddress());
            psDetail.setString(5, emp.getEducation());
            psDetail.setString(6, emp.getExperience());
            psDetail.setString(7, emp.getAvatar());
            psDetail.setInt(8, emp.getId());
            psDetail.executeUpdate();

            // 3. Cập nhật bảng 'salaries' (Lương và Phụ cấp)
            String sqlSalary = "UPDATE salaries SET base_salary=?, allowance=? WHERE emp_id=?";
            PreparedStatement psSalary = conn.prepareStatement(sqlSalary);
            psSalary.setDouble(1, emp.getBaseSalary());
            psSalary.setDouble(2, emp.getAllowance());
            psSalary.setInt(3, emp.getId());
            psSalary.executeUpdate();

            conn.commit(); // Lưu tất cả thay đổi
            return true;
        } catch (SQLException e) {
            if (conn != null) try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // --- 5. XÓA NHÂN VIÊN ---
    public boolean deleteEmployee(int id) {
        // Thay vì DELETE, chúng ta UPDATE status về 0 (đã nghỉ việc)
        String sql = "UPDATE employees SET status = 0 WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            // Trả về true nếu có ít nhất 1 dòng được cập nhật thành công
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi chuyển trạng thái nghỉ việc (Soft Delete)", e);
            return false;
        }
    }

    public List<EmployeeDTO> searchEmployees(String keyword, int deptId, String sortType, int page, int pageSize) {
        List<EmployeeDTO> list = new ArrayList<>();
        // Tính toán vị trí bắt đầu lấy dữ liệu
        int offset = (page - 1) * pageSize;

        StringBuilder sql = new StringBuilder(
                "SELECT e.*, d.dept_name, p.pos_name, p.coefficient, " +
                        "ed.birthday, ed.gender, ed.address, ed.avatar, ed.id_card, ed.education, ed.experience, " +
                        "s.base_salary, s.allowance, s.bonus, " +
                        "((s.base_salary * p.coefficient) + s.allowance + s.bonus) AS total_val " +
                        "FROM employees e " +
                        "LEFT JOIN departments d ON e.dept_id = d.id " +
                        "LEFT JOIN positions p ON e.pos_id = p.id " +
                        "LEFT JOIN employee_details ed ON e.id = ed.emp_id " +
                        "LEFT JOIN salaries s ON e.id = s.emp_id " +
                        "WHERE e.status = 1 "
        );

        // Thêm điều kiện lọc
        if (keyword != null && !keyword.isEmpty()) {
            sql.append(" AND (e.emp_name LIKE ? OR e.id LIKE ?) ");
        }
        if (deptId > 0) {
            sql.append(" AND e.dept_id = ? ");
        }

        // Sắp xếp
        switch (sortType) {
            case "Tên (A-Z)":
                sql.append(" ORDER BY e.emp_name ASC");
                break;
            case "Tên (Z-A)":
                sql.append(" ORDER BY e.emp_name DESC");
                break;
            case "Lương: Thấp đến Cao":
                sql.append(" ORDER BY total_val ASC");
                break;
            case "Lương: Cao đến Thấp":
                sql.append(" ORDER BY total_val DESC");
                break;
            default:
                sql.append(" ORDER BY e.id ASC");
                break;
        }

        // PHÂN TRANG: Thêm LIMIT và OFFSET
        sql.append(" LIMIT ? OFFSET ?");

        try (java.sql.Connection conn = DBConnection.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (keyword != null && !keyword.isEmpty()) {
                ps.setString(paramIndex++, "%" + keyword + "%");
                ps.setString(paramIndex++, "%" + keyword + "%");
            }
            if (deptId > 0) {
                ps.setInt(paramIndex++, deptId);
            }

            // Gán tham số cho phân trang
            ps.setInt(paramIndex++, pageSize);
            ps.setInt(paramIndex++, offset);

            java.sql.ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                // Mapping dữ liệu vào EmployeeDTO (giữ nguyên logic mapping cũ của bạn)
                list.add(mapEmployee(rs));
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Hàm bổ trợ đếm tổng số bản ghi (Dùng để tính tổng số trang)
    public int getTotalCount(String keyword, int deptId) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM employees e WHERE e.status = 1");

        if (keyword != null && !keyword.isEmpty()) {
            sql.append(" AND (e.emp_name LIKE ? OR e.id LIKE ?)");
        }
        if (deptId > 0) {
            sql.append(" AND e.dept_id = ?");
        }

        try (java.sql.Connection conn = DBConnection.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int idx = 1;
            if (keyword != null && !keyword.isEmpty()) {
                ps.setString(idx++, "%" + keyword + "%");
                ps.setString(idx++, "%" + keyword + "%");
            }
            if (deptId > 0) {
                ps.setInt(idx++, deptId);
            }

            java.sql.ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // --- HÀM HỖ TRỢ MAPPING DỮ LIỆU ---
    private EmployeeDTO mapEmployee(ResultSet rs) throws SQLException {
        EmployeeDTO emp = new EmployeeDTO();
        emp.setId(rs.getInt("id"));
        emp.setEmpName(rs.getString("emp_name"));
        emp.setEmail(rs.getString("email"));
        emp.setPhone(rs.getString("phone"));
        emp.setHireDate(rs.getDate("hire_date"));
        emp.setDeptName(rs.getString("dept_name"));
        emp.setPosName(rs.getString("pos_name"));

        // Chi tiết
        emp.setBirthday(rs.getDate("birthday"));
        emp.setGender(rs.getInt("gender"));
        emp.setAddress(rs.getString("address"));
        emp.setAvatar(rs.getString("avatar"));
        emp.setIdCard(rs.getString("id_card"));
        emp.setEducation(rs.getString("education"));
        emp.setExperience(rs.getString("experience"));

        // Lương - Đọc chính xác từ ResultSet
        double base = rs.getDouble("base_salary");
        double coeff = rs.getDouble("coefficient");
        double allow = rs.getDouble("allowance");
        double bonus = rs.getDouble("bonus");

        emp.setBaseSalary(base);
        emp.setCoefficient(coeff);
        emp.setAllowance(allow);
        emp.setBonus(bonus);

        // TÍNH TỔNG LƯƠNG NGAY TẠI ĐÂY (Để danh sách không bị sai)
        double total = (base * coeff) + allow + bonus;
        emp.setTotalSalary(total); // Đảm bảo DTO của bạn có field totalSalary

        return emp;
    }

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        // Khởi tạo giá trị mặc định
        stats.put("totalEmp", 0);
        stats.put("totalSalary", 0.0);
        stats.put("totalDept", 0);

        // SQL tính tổng lương = lương cơ bản + phụ cấp + thưởng
        String sqlCount = "SELECT COUNT(*) FROM employees";
        String sqlDept = "SELECT COUNT(*) FROM departments";
        String sqlSalary = "SELECT SUM(base_salary + allowance + bonus) FROM salaries";

        try (Connection conn = DBConnection.getConnection()) {
            // 1. Lấy tổng nhân viên
            PreparedStatement ps1 = conn.prepareStatement(sqlCount);
            ResultSet rs1 = ps1.executeQuery();
            if (rs1.next()) stats.put("totalEmp", rs1.getInt(1));

            // 2. Lấy tổng phòng ban
            PreparedStatement ps2 = conn.prepareStatement(sqlDept);
            ResultSet rs2 = ps2.executeQuery();
            if (rs2.next()) stats.put("totalDept", rs2.getInt(1));

            // 3. Lấy tổng quỹ lương
            PreparedStatement ps3 = conn.prepareStatement(sqlSalary);
            ResultSet rs3 = ps3.executeQuery();
            if (rs3.next()) stats.put("totalSalary", rs3.getDouble(1));

        } catch (SQLException e) {
            System.err.println("Lỗi truy vấn Dashboard: " + e.getMessage());
            e.printStackTrace();
        }
        return stats;
    }


    //Biểu Đồ
    public Map<String, Integer> getEmployeeCountByDept() {
        Map<String, Integer> data = new HashMap<>();
        // Câu lệnh JOIN để lấy tên phòng và đếm số nhân viên thuộc phòng đó
        String sql = "SELECT d.dept_name, COUNT(e.id) AS total " +
                "FROM departments d " +
                "LEFT JOIN employees e ON d.id = e.dept_id " +
                "GROUP BY d.dept_name";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                // Đưa dữ liệu vào Map: Key là tên phòng, Value là số lượng[cite: 1]
                data.put(rs.getString("dept_name"), rs.getInt("total"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    //5 nhân viên mới
    public List<EmployeeDTO> getRecentEmployees() {
        List<EmployeeDTO> list = new ArrayList<>();

        // Truy vấn kết hợp các bảng theo đúng schema qly_nhansu
        String sql = "SELECT e.emp_name, ed.gender, e.hire_date, ed.avatar, " + // Thêm ed.avatar vào đây
                "(YEAR(CURDATE()) - YEAR(ed.birthday)) AS calculated_age, " +
                "p.pos_name, d.dept_name " +
                "FROM employees e " +
                "LEFT JOIN employee_details ed ON e.id = ed.emp_id " +
                "LEFT JOIN departments d ON e.dept_id = d.id " +
                "LEFT JOIN positions p ON e.pos_id = p.id " +
                "ORDER BY e.id DESC LIMIT 5";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                EmployeeDTO emp = new EmployeeDTO();
                // Lấy dữ liệu từ bảng employees[cite: 1]
                emp.setEmpName(rs.getString("emp_name"));
                emp.setHireDate(rs.getDate("hire_date"));

                // Lấy dữ liệu từ bảng employee_details[cite: 1]
                emp.setGender(rs.getInt("gender"));
                // Lấy tuổi đã được tính toán từ SQL
                emp.setAge(rs.getInt("calculated_age"));

                // Lấy dữ liệu từ bảng positions và departments[cite: 1]
                emp.setPosName(rs.getString("pos_name"));
                emp.setDeptName(rs.getString("dept_name"));
                emp.setAvatar(rs.getString("avatar"));
                list.add(emp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // --- VALIDATION & COMBOBOX ---
    private boolean validateEmployee(EmployeeDTO emp) {
        if (emp.getEmail() == null || !Pattern.matches(EMAIL_REGEX, emp.getEmail())) {
            JOptionPane.showMessageDialog(null, "Email không hợp lệ!");
            return false;
        }
        if (emp.getPhone() == null || !Pattern.matches(PHONE_REGEX, emp.getPhone())) {
            JOptionPane.showMessageDialog(null, "Số điện thoại phải từ 10-11 số!");
            return false;
        }
        return true;
    }

    public List<ComboboxItem> getAllDepartmentsForCombobox() {
        return fetchComboboxData("SELECT id, dept_name FROM departments");
    }

    public List<ComboboxItem> getAllPositionsForCombobox() {
        return fetchComboboxData("SELECT id, pos_name FROM positions");
    }

    private List<ComboboxItem> fetchComboboxData(String sql) {
        List<ComboboxItem> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new ComboboxItem(rs.getInt(1), rs.getString(2)));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Combobox error", e);
        }
        return list;
    }
}