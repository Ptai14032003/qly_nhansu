package view;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class SalaryView extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> cbMonth, cbYear;
    private JButton btnExportExcel, btnCalculate;
    private JLabel lblTotalPayroll;

    // Màu sắc đồng bộ với EmployeeView
    private final Color COLOR_PRIMARY = new Color(52, 152, 219);
    private final Color COLOR_SUCCESS = new Color(39, 174, 96);
    private final Color COLOR_DANGER = new Color(231, 76, 60);
    private final Color COLOR_TEXT_MAIN = new Color(44, 62, 80); // Màu xanh đen đậm

    public SalaryView() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 246, 250));

        initTopPanel();
        initTablePanel();
        initBottomPanel();
    }

    private void initTopPanel() {
        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);
        container.setBorder(BorderFactory.createEmptyBorder(20, 25, 15, 25));

        // 1. KHỞI TẠO CÁC ĐỐI TƯỢNG TRƯỚC (Quan trọng nhất)
        cbMonth = new JComboBox<>(new String[]{
                "Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6",
                "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"
        });

        cbYear = new JComboBox<>(new String[]{"2024", "2025", "2026"});

        btnCalculate = new JButton("Tính toán lương");
        btnCalculate.setBackground(COLOR_PRIMARY);
        btnCalculate.setForeground(Color.WHITE);
        btnCalculate.setFocusPainted(false); // Làm nút đẹp hơn

        // 2. THIẾT LẬP GIÁ TRỊ MẶC ĐỊNH (Sau khi đã khởi tạo xong)
        LocalDate now = LocalDate.now();
        cbMonth.setSelectedIndex(now.getMonthValue() - 1);
        cbYear.setSelectedItem(String.valueOf(now.getYear()));

        // 3. THIẾT LẬP GIAO DIỆN
        JLabel lblTitle = new JLabel("Bảng Lương Nhân Viên");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(COLOR_TEXT_MAIN); // Sử dụng màu đã khai báo

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filterPanel.setOpaque(false);

        filterPanel.add(new JLabel("Chọn kỳ lương:"));
        filterPanel.add(cbMonth);
        filterPanel.add(cbYear);
        filterPanel.add(btnCalculate);

        // 4. ADD VÀO CONTAINER THEO THỨ TỰ
        container.add(lblTitle, BorderLayout.NORTH);
        container.add(filterPanel, BorderLayout.SOUTH);

        add(container, BorderLayout.NORTH);
    }

    private void initTablePanel() {
        // Các cột chuyên sâu về lương
        String[] columns = {
                "Mã NV", "Họ Tên", "Lương CB", "Hệ số", "Phụ cấp",
                "Thưởng", "Phạt", "Thực nhận"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(249, 250, 251));
                }
                return c;
            }
        };

        formatTable();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 233, 237)));

        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);
        container.setBorder(BorderFactory.createEmptyBorder(0, 25, 10, 25));
        container.add(scrollPane, BorderLayout.CENTER);
        add(container, BorderLayout.CENTER);
    }

    private void formatTable() {
        table.setRowHeight(45);
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.setShowVerticalLines(false);

        // Renderer cho tiền tệ (Cột 2, 4, 5, 6, 7)
        DefaultTableCellRenderer currencyRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(RIGHT);
                if (column == 7) { // Cột Thực nhận
                    setForeground(COLOR_SUCCESS);
                    setFont(getFont().deriveFont(Font.BOLD));
                } else if (column == 6) { // Cột Phạt
                    setForeground(COLOR_DANGER);
                } else {
                    setForeground(Color.BLACK);
                }
                return this;
            }
        };

        table.getColumnModel().getColumn(2).setCellRenderer(currencyRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(currencyRenderer);
        table.getColumnModel().getColumn(5).setCellRenderer(currencyRenderer);
        table.getColumnModel().getColumn(6).setCellRenderer(currencyRenderer);
        table.getColumnModel().getColumn(7).setCellRenderer(currencyRenderer);
    }

    private void initBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 25, 20, 25));

        lblTotalPayroll = new JLabel("Tổng chi trả: 0 VNĐ");
        lblTotalPayroll.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotalPayroll.setForeground(COLOR_TEXT_MAIN);

        btnExportExcel = new JButton("Xuất file Excel");
        btnExportExcel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        bottomPanel.add(lblTotalPayroll, BorderLayout.WEST);
        bottomPanel.add(btnExportExcel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // Hàm đổ dữ liệu từ Controller
    public void displaySalaryData(List<Object[]> dataList) {
        tableModel.setRowCount(0);
        double total = 0;
        for (Object[] row : dataList) {
            tableModel.addRow(row);
            // Giả sử thực nhận ở vị trí index 7
            total += Double.parseDouble(row[7].toString().replace(",", "").replace(" VNĐ", ""));
        }
        lblTotalPayroll.setText(String.format("Tổng chi trả: %,.0f VNĐ", total));
    }

    public int getSelectedMonth() {
        // Trả về số tháng từ 1 đến 12 (ComboBox index bắt đầu từ 0)
        return cbMonth.getSelectedIndex() + 1;
    }

    public int getSelectedYear() {
        // Lấy chuỗi năm đang chọn và chuyển về kiểu int
        return Integer.parseInt(cbYear.getSelectedItem().toString());
    }

    public JButton getBtnCalculate() {
        // Trả về đối tượng nút bấm để Controller có thể bắt sự kiện click
        return btnCalculate;
    }
}