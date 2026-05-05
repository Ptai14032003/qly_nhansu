package view;

import dto.User;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class UserView extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private DeleteCallback deleteCallback;
    private AddCallback addCallback;
    private PagingCallback pagingCallback;

    // Khai báo các thành phần tìm kiếm
    private JTextField txtSearch;
    private JComboBox<String> cbSort;
    private JButton btnSearch;

    private final Color PRIMARY_COLOR = new Color(52, 152, 219);
    private final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private final Color DANGER_COLOR = new Color(231, 76, 60);
    private final Color BACKGROUND_COLOR = new Color(245, 246, 250);

    public UserView() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // 1. GỌI HÀM INIT TOP BAR TẠI ĐÂY (Thay thế cho đoạn topBar cũ)
        initTopBar();

        // 2. TABLE CONTAINER
        table = new JTable();
        setupTable();
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 221, 225)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);

        // 3. BOTTOM PANEL (Phân trang)
        initBottomBar();
    }

    private void initTopBar() {
        // Sử dụng GridBagLayout hoặc Box để row2 không bị dính sát row1
        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setBackground(BACKGROUND_COLOR);
        topContainer.setBorder(new EmptyBorder(0, 0, 15, 0));

        // Dòng 1: Tiêu đề và nút Thêm
        JPanel row1 = new JPanel(new BorderLayout());
        row1.setBackground(BACKGROUND_COLOR);
        JLabel lblTitle = new JLabel("Quản lý tài khoản hệ thống");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        JButton btnAdd = new JButton("+ Tạo user");
        stylePrimaryButton(btnAdd, SUCCESS_COLOR);
        btnAdd.addActionListener(e -> { if (addCallback != null) addCallback.onAdd(); });
        row1.add(lblTitle, BorderLayout.WEST);
        row1.add(btnAdd, BorderLayout.EAST);

        // Dòng 2: Thanh tìm kiếm và Sắp xếp
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 10));
        row2.setBackground(BACKGROUND_COLOR);

        txtSearch = new JTextField(20);
        txtSearch.setPreferredSize(new Dimension(200, 30));
        btnSearch = new JButton("Tìm kiếm");
        styleSecondaryButton(btnSearch);

        cbSort = new JComboBox<>(new String[]{
                "Mới nhất", "Cũ nhất", "Tên A-Z", "Quyền hạn"
        });
        cbSort.setPreferredSize(new Dimension(150, 30));

        row2.add(new JLabel("Tìm kiếm: "));
        row2.add(txtSearch);
        row2.add(Box.createHorizontalStrut(10));
        row2.add(btnSearch);
        row2.add(Box.createHorizontalStrut(30));
        row2.add(new JLabel("Sắp xếp: "));
        row2.add(cbSort);

        topContainer.add(row1);
        topContainer.add(row2);
        add(topContainer, BorderLayout.NORTH);

        // Sự kiện Search & Sort
        btnSearch.addActionListener(e -> {
            if(pagingCallback != null) pagingCallback.onSearch(txtSearch.getText());
        });
        cbSort.addActionListener(e -> {
            if(pagingCallback != null) pagingCallback.onSearch(txtSearch.getText());
        });
    }

    private void initBottomBar() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(BACKGROUND_COLOR);
        bottomPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        JPanel pagingPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pagingPanel.setBackground(BACKGROUND_COLOR);

        JButton btnPrev = new JButton("Trang trước");
        JButton btnNext = new JButton("Trang sau");
        styleSecondaryButton(btnPrev);
        styleSecondaryButton(btnNext);

        btnNext.addActionListener(e -> { if(pagingCallback != null) pagingCallback.onNext(); });
        btnPrev.addActionListener(e -> { if(pagingCallback != null) pagingCallback.onPrev(); });

        pagingPanel.add(btnPrev);
        pagingPanel.add(btnNext);
        bottomPanel.add(pagingPanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // Các hàm Helper Style và Table logic (giữ nguyên của bạn)
    private void setupTable() {
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setReorderingAllowed(false);
        table.setShowVerticalLines(false);
    }

    private void stylePrimaryButton(JButton btn, Color color) {
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(120, 35));
    }

    private void styleSecondaryButton(JButton btn) {
        btn.setBackground(Color.WHITE);
        btn.setForeground(new Color(127, 140, 141));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199)));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public void setUserTable(List<User> users) {
        String[] cols = {"ID", "Username", "Quyền hạn", "Nhân viên", "Hành động"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return col == 4; }
        };
        for (User u : users) {
            String roleText = switch (u.getRole()) {
                case 0 -> "ADMIN"; case 1 -> "MANAGER"; case 2 -> "EMPLOYEE"; default -> "UNKNOWN";
            };
            tableModel.addRow(new Object[]{ u.getId(), u.getUsername(), roleText, u.getEmpName(), "" });
        }
        table.setModel(tableModel);
        table.getColumnModel().getColumn(2).setCellRenderer(new RoleColorRenderer());
        table.getColumnModel().getColumn(4).setCellRenderer(new ActionRenderer());
        table.getColumnModel().getColumn(4).setCellEditor(new ActionEditor(table, this));
    }

    // --- GETTERS ---
    public JComboBox<String> getCbSort() { return cbSort; }
    public String getSearchKeyword() { return txtSearch.getText(); }

    // --- CALLBACK SETTERS ---
    public void setAddAction(AddCallback cb) { this.addCallback = cb; }
    public void setDeleteAction(DeleteCallback cb) { this.deleteCallback = cb; }
    public void setPagingAction(PagingCallback cb) { this.pagingCallback = cb; }
    public void showMessage(String msg) { JOptionPane.showMessageDialog(this, msg); }

    // --- INTERFACES ---
    public interface AddCallback { void onAdd(); }
    public interface DeleteCallback { void onDelete(int userId); }
    public interface PagingCallback { void onNext(); void onPrev(); void onSearch(String kw); }

    // --- INNER CLASSES (Renderer/Editor giữ nguyên logic) ---
    class RoleColorRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            lbl.setHorizontalAlignment(JLabel.CENTER);
            String role = value.toString();
            lbl.setForeground(role.equals("ADMIN") ? DANGER_COLOR : role.equals("MANAGER") ? PRIMARY_COLOR : new Color(243, 156, 18));
            return lbl;
        }
    }

    class ActionRenderer extends JPanel implements TableCellRenderer {
        private JButton btnEdit = new JButton("Sửa");
        private JButton btnDelete = new JButton("Xóa");
        public ActionRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 8, 5));
            setBackground(Color.WHITE);
            stylePrimaryButton(btnEdit, PRIMARY_COLOR);
            stylePrimaryButton(btnDelete, DANGER_COLOR);
            btnEdit.setPreferredSize(new Dimension(60, 25));
            btnDelete.setPreferredSize(new Dimension(60, 25));

            add(btnEdit);
            add(btnDelete);
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            return this;
        }
    }

    class ActionEditor extends AbstractCellEditor implements TableCellEditor {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 5));
        JButton btnEdit = new JButton("Sửa");
        JButton btnDelete = new JButton("Xóa");
        int row;
        public ActionEditor(JTable table, UserView view) {
            stylePrimaryButton(btnEdit, PRIMARY_COLOR);
            stylePrimaryButton(btnDelete, DANGER_COLOR);

            btnEdit.setPreferredSize(new Dimension(60, 25));
            btnDelete.setPreferredSize(new Dimension(60, 25));

            panel.add(btnEdit);
            panel.add(btnDelete);
            btnDelete.addActionListener(e -> {
                int userId = (int) table.getValueAt(row, 0);
                view.deleteCallback.onDelete(userId);
                fireEditingStopped();
            });
        }
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.row = row;
            panel.setBackground(table.getSelectionBackground());
            return panel;
        }
        @Override public Object getCellEditorValue() { return null; }
    }
}