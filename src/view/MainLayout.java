package view;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class MainLayout extends JFrame {
    private JPanel sidebar;
    private JPanel mainContent;
    private CardLayout cardLayout;
    private Map<String, JButton> menuButtons = new HashMap<>();

    public MainLayout() {
        setTitle("Hệ thống Quản lý Nhân Sự");
        setSize(1300, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. Sidebar bên trái
        sidebar = new JPanel(new GridLayout(12, 1, 0, 5));
        sidebar.setBackground(new Color(45, 62, 80));
        sidebar.setPreferredSize(new Dimension(230, 0));
        add(sidebar, BorderLayout.WEST);

        // 2. Vùng nội dung chính (Dùng CardLayout để chuyển trang)
        cardLayout = new CardLayout();
        mainContent = new JPanel(cardLayout);
        add(mainContent, BorderLayout.CENTER);
    }

    // Hàm để thêm một mục menu và trang tương ứng
    // Khai báo màu sắc đặc trưng (Thương hiệu)
    public void addMenuItem(String name, JPanel pagePanel) {
        JButton btn = new JButton(name);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // THIẾT LẬP MÀU SẮC
        btn.setForeground(Color.WHITE);           // Chữ trắng
        btn.setBackground(new Color(34, 45, 50)); // Nền xám đen

        // KHẮC PHỤC LỖI HIỂN THỊ
        btn.setContentAreaFilled(false); // Vô hiệu hóa vùng phủ mặc định
        btn.setOpaque(true);             // Cho phép hiển thị màu nền tự chọn
        btn.setBorderPainted(false);     // Tắt đường viền thô của Swing
        btn.setFocusPainted(false);

        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Hiệu ứng hover cho nút (Đổi màu khi di chuột vào)
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(41, 128, 185)); // Xanh dương khi di chuột
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(34, 45, 50)); // Quay lại màu cũ
            }
        });

        btn.addActionListener(e -> cardLayout.show(mainContent, name));
        sidebar.add(btn);
        mainContent.add(pagePanel, name);
    }
    public void addMenuAction(String name, Runnable action) {
        JButton btn = new JButton(name);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));

        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(34, 45, 50));

        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);

        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // hover giống bạn
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(41, 128, 185));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(34, 45, 50));
            }
        });

        // 👉 KHÔNG dùng cardLayout nữa
        btn.addActionListener(e -> action.run());

        sidebar.add(btn);
    }

    public void showPage(String name) {
        cardLayout.show(mainContent, name);
    }
}