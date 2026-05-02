package view;

import dto.DepartmentDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DepartmentView extends JPanel {
    private DefaultTableModel tableModel;
    private JTable table;
    private JButton btnAdd, btnEdit, btnDelete;

    public DepartmentView() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Header
        JLabel title = new JLabel("QUẢN LÝ PHÒNG BAN", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(title, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(new String[]{"ID", "Tên Phòng Ban"}, 0);
        table = new JTable(tableModel);
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnAdd = new JButton("Thêm phòng ban mới");
        btnEdit = new JButton("Sửa tên");
        btnDelete = new JButton("Xóa");

        // Style nhanh (Nên dùng ContentAreaFilled(false) + Opaque(true) như đã hướng dẫn trước đó)
        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);
        add(btnPanel, BorderLayout.SOUTH);
    }

    public String getSelectedId() {
        int row = table.getSelectedRow();
        return (row != -1) ? table.getValueAt(row, 0).toString() : "";
    }

    public String getSelectedName() {
        int row = table.getSelectedRow();
        return (row != -1) ? table.getValueAt(row, 1).toString() : "";
    }

    public void displayData(List<DepartmentDTO> list) {
        tableModel.setRowCount(0);
        for (DepartmentDTO d : list) tableModel.addRow(new Object[]{d.getId(), d.getDeptName()});
    }

    // Các hàm Dialog
    public String showInputDialog(String title, String message, String initialValue) {
        return (String) JOptionPane.showInputDialog(this, message, title, JOptionPane.QUESTION_MESSAGE, null, null, initialValue);
    }

    public void addBtnAddListener(java.awt.event.ActionListener l) {
        btnAdd.addActionListener(l);
    }

    public void addBtnEditListener(java.awt.event.ActionListener l) {
        btnEdit.addActionListener(l);
    }

    public void addBtnDeleteListener(java.awt.event.ActionListener l) {
        btnDelete.addActionListener(l);
    }
}