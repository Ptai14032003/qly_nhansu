package service;

import dao.UserDAO;
import dto.User;

public class AuthService {

    private UserDAO userDAO = new UserDAO();

    public User login(String username, String password) throws Exception {
        User user = userDAO.findByUsername(username);

        if (user == null) {
            System.out.println("User không tồn tại!");
            return null;
        }

        // So sánh password (tạm thời plain text)
        if (!user.getPassword().equals(password)) {
            System.out.println("Sai mật khẩu!");
            return null;
        }

        return user;
    }
}
