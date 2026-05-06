package view;

import dto.EmployeeDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class EmployeeView extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnAdd;
    // Vẫn giữ các handlers để Controller truyền logic vào
    private Consumer<Integer> detailHandler; // THÊM DÒNG NÀY
    private Consumer<Integer> editHandler, deleteHandler;
    private Consumer<Void> addHandler;

    private JTextField txtSearch;
    private JComboBox<String> cbSort;
    private JComboBox<Object> cbDeptFilter;
    private JButton btnPrev, btnNext;
    private JLabel lblPageInfo;

    public EmployeeView() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // 1. Khởi tạo thanh công cụ và phân trang
        initTopPanel();
        initPaginationPanel();

        // 2. Cấu hình bảng (Cột Hành động ở index 8)
        String[] columns = {"Mã NV", "Họ Tên", "Email", "SĐT", "Lương", "Phòng Ban", "Chức Vụ", "Ảnh", "Hành động"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                // Cột Hành động (index 8) phải trả về true để bấm được nút
                return col == 8;
            }
        };

        table = new JTable(tableModel);
        applyCustomLook();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        add(scrollPane, BorderLayout.CENTER);
    }

    private void initTopPanel() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(Color.WHITE);
        // --- DÒNG 0: TIÊU ĐỀ TRANG ---
        JLabel lblTitle = new JLabel("QUẢN LÝ NHÂN VIÊN", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22)); // Font lớn, đậm
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT); // Căn giữa theo BoxLayout
        lblTitle.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0)); // Tạo khoảng cách
        container.add(lblTitle);
        // --- DÒNG 1: NÚT THÊM ---
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        actionPanel.setOpaque(false);

        btnAdd = new JButton("<html><b>+</b> Thêm nhân viên mới</html>");
        btnAdd.setBackground(new Color(40, 167, 69));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setPreferredSize(new Dimension(180, 35));
        btnAdd.addActionListener(e -> {
            if (addHandler != null) addHandler.accept(null);
        });
        actionPanel.add(btnAdd);

        // --- DÒNG 2: THANH TÌM KIẾM ---
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        searchPanel.setOpaque(false);

        txtSearch = new JTextField(15);
        cbSort = new JComboBox<>(new String[]{
                "Mặc định", "Tên (A-Z)", "Tên (Z-A)",
                "Lương: Thấp đến Cao", "Lương: Cao đến Thấp"
        });
        cbDeptFilter = new JComboBox<>();
        cbDeptFilter.addItem("Tất cả phòng ban");

        searchPanel.add(new JLabel("Tìm kiếm:"));
        searchPanel.add(txtSearch);
        searchPanel.add(new JLabel("Phòng ban:"));
        searchPanel.add(cbDeptFilter);
        searchPanel.add(new JLabel("Sắp xếp:"));
        searchPanel.add(cbSort);

        container.add(actionPanel);
        container.add(searchPanel);
        add(container, BorderLayout.NORTH);
    }

    private void initPaginationPanel() {
        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        paginationPanel.setBackground(Color.WHITE);

        btnPrev = new JButton("< Trước");
        btnNext = new JButton("Sau >");
        lblPageInfo = new JLabel("Trang 1 / 1");

        paginationPanel.add(btnPrev);
        paginationPanel.add(lblPageInfo);
        paginationPanel.add(btnNext);
        add(paginationPanel, BorderLayout.SOUTH);
    }

    private void applyCustomLook() {
        // 1. Cấu hình cơ bản cho bảng
        table.setRowHeight(45);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setReorderingAllowed(false); // Không cho kéo đổi thứ tự cột

        // Căn giữa cột ID (cột 0)
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);

        // 2. Cấu hình cột Hành động (cột 8)
        TableColumn actionCol = table.getColumnModel().getColumn(8);
        actionCol.setPreferredWidth(220); // Độ rộng đủ cho 3 nút

        // Gán Renderer với tham số true
        actionCol.setCellRenderer(new TableActionCellRender(true));

        // Gán Editor với tham số true và kết nối với các Handler
        actionCol.setCellEditor(new TableActionCellEditor(true) {
            @Override
            public void onDetail(int row) {
                if (detailHandler != null) {
                    // Chuyển đổi index hiển thị sang index model để lấy ID chính xác
                    int modelRow = table.convertRowIndexToModel(row);
                    int id = (int) table.getModel().getValueAt(modelRow, 0);
                    detailHandler.accept(id);
                }
            }

            @Override
            public void onEdit(int row) {
                if (editHandler != null) {
                    int modelRow = table.convertRowIndexToModel(row);
                    int id = (int) table.getModel().getValueAt(modelRow, 0);
                    editHandler.accept(id);
                }
            }

            @Override
            public void onDelete(int row) {
                if (deleteHandler != null) {
                    int modelRow = table.convertRowIndexToModel(row);
                    int id = (int) table.getModel().getValueAt(modelRow, 0);
                    deleteHandler.accept(id);
                }
            }
        });
    }

    public void displayData(List<EmployeeDTO> list) {
        tableModel.setRowCount(0);
        for (EmployeeDTO emp : list) {
            tableModel.addRow(new Object[]{
                    emp.getId(),
                    emp.getEmpName(),
                    emp.getEmail(),
                    emp.getPhone(),
                    String.format("%,.0f VNĐ", emp.getBaseSalary() * emp.getCoefficient()),
                    emp.getDeptName(),
                    emp.getPosName(),
                    (emp.getAvatar() != null ? "Đã có" : "N/A"),
                    "" // Cột hành động để trống để Renderer vẽ nút
            });
        }
    }

    // Các hàm đăng ký sự kiện từ Controller
    public void onAdd(Consumer<Void> h) {
        this.addHandler = h;
    }

    public void onEdit(Consumer<Integer> h) {
        this.editHandler = h;
    }

    public void onDelete(Consumer<Integer> h) {
        this.deleteHandler = h;
    }

    // Getters cho Controller
    public JTable getTable() {
        return table;
    }

    public JTextField getTxtSearch() {
        return txtSearch;
    }

    public JComboBox<String> getCbSort() {
        return cbSort;
    }

    public JComboBox<Object> getCbDeptFilter() {
        return cbDeptFilter;
    }

    public JButton getBtnPrev() {
        return btnPrev;
    }

    public JButton getBtnNext() {
        return btnNext;
    }

    public void onDetail(Consumer<Integer> h) {
        this.detailHandler = h;
    }

    public void setPageInfo(int current, int total) {
        lblPageInfo.setText("Trang " + current + " / " + total);
    }

    // ĐÃ XÓA CÁC INNER CLASS CŨ (ActionPanel, ActionPanelRenderer, ActionPanelEditor)
}