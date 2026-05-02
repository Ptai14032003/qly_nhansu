package view;

import dto.EmployeeDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class EmployeeView extends JPanel { // Đổi từ JFrame sang JPanel
    private DefaultTableModel tableModel;

    public EmployeeView() {
        setLayout(new BorderLayout());

        // 1. Phông chữ cho tiêu đề bảng
        Font headerFont = new Font("Segoe UI", Font.BOLD, 14);
        // 2. Phông chữ cho nội dung bảng
        Font rowFont = new Font("Segoe UI", Font.PLAIN, 13);

        String[] columns = {"Mã NV", "Họ Tên", "Phòng Ban", "Chức Vụ", "Tổng Lương"};
        tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);

        // Tùy chỉnh Header (Tiêu đề cột)
        table.getTableHeader().setFont(headerFont);
        table.getTableHeader().setBackground(new Color(236, 240, 241));
        table.getTableHeader().setForeground(new Color(44, 62, 80));
        table.getTableHeader().setPreferredSize(new Dimension(0, 40)); // Độ cao tiêu đề

        // Tùy chỉnh hàng (Row)
        table.setFont(rowFont);
        table.setRowHeight(35); // Độ cao mỗi hàng (quan trọng để trông giống Web)
        table.setGridColor(new Color(230, 230, 230)); // Màu đường kẻ
        table.setSelectionBackground(new Color(52, 152, 219)); // Màu khi click chọn hàng
        table.setSelectionForeground(Color.WHITE);

        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    public void displayData(List<EmployeeDTO> list) {
        tableModel.setRowCount(0);
        for (EmployeeDTO emp : list) {
            tableModel.addRow(new Object[]{
                    emp.getId(), emp.getEmpName(), emp.getDeptName(),
                    emp.getPosName(), String.format("%,.0f", emp.getTotalSalary())
            });
        }
    }
}