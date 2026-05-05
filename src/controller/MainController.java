package controller;

import dto.User;
import util.AppNavigator;
import view.DepartmentView;
import view.EmployeeView;
import view.MainLayout;
import view.UserView;

public class MainController {
    private MainLayout mainLayout;
    private EmployeeController employeeController;
    private DepartmentController departmentController;
    private User currentUser;
    private UserController userController;

    public MainController(User user) {
        // Khởi tạo khung xương và các controller con
        this.currentUser = user;
        this.mainLayout = new MainLayout();
        this.userController = new UserController(mainLayout);

        this.employeeController = new EmployeeController();
        this.departmentController = new DepartmentController();
        initSystem();
    }

    public void initSystem() {
        // Bước 1: Xóa toàn bộ menu cũ để tránh trùng lặp (nếu MainLayout hỗ trợ)
        mainLayout.clearMenu();

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
//            mainLayout.addMenuLink("Trang chủ", homePage);
            mainLayout.addMenuLink("Quản lý phòng ban", departmentController.getDepartmentPage());
            mainLayout.addMenuLink("Quản lý nhân viên", empPage);
            mainLayout.addMenuLink("Quản lý User", userPage);
            mainLayout.addPage("UserForm", userController.getFormView());

        }
        else if (role == 1) { // MANAGER
//            mainLayout.addMenuLink("Trang chủ", homePage);
            mainLayout.addMenuLink("Quản lý nhân viên", empPage);
        }
        else { // EMPLOYEE
//            mainLayout.addMenuLink("Trang chủ", homePage);
        }

        mainLayout.setVisible(true);
        mainLayout.showPage("Quản lý nhân viên");

        mainLayout.addMenuAction("Đăng xuất", () -> {
            AppNavigator.logout(mainLayout);
        });
    }
}