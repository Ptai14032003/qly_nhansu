package controller;

import view.DepartmentView;
import view.EmployeeView;
import view.MainLayout;

import javax.swing.*;

public class MainController {
    private MainLayout mainLayout;
    private EmployeeController employeeController;
    private DepartmentController departmentController;

    public MainController() {
        // Khởi tạo khung xương và các controller con
        this.mainLayout = new MainLayout();
        this.employeeController = new EmployeeController();
        this.departmentController = new DepartmentController();
    }

    public void initSystem() {
        // 1. Trang chủ (Tạo nhanh một Panel)
        JPanel homePage = new JPanel();
        homePage.add(new JLabel("CHÀO MỪNG BẠN ĐẾN VỚI HỆ THỐNG QUẢN LÝ NHÂN SỰ"));

        // 2. Lấy trang nhân viên từ Controller con
        EmployeeView empPage = employeeController.getEmployeePage();
        DepartmentView deptPage = departmentController.getDepartmentPage();

        // 3. Đăng ký vào Menu của MainLayout
        mainLayout.addMenuItem("Trang chủ", homePage);
        mainLayout.addMenuItem("Quản lý phòng ban", deptPage);
        mainLayout.addMenuItem("Quản lý nhân viên", empPage);


        // 4. Hiển thị
        mainLayout.setVisible(true);
        mainLayout.showPage("Trang chủ");
    }
}