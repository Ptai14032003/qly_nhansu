package controller;

import dao.EmployeeDAO;
import dao.UserDAO;
import dto.EmployeeDTO;
import dto.User;
import view.UserView;

import javax.swing.*;
import java.util.List;

public class UserController {
    private UserView view;
    private UserDAO dao;
    private EmployeeDAO employeeDAO = new EmployeeDAO();


    public UserController() {
        view = new UserView();
        dao = new UserDAO();


        init();
    }

    private void init() {
        // load danh sách ban đầu
        refreshTable();

        loadEmployeesToComboBox();

        // xử lý nút tạo user
        view.setCreateAction((username, password, role, empId) -> {
            try {
                if (username == null || username.trim().isEmpty()) {
                    view.showMessage("Username không được để trống!");
                    return;
                }
                else {
                    view.getTxtUsername().setBorder(UIManager.getBorder("TextField.border"));
                }

                if (password == null || password.trim().isEmpty()) {
                    view.showMessage("Password không được để trống!");
                    return;
                }
                else {
                    view.getTxtPassword().setBorder(UIManager.getBorder("TextField.border"));
                }

                // 🔴 2. Password >= 8 ký tự
                if (password.length() < 8) {
                    view.showMessage("Password phải >= 8 ký tự!");
                    return;
                } else {
                    view.getTxtPassword().setBorder(UIManager.getBorder("TextField.border"));
                }

                // 🔴 3. Username không được trùng
                User existing = dao.findByUsername(username);
                if (existing != null) {
                    view.showMessage("Username đã tồn tại!");
                    return;
                }
                else {
                    view.getTxtUsername().setBorder(UIManager.getBorder("TextField.border"));
                }
                User user = new User(0, username, password, role, empId);
                dao.createUser(user);

                view.showMessage("Tạo user thành công!");
                view.clearForm();
                refreshTable();

            } catch (Exception e) {
                e.printStackTrace();
                view.showMessage("Lỗi tạo user!");
            }
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
    }

    private void refreshTable() {
        try {
            List<User> users = dao.findAll();
            view.setUserTable(users);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public UserView getView() {
        return view;
    }
    public void loadEmployeesToComboBox() {
        List<EmployeeDTO> list = employeeDAO.getAllEmployees();

        JComboBox<EmployeeDTO> cbEmployee = view.getCbEmployee(); // 🔥 lấy từ View

        cbEmployee.removeAllItems();

        for (EmployeeDTO e : list) {
            cbEmployee.addItem(e);
        }
    }
}
