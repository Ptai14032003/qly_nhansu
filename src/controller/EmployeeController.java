package controller;

import dao.DepartmentDAO;
import dao.EmployeeDAO;
import dto.DepartmentDTO;
import dto.EmployeeDTO;
import view.AddEmployeeDialog;
import view.EmployeeProfileView;
import view.EmployeeView;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.List;

public class EmployeeController {
    // Định nghĩa hằng số để tránh lặp lại literal
    private static final String CURRENCY_FORMAT = "%,.0f VNĐ";
    private final EmployeeDAO dao;
    private final EmployeeView empView;
    private DepartmentDAO departmentDAO;
    private int currentPage = 1;
    private final int PAGE_SIZE = 10;
    private EmployeeProfileView profileView;

    public EmployeeController() {
        this.dao = new EmployeeDAO();
        this.departmentDAO = new DepartmentDAO();
        this.empView = new EmployeeView();
        this.profileView = new EmployeeProfileView();
        empView.getTxtSearch().getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                resetAndSearch();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                resetAndSearch();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                resetAndSearch();
            }
        });
        //nạp dữ liệu lọc
        loadDataToFilter();
        //tìm Kiếm
        initSearchEvents();
        //hành động
        setupEvents();
        empView.getBtnNext().addActionListener(e -> goToNextPage());
        // Khi người dùng chọn phòng ban khác
        empView.getCbDeptFilter().addActionListener(e -> resetAndSearch());

        // Khi người dùng chọn kiểu sắp xếp khác (Tên A-Z, Lương cao-thấp...)
        empView.getCbSort().addActionListener(e -> resetAndSearch());
        // Tương tự cho nút "Trước"
        empView.getBtnPrev().addActionListener(e -> goToPrevPage());
        refreshData();

    }
    public EmployeeProfileView getProfilePage() {
        return profileView;
    }
    public void showIndividualProfile(int empId) {
        EmployeeDTO emp = dao.getEmployeeById(empId);
        if (emp != null) {
            profileView.setProfileData(emp);
        }
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
                    "Bạn có chắc chắn muốn xóa nhân viên có mã số " + id + "  không?",
                    "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                // 1. Gọi xuống DAO để cập nhật status = 0
                boolean success = dao.deleteEmployee(id); //

                if (success) {
                    JOptionPane.showMessageDialog(empView, "Đã xóa nhân viên ID: " + id);

                    // 3. Quan trọng: Refresh lại bảng dữ liệu để ẩn nhân viên vừa xóa
                    refreshData();
                } else {
                    JOptionPane.showMessageDialog(empView, "Lỗi: Không thể cập nhật trạng thái nhân viên!",
                            "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // 4. Sự kiện Thêm mới (Tối ưu Java 16+ instanceof và nạp dữ liệu)
        this.empView.onAdd(v -> {
            System.out.println("Debug: Nut Them da duoc click!");
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
        this.currentPage = 1;
        doSearch();
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


    private void initSearchEvents() {
        // 1. Tìm kiếm khi đang gõ (Real-time)
        empView.getTxtSearch().getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                doSearch();
            }

            public void removeUpdate(DocumentEvent e) {
                doSearch();
            }

            public void changedUpdate(DocumentEvent e) {
                doSearch();
            }
        });

        // 2. Lọc khi chọn Combobox
        empView.getCbSort().addActionListener(e -> doSearch());
        empView.getCbDeptFilter().addActionListener(e -> doSearch());
    }

    private void doSearch() {
        String key = empView.getTxtSearch().getText().trim();
        String sort = (String) empView.getCbSort().getSelectedItem();
        int deptId = getSelectedDeptId();

        List<EmployeeDTO> result = dao.searchEmployees(key, deptId, sort, currentPage, PAGE_SIZE);
        empView.displayData(result);

        // Sử dụng hàm hỗ trợ ở trên
        int totalPages = getTotalPages();
        empView.setPageInfo(currentPage, totalPages);

        empView.getBtnPrev().setEnabled(currentPage > 1);
        empView.getBtnNext().setEnabled(currentPage < totalPages);
    }

    private void loadDataToFilter() {
        // Lấy danh sách DTO của phòng ban
        List<DepartmentDTO> depts = departmentDAO.getAll();

        empView.getCbDeptFilter().removeAllItems();
        empView.getCbDeptFilter().addItem("Tất cả phòng ban"); // Lựa chọn mặc định

        for (DepartmentDTO d : depts) {
            // Dùng cái ComboboxItem bạn đã có để giữ ID của phòng ban đó
            empView.getCbDeptFilter().addItem(new AddEmployeeDialog.ComboboxItem(d.getId(), d.getDeptName()));
        }
    }

    private int getTotalPages() {
        String key = empView.getTxtSearch().getText().trim();
        int deptId = getSelectedDeptId();
        int totalEmployees = dao.getTotalCount(key, deptId);

        int totalPages = (int) Math.ceil((double) totalEmployees / PAGE_SIZE);
        return totalPages <= 0 ? 1 : totalPages;
    }

    private void goToNextPage() {
        if (currentPage < getTotalPages()) {
            currentPage++;
            doSearch();
        }
    }

    private int getSelectedDeptId() {
        Object selected = empView.getCbDeptFilter().getSelectedItem();
        if (selected instanceof view.AddEmployeeDialog.ComboboxItem item) {
            return item.getId();
        }
        return 0;
    }

    // Hàm xử lý khi bấm nút "< Trước"
    private void goToPrevPage() {
        // Nếu trang hiện tại lớn hơn 1 thì mới cho giảm trang
        if (currentPage > 1) {
            currentPage--;
            doSearch(); // Gọi lại doSearch để nạp dữ liệu trang cũ
        }
    }

    private void resetAndSearch() {
        this.currentPage = 1; // Luôn đưa về trang đầu tiên khi tìm kiếm/lọc
        doSearch();           // Gọi hàm hiển thị dữ liệu
    }

    /**
     * Hàm hỗ trợ cập nhật nhãn hiển thị số trang và trạng thái các nút bấm
     */
    private void updatePaginationInfo(String key, int deptId) {
        // Lấy tổng số bản ghi từ database (hàm getTotalCount chúng ta đã viết ở DAO)
        int totalEmployees = dao.getTotalCount(key, deptId);

        // Tính tổng số trang
        int totalPages = (int) Math.ceil((double) totalEmployees / PAGE_SIZE);
        if (totalPages == 0) totalPages = 1; // Luôn hiển thị ít nhất 1 trang

        // Cập nhật text hiển thị lên View (Ví dụ: "Trang 1 / 5")
        empView.setPageInfo(currentPage, totalPages);

        // Bật/Tắt các nút điều hướng dựa trên trang hiện tại
        empView.getBtnPrev().setEnabled(currentPage > 1);
        empView.getBtnNext().setEnabled(currentPage < totalPages);
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