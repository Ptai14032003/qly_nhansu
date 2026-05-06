package view;

import dto.EmployeeDTO;
import javax.swing.*;
import java.awt.*;
import java.io.File;

public class EmployeeEditView extends JPanel {
    // Các trường được phép sửa
    private JTextField txtEmail, txtPhone, txtAddress;
    private JLabel lblAvatar;
    private JButton btnSave, btnCancel, btnChooseImg;
    private String selectedImagePath = "";
    // Các trường chỉ xem (không được sửa)
    private JLabel lblName, lblId, lblDept, lblPos, lblSalary;

    public EmployeeEditView() {
        setLayout(new BorderLayout());
        initComponents();
    }

    private void initComponents() {
        setBackground(new Color(245, 245, 247));

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Avatar & Nút chọn ảnh ---
        JPanel avatarPanel = new JPanel(new BorderLayout(5, 5));
        avatarPanel.setOpaque(false);
        lblAvatar = new JLabel();
        lblAvatar.setPreferredSize(new Dimension(120, 120));
        lblAvatar.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        btnChooseImg = new JButton("Chọn ảnh");
        avatarPanel.add(lblAvatar, BorderLayout.CENTER);
        avatarPanel.add(btnChooseImg, BorderLayout.SOUTH);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(avatarPanel, gbc);

        // --- Các trường thông tin ---
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;

        // Chỉ xem
        addReadOnlyRow(mainPanel, "Mã nhân viên:", lblId = new JLabel(), gbc, 1);
        addReadOnlyRow(mainPanel, "Họ tên:", lblName = new JLabel(), gbc, 2);

        // Được sửa (Sử dụng JTextField)
        addEditRow(mainPanel, "Email:", txtEmail = new JTextField(20), gbc, 3);
        addEditRow(mainPanel, "Số điện thoại:", txtPhone = new JTextField(20), gbc, 4);
        addEditRow(mainPanel, "Địa chỉ:", txtAddress = new JTextField(20), gbc, 5);

        // Chỉ xem tiếp
        addReadOnlyRow(mainPanel, "Phòng ban:", lblDept = new JLabel(), gbc, 6);
        addReadOnlyRow(mainPanel, "Chức vụ:", lblPos = new JLabel(), gbc, 7);
        addReadOnlyRow(mainPanel, "Lương:", lblSalary = new JLabel(), gbc, 8);

        // --- Nút bấm ---
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        southPanel.setOpaque(false);
        btnSave = new JButton("Lưu thay đổi");
        btnCancel = new JButton("Hủy bỏ");
        southPanel.add(btnCancel);
        southPanel.add(btnSave);

        add(new JLabel("CHỈNH SỬA THÔNG TIN", JLabel.CENTER), BorderLayout.NORTH);
        add(new JScrollPane(mainPanel), BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
    }

    private void addReadOnlyRow(JPanel p, String text, JLabel lbl, GridBagConstraints gbc, int row) {
        gbc.gridy = row; gbc.gridx = 0; p.add(new JLabel(text), gbc);
        gbc.gridx = 1; p.add(lbl, gbc);
        lbl.setForeground(Color.GRAY); // Làm mờ để biết không sửa được
    }

    private void addEditRow(JPanel p, String text, JTextField txt, GridBagConstraints gbc, int row) {
        gbc.gridy = row; gbc.gridx = 0; p.add(new JLabel(text), gbc);
        gbc.gridx = 1; p.add(txt, gbc);
    }
    public void setEditData(EmployeeDTO emp) {
        if (emp != null) {
            // Đổ dữ liệu vào các nhãn chỉ xem
            lblId.setText("NV " + emp.getId());
            lblName.setText(emp.getEmpName());
            lblDept.setText(emp.getDeptName());
            lblPos.setText(emp.getPosName());
            lblSalary.setText(String.format("%,.0f VNĐ", emp.getBaseSalary()));

            // Đổ dữ liệu vào các ô cho phép sửa
            txtEmail.setText(emp.getEmail());
            txtPhone.setText(emp.getPhone());
            txtAddress.setText(emp.getAddress());

            // Hiển thị ảnh đại diện cũ
            if (emp.getAvatar() != null && !emp.getAvatar().isEmpty()) {
                ImageIcon icon = new ImageIcon(emp.getAvatar());
                Image img = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                lblAvatar.setIcon(new ImageIcon(img));
            }
        }
    }
    private void btnChooseImageActionPerformed() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            // Lưu đường dẫn này vào biến để tí nữa gán cho DTO
            this.selectedImagePath = selectedFile.getAbsolutePath();

            // Hiển thị tạm lên Label để người dùng thấy
            ImageIcon icon = new ImageIcon(new ImageIcon(selectedImagePath)
                    .getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH));
            lblAvatar.setIcon(icon);
        }
    }
    // 2. Hàm đóng gói dữ liệu để lưu (Sửa lỗi getEmployeeDataFromInput)
    public EmployeeDTO getEmployeeDataFromInput() {
        EmployeeDTO emp = new EmployeeDTO();

        // Lấy ID từ nhãn (cần cắt chữ "NV " đi để lấy số)
        try {
            String idText = lblId.getText().replace("NV ", "").trim();
            emp.setId(Integer.parseInt(idText));
        } catch (Exception e) {
            // Xử lý nếu ID không hợp lệ
        }
        emp.setEmpName(lblName.getText().trim());
        // Lấy thông tin từ các ô JTextField
        emp.setEmail(txtEmail.getText().trim());
        emp.setPhone(txtPhone.getText().trim());
        emp.setAddress(txtAddress.getText().trim());

        emp.setAvatar(this.selectedImagePath);

        return emp;
    }
    // Getters cho các nút để Controller xử lý
    public JButton getBtnSave() { return btnSave; }
    public JButton getBtnCancel() { return btnCancel; }

}