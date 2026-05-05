package controller;

import dto.User;
import util.AppNavigator;
import view.DepartmentView;
import view.EmployeeView;
import view.MainLayout;
import view.UserView;

public class MainController {
    private MainLayout mainLayout;
    private HomeController homeController;
    private EmployeeController employeeController;
    private DepartmentController departmentController;
    private User currentUser;
    private UserController userController;

    public MainController(User user) {
        // Khởi tạo khung xương và các controller con
        this.currentUser = user;
        this.mainLayout = new MainLayout();
        this.homeController = new HomeController();
        this.userController = new UserController(mainLayout);

        this.employeeController = new EmployeeController();
        this.departmentController = new DepartmentController();
        initSystem();
    }

    public void initSystem() {
        // --- BƯỚC 1: ĐĂNG KÝ MENU VÀ TRANG ---
        // addMenuLink sẽ vừa tạo nút trên thanh bên, vừa add Panel vào CardLayout
        mainLayout.addMenuLink("Trang chủ", homeController.getHomePage());
        mainLayout.addMenuLink("Quản lý phòng ban", departmentController.getDepartmentPage());
        mainLayout.addMenuLink("Quản lý nhân viên", employeeController.getEmployeePage());

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

        // --- BƯỚC 3: KHỞI TẠO MẶC ĐỊNH ---
        mainLayout.showPage("Trang chủ"); // Hiển thị trang đầu tiên khi mở app
        homeController.refreshData();     // Đổ dữ liệu vào các thẻ và biểu đồ tròn[cite: 1]

        mainLayout.setVisible(true);
        mainLayout.showPage("Quản lý nhân viên");

        mainLayout.addMenuAction("Đăng xuất", () -> {
            AppNavigator.logout(mainLayout);
        });
    }
}