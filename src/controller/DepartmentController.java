package controller;

import dao.DepartmentDAO;
import dto.DepartmentDTO;
import view.DepartmentView;
import view.TableActionCellEditor;

import javax.swing.*;
import java.util.List;

public class DepartmentController {
    private final DepartmentView view;
    private final DepartmentDAO dao;

    public DepartmentController() {
        this.view = new DepartmentView();
        this.dao = new DepartmentDAO();
        initEvents();
        refreshData();
    }

    public JPanel getDepartmentPage() {
        return view;
    }

    public void refreshData() {
        // Sử dụng đúng hàm getAll() từ file DAO của bạn
        List<DepartmentDTO> list = dao.getAll();
        view.displayData(list);
    }

    private void initEvents() {
        // 1. XỬ LÝ NÚT THÊM
        view.getBtnAdd().addActionListener(e -> {
            String name = JOptionPane.showInputDialog(view, "Nhập tên phòng ban mới:");

            if (name == null) return; // Người dùng bấm Cancel

            name = name.trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Tên phòng ban không được để trống!");
                return;
            }

            // VALIDATE: Kiểm tra tồn tại
            if (dao.isNameExists(name)) {
                JOptionPane.showMessageDialog(view, "Tên phòng ban '" + name + "' đã tồn tại!");
            } else {
                if (dao.insert(name)) {
                    JOptionPane.showMessageDialog(view, "Thêm thành công!");
                    refreshData();
                } else {
                    JOptionPane.showMessageDialog(view, "Lỗi khi thêm dữ liệu vào Database!");
                }
            }
        });

        // 2. XỬ LÝ NÚT TRONG BẢNG
        view.setupTableAction(new TableActionCellEditor(false) {
            @Override
            public void onDetail(int row) {
            }

            @Override
            public void onEdit(int row) {
                int id = (int) view.getTable().getValueAt(row, 0);
                String currentName = (String) view.getTable().getValueAt(row, 1);

                String newName = JOptionPane.showInputDialog(view, "Sửa tên phòng ban:", currentName);

                if (newName == null) return;

                newName = newName.trim();
                if (newName.isEmpty()) {
                    JOptionPane.showMessageDialog(view, "Tên phòng ban không được để trống!");
                    return;
                }

                // Nếu tên không đổi thì không cần validate/update
                if (newName.equalsIgnoreCase(currentName)) return;

                // VALIDATE: Kiểm tra tồn tại (trừ chính nó ra)
                if (dao.isNameExistsExcept(newName, id)) {
                    JOptionPane.showMessageDialog(view, "Tên phòng ban '" + newName + "' đã tồn tại!");
                } else {
                    if (dao.update(id, newName)) {
                        JOptionPane.showMessageDialog(view, "Cập nhật thành công!");
                        refreshData();
                    } else {
                        JOptionPane.showMessageDialog(view, "Lỗi khi cập nhật dữ liệu!");
                    }
                }
            }

            @Override
            public void onDelete(int row) {
                int id = (int) view.getTable().getValueAt(row, 0);
                String name = (String) view.getTable().getValueAt(row, 1);

                int confirm = JOptionPane.showConfirmDialog(view,
                        "Bạn có chắc muốn xóa phòng ban: " + name + " (ID: " + id + ")?",
                        "Xác nhận xóa", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    if (dao.delete(id)) {
                        JOptionPane.showMessageDialog(view, "Đã xóa thành công!");
                        refreshData();
                    } else {
                        JOptionPane.showMessageDialog(view, "Xóa thất bại! (Có thể do ràng buộc dữ liệu)");
                    }
                }
            }
        });
    }
}