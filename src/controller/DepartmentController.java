package controller;

import dao.DepartmentDAO;
import view.DepartmentView;

import javax.swing.*;

public class DepartmentController {
    private DepartmentDAO dao;
    private DepartmentView view;

    public DepartmentController() {
        this.dao = new DepartmentDAO();
        this.view = new DepartmentView();
        view.displayData(dao.getAll());
        initEvents();
    }

    private void initEvents() {
        // XỬ LÝ THÊM
        view.addBtnAddListener(e -> {
            String name = view.showInputDialog("Thêm phòng ban", "Nhập tên mới:", "");
            if (name == null || name.trim().isEmpty()) return;
            name = name.trim();

            if (dao.isNameExists(name)) {
                JOptionPane.showMessageDialog(view, "Tên phòng ban này đã tồn tại!");
            } else if (dao.insert(name)) {
                view.displayData(dao.getAll());
            }
        });

        // XỬ LÝ SỬA
        view.addBtnEditListener(e -> {
            String idStr = view.getSelectedId();
            if (idStr.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Vui lòng chọn 1 dòng để sửa!");
                return;
            }
            int id = Integer.parseInt(idStr);
            String oldName = view.getSelectedName();
            String newName = view.showInputDialog("Sửa phòng ban", "Nhập tên mới:", oldName);

            if (newName == null || newName.trim().equals(oldName)) return;
            newName = newName.trim();

            if (dao.isNameExistsExcept(newName, id)) {
                JOptionPane.showMessageDialog(view, "Tên này đã được sử dụng bởi phòng ban khác!");
            } else if (dao.update(id, newName)) {
                view.displayData(dao.getAll());
            }
        });

        // XỬ LÝ XÓA
        view.addBtnDeleteListener(e -> {
            String idStr = view.getSelectedId();
            String deptName = view.getSelectedName();

            if (idStr.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Vui lòng chọn phòng ban cần xóa!");
                return;
            }

            // Thông báo xác nhận có cảnh báo hệ quả
            int confirm = JOptionPane.showConfirmDialog(view,
                    "Bạn có chắc chắn muốn xóa phòng ban: " + deptName + "?\n" +
                            "Lưu ý: Sau khi xóa, tất cả nhân viên thuộc phòng này sẽ tạm thời không thuộc phòng ban nào.",
                    "Cảnh báo xóa dữ liệu",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                if (dao.delete(Integer.parseInt(idStr))) {
                    JOptionPane.showMessageDialog(view, "Đã xóa phòng ban thành công.");
                    view.displayData(dao.getAll());
                } else {
                    JOptionPane.showMessageDialog(view, "Không thể thực hiện lệnh xóa. Vui lòng kiểm tra lại kết nối!");
                }
            }
        });
    }

    public DepartmentView getDepartmentPage() {
        return this.view;
    }
}