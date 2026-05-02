package dao;

import dto.EmployeeDTO;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {
    public List<EmployeeDTO> getAllEmployeesSummary() {
        List<EmployeeDTO> list = new ArrayList<>();
        String sql = "SELECT e.id, e.emp_name, d.dept_name, p.pos_name, " +
                "s.base_salary, p.coefficient, s.allowance, s.bonus " +
                "FROM employees e " +
                "LEFT JOIN departments d ON e.dept_id = d.id " +
                "LEFT JOIN positions p ON e.pos_id = p.id " +
                "LEFT JOIN salaries s ON e.id = s.emp_id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                EmployeeDTO emp = new EmployeeDTO();
                emp.setId(rs.getInt("id"));
                emp.setEmpName(rs.getString("emp_name"));
                emp.setDeptName(rs.getString("dept_name"));
                emp.setPosName(rs.getString("pos_name"));
                emp.setBaseSalary(rs.getDouble("base_salary"));
                emp.setCoefficient(rs.getDouble("coefficient"));
                emp.setAllowance(rs.getDouble("allowance"));
                emp.setBonus(rs.getDouble("bonus"));

                // Công thức tính tổng lương
                double total = (emp.getBaseSalary() * emp.getCoefficient()) + emp.getAllowance() + emp.getBonus();
                emp.setTotalSalary(total);

                list.add(emp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}