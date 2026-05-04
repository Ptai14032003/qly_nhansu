package controller;

import dao.EmployeeDAO;
import dto.EmployeeDTO;
import view.AddEmployeeDialog;
import view.EmployeeView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;

public class EmployeeController {
    // Định nghĩa hằng số để tránh lặp lại literal
    private static final String CURRENCY_FORMAT = "%,.0f VNĐ";
    private final EmployeeDAO dao;
    private final EmployeeView empView;

    public EmployeeController() {
        this.dao = new EmployeeDAO();
        this.empView = new EmployeeView();
        setupEvents();
        refreshData();
    }

    private void setupEvents() {
        // 1. Nút Xem chi tiết
        this.empView.onDetail(this::showDetail);

        // 2. Nút Sửa (Sử dụng Expression Lambda)
        this.empView.onEdit(id -> {
            // Lấy đầy đủ thông tin nhân viên từ Database bao gồm cả Detail
            EmployeeDTO emp = dao.getEmployeeById(id);
            if (emp == null) {
                JOptionPane.showMessageDialog(empView, "Không tìm thấy dữ liệu nhân viên!");
                return;
            }

            Window window = SwingUtilities.getWindowAncestor(empView);
            Frame parentFrame = (window instanceof Frame frame) ? frame : null;

            // Khởi tạo Dialog
            AddEmployeeDialog dialog = new AddEmployeeDialog(parentFrame);
            dialog.setTitle("Sửa thông tin nhân viên");

            // Nạp danh sách cho Combobox
            dialog.setDepartmentList(dao.getAllDepartmentsForCombobox());
            dialog.setPositionList(dao.getAllPositionsForCombobox());

            // ĐỔ DỮ LIỆU CŨ VÀO FORM (Quan trọng nhất)
            dialog.setEmployeeData(emp);

            dialog.setVisible(true);

            // Sau khi người dùng đóng Dialog, lấy dữ liệu đã sửa
            EmployeeDTO updatedEmp = dialog.getEmployeeData();
            if (updatedEmp != null) {
                updatedEmp.setId(id); // Giữ nguyên ID cũ để Update
                if (dao.updateEmployee(updatedEmp)) {
                    JOptionPane.showMessageDialog(empView, "Cập nhật thông tin thành công!");
                    refreshData(); // Load lại bảng
                } else {
                    JOptionPane.showMessageDialog(empView, "Cập nhật thất bại!");
                }
            }
        });

        // 3. Nút Xóa
        this.empView.onDelete(id -> {
            int confirm = JOptionPane.showConfirmDialog(empView,
                    "Bạn có chắc chắn muốn xóa nhân viên có mã số " + id + " không?",
                    "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                // Thực hiện xóa tại đây nếu cần
                JOptionPane.showMessageDialog(empView, "Đã thực hiện lệnh xóa nhân viên ID: " + id);
            }
        });

        // 4. Sự kiện Thêm mới (Tối ưu Java 16+ instanceof và nạp dữ liệu)
        this.empView.onAdd(v -> {
            Window window = SwingUtilities.getWindowAncestor(empView);
            // Sử dụng Pattern Matching for instanceof
            Frame parentFrame = (window instanceof Frame frame) ? frame : null;

            AddEmployeeDialog dialog = new AddEmployeeDialog(parentFrame);

            // Nạp dữ liệu TRƯỚC KHI setVisible
            dialog.setDepartmentList(dao.getAllDepartmentsForCombobox());
            dialog.setPositionList(dao.getAllPositionsForCombobox());

            dialog.setVisible(true);

            EmployeeDTO newEmp = dialog.getEmployeeData();
            if (newEmp != null) {
                if (dao.addEmployee(newEmp)) {
                    JOptionPane.showMessageDialog(empView, "Thêm nhân viên thành công!");
                    refreshData();
                } else {
                    JOptionPane.showMessageDialog(empView, "Có lỗi xảy ra khi lưu dữ liệu.");
                }
            }
        });

        // 5. Double Click
        this.empView.getTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) handleRowClick();
            }
        });
    }

    private void handleRowClick() {
        int row = empView.getTable().getSelectedRow();
        if (row != -1) {
            Object value = empView.getTable().getValueAt(row, 0);
            if (value != null) {
                showDetail(Integer.parseInt(value.toString()));
            }
        }
    }

    public void refreshData() {
        empView.displayData(dao.getAllEmployees());
    }

    private void showDetail(int id) {
        EmployeeDTO emp = dao.getEmployeeById(id);
        if (emp == null) return;

        String avatarPath = (emp.getAvatar() != null && !emp.getAvatar().isEmpty())
                ? "file:" + emp.getAvatar() : "https://via.placeholder.com/100";

        String htmlContent = "<html><body style='width: 320px; font-family: sans-serif;'>" +
                "<div style='text-align: center;'>" +
                "   <img src='" + avatarPath + "' width='100' height='100' style='border-radius: 50%;'>" +
                "   <h2 style='margin: 10px 0 0 0;'>" + emp.getEmpName().toLowerCase() + "</h2>" + // Cho tên viết thường giống ảnh
                "   <p style='color: gray;'>" + val(emp.getPosName()) + "</p>" +
                "</div>" +
                "<hr>" +
                "<table style='width: 100%;'>" +
                "<tr><td><b>Mã nhân viên:</b></td><td>" + emp.getId() + "</td></tr>" +
                "<tr><td><b>Email:</b></td><td>" + val(emp.getEmail()) + "</td></tr>" +
                "<tr><td><b>SĐT:</b></td><td>" + val(emp.getPhone()) + "</td></tr>" +
                "<tr><td><b>Ngày vào làm:</b></td><td>" + formatDate(emp.getHireDate()) + "</td></tr>" +
                "<tr><td><b>Phòng ban:</b></td><td>" + val(emp.getDeptName()) + "</td></tr>" +
                "</table>" +
                "<p style='background-color: #eee; padding: 2px;'><b>HỒ SƠ CÁ NHÂN</b></p>" +
                "<table style='width: 100%;'>" +
                "<tr><td>Ngày sinh:</td><td>" + formatDate(emp.getBirthday()) + "</td></tr>" +
                "<tr><td>Giới tính:</td><td>" + (emp.getGender() != null && emp.getGender() == 1 ? "Nam" : "Nữ") + "</td></tr>" +
                "<tr><td>Số CCCD:</td><td>" + val(emp.getIdCard()) + "</td></tr>" +
                "<tr><td>Địa chỉ:</td><td>" + val(emp.getAddress()) + "</td></tr>" +

                // --- THÊM 2 DÒNG NÀY VÀO ĐÂY ---
                "<tr><td>Học vấn:</td><td>" + val(emp.getEducation()) + "</td></tr>" +
                "<tr><td>Kinh nghiệm:</td><td>" + val(emp.getExperience()) + "</td></tr>" +
                // ------------------------------

                "</table>" +
                "<p style='background-color: #eee; padding: 2px;'><b>CHI TIẾT LƯƠNG</b></p>" +
                "<table style='width: 100%;'>" +
                "<tr><td>Lương cơ bản:</td><td align='right'>" + String.format(CURRENCY_FORMAT, emp.getBaseSalary()) + "</td></tr>" +
                "<tr><td>Hệ số lương:</td><td align='right'>" + String.format("%.2f", emp.getCoefficient()) + "</td></tr>" +
                "<tr><td>Phụ cấp:</td><td align='right'>" + String.format(CURRENCY_FORMAT, emp.getAllowance()) + "</td></tr>" +
                "<tr><td>Thưởng:</td><td align='right'>" + String.format(CURRENCY_FORMAT, emp.getBonus()) + "</td></tr>" +
                "<tr style='color: red; font-weight: bold;'><td>TỔNG NHẬN:</td><td align='right'>" + String.format(CURRENCY_FORMAT, emp.getTotalSalary()) + "</td></tr>" +
                "</table>" +
                "</body></html>";

        JOptionPane.showMessageDialog(empView, new JLabel(htmlContent), "Chi tiết nhân viên", JOptionPane.PLAIN_MESSAGE);
    }

    private String formatDate(java.util.Date date) {
        if (date == null) return "N/A";
        return new SimpleDateFormat("dd/MM/yyyy").format(date);
    }

    private String val(Object obj) {
        return (obj == null || obj.toString().trim().isEmpty()) ? "N/A" : obj.toString();
    }

    public EmployeeView getEmployeePage() {
        return empView;
    }
}