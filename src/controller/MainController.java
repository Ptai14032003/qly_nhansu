package controller;

import dto.User;
import util.AppNavigator;
import view.DepartmentView;
import view.EmployeeView;
import view.MainLayout;
import view.UserView;
import view.AttendanceView; // ✅ thêm

public class MainController {

    private MainLayout mainLayout;
    private EmployeeController employeeController;
    private DepartmentController departmentController;
    private UserController userController;
    private AttendanceController attendanceController;

    private User currentUser;

    public MainController(User user) {

        this.currentUser = user;

        this.userController = new UserController();
        this.employeeController = new EmployeeController();
        this.departmentController = new DepartmentController();

        // ✅ FIX Ở ĐÂY
        AttendanceView attendanceView = new AttendanceView();
        this.attendanceController = new AttendanceController(attendanceView);

        this.mainLayout = new MainLayout();

        initSystem();
    }

    public void initSystem() {

        mainLayout.clearMenu();

        EmployeeView empPage = employeeController.getEmployeePage();
        DepartmentView deptPage = departmentController.getDepartmentPage();
        UserView userPage = userController.getView();

        int role = currentUser.getRole();

        // ================= ADMIN =================
        if (role == 0) {

            mainLayout.addMenuLink("Quản lý phòng ban", deptPage);
            mainLayout.addMenuLink("Quản lý nhân viên", empPage);
            mainLayout.addMenuLink("Quản lý User", userPage);

            mainLayout.addMenuLink("Chấm công", attendanceController.getView());
        }

        // ================= MANAGER =================
        else if (role == 1) {

            mainLayout.addMenuLink("Quản lý nhân viên", empPage);
            mainLayout.addMenuLink("Chấm công", attendanceController.getView());
        }

        // ================= EMPLOYEE =================
        else {

            mainLayout.addMenuLink("Chấm công", attendanceController.getView());
        }

        mainLayout.showPage("Quản lý nhân viên");

        mainLayout.addMenuAction("Đăng xuất", () -> {
            AppNavigator.logout(mainLayout);
        });

        mainLayout.setVisible(true);
    }
}