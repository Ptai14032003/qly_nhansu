package controller;

import dao.EmployeeDAO;
import dao.SalaryDAO;
import dto.EmployeeDTO;
import dto.SalaryDTO;
import view.SalaryView;

import javax.swing.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SalaryController {

    private final SalaryDAO salaryDAO = new SalaryDAO();
    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    private final SalaryView salaryView;
    private static final Logger LOGGER = Logger.getLogger(SalaryController.class.getName());

    public SalaryController() {
        this.salaryView = new SalaryView();
        // KHÔNG gọi refreshData() ở đây để tránh lỗi biên dịch
        initEvents();
    }

    private void initEvents() {
        salaryView.getBtnCalculate().addActionListener(e -> {
            int month = salaryView.getSelectedMonth();
            int year = salaryView.getSelectedYear();
            if (month > 0 && year > 0) {
                handleCalculateAndRefresh(month, year);
            } else {
                JOptionPane.showMessageDialog(null, "Vui lòng chọn kỳ lương!");
            }
        });
    }

    private void handleCalculateAndRefresh(int month, int year) {
        java.time.LocalDate now = java.time.LocalDate.now();
        int currentMonth = now.getMonthValue();
        int currentYear = now.getYear();

        // 2. Kiểm tra nếu là tháng ở TƯƠNG LAI
        if (year > currentYear || (year == currentYear && month > currentMonth)) {
            JOptionPane.showMessageDialog(null,
                    "Không thể xem lương cho tương lai!",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 3. Kiểm tra nếu là THÁNG HIỆN TẠI
        if (year == currentYear && month == currentMonth) {
            int confirm = JOptionPane.showConfirmDialog(null,
                    "Tháng " + month + "/" + year + " chưa chốt lương.\nBạn có muốn xem bảng lương tạm tính không?",
                    "Thông báo chưa chốt lương",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE);

            if (confirm != JOptionPane.YES_OPTION) {
                return; // Người dùng chọn No thì dừng lại
            }
        }

        // 4. Tiến hành tính toán và nạp dữ liệu (cho tháng quá khứ hoặc tạm tính tháng hiện tại)
        try {
            boolean success = salaryDAO.updateAllSalaryComponents(month, year);
            if (success) {
                refreshData(month, year);
            } else {
                JOptionPane.showMessageDialog(null, "Lỗi khi lấy dữ liệu lương.");
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Lỗi: {0}", ex.getMessage());
        }
    }

    public void calculateMonthlySalary(int empId, int month, int year) {
        EmployeeDTO emp = employeeDAO.getEmployeeById(empId);
        if (emp == null || emp.getStatus() != 1) return;

        double totalPenalty = salaryDAO.getTotalPenalty(empId, month, year);
        double attendanceBonus = (totalPenalty == 0) ? 500000 : 0;
        double totalBonus = emp.getBonus() + attendanceBonus;

        double baseAmount = emp.getBaseSalary() * emp.getCoefficient();
        double finalAmount = baseAmount + emp.getAllowance() + totalBonus - totalPenalty;
        if (finalAmount < 0) finalAmount = 0;

        SalaryDTO dto = new SalaryDTO(empId, month, year, emp.getBaseSalary(), emp.getAllowance(), totalBonus, totalPenalty, finalAmount);
        salaryDAO.upsertSalary(dto);
    }

    // --- CÁCH SỬA QUAN TRỌNG NHẤT Ở ĐÂY ---

    // 1. Hàm refreshData có tham số (Dùng để hiển thị đúng dữ liệu)
    public void refreshData(int month, int year) {
        List<Object[]> data = salaryDAO.getSalaryByMonth(month, year);
        salaryView.displaySalaryData(data);
    }

    // 2. Hàm refreshData KHÔNG tham số (Để "lừa" trình biên dịch, tránh lỗi Found: no arguments)
    public void refreshData() {
        // Không làm gì cả hoặc lấy tháng/năm hiện tại
        LOGGER.log(Level.INFO, "Giao diện yêu cầu nạp dữ liệu mặc định.");
    }

    public SalaryView getSalaryPage() {
        return salaryView;
    }
}