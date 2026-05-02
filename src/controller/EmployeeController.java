package controller;

import dao.EmployeeDAO;
import view.EmployeeView;

public class EmployeeController {
    private EmployeeDAO dao;
    private EmployeeView empView;

    public EmployeeController() {
        this.dao = new EmployeeDAO();
        this.empView = new EmployeeView();

        // Load dữ liệu ngay khi khởi tạo
        loadData();
    }

    private void loadData() {
        empView.displayData(dao.getAllEmployeesSummary());
    }

    // Đây chính là hàm mà MainController sẽ gọi
    public EmployeeView getEmployeePage() {
        return this.empView;
    }
}