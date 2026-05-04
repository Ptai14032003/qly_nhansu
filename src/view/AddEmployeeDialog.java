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

        // Thông tin văn bản
        txtName.setText(emp.getEmpName());
        txtPhone.setText(emp.getPhone());
        txtEmail.setText(emp.getEmail());
        txtAddress.setText(emp.getAddress());
        txtIdCard.setText(emp.getIdCard());
        txtEducation.setText(emp.getEducation());
        txtExperience.setText(emp.getExperience());
        txtSalary.setText(String.valueOf(emp.getBaseSalary()));
        txtAllowance.setText(String.valueOf(emp.getAllowance()));
        txtAvatarPath.setText(emp.getAvatar());

        // Ngày tháng
        if (emp.getBirthday() != null) dcBirthday.setDate(emp.getBirthday());
        if (emp.getHireDate() != null) dcHireDate.setDate(emp.getHireDate());

        // Giới tính
        if (emp.getGender() != null) cbGender.setSelectedIndex(emp.getGender());

        // Phòng ban & Chức vụ (Duyệt list để chọn đúng ID)
        setSelectedComboboxItem(cbDept, emp.getDeptId());
        setSelectedComboboxItem(cbPos, emp.getPosId());

        // Hiển thị ảnh
        if (emp.getAvatar() != null && !emp.getAvatar().isEmpty()) {
            File file = new File(emp.getAvatar());
            if (file.exists()) {
                ImageIcon icon = new ImageIcon(new ImageIcon(emp.getAvatar())
                        .getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH));
                lblAvatarPreview.setIcon(icon);
                lblAvatarPreview.setText("");
            }
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
        setSize(550, 850);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        dcBirthday.setDateFormatString("dd/MM/yyyy");
        dcHireDate.setDateFormatString("dd/MM/yyyy");

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        int r = 0;
        addFormItem(formPanel, "Tên nhân viên *:", txtName, gbc, r++);
        addFormItem(formPanel, "Ngày sinh:", dcBirthday, gbc, r++);
        addFormItem(formPanel, "Giới tính:", cbGender, gbc, r++);
        addFormItem(formPanel, "Số CCCD:", txtIdCard, gbc, r++);
        addFormItem(formPanel, "Học vấn:", txtEducation, gbc, r++);
        addFormItem(formPanel, "Kinh nghiệm:", txtExperience, gbc, r++);
        addFormItem(formPanel, "Quê quán:", txtAddress, gbc, r++);
        addFormItem(formPanel, "Số điện thoại:", txtPhone, gbc, r++);
        addFormItem(formPanel, "Email:", txtEmail, gbc, r++);
        addFormItem(formPanel, "Ngày vào làm:", dcHireDate, gbc, r++);
        addFormItem(formPanel, "Lương cơ bản:", txtSalary, gbc, r++);
        addFormItem(formPanel, "Phụ cấp:", txtAllowance, gbc, r++);
        addFormItem(formPanel, "Phòng ban:", cbDept, gbc, r++);
        addFormItem(formPanel, "Chức vụ:", cbPos, gbc, r++);

        gbc.gridx = 0;
        gbc.gridy = r;
        formPanel.add(new JLabel("Ảnh đại diện:"), gbc);
        JPanel pnlImg = new JPanel(new BorderLayout(5, 0));
        txtAvatarPath.setEditable(false);
        JButton btnChooseAvatar = new JButton("Chọn ảnh");
        pnlImg.add(txtAvatarPath, BorderLayout.CENTER);
        pnlImg.add(btnChooseAvatar, BorderLayout.EAST);
        gbc.gridx = 1;
        formPanel.add(pnlImg, gbc);
        r++;

        lblAvatarPreview.setPreferredSize(new Dimension(100, 100));
        lblAvatarPreview.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        gbc.gridx = 1;
        gbc.gridy = r;
        formPanel.add(lblAvatarPreview, gbc);

        btnChooseAvatar.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                txtAvatarPath.setText(file.getAbsolutePath());
                ImageIcon icon = new ImageIcon(new ImageIcon(file.getAbsolutePath())
                        .getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH));
                lblAvatarPreview.setIcon(icon);
                lblAvatarPreview.setText("");
            }
        });

        JButton btnSave = new JButton("Lưu thông tin");
        btnSave.addActionListener(e -> {
            // Gọi hàm kiểm tra dữ liệu trước khi cho phép xác nhận
            if (validateInput()) {
                confirmed = true;
                dispose();
            }
        });

        JPanel bp = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bp.add(btnSave);
        bp.add(new JButton("Hủy") {{
            addActionListener(x -> dispose());
        }});

        add(new JScrollPane(formPanel), BorderLayout.CENTER);
        add(bp, BorderLayout.SOUTH);
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