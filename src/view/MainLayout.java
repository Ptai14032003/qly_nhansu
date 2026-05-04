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
        sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(45, 62, 80));
        sidebar.setPreferredSize(new Dimension(230, 0));
        add(sidebar, BorderLayout.WEST);

        // 2. Vùng nội dung chính
        cardLayout = new CardLayout();
        mainContent = new JPanel(cardLayout);
        add(mainContent, BorderLayout.CENTER);
    }

    public void addMenuLink(String name, JPanel pagePanel) {
        JButton btn = new JButton(name);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50)); // Nút trải dài theo sidebar
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(34, 45, 50));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        
        // Hiệu ứng Hover
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(41, 128, 185));
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(34, 45, 50));
            }
        });
        btn.addActionListener(e -> showPage(name));
        sidebar.add(btn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 5))); // Khoảng cách giữa các nút

        mainContent.add(pagePanel, name);
        menuButtons.put(name, btn);
    }

    // Để Controller đăng ký sự kiện click cho từng link
    public void setMenuEvent(String name, java.awt.event.ActionListener listener) {
        if (menuButtons.containsKey(name)) {
            menuButtons.get(name).addActionListener(listener);
        }
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

    public void clearMenu() {
        sidebar.removeAll();          // Xóa toàn bộ menu cũ
        menuButtons.clear();          // Xóa map button

        mainContent.removeAll();      // ❗ QUAN TRỌNG: xóa luôn page cũ

        sidebar.revalidate();
        sidebar.repaint();

        mainContent.revalidate();
        mainContent.repaint();
    }
}