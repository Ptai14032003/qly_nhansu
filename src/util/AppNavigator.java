package util;

import controller.LoginController;

import javax.swing.*;

public class AppNavigator {
    public static void logout(JFrame currentFrame) {
        // Xóa session
        Session.currentUser = null;

        // Đóng màn hình hiện tại
        if (currentFrame != null) {
            currentFrame.dispose();
        }

        // Mở lại login
        SwingUtilities.invokeLater(() -> {
            new LoginController().init();
        });
    }
}
