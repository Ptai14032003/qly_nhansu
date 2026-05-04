package view;

import dto.EmployeeDTO;
import dto.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UserView extends JPanel {

    private JTextField txtUsername;
    private JTextField txtPassword;
    private JComboBox<String> cbRole;
    private JComboBox<EmployeeDTO> cbEmployee;
    private JTable table;
    private DeleteCallback deleteCallback;
    public JComboBox<EmployeeDTO> getCbEmployee() {
        return cbEmployee;
    }
    public interface CreateCallback {
        void onCreate(String username, String password, int role, Integer empId);
    }

    private CreateCallback callback;

    public UserView() {
        setLayout(new BorderLayout());

        // ===== FORM =====
        JPanel form = new JPanel(new GridLayout(5, 2, 10, 10));
        form.setBorder(BorderFactory.createTitledBorder("Tạo User"));
//        form.add(new JLabel());
        txtUsername = new JTextField();
        txtPassword = new JTextField();
        cbRole = new JComboBox<>(new String[]{"ADMIN", "MANAGER", "EMPLOYEE"});


        form.add(new JLabel("Username"));
        form.add(txtUsername);

        form.add(new JLabel("Password"));
        form.add(txtPassword);

        form.add(new JLabel("Role"));
        form.add(cbRole);

        cbEmployee = new JComboBox<>();

        form.add(new JLabel("Employee Name"));
        form.add(cbEmployee);

        JButton btnCreate = new JButton("Tạo user");
        form.add(new JLabel());
        form.add(btnCreate);

        add(form, BorderLayout.NORTH);

        // ===== TABLE =====
        table = new JTable();
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton btnDelete = new JButton("Xóa user");
        btnDelete.addActionListener(e -> handleDelete());
        bottomPanel.add(btnDelete);

        add(bottomPanel, BorderLayout.SOUTH);

        // event
        btnCreate.addActionListener(e -> handleCreate());
    }

    private void handleCreate() {
        String username = txtUsername.getText();
        String password = txtPassword.getText();
        int role = cbRole.getSelectedIndex(); // 0-1-2

        EmployeeDTO selected = (EmployeeDTO) cbEmployee.getSelectedItem();
        Integer empId = (selected != null) ? selected.getId() : null;

        if (callback != null) {
            callback.onCreate(username, password, role, empId);
        }
    }

    private void handleDelete() {
        int row = table.getSelectedRow();

        if (row == -1) {
            showMessage("Vui lòng chọn user để xóa!");
            return;
        }

        int userId = (int) table.getValueAt(row, 0); // cột ID

        if (deleteCallback != null) {
            deleteCallback.onDelete(userId);
        }
    }

    public void setCreateAction(CreateCallback cb) {
        this.callback = cb;
    }

    public void setUserTable(List<User> users) {
        String[] cols = {"ID", "Username", "Role", "Employee Name"};

        DefaultTableModel model = new DefaultTableModel(cols, 0);

        for (User u : users) {
            model.addRow(new Object[]{
                    u.getId(),
                    u.getUsername(),
                    u.getRole(),
                    u.getEmpName()
            });
        }

        table.setModel(model);
    }

    public void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }
    public JTextField getTxtUsername() {
        return txtUsername;
    }

    public JTextField getTxtPassword() {
        return txtPassword;
    }
    public void clearForm() {
        txtUsername.setText("");
        txtPassword.setText("");
        cbRole.setSelectedIndex(0);
        cbEmployee.setSelectedIndex(-1); // bỏ chọn
    }
    public interface DeleteCallback {
        void onDelete(int userId);
    }
    public void setDeleteAction(DeleteCallback cb) {
        this.deleteCallback = cb;
    }
}
