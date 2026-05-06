package view;

import dto.AttendanceDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class AttendanceView extends JPanel {

    private JButton btnIn, btnOut, btnMyHistory, btnBack;
    private JComboBox<LocalDate> cbDates;
    private JTable table;
    private DefaultTableModel model;
    private JPanel topPanel, filterPanel;

    public AttendanceView() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        // ===== TITLE =====
        JLabel title = new JLabel("CHẤM CÔNG NHÂN VIÊN", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // ===== BUTTONS =====
        btnIn = new JButton("Check In");
        btnOut = new JButton("Check Out");
        btnMyHistory = new JButton("Lịch sử của tôi");
        btnBack = new JButton("Quay lại danh sách");

        styleButton(btnIn, new Color(46, 204, 113));
        styleButton(btnOut, new Color(231, 76, 60));
        styleButton(btnMyHistory, new Color(52, 152, 219));
        styleButton(btnBack, new Color(149, 165, 166));

        topPanel = new JPanel();
        topPanel.setBackground(Color.WHITE);
        topPanel.add(btnIn);
        topPanel.add(btnOut);
        topPanel.add(btnMyHistory);
        topPanel.add(btnBack);
        // ===== FILTER (DATE FROM DB) =====
        filterPanel = new JPanel();
        filterPanel.setBackground(Color.WHITE);
        cbDates = new JComboBox<>();
        cbDates.setPreferredSize(new Dimension(150, 30));
        filterPanel.add(new JLabel("Xem theo ngày: "));
        filterPanel.add(cbDates);

        // Gộp top và filter vào một container phía bắc
        JPanel northContainer = new JPanel(new BorderLayout());
        northContainer.setBackground(Color.WHITE);
        northContainer.add(title, BorderLayout.NORTH);
        northContainer.add(topPanel, BorderLayout.CENTER);
        northContainer.add(filterPanel, BorderLayout.SOUTH);

        // ===== TABLE =====
        String[] columnNames = {"Mã NV", "Họ Tên", "Ngày", "Giờ vào", "Giờ ra", "Trạng thái"};
        model = new DefaultTableModel(columnNames, 0);
        table = new JTable(model);
        setupTableStyle();

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        add(northContainer, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    // Hàm phân quyền giao diện
    public void setupViewByRole(int role, boolean isPersonalMode) {
        if (role == 0 || role == 1) { // Trường hợp Admin/Manager
            if (isPersonalMode) {
                // Đang xem lịch sử cá nhân: Hiện nút chấm công và nút Quay lại
                btnIn.setVisible(true);
                btnOut.setVisible(true);
                btnBack.setVisible(true);
                btnMyHistory.setVisible(false);
                filterPanel.setVisible(false);
            } else {
                // Đang quản lý danh sách tổng: Ẩn nút chấm công, hiện lọc ngày
                btnIn.setVisible(false);
                btnOut.setVisible(false);
                btnBack.setVisible(false);
                btnMyHistory.setVisible(true);
                filterPanel.setVisible(true);
            }
        } else {
            // Trường hợp Nhân viên: Luôn hiện nút chấm công, không hiện nút Quay lại
            btnIn.setVisible(true);
            btnOut.setVisible(true);
            btnBack.setVisible(false);
            btnMyHistory.setVisible(false);
            filterPanel.setVisible(false);
        }
    }

    private void setupTableStyle() {
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(52, 152, 219));
        table.getTableHeader().setForeground(Color.WHITE);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 245));
                }
                setHorizontalAlignment(JLabel.CENTER);
                return c;
            }
        };
        table.setDefaultRenderer(Object.class, centerRenderer);
    }

    private void styleButton(JButton btn, Color color) {
        btn.setFocusPainted(false);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setPreferredSize(new Dimension(150, 35));
    }

    // Getters cho Controller
    public JComboBox<LocalDate> getCbDates() {
        return cbDates;
    }

    public void onMyHistoryClick(java.awt.event.ActionListener e) {
        btnMyHistory.addActionListener(e);
    }

    public void onDateChange(java.awt.event.ActionListener e) {
        cbDates.addActionListener(e);
    }

    public void onCheckIn(java.awt.event.ActionListener e) {
        btnIn.addActionListener(e);
    }

    public void onCheckOut(java.awt.event.ActionListener e) {
        btnOut.addActionListener(e);
    }

    public void setDateList(List<LocalDate> dates) {
        cbDates.removeAllItems();
        for (LocalDate d : dates) cbDates.addItem(d);
    }

    public void onBackClick(java.awt.event.ActionListener e) {
        btnBack.addActionListener(e);
    }

    public void render(List<AttendanceDTO> list) {
        model.setRowCount(0);
        for (AttendanceDTO dto : list) {
            model.addRow(new Object[]{
                    dto.getEmpId(),
                    dto.getEmpName(), // Đảm bảo DTO có phương thức này
                    dto.getWorkDate(),
                    dto.getCheckIn(),
                    dto.getCheckOut(),
                    dto.getStatus()
            });
        }
    }
}