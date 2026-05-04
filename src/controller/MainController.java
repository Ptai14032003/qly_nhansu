package controller;

import dto.User;
import util.AppNavigator;
import view.DepartmentView;
import view.EmployeeView;
import view.MainLayout;
import view.UserView;

import javax.swing.*;

public class MainController {
    private MainLayout mainLayout;
    private EmployeeController employeeController;
    private DepartmentController departmentController;
    private User currentUser;
    private UserController userController;

    public MainController(User user) {
        // Khởi tạo khung xương và các controller con
        this.currentUser = user;
        this.userController = new UserController();
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
        UserView userPage = userController.getView();
//        // 3. Đăng ký vào Menu của MainLayout
//        mainLayout.addMenuItem("Trang chủ", homePage);
//        mainLayout.addMenuItem("Quản lý phòng ban", deptPage);
//        mainLayout.addMenuItem("Quản lý nhân viên", empPage);

        int role = currentUser.getRole();

        if (role == 0) { // ADMIN
            mainLayout.addMenuItem("Trang chủ", homePage);
            mainLayout.addMenuItem("Quản lý phòng ban", deptPage);
            mainLayout.addMenuItem("Quản lý nhân viên", empPage);
            mainLayout.addMenuItem("Quản lý User", userPage);
        }
        else if (role == 1) { // MANAGER
            mainLayout.addMenuItem("Trang chủ", homePage);
            mainLayout.addMenuItem("Quản lý nhân viên", empPage);
        }
        else { // EMPLOYEE
            mainLayout.addMenuItem("Trang chủ", homePage);
        }

        // 4. Hiển thị
        mainLayout.setVisible(true);
        mainLayout.showPage("Trang chủ");

        mainLayout.addMenuAction("Đăng xuất", () -> {
            AppNavigator.logout(mainLayout);
        });
    }
}