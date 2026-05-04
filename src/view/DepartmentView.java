package view;

import dto.DepartmentDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.List;

public class DepartmentView extends JPanel {
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JButton btnAdd;

    public DepartmentView() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // --- Header ---
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setBackground(Color.WHITE);
        JLabel title = new JLabel("QUẢN LÝ PHÒNG BAN", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));
        northPanel.add(title, BorderLayout.NORTH);

        JPanel toolPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolPanel.setBackground(Color.WHITE);
        btnAdd = new JButton("+ Thêm phòng ban mới");
        btnAdd.setBackground(new Color(40, 167, 69));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setPreferredSize(new Dimension(200, 35));
        btnAdd.setFocusPainted(false);
        toolPanel.add(btnAdd);
        northPanel.add(toolPanel, BorderLayout.SOUTH);
        add(northPanel, BorderLayout.NORTH);

        // --- Table ---
        String[] columns = {"ID", "Tên Phòng Ban", "Hành động"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2; // Cột hành động phải trả về true để click được nút
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(45);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    // Hàm gắn sự kiện cho các nút trong bảng (Sửa/Xóa)
    public void setupTableAction(TableActionCellEditor editor) {
        TableColumn col = table.getColumnModel().getColumn(2);
        col.setCellRenderer(new TableActionCellRender(false));
        col.setCellEditor(editor);
    }

    public void displayData(List<DepartmentDTO> list) {
        tableModel.setRowCount(0);
        if (list != null) {
            for (DepartmentDTO d : list) {
                tableModel.addRow(new Object[]{d.getId(), d.getDeptName(), ""});
            }
        }
    }

    public JTable getTable() {
        return table;
    }

    public JButton getBtnAdd() {
        return btnAdd;
    }
}