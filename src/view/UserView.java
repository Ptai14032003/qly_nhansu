package view;

import dto.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UserView extends JPanel {

    private JTextField txtUsername;
    private JTextField txtPassword;
    private JComboBox<String> cbRole;
    private JTextField txtEmpId;
    private JTable table;

    public interface CreateCallback {
        void onCreate(String username, String password, int role, Integer empId);
    }

    private CreateCallback callback;

    public UserView() {
        setLayout(new BorderLayout());

        // ===== FORM =====
        JPanel form = new JPanel(new GridLayout(5, 2, 10, 10));
        form.setBorder(BorderFactory.createTitledBorder("Tạo User"));

        txtUsername = new JTextField();
        txtPassword = new JTextField();
        cbRole = new JComboBox<>(new String[]{"ADMIN", "MANAGER", "EMPLOYEE"});
        txtEmpId = new JTextField();

        form.add(new JLabel("Username"));
        form.add(txtUsername);

        form.add(new JLabel("Password"));
        form.add(txtPassword);

        form.add(new JLabel("Role"));
        form.add(cbRole);

        form.add(new JLabel("Employee ID"));
        form.add(txtEmpId);

        JButton btnCreate = new JButton("Tạo user");
        form.add(new JLabel());
        form.add(btnCreate);

        add(form, BorderLayout.NORTH);

        // ===== TABLE =====
        table = new JTable();
        add(new JScrollPane(table), BorderLayout.CENTER);

        // event
        btnCreate.addActionListener(e -> handleCreate());
    }

    private void handleCreate() {
        String username = txtUsername.getText();
        String password = txtPassword.getText();
        int role = cbRole.getSelectedIndex(); // 0-1-2

        Integer empId = null;
        if (!txtEmpId.getText().isEmpty()) {
            empId = Integer.parseInt(txtEmpId.getText());
        }

        if (callback != null) {
            callback.onCreate(username, password, role, empId);
        }
    }

    public void setCreateAction(CreateCallback cb) {
        this.callback = cb;
    }

    public void setUserTable(List<User> users) {
        String[] cols = {"ID", "Username", "Role", "EmpID"};

        DefaultTableModel model = new DefaultTableModel(cols, 0);

        for (User u : users) {
            model.addRow(new Object[]{
                    u.getId(),
                    u.getUsername(),
                    u.getRole(),
                    u.getEmpId()
            });
        }

        table.setModel(model);
    }

    public void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }
}
