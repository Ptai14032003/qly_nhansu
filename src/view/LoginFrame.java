package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    // callback từ controller
    public interface LoginCallback {
        void onLogin(String username, String password);
    }

    private LoginCallback loginCallback;

    public LoginFrame() {
        setTitle("Đăng nhập hệ thống");
        setSize(400, 280);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 🌑 nền giống MainLayout
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(45, 62, 80));
        add(panel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 🧾 Title
        JLabel title = new JLabel("LOGIN", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(title, gbc);

        // 👤 Username
        gbc.gridwidth = 1;
        gbc.gridy++;
        panel.add(createLabel("Username"), gbc);

        txtUsername = createTextField();
        gbc.gridx = 1;
        panel.add(txtUsername, gbc);

        // 🔐 Password
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(createLabel("Password"), gbc);

        txtPassword = createPasswordField();
        gbc.gridx = 1;
        panel.add(txtPassword, gbc);

        // 🔘 Button
        btnLogin = new JButton("Đăng nhập");
        styleButton(btnLogin);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        panel.add(btnLogin, gbc);

        // 🎯 event
        btnLogin.addActionListener(e -> handleLogin());

        txtPassword.addActionListener(e -> handleLogin());
    }

    // ===== UI helper =====

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return lbl;
    }

    private JTextField createTextField() {
        JTextField tf = new JTextField(15);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return tf;
    }

    private JPasswordField createPasswordField() {
        JPasswordField pf = new JPasswordField(15);
        pf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return pf;
    }

    private void styleButton(JButton btn) {
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(34, 45, 50));

        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);

        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // hover giống sidebar
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(41, 128, 185));
            }

            public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(34, 45, 50));
            }
        });
    }

    // ===== logic =====

    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if(username.isEmpty() || password.isEmpty()){
            showError("Không được để trống!");
            return;
        }

        if (loginCallback != null) {
            loginCallback.onLogin(username, password);
        }
    }

    // controller sẽ set vào
    public void setLoginAction(LoginCallback callback) {
        this.loginCallback = callback;
    }

    public void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }
}
