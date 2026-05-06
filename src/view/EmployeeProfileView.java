package view;

import dto.EmployeeDTO;
import javax.swing.*;
import java.awt.*;

public class EmployeeProfileView extends JPanel {
    private JLabel lblName, lblDept, lblPos, lblEmail, lblAddress, lblSalary, lblAvatar,lblPhone,lblId;

    public EmployeeProfileView() {
        setLayout(new BorderLayout());
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

//        mainPanel.add(new JLabel("Avatar:"));
//        lblName = new JLabel("...");
//        mainPanel.add(lblAvatar);

        mainPanel.add(new JLabel("Mã nhân viên:"));
        lblId = new JLabel("...");
        mainPanel.add(lblId);

        mainPanel.add(new JLabel("Họ tên:"));
        lblName = new JLabel("...");
        mainPanel.add(lblName);

        mainPanel.add(new JLabel("Email:"));
        lblEmail = new JLabel("...");
        mainPanel.add(lblEmail);

        mainPanel.add(new JLabel("Số điện thoại:"));
        lblPhone = new JLabel("...");
        mainPanel.add(lblPhone);

        mainPanel.add(new JLabel("Phòng ban:"));
        lblDept = new JLabel("...");
        mainPanel.add(lblDept);

        mainPanel.add(new JLabel("Chức vụ:"));
        lblPos = new JLabel("...");
        mainPanel.add(lblPos);

        mainPanel.add(new JLabel("Địa chỉ:"));
        lblAddress = new JLabel("...");
        mainPanel.add(lblAddress);

        mainPanel.add(new JLabel("Lương cơ bản:"));
        lblSalary = new JLabel("...");
        mainPanel.add(lblSalary);

        add(new JLabel("THÔNG TIN CÁ NHÂN", JLabel.CENTER), BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
    }

    // Hàm để Controller đẩy dữ liệu vào
    public void setProfileData(EmployeeDTO emp) {
        if (emp != null) {
//            lblAvatar.setText(emp.getAvatar());
            lblId.setText(String.format("NV %d",emp.getId()));
            lblName.setText(emp.getEmpName());
            lblDept.setText(emp.getDeptName());
            lblEmail.setText(emp.getEmail());
            lblPhone.setText(emp.getPhone());
            lblPos.setText(emp.getPosName());
            lblAddress.setText(emp.getAddress());
            lblSalary.setText(String.format("%,.0f VNĐ", emp.getBaseSalary()));
        }
    }
}