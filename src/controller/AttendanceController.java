package controller;

import dao.AttendanceDAO;
import dto.AttendanceDTO;
import util.Session;
import view.AttendanceView;

import javax.swing.*;
import java.util.List;

public class AttendanceController {

    private AttendanceDAO dao;
    private AttendanceView view;

    public AttendanceController(AttendanceView view) {
        this.view = view;
        this.dao = new AttendanceDAO();

        initController();
        loadData();
    }

    public AttendanceView getView() {
        return view;
    }

    private void initController() {
        view.onCheckIn(e -> handleCheckIn());
        view.onCheckOut(e -> handleCheckOut());
    }

    private void loadData() {

        if (Session.currentUser == null) {
            JOptionPane.showMessageDialog(view, "Chưa đăng nhập!");
            return;
        }

        Integer empId = Session.currentUser.getEmpId();

        List<AttendanceDTO> list = dao.getByEmp(empId);
        view.render(list);
    }

    private void handleCheckIn() {

        if (Session.currentUser == null) {
            JOptionPane.showMessageDialog(view, "Chưa đăng nhập!");
            return;
        }

        Integer empId = Session.currentUser.getEmpId();

        boolean success = dao.checkIn(empId);

        JOptionPane.showMessageDialog(view,
                success ? "Check-in thành công!" : "Bạn đã check-in hôm nay rồi!");

        loadData();
    }

    private void handleCheckOut() {

        if (Session.currentUser == null) {
            JOptionPane.showMessageDialog(view, "Chưa đăng nhập!");
            return;
        }

        Integer empId = Session.currentUser.getEmpId();

        boolean success = dao.checkOut(empId);

        JOptionPane.showMessageDialog(view,
                success ? "Check-out thành công!" : "Bạn chưa check-in hoặc đã check-out!");

        loadData();
    }
}