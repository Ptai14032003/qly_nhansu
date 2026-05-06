package controller;

import dao.AttendanceDAO;
import dto.AttendanceDTO;
import util.Session;
import view.AttendanceView;

import javax.swing.*;
import java.time.LocalDate;
import java.util.List;

public class AttendanceController {

    private AttendanceDAO dao;
    private AttendanceView view;
    private int currentRole;

    public AttendanceController(AttendanceView view) {
        this.view = view;
        this.dao = new AttendanceDAO();

        if (Session.currentUser != null) {
            this.currentRole = Session.currentUser.getRole();
        }

        initController();
        setupInitialState();
    }

    public AttendanceView getView() {
        return view;
    }

    private void initController() {
        // Đăng ký các sự kiện từ View
        view.onCheckIn(e -> handleCheckIn());
        view.onCheckOut(e -> handleCheckOut());
        view.onMyHistoryClick(e -> handleShowMyHistory());
        view.onDateChange(e -> handleFilterByDate());

        // Sửa lỗi nút Quay lại: Đăng ký sự kiện quay về chế độ quản lý tổng
        view.onBackClick(e -> {
            // Chế độ quản lý tổng cho Admin (isPersonalMode = false)
            view.setupViewByRole(currentRole, false);
            loadAllAttendance();
        });
    }

    private void setupInitialState() {
        if (Session.currentUser == null) return;

        // FIXED: Thêm tham số thứ 2 (false) để báo hiệu đây không phải mode cá nhân
        view.setupViewByRole(currentRole, false);

        if (currentRole == 0 || currentRole == 1) {
            refreshDateComboBox(); // Nạp danh sách ngày từ DB
            loadAllAttendance();   // Hiện danh sách toàn bộ nhân viên
        } else {
            handleShowMyHistory(); // Nhân viên thường thì vào thẳng lịch sử cá nhân
        }
    }

    private void loadAllAttendance() {
        List<AttendanceDTO> list = dao.getAllAttendance();
        view.render(list);
    }

    private void handleFilterByDate() {
        Object selected = view.getCbDates().getSelectedItem();
        if (selected instanceof LocalDate) {
            LocalDate selectedDate = (LocalDate) selected;
            List<AttendanceDTO> list = dao.getAttendanceByDate(selectedDate);
            view.render(list);
        }
    }

    private void handleShowMyHistory() {
        if (Session.currentUser == null) return;
        int myId = Session.currentUser.getEmpId();
        List<AttendanceDTO> myHistory = dao.getByEmp(myId);

        // FIXED: Thêm tham số thứ 2 (true) để bật mode cá nhân (hiện nút chấm công)
        view.setupViewByRole(currentRole, true);
        view.render(myHistory);
    }

    private void refreshDateComboBox() {
        List<LocalDate> dates = dao.getDistinctWorkDates();
        if (dates != null && !dates.isEmpty()) {
            view.setDateList(dates);
        }
    }

    private void handleCheckIn() {
        if (Session.currentUser == null) return;

        int empId = Session.currentUser.getEmpId();
        boolean success = dao.checkIn(empId);

        // Hiển thị thông báo chính xác theo trạng thái
        if (success) {
            JOptionPane.showMessageDialog(view, "Check-in thành công!");
        } else {
            JOptionPane.showMessageDialog(view, "Bạn đã thực hiện check-in hôm nay rồi!");
        }
        handleShowMyHistory(); // Cập nhật lại bảng lịch sử ngay lập tức
    }

    private void handleCheckOut() {
        if (Session.currentUser == null) return;

        int empId = Session.currentUser.getEmpId();
        boolean success = dao.checkOut(empId);

        // Hiển thị thông báo lỗi logic theo yêu cầu
        if (success) {
            JOptionPane.showMessageDialog(view, "Check-out thành công!");
        } else {
            JOptionPane.showMessageDialog(view, "Lỗi: Bạn chưa check-in hoặc đã hoàn thành check-out hôm nay!");
        }
        handleShowMyHistory(); // Cập nhật lại bảng lịch sử ngay lập tức
    }
}