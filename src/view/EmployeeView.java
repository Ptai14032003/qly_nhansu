package view;

import dto.EmployeeDTO;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class EmployeeView extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnAdd;
    private Consumer<Integer> detailHandler, editHandler, deleteHandler;
    private Consumer<Void> addHandler;

    public EmployeeView() {
        setLayout(new BorderLayout());

        // 1. Thanh công cụ phía trên
        initTopPanel();

        // 2. Cấu hình bảng
        String[] columns = {"Mã NV", "Họ Tên", "Email", "SĐT", "Lương", "Phòng Ban", "Chức Vụ", "Ảnh", "Hành động"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 8;
            }
        };
        table = new JTable(tableModel);
        applyCustomLook();

        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void initTopPanel() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        topPanel.setBackground(Color.WHITE);

        btnAdd = new JButton("<html><b>+</b> Thêm nhân viên mới</html>");
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnAdd.setBackground(new Color(46, 204, 113));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAdd.addActionListener(e -> {
            if (addHandler != null) addHandler.accept(null);
        });

        topPanel.add(btnAdd);
        add(topPanel, BorderLayout.NORTH);
    }

    private void applyCustomLook() {
        table.setRowHeight(40);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(230, 240, 250));

        // Căn giữa các cột số liệu
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        int[] centerCols = {0, 3, 7};
        for (int col : centerCols) table.getColumnModel().getColumn(col).setCellRenderer(center);

        // Cột hành động
        TableColumn actCol = table.getColumnModel().getColumn(8);
        actCol.setPreferredWidth(220);
        actCol.setCellRenderer(new ActionPanelRenderer());
        actCol.setCellEditor(new ActionPanelEditor());
    }

    public void displayData(List<EmployeeDTO> list) {
        tableModel.setRowCount(0);
        for (EmployeeDTO emp : list) {
            tableModel.addRow(new Object[]{
                    emp.getId(), emp.getEmpName(), emp.getEmail(), emp.getPhone(),
                    String.format("%,.0f VNĐ", emp.getTotalSalary()),
                    emp.getDeptName(), emp.getPosName(),
                    (emp.getAvatar() != null ? "Đã có" : "N/A"), ""
            });
        }
    }

    public void onAdd(Consumer<Void> h) {
        this.addHandler = h;
    }

    public void onDetail(Consumer<Integer> h) {
        this.detailHandler = h;
    }

    public void onEdit(Consumer<Integer> h) {
        this.editHandler = h;
    }

    public void onDelete(Consumer<Integer> h) {
        this.deleteHandler = h;
    }

    public JTable getTable() {
        return table;
    }

    // --- Inner Classes for Action Buttons ---
    class ActionPanel extends JPanel {
        public JButton bD = new JButton("Xem"), bE = new JButton("Sửa"), bDel = new JButton("Xóa");

        public ActionPanel() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 8, 5));
            setOpaque(false);
            format(bD, new Color(52, 152, 219));
            format(bE, new Color(241, 196, 15));
            format(bDel, new Color(231, 76, 60));
            add(bD);
            add(bE);
            add(bDel);
        }

        private void format(JButton b, Color bg) {
            b.setBackground(bg);
            b.setForeground(bg == Color.YELLOW ? Color.BLACK : Color.WHITE);
            b.setPreferredSize(new Dimension(60, 25));
            b.setFocusPainted(false);
            b.setBorderPainted(false);
        }
    }

    class ActionPanelRenderer extends ActionPanel implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
            setBackground(s ? t.getSelectionBackground() : t.getBackground());
            return this;
        }
    }

    class ActionPanelEditor extends AbstractCellEditor implements TableCellEditor {
        private ActionPanel p = new ActionPanel();

        public ActionPanelEditor() {
            p.bD.addActionListener(e -> fireAction(detailHandler));
            p.bE.addActionListener(e -> fireAction(editHandler));
            p.bDel.addActionListener(e -> fireAction(deleteHandler));
        }

        private void fireAction(Consumer<Integer> h) {
            if (h != null) h.accept((int) table.getValueAt(table.getSelectedRow(), 0));
            fireEditingStopped();
        }

        @Override
        public Component getTableCellEditorComponent(JTable t, Object v, boolean s, int r, int c) {
            return p;
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }
    }
}