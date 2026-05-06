package view;

import dto.EmployeeDTO;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

public class HomeView extends JPanel {
    private JLabel lblTotalEmpValue;
    private JLabel lblTotalDeptValue;
    private JLabel lblTotalSalaryValue;
    private JPanel chartWrapper;
    private JPanel recentEmpWrapper;
    private static final String FONT_FAMILY = "Segoe UI";

    public HomeView() {
        recentEmpWrapper = new JPanel();
        recentEmpWrapper.setLayout(new BoxLayout(recentEmpWrapper, BoxLayout.Y_AXIS));
        recentEmpWrapper.setBackground(Color.WHITE);

        setLayout(new BorderLayout());
        setBackground(new Color(240, 242, 245));

        // Header
        JLabel lblHeader = new JLabel("BẢNG ĐIỀU KHIỂN HỆ THỐNG", JLabel.LEFT);
        lblHeader.setFont(new Font(FONT_FAMILY, Font.BOLD, 24));
        lblHeader.setBorder(new EmptyBorder(25, 30, 20, 30));
        add(lblHeader, BorderLayout.NORTH);

        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setOpaque(false);

        // 1. Khối Card Thống kê (Có Icon Emoji 👤, 🏢, 💰)
        JPanel cardContainer = new JPanel(new GridLayout(1, 3, 25, 0));
        cardContainer.setOpaque(false);
        cardContainer.setBorder(new EmptyBorder(0, 30, 25, 30));
        cardContainer.add(createCard("TỔNG NHÂN VIÊN", "0", "👤", new Color(52, 152, 219), "EMP"));
        cardContainer.add(createCard("PHÒNG BAN", "0", "🏢", new Color(241, 196, 15), "DEPT"));
        cardContainer.add(createCard("QUỸ LƯƠNG (VNĐ)", "0", "💰", new Color(46, 204, 113), "SALARY"));
        mainContent.add(cardContainer);

        // 2. Khối Chi tiết (Đã chỉnh nhỏ lại - 350px)
        JPanel detailPanel = new JPanel(new GridLayout(1, 2, 25, 0));
        detailPanel.setOpaque(false);
        detailPanel.setBorder(new EmptyBorder(0, 30, 30, 30));
        detailPanel.setPreferredSize(new Dimension(0, 350)); // Chiều cao nhỏ hơn cũ

        detailPanel.add(createChartBlock("Cơ cấu nhân sự theo phòng ban"));
        detailPanel.add(createRecentEmpBlock("Nhân viên mới gia nhập"));

        mainContent.add(detailPanel);
        add(mainContent, BorderLayout.CENTER);
    }

    // --- HIỂN THỊ NHÂN VIÊN MỚI (Hiện đại & Bo tròn) ---
    private JPanel createEmployeeItem(EmployeeDTO dto) {
        JPanel item = new JPanel(new BorderLayout(15, 10));
        item.setBackground(Color.WHITE);
        item.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));

        JPanel avatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                String path = dto.getAvatar();
                boolean imgOk = false;
                if (path != null && !path.isEmpty()) {
                    try {
                        Image img = new ImageIcon(path).getImage();
                        if (img != null && img.getWidth(null) > 0) {
                            // Vẽ ảnh bo tròn
                            g2.setClip(new java.awt.geom.Ellipse2D.Float(0, 0, 45, 45));
                            g2.drawImage(img, 0, 0, 45, 45, null);
                            imgOk = true;
                        }
                    } catch (Exception e) {
                    }
                }
                if (!imgOk) {
                    g2.setColor(dto.getGender() == 1 ? new Color(200, 230, 255) : new Color(255, 200, 230));
                    g2.fillOval(0, 0, 45, 45);
                    g2.setColor(dto.getGender() == 1 ? new Color(0, 100, 200) : new Color(200, 0, 100));
                    g2.setFont(new Font(FONT_FAMILY, Font.BOLD, 18));
                    String initial = (dto.getEmpName() != null && !dto.getEmpName().isEmpty())
                            ? dto.getEmpName().substring(0, 1).toUpperCase() : "?";
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(initial, (45 - fm.stringWidth(initial)) / 2, ((45 - fm.getHeight()) / 2) + fm.getAscent());
                }
            }
        };
        avatar.setPreferredSize(new Dimension(45, 45));
        avatar.setOpaque(false);

        JPanel info = new JPanel(new GridLayout(2, 1, 0, 0));
        info.setOpaque(false);
        JLabel lblN = new JLabel(dto.getEmpName() + " (" + dto.getAge() + "t)");
        lblN.setFont(new Font(FONT_FAMILY, Font.BOLD, 14));
        JLabel lblP = new JLabel(dto.getPosName());
        lblP.setFont(new Font(FONT_FAMILY, Font.PLAIN, 12));
        lblP.setForeground(Color.GRAY);
        info.add(lblN);
        info.add(lblP);

        JLabel lblD = new JLabel("<html><div style='color: #3498db; background: #ebf5fb; padding: 2px 8px; border-radius: 8px;'>"
                + dto.getDeptName() + "</div></html>");

        item.add(avatar, BorderLayout.WEST);
        item.add(info, BorderLayout.CENTER);
        item.add(lblD, BorderLayout.EAST);
        item.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(245, 245, 245)));
        return item;
    }

    public void updateRecentEmployees(List<EmployeeDTO> list) {
        if (recentEmpWrapper == null) return;
        recentEmpWrapper.removeAll();
        if (list == null || list.isEmpty()) {
            recentEmpWrapper.add(new JLabel("Không có nhân viên mới gia nhập."));
        } else {
            for (EmployeeDTO dto : list) {
                recentEmpWrapper.add(createEmployeeItem(dto));
                recentEmpWrapper.add(Box.createRigidArea(new Dimension(0, 5))); // Khoảng cách nhỏ hơn
            }
        }
        recentEmpWrapper.revalidate();
        recentEmpWrapper.repaint();
    }

    // --- BIỂU ĐỒ TRÒN (Có % và số người) ---
    public void updatePieChart(Map<String, Integer> data) {
        if (data == null || data.isEmpty()) return;
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        data.forEach(dataset::setValue);

        JFreeChart chart = ChartFactory.createPieChart("", dataset, true, true, false);
        PiePlot<String> plot = (PiePlot<String>) chart.getPlot();

        // Cấu hình hiển thị Nhãn: Tên: Số người (Phần trăm %)
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator(
                "{0}: {1} người ({2})",
                new DecimalFormat("0"),
                new DecimalFormat("0%")
        ));

        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setShadowPaint(null);
        plot.setLabelBackgroundPaint(new Color(255, 255, 255, 180));
        plot.setLabelFont(new Font(FONT_FAMILY, Font.PLAIN, 11));

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBackground(Color.WHITE);
        chartWrapper.removeAll();
        chartWrapper.add(chartPanel, BorderLayout.CENTER);
        chartWrapper.revalidate();
        chartWrapper.repaint();
    }

    // --- CÁC HÀM TIỆN ÍCH ---
    private JPanel createCard(String title, String value, String icon, Color color, String type) {
        JPanel card = new JPanel(null);
        card.setBackground(color);
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font(FONT_FAMILY, Font.BOLD, 13));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBounds(20, 15, 180, 20);
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font(FONT_FAMILY, Font.BOLD, 36));
        lblValue.setForeground(Color.WHITE);
        lblValue.setBounds(20, 45, 180, 50);

        if (type.equals("EMP")) lblTotalEmpValue = lblValue;
        else if (type.equals("DEPT")) lblTotalDeptValue = lblValue;
        else if (type.equals("SALARY")) lblTotalSalaryValue = lblValue;

        JLabel lblIcon = new JLabel(icon);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 55));
        lblIcon.setForeground(new Color(255, 255, 255, 90));
        lblIcon.setBounds(160, 20, 100, 80);

        card.add(lblTitle);
        card.add(lblValue);
        card.add(lblIcon);
        return card;
    }

    private JPanel createChartBlock(String title) {
        JPanel block = new JPanel(new BorderLayout());
        block.setBackground(Color.WHITE);
        block.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 225, 225), 1),
                new EmptyBorder(15, 15, 15, 15)
        ));
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font(FONT_FAMILY, Font.BOLD, 17));
        lblTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        block.add(lblTitle, BorderLayout.NORTH);
        chartWrapper = new JPanel(new BorderLayout());
        chartWrapper.setBackground(Color.WHITE);
        block.add(chartWrapper, BorderLayout.CENTER);
        return block;
    }

    private JPanel createRecentEmpBlock(String title) {
        JPanel block = new JPanel(new BorderLayout());
        block.setBackground(Color.WHITE);
        block.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 225, 225), 1),
                new EmptyBorder(15, 15, 15, 15)
        ));
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font(FONT_FAMILY, Font.BOLD, 17));
        lblTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        block.add(lblTitle, BorderLayout.NORTH);
        JScrollPane scroll = new JScrollPane(recentEmpWrapper);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);
        block.add(scroll, BorderLayout.CENTER);
        return block;
    }

    public void setStats(int totalEmp, int totalDept, double totalSalary) {
        if (lblTotalEmpValue != null) lblTotalEmpValue.setText(String.valueOf(totalEmp));
        if (lblTotalDeptValue != null) lblTotalDeptValue.setText(String.valueOf(totalDept));
        if (lblTotalSalaryValue != null) {
            lblTotalSalaryValue.setText(totalSalary >= 1000000 ? String.format("%.0fM", totalSalary / 1000000) : String.format("%,.0f", totalSalary));
        }
    }
}