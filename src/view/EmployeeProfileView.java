package view;

import dto.EmployeeDTO;
import javax.swing.*;
import java.awt.*;

public class EmployeeProfileView extends JPanel {
    private JLabel lblName, lblDept, lblPos, lblEmail,lblGender,lblBirtday, lblAddress, lblSalary, lblAvatar,lblPhone,lblId,lblIdCard,lblAl,lblBonus;
    private JButton btnEdit;
    public EmployeeProfileView() {
        setLayout(new BorderLayout());

        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 247)); // Màu nền xám nhạt hiện đại

        // 1. Tiêu đề phía trên
        JLabel title = new JLabel("THÔNG TIN CÁ NHÂN");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        title.setHorizontalAlignment(JLabel.CENTER);
        add(title, BorderLayout.NORTH);

        // 2. Panel chính chứa thông tin
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setOpaque(false); // Để lộ màu nền của lớp cha
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 15, 8, 15); // Khoảng cách giữa các dòng
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- KHU VỰC AVATAR ---
        lblAvatar = new JLabel();
        lblAvatar.setPreferredSize(new Dimension(150, 150));
        lblAvatar.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2));
        lblAvatar.setHorizontalAlignment(JLabel.CENTER);

        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2; // Chiếm cả 2 cột
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(lblAvatar, gbc);

        // --- KHU VỰC CHI TIẾT (Dòng tiếp theo) ---
        gbc.gridwidth = 1; // Trở lại 1 cột
        gbc.anchor = GridBagConstraints.WEST;

        // Ví dụ một hàng thông tin: Mã nhân viên
        addInfoRow(mainPanel, "Mã nhân viên:", lblId = new JLabel("..."), gbc, 1);
        addInfoRow(mainPanel, "Họ tên:", lblName = new JLabel("..."), gbc, 2);
        addInfoRow(mainPanel, "Email:", lblEmail = new JLabel("..."), gbc, 3);
        addInfoRow(mainPanel, "Số CCCD:", lblIdCard = new JLabel("..."), gbc, 4);
        addInfoRow(mainPanel, "Số điện thoại:", lblPhone = new JLabel("..."), gbc, 5);
        addInfoRow(mainPanel, "Phòng ban:", lblDept = new JLabel("..."), gbc, 6);
        addInfoRow(mainPanel, "Chức vụ:", lblPos = new JLabel("..."), gbc, 7);
        addInfoRow(mainPanel, "Địa chỉ:", lblAddress = new JLabel("..."), gbc, 8);
        addInfoRow(mainPanel, "Lương cơ bản:", lblSalary = new JLabel("..."), gbc, 9);
        addInfoRow(mainPanel, "Phụ cấp:", lblAl= new JLabel("..."), gbc, 10);
        addInfoRow(mainPanel, "Thưởng:", lblBonus = new JLabel("..."), gbc, 11);

        // Dùng JScrollPane để đề phòng màn hình nhỏ
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // Căn phải cho chuyên nghiệp
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 20)); // Cách lề phải và lề dưới

        btnEdit = new JButton("Thay đổi thông tin");
        btnEdit.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnEdit.setFocusPainted(false);

        // Tạo phong cách hiện đại cho nút (Màu xanh dương, chữ trắng)
        btnEdit.setBackground(new Color(0, 122, 255));
        btnEdit.setForeground(Color.WHITE);
        btnEdit.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Đổi con trỏ chuột khi di qua
        btnEdit.setPreferredSize(new Dimension(180, 40));

        buttonPanel.add(btnEdit);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    // Hàm bổ trợ để thêm hàng thông tin cho gọn code
    private void addInfoRow(JPanel panel, String labelText, JLabel valueLabel, GridBagConstraints gbc, int row) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(new Color(80, 80, 80));

        valueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        gbc.gridy = row;
        gbc.gridx = 0; // Cột 1: Nhãn
        gbc.weightx = 0.3;
        panel.add(label, gbc);

        gbc.gridx = 1; // Cột 2: Giá trị
        gbc.weightx = 0.7;
        panel.add(valueLabel, gbc);
    }

    // Hàm để Controller đẩy dữ liệu vào
    public void setProfileData(EmployeeDTO emp) {
        if (emp != null) {
            if (emp.getAvatar() != null && !emp.getAvatar().isEmpty()) {
                ImageIcon icon = new ImageIcon(emp.getAvatar());
                // Scale ảnh lại cho vừa khung (ví dụ 100x100)
                Image img = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                lblAvatar.setIcon(new ImageIcon(img));
            } else {
                lblAvatar.setIcon(null);
                lblAvatar.setText("No Image");
            }
            lblId.setText(String.format("NV %d",emp.getId()));
            lblName.setText(emp.getEmpName());
            lblDept.setText(emp.getDeptName());
            lblIdCard.setText(emp.getIdCard());
            lblEmail.setText(emp.getEmail());
            lblPhone.setText(emp.getPhone());
            lblPos.setText(emp.getPosName());
            lblAddress.setText(emp.getAddress());
            lblSalary.setText(String.format("%,.0f VNĐ", emp.getBaseSalary()));
            lblAl.setText(String.format("%,.0f VNĐ", emp.getAllowance()));
            lblBonus.setText(String.format("%,.0f VNĐ", emp.getBonus()));
        }
    }
    public JButton getBtnEdit() {
        return this.btnEdit; // btnEdit là nút "Thay đổi thông tin" bạn đã tạo ở các bước trước
    }
}