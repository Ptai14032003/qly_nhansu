package controller;

import dto.User;
import util.AppNavigator;
import view.*;

public class MainController {

    private MainLayout mainLayout;
    private HomeController homeController;
    private EmployeeController employeeController;
    private DepartmentController departmentController;
    private UserController userController;
    private User currentUser;
    private SalaryController salaryController;
    private AttendanceController attendanceController;

    public MainController(User user) {

        this.currentUser = user;

        this.mainLayout = new MainLayout();
        this.homeController = new HomeController();
        this.userController = new UserController(mainLayout);
        this.salaryController = new SalaryController();

        this.employeeController = new EmployeeController();
        this.departmentController = new DepartmentController();

        // ✅ FIX Ở ĐÂY
        AttendanceView attendanceView = new AttendanceView();
        this.attendanceController = new AttendanceController(attendanceView);

        this.mainLayout = new MainLayout();

        initSystem();
    }

    public void initSystem() {

        // --- BƯỚC 1: ĐĂNG KÝ MENU VÀ TRANG ---

        // --- BƯỚC 2: THIẾT LẬP SỰ KIỆN CLICK CHO TỪNG NÚT ---

        // Sự kiện cho nút Trang chủ
        mainLayout.setMenuEvent("Trang chủ", e -> {
            mainLayout.showPage("Trang chủ");
            // Cập nhật dữ liệu Dashboard (Tổng NV, Phòng ban, Quỹ lương và Biểu đồ)
            homeController.refreshData();
        });

        // Sự kiện cho nút Phòng ban
        mainLayout.setMenuEvent("Quản lý phòng ban", e -> {
            mainLayout.showPage("Quản lý phòng ban");
            // Có thể thêm departmentController.refreshData() nếu có hàm này
        });

        // Sự kiện cho nút Nhân viên
        mainLayout.setMenuEvent("Quản lý nhân viên", e -> {
            mainLayout.showPage("Quản lý nhân viên");
            // Làm mới danh sách nhân sự từ database[cite: 1]
            employeeController.refreshData();
        });
        mainLayout.setMenuEvent("Bảng lương", e -> {
            mainLayout.showPage("Bảng lương");
            salaryController.refreshData(); // Tải dữ liệu lương mới nhất khi click
        });
        // Bước 1: Xóa toàn bộ menu cũ để tránh trùng lặp (nếu MainLayout hỗ trợ)

        mainLayout.clearMenu();

        EmployeeView empPage = employeeController.getEmployeePage();
        UserView userPage = userController.getView();

        SalaryView salaryPage = salaryController.getSalaryPage();

        int role = currentUser.getRole();

        if (role == 0) { // ADMIN
            mainLayout.addMenuLink("Trang chủ", homeController.getHomePage());
            mainLayout.addMenuLink("Quản lý phòng ban", departmentController.getDepartmentPage());
            mainLayout.addMenuLink("Quản lý nhân viên", empPage);
            mainLayout.addMenuLink("Quản lý User", userPage);
            mainLayout.addMenuLink("Bảng lương", salaryPage);
            mainLayout.addPage("UserForm", userController.getFormView());
            mainLayout.addMenuLink("Chấm công", attendanceController.getView());

        } else if (role == 1) { // MANAGER
            mainLayout.addMenuLink("Trang chủ", homeController.getHomePage());
            mainLayout.addMenuLink("Quản lý nhân viên", empPage);
            mainLayout.addMenuLink("Bảng lương", salaryPage);
            mainLayout.addMenuLink("Chấm công", attendanceController.getView());
        } else { // EMPLOYEE
            mainLayout.addMenuLink("Thông tin cá nhân", employeeController.getProfilePage());
            mainLayout.setMenuEvent("Thông tin cá nhân", e -> {
                mainLayout.showPage("Thông tin cá nhân");
                // Lấy ID từ currentUser mà MainController đang giữ
                employeeController.showIndividualProfile(currentUser.getEmpId());
                mainLayout.addMenuLink("Chấm công", attendanceController.getView());
            });
            mainLayout.showPage("Thông tin cá nhân");
            employeeController.showIndividualProfile(currentUser.getEmpId());
        }

        // --- BƯỚC 3: KHỞI TẠO MẶC ĐỊNH ---
        mainLayout.showPage("Trang chủ"); // Hiển thị trang đầu tiên khi mở app
        homeController.refreshData();     // Đổ dữ liệu vào các thẻ và biểu đồ tròn[cite: 1]

        mainLayout.setVisible(true);


        mainLayout.addMenuAction("Đăng xuất", () -> {
            AppNavigator.logout(mainLayout);
        });

        mainLayout.setVisible(true);
    }

}