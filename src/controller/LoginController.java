package controller;

import dto.User;
import service.AuthService;
import util.Session;
import view.LoginFrame;

public class LoginController {
    private LoginFrame loginFrame;
    private AuthService authService;

    public LoginController() {
        loginFrame = new LoginFrame();
        authService = new AuthService();
    }

    public void init() {
        loginFrame.setLoginAction((username, password) -> {
            try {
                User user = authService.login(username, password);

                if (user != null) {
                    Session.currentUser = user;

                    loginFrame.dispose();

                    // 👉 Mở MainController
                    MainController mainController = new MainController(user);
                    mainController.initSystem();

                } else {
                    loginFrame.showError("Sai tài khoản hoặc mật khẩu!");
                }

            } catch (Exception e) {
                e.printStackTrace();
                loginFrame.showError("Lỗi hệ thống!");
            }
        });

        loginFrame.setVisible(true);
    }
}
