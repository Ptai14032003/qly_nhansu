package view;

import controller.MainController;

public class Main {
    public static void main(String[] args) {
        // MainApp bây giờ chỉ biết mỗi MainController
        MainController sysAdmin = new MainController();
        sysAdmin.initSystem();
    }
}