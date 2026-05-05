package controller;

import dao.EmployeeDAO;
import dto.EmployeeDTO;
import view.HomeView;

import java.util.List;
import java.util.Map;

public class HomeController {
    private HomeView homeView;
    private EmployeeDAO employeeDAO;

    public HomeController() {
        this.homeView = new HomeView();
        this.employeeDAO = new EmployeeDAO();
    }

    public HomeView getHomePage() {
        return homeView;
    }

    public void refreshData() {
        Map<String, Object> data = employeeDAO.getDashboardStats();

        int emp = (int) data.getOrDefault("totalEmp", 0);
        int dept = (int) data.getOrDefault("totalDept", 0);
        double salary = (double) data.getOrDefault("totalSalary", 0.0);
        Map<String, Integer> chartData = employeeDAO.getEmployeeCountByDept();
        List<EmployeeDTO> recentList = employeeDAO.getRecentEmployees();

        // Bây giờ truyền recentList (kiểu List<EmployeeDTO>) vào View sẽ hết lỗi
        homeView.updateRecentEmployees(recentList);
        homeView.updatePieChart(chartData);
        // Đổ dữ liệu vào giao diện
        homeView.setStats(emp, dept, salary);
    }
}