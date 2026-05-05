package controller;

import dao.EmployeeDAO;
import dao.UserDAO;
import dto.EmployeeDTO;
import dto.User;
import util.AppNavigator;
import view.MainLayout;
import view.UserFormView;
import view.UserView;

import javax.swing.*;
import java.util.List;

public class UserController {
    private UserView view;
    private UserDAO dao;
    private EmployeeDAO employeeDAO = new EmployeeDAO();
    private int currentPage = 1;
    private final int pageSize = 10;
    private String keyword = "";
    private UserFormView formView;
    private MainLayout mainLayout;
    private String currentSort = "id DESC";
    public UserController(MainLayout mainLayout) {
        this.mainLayout = mainLayout;
        view = new UserView();
        dao = new UserDAO();
        formView = new UserFormView();

        init();
    }

    private void init() {
        // load danh sách ban đầu
        refreshTable();

        formView.setSubmitAction((username, password, role, empId) -> {
            // Gọi hàm validate đã viết ở trên
            if (!validateUser(username, password)) {
                return; // Nếu validate sai thì dừng lại luôn, không chạy code bên dưới
            }

            try {
                User user = new User(0, username, password, role, empId);
                dao.createUser(user);

                formView.showMessage("Tạo thành công!"); // Dùng formView để hiện thông báo cho đồng bộ
                refreshTable();

                // Quay lại trang danh sách
                AppNavigator.navigate(mainLayout, view, "Quản lý User");

                // Clear form để lần sau mở lại là form trắng
                formView.clearForm();

            } catch (Exception e) {
                e.printStackTrace();
                formView.showMessage("Lỗi khi lưu dữ liệu!");
            }
        });
        // xử lý nút tạo user
        view.setAddAction(() -> {
            loadEmployeesToComboBox();
            AppNavigator.navigate(mainLayout, formView, "UserForm");

        });
        formView.setBackAction(() -> {
            // Quay lại trang danh sách User
            AppNavigator.navigate(mainLayout, view, "Quản lý User");
        });
        view.setDeleteAction(userId -> {
            try {
                int confirm = JOptionPane.showConfirmDialog(
                        null,
                        "Bạn có chắc muốn xóa?",
                        "Xác nhận",
                        JOptionPane.YES_NO_OPTION
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    dao.deleteUser(userId);
                    view.showMessage("Xóa thành công!");
                    refreshTable();
                }

            } catch (Exception e) {
                e.printStackTrace();
                view.showMessage("Lỗi khi xóa!");
            }
        });
        view.setPagingAction(new UserView.PagingCallback() {
            @Override
            public void onNext() {
                currentPage++;
                refreshTable();
            }

            @Override
            public void onPrev() {
                if (currentPage > 1) {
                    currentPage--;
                    refreshTable();
                }
            }

            @Override
            public void onSearch(String kw) {
                keyword = kw;
                currentPage = 1; // Reset về trang đầu khi tìm kiếm/sắp xếp

                // Lấy vị trí được chọn trong ComboBox
                int sortIndex = view.getCbSort().getSelectedIndex();

                // Chuyển đổi Index thành câu lệnh SQL ORDER BY
                switch (sortIndex) {
                    case 0 -> currentSort = "id DESC";       // Mới nhất
                    case 1 -> currentSort = "id ASC";        // Cũ nhất
                    case 2 -> currentSort = "username ASC";  // Tên A-Z
                    case 3 -> currentSort = "role ASC";      // Theo quyền hạn
                    default -> currentSort = "id DESC";
                }

                refreshTable(); // Gọi hàm này để load lại dữ liệu từ DB với tham số sắp xếp mới
            }
        });
    }

    private void refreshTable() {
        try {
            List<User> users = dao.findWithPaging(currentPage, pageSize, keyword,currentSort);
            view.setUserTable(users);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public UserView getView() {
        return view;
    }
    public void     loadEmployeesToComboBox() {
        List<EmployeeDTO> list = employeeDAO.getAllEmployees();

        JComboBox<EmployeeDTO> cbEmployee = formView.getCbEmployee();
        cbEmployee.removeAllItems();

        for (EmployeeDTO e : list) {
            cbEmployee.addItem(e);
        }
    }
    public void nextPage() {
        currentPage++;
        refreshTable();
    }

    public void prevPage() {
        if (currentPage > 1) {
            currentPage--;
            refreshTable();
        }
    }
    public void search(String keyword) {
        this.keyword = keyword;
        currentPage = 1;
        refreshTable();
    }
    public UserFormView getFormView() {
        return formView;
    }
    private boolean validateUser(String username, String password) {
        // 1. Kiểm tra trống
        if (username == null || username.trim().isEmpty()) {
            formView.showMessage("Username không được để trống!");
            return false;
        }

        if (password == null || password.trim().isEmpty()) {
            formView.showMessage("Password không được để trống!");
            return false;
        }

        // 2. Độ dài password
        if (password.length() < 8) {
            formView.showMessage("Password phải >= 8 ký tự!");
            return false;
        }

        // 3. Kiểm tra trùng Username (Logic nghiệp vụ)
        try {
            User existing = dao.findByUsername(username);
            if (existing != null) {
                formView.showMessage("Username này đã tồn tại!");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true; // Tất cả đều ổn
    }
}
