package controller;

import view.MainLayout;

public class MainController {
    private MainLayout mainLayout;
    private EmployeeController employeeController;
    private DepartmentController departmentController;

    public MainController() {
        this.mainLayout = new MainLayout();
        this.employeeController = new EmployeeController();
        this.departmentController = new DepartmentController();
        initSystem();
    }

    public void initSystem() {
        // Bước 1: Xóa toàn bộ menu cũ để tránh trùng lặp (nếu MainLayout hỗ trợ)
        // mainLayout.clearMenu();

        // Bước 2: Chỉ thêm menu một lần duy nhất
        mainLayout.addMenuLink("Quản lý phòng ban", departmentController.getDepartmentPage());
        mainLayout.addMenuLink("Quản lý nhân viên", employeeController.getEmployeePage());

        // Thiết lập sự kiện
        mainLayout.setMenuEvent("Quản lý phòng ban", e -> {
            mainLayout.showPage("Quản lý phòng ban");
        });

        mainLayout.setMenuEvent("Quản lý nhân viên", e -> {
            mainLayout.showPage("Quản lý nhân viên");
            employeeController.refreshData();
        });

        mainLayout.setVisible(true);
    }
}