package view;

import com.toedter.calendar.JDateChooser;
import dto.EmployeeDTO;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

public class AddEmployeeDialog extends JDialog {
    private final JTextField txtName = new JTextField(20);
    private final JTextField txtPhone = new JTextField(20);
    private final JTextField txtEmail = new JTextField(20);
    private final JTextField txtSalary = new JTextField("0", 20);
    private final JTextField txtAllowance = new JTextField("0", 20);
    private final JTextField txtAddress = new JTextField(20);

    private final JTextField txtIdCard = new JTextField(20);
    private final JTextField txtEducation = new JTextField(20);
    private final JTextField txtExperience = new JTextField(20);

    private final JTextField txtAvatarPath = new JTextField(15);
    private final JDateChooser dcBirthday = new JDateChooser();
    private final JDateChooser dcHireDate = new JDateChooser();
    private final JComboBox<String> cbGender = new JComboBox<>(new String[]{"Nữ", "Nam"});
    private final JComboBox<ComboboxItem> cbDept = new JComboBox<>();
    private final JComboBox<ComboboxItem> cbPos = new JComboBox<>();

    private final JLabel lblAvatarPreview = new JLabel("Chưa có ảnh", SwingConstants.CENTER);
    private boolean confirmed = false;

    public static class ComboboxItem {
        public int id;
        public String name;

        public ComboboxItem(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public AddEmployeeDialog(Frame parent) {
        super(parent, "Thêm nhân viên mới", true);
        setupUI(parent);
    }

    // --- HÀM ĐỔ DỮ LIỆU LÊN FORM ĐỂ SỬA ---
    public void setEmployeeData(EmployeeDTO emp) {
        if (emp == null) return;

        // 1. Khai báo định dạng để ép số về dạng chuỗi bình thường (không hiện 1.5E7)
        java.text.DecimalFormat df = new java.text.DecimalFormat("#");
        df.setMaximumFractionDigits(0);

        // 2. Đổ các thông tin văn bản cơ bản
        txtName.setText(emp.getEmpName());
        txtPhone.setText(emp.getPhone());
        txtEmail.setText(emp.getEmail());
        txtAddress.setText(emp.getAddress());
        txtIdCard.setText(emp.getIdCard());
        txtEducation.setText(emp.getEducation());
        txtExperience.setText(emp.getExperience());

        // 3. SỬA LỖI LƯƠNG: Sử dụng df.format thay vì String.valueOf
        txtSalary.setText(df.format(emp.getBaseSalary()));
        txtAllowance.setText(df.format(emp.getAllowance()));

        // 4. Xử lý ngày tháng
        if (emp.getBirthday() != null) dcBirthday.setDate(emp.getBirthday());
        if (emp.getHireDate() != null) dcHireDate.setDate(emp.getHireDate());

        // 5. Giới tính
        if (emp.getGender() != null) cbGender.setSelectedIndex(emp.getGender());

        // 6. Phòng ban & Chức vụ
        setSelectedComboboxItem(cbDept, emp.getDeptId());
        setSelectedComboboxItem(cbPos, emp.getPosId());

        // 7. Hiển thị ảnh đại diện (Tận dụng hàm updateAvatar để code sạch hơn)
        if (emp.getAvatar() != null && !emp.getAvatar().isEmpty()) {
            updateAvatarPreview(emp.getAvatar());
        } else {
            lblAvatarPreview.setIcon(null);
            lblAvatarPreview.setText("Chưa có ảnh");
            txtAvatarPath.setText("");
        }
    }

    // Hàm hỗ trợ chọn đúng item trong Combobox theo ID
    private void setSelectedComboboxItem(JComboBox<ComboboxItem> cb, Integer id) {
        if (id == null) return;
        for (int i = 0; i < cb.getItemCount(); i++) {
            if (cb.getItemAt(i).id == id) {
                cb.setSelectedIndex(i);
                break;
            }
        }
    }

    public void setDepartmentList(List<ComboboxItem> items) {
        cbDept.removeAllItems();
        if (items != null) for (ComboboxItem item : items) cbDept.addItem(item);
    }

    public void setPositionList(List<ComboboxItem> items) {
        cbPos.removeAllItems();
        if (items != null) for (ComboboxItem item : items) cbPos.addItem(item);
    }

    private void setupUI(Frame parent) {
        // 1. Thiết lập kích thước dialog phù hợp với màn hình laptop
        setSize(850, 580);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        dcBirthday.setDateFormatString("dd/MM/yyyy");
        dcHireDate.setDateFormatString("dd/MM/yyyy");

        // 2. Panel chính chứa 2 cột thông tin
        JPanel mainFormPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        mainFormPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // --- CỘT TRÁI: THÔNG TIN CÁ NHÂN ---
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Thông tin cá nhân"));
        GridBagConstraints gbcL = new GridBagConstraints();
        gbcL.fill = GridBagConstraints.HORIZONTAL;
        gbcL.insets = new Insets(5, 5, 5, 5);

        int rL = 0;
        addFormItem(leftPanel, "Tên nhân viên *:", txtName, gbcL, rL++);
        addFormItem(leftPanel, "Ngày sinh:", dcBirthday, gbcL, rL++);
        addFormItem(leftPanel, "Giới tính:", cbGender, gbcL, rL++);
        addFormItem(leftPanel, "Số CCCD:", txtIdCard, gbcL, rL++);
        addFormItem(leftPanel, "Số điện thoại:", txtPhone, gbcL, rL++);
        addFormItem(leftPanel, "Email:", txtEmail, gbcL, rL++);
        addFormItem(leftPanel, "Quê quán:", txtAddress, gbcL, rL++);

        // --- CỘT PHẢI: CÔNG VIỆC, TÀI CHÍNH & ẢNH ---
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Công việc & Tài chính"));
        GridBagConstraints gbcR = new GridBagConstraints();
        gbcR.fill = GridBagConstraints.HORIZONTAL;
        gbcR.insets = new Insets(5, 5, 5, 5);

        int rR = 0;
        addFormItem(rightPanel, "Ngày vào làm:", dcHireDate, gbcR, rR++);
        addFormItem(rightPanel, "Phòng ban:", cbDept, gbcR, rR++);
        addFormItem(rightPanel, "Chức vụ:", cbPos, gbcR, rR++);
        addFormItem(rightPanel, "Lương cơ bản:", txtSalary, gbcR, rR++);
        addFormItem(rightPanel, "Phụ cấp:", txtAllowance, gbcR, rR++);
        addFormItem(rightPanel, "Học vấn:", txtEducation, gbcR, rR++);
        addFormItem(rightPanel, "Kinh nghiệm:", txtExperience, gbcR, rR++);

        // Phần chọn ảnh lồng vào phía dưới cột phải
        gbcR.gridx = 0;
        gbcR.gridy = rR;
        rightPanel.add(new JLabel("Ảnh đại diện:"), gbcR);

        JPanel pnlImgActions = new JPanel(new BorderLayout(5, 0));
        txtAvatarPath.setEditable(false);
        JButton btnChooseAvatar = new JButton("Chọn ảnh");
        pnlImgActions.add(txtAvatarPath, BorderLayout.CENTER);
        pnlImgActions.add(btnChooseAvatar, BorderLayout.EAST);

        gbcR.gridx = 1;
        rightPanel.add(pnlImgActions, gbcR);
        rR++;

        lblAvatarPreview.setPreferredSize(new Dimension(100, 100));
        lblAvatarPreview.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        gbcR.gridx = 1;
        gbcR.gridy = rR;
        gbcR.fill = GridBagConstraints.NONE;
        gbcR.anchor = GridBagConstraints.WEST;
        rightPanel.add(lblAvatarPreview, gbcR);

        // 3. Thêm 2 cột vào panel chính và thêm trực tiếp vào Dialog (Xóa Scroll)
        mainFormPanel.add(leftPanel);
        mainFormPanel.add(rightPanel);
        add(mainFormPanel, BorderLayout.CENTER);

        // 4. Panel chứa các nút điều khiển phía dưới
        JButton btnSave = new JButton("Lưu thông tin");
        JButton btnCancel = new JButton("Hủy");

        JPanel bp = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bp.add(btnSave);
        bp.add(btnCancel);
        add(bp, BorderLayout.SOUTH);

        // --- CÁC SỰ KIỆN NÚT BẤM ---
        btnChooseAvatar.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                updateAvatarPreview(fc.getSelectedFile().getAbsolutePath());
            }
        });

        btnSave.addActionListener(e -> {
            if (validateInput()) {
                confirmed = true;
                dispose();
            }
        });

        btnCancel.addActionListener(e -> dispose());
    }

    // Hàm bổ trợ để cập nhật ảnh (dùng chung cho cả thêm và sửa)
    private void updateAvatarPreview(String path) {
        if (path != null && !path.isEmpty()) {
            File file = new File(path);
            if (file.exists()) {
                txtAvatarPath.setText(path);
                ImageIcon icon = new ImageIcon(new ImageIcon(path)
                        .getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH));
                lblAvatarPreview.setIcon(icon);
                lblAvatarPreview.setText("");
            }
        }
    }

    private static final String DATE_PATTERN = "dd/MM/yyyy";

    private boolean validateInput() {
        // 1. Kiểm tra trống các trường cơ bản (Dựa trên ràng buộc NOT NULL trong DB)
        if (txtName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên nhân viên không được để trống!");
            txtName.requestFocus();
            return false;
        }

        // 2. Validate định dạng Email và SĐT (Chặn sớm trước khi gửi xuống DAO)
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(this, "Email không đúng định dạng!");
            txtEmail.requestFocus();
            return false;
        }
        if (!phone.matches("^\\d{10,11}$")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại phải từ 10-11 chữ số!");
            txtPhone.requestFocus();
            return false;
        }

        // 3. Kiểm tra ngày sinh và ngày vào làm (Dựa trên nghiệp vụ 18 tuổi)
        java.util.Date birthday = dcBirthday.getDate();
        java.util.Date hireDate = dcHireDate.getDate();

        if (birthday == null || hireDate == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đầy đủ Ngày sinh và Ngày vào làm!");
            return false;
        }

        java.util.Calendar calBirth = java.util.Calendar.getInstance();
        calBirth.setTime(birthday);
        java.util.Calendar calHire = java.util.Calendar.getInstance();
        calHire.setTime(hireDate);

        // Kiểm tra không cho phép đi làm trước khi sinh
        if (hireDate.before(birthday)) {
            JOptionPane.showMessageDialog(this, "Ngày vào làm không thể trước ngày sinh!");
            return false;
        }

        // Tính tuổi tại thời điểm vào làm
        int ageAtHire = calHire.get(java.util.Calendar.YEAR) - calBirth.get(java.util.Calendar.YEAR);
        if (calHire.get(java.util.Calendar.DAY_OF_YEAR) < calBirth.get(java.util.Calendar.DAY_OF_YEAR)) {
            ageAtHire--;
        }

        if (ageAtHire < 18) {
            JOptionPane.showMessageDialog(this, "Nhân viên phải từ 18 tuổi trở lên tính đến ngày vào làm!");
            return false;
        }

        return true;
    }

    public EmployeeDTO getEmployeeData() {
        if (!confirmed) return null;
        EmployeeDTO emp = new EmployeeDTO();
        emp.setEmpName(txtName.getText());
        emp.setBirthday(dcBirthday.getDate());
        emp.setHireDate(dcHireDate.getDate());
        emp.setPhone(txtPhone.getText());
        emp.setEmail(txtEmail.getText());
        emp.setAddress(txtAddress.getText());
        emp.setIdCard(txtIdCard.getText());
        emp.setEducation(txtEducation.getText());
        emp.setExperience(txtExperience.getText());

        try {
            emp.setBaseSalary(Double.parseDouble(txtSalary.getText()));
            emp.setAllowance(Double.parseDouble(txtAllowance.getText()));
        } catch (Exception e) {
            emp.setBaseSalary(0);
            emp.setAllowance(0);
        }

        emp.setAvatar(txtAvatarPath.getText());
        emp.setGender(cbGender.getSelectedIndex());

        ComboboxItem selectedDept = (ComboboxItem) cbDept.getSelectedItem();
        ComboboxItem selectedPos = (ComboboxItem) cbPos.getSelectedItem();
        if (selectedDept != null) emp.setDeptId(selectedDept.id);
        if (selectedPos != null) emp.setPosId(selectedPos.id);

        return emp;
    }

    private void addFormItem(JPanel p, String l, JComponent c, GridBagConstraints g, int r) {
        g.gridx = 0;
        g.gridy = r;
        g.weightx = 0.3;
        p.add(new JLabel(l), g);
        g.gridx = 1;
        g.weightx = 0.7;
        p.add(c, g);
    }
}