package controller;

import dao.UserDAO;
import dto.User;
import view.UserView;

import java.util.List;

public class UserController {
    private UserView view;
    private UserDAO dao;

    public UserController() {
        view = new UserView();
        dao = new UserDAO();


        init();
    }

    private void init() {
        // load danh sách ban đầu
        refreshTable();

        // xử lý nút tạo user
        view.setCreateAction((username, password, role, empId) -> {
            try {
                User user = new User(0, username, password, role, empId);
                dao.createUser(user);

                view.showMessage("Tạo user thành công!");
                refreshTable();

            } catch (Exception e) {
                e.printStackTrace();
                view.showMessage("Lỗi tạo user!");
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
}
