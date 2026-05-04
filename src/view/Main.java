package view;

import controller.MainController;

public class Main {
    public static void main(String[] args) {
        // Chạy giao diện trên luồng sự kiện của Swing (Khuyên dùng)
        javax.swing.SwingUtilities.invokeLater(() -> {
            MainController controller = new MainController();
        });
    }
}