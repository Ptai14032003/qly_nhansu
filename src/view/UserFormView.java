package view;

import dto.EmployeeDTO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class UserFormView extends JPanel {
    private JTextField txtUsername;
    private JPasswordField txtPassword; // Dùng PasswordField cho bảo mật
    private JComboBox<String> cbRole;
    private JComboBox<EmployeeDTO> cbEmployee;
    private JButton btnSave, btnBack;

    public interface SubmitCallback {
        void onSubmit(String username, String password, int role, Integer empId);
    }

    public interface BackCallback {
        void onBack();
    }

    private SubmitCallback submitCallback;
    private BackCallback backCallback;

    public UserFormView() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 246, 250)); // Màu nền xám nhạt hiện đại

        // --- Header Panel ---
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(210, 210, 210)));

        btnBack = new JButton("← Quay lại");
        btnBack.setFocusPainted(false);
        btnBack.setContentAreaFilled(false);
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnBack.addActionListener(e -> {
            if (backCallback != null) backCallback.onBack();
        });
        headerPanel.add(btnBack);
        add(headerPanel, BorderLayout.NORTH);

        // --- Form Content ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(30, 50, 30, 50),
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Font chữ chung
        Font labelFont = new Font("Segoe UI", Font.BOLD, 13);
        Font inputFont = new Font("Segoe UI", Font.PLAIN, 14);

        // Username
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblUser = new JLabel("Username:");
        lblUser.setFont(labelFont);
        formPanel.add(lblUser, gbc);

        gbc.gridx = 1;
        txtUsername = new JTextField(20);
        txtUsername.setFont(inputFont);
        txtUsername.setPreferredSize(new Dimension(0, 35));
        formPanel.add(txtUsername, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblPass = new JLabel("Password:");
        lblPass.setFont(labelFont);
        formPanel.add(lblPass, gbc);

        gbc.gridx = 1;
        txtPassword = new JPasswordField(20);
        txtPassword.setFont(inputFont);
        txtPassword.setPreferredSize(new Dimension(0, 35));
        formPanel.add(txtPassword, gbc);

        // Role
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblRole = new JLabel("Quyền hạn:");
        lblRole.setFont(labelFont);
        formPanel.add(lblRole, gbc);

        gbc.gridx = 1;
        cbRole = new JComboBox<>(new String[]{"ADMIN", "MANAGER", "EMPLOYEE"});
        cbRole.setFont(inputFont);
        cbRole.setPreferredSize(new Dimension(0, 35));
        formPanel.add(cbRole, gbc);

        // Employee
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel lblEmp = new JLabel("Nhân viên:");
        lblEmp.setFont(labelFont);
        formPanel.add(lblEmp, gbc);

        gbc.gridx = 1;
        cbEmployee = new JComboBox<>();
        cbEmployee.setFont(inputFont);
        cbEmployee.setPreferredSize(new Dimension(0, 35));
        formPanel.add(cbEmployee, gbc);

        // Nút Lưu
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(30, 10, 10, 10);
        btnSave = new JButton("LƯU THÔNG TIN");
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSave.setBackground(new Color(52, 152, 219));
        btnSave.setForeground(Color.WHITE);
        btnSave.setPreferredSize(new Dimension(0, 45));
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSave.addActionListener(e -> handleSave());
        formPanel.add(btnSave, gbc);

        // Container căn giữa form
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setBackground(new Color(245, 246, 250));
        centerWrapper.add(formPanel);
        add(centerWrapper, BorderLayout.CENTER);
    }

    private void handleSave() {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());
        int role = cbRole.getSelectedIndex();
        EmployeeDTO selected = (EmployeeDTO) cbEmployee.getSelectedItem();
        Integer empId = (selected != null) ? selected.getId() : null;

        if (submitCallback != null) {
            submitCallback.onSubmit(username, password, role, empId);
        }
    }

    // --- Các hàm hỗ trợ Controller ---
    public void setSubmitAction(SubmitCallback cb) { this.submitCallback = cb; }
    public void setBackAction(BackCallback cb) { this.backCallback = cb; }

    public JComboBox<EmployeeDTO> getCbEmployee() { return cbEmployee; }

    public void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }

    public void clearForm() {
        txtUsername.setText("");
        txtPassword.setText("");
        cbRole.setSelectedIndex(0);
        cbEmployee.setSelectedIndex(-1);
    }
}