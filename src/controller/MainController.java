package controller;

import view.MainLayout;

public class MainController {
    private MainLayout mainLayout;
    private HomeController homeController;
    private EmployeeController employeeController;
    private DepartmentController departmentController;

    public MainController() {
        this.mainLayout = new MainLayout();
        this.homeController = new HomeController();
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

        // --- BƯỚC 3: KHỞI TẠO MẶC ĐỊNH ---
        mainLayout.showPage("Trang chủ"); // Hiển thị trang đầu tiên khi mở app
        homeController.refreshData();     // Đổ dữ liệu vào các thẻ và biểu đồ tròn[cite: 1]

        mainLayout.setVisible(true);
    }
}