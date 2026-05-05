package view;

import dto.AttendanceDTO;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AttendanceView extends JPanel {

    private JButton btnIn;
    private JButton btnOut;

    private JTable table;
    private DefaultTableModel model;

    public AttendanceView() {

        setLayout(new BorderLayout(10,10));
        setBackground(Color.WHITE);

        // ===== TITLE =====
        JLabel title = new JLabel("CHẤM CÔNG NHÂN VIÊN", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(10,0,10,0));

        // ===== BUTTON =====
        btnIn = new JButton("Check In");
        btnOut = new JButton("Check Out");

        styleButton(btnIn, new Color(46, 204, 113));   // xanh lá
        styleButton(btnOut, new Color(231, 76, 60));   // đỏ

        JPanel top = new JPanel();
        top.setBackground(Color.WHITE);
        top.add(btnIn);
        top.add(btnOut);

        // ===== TABLE =====
        model = new DefaultTableModel(
                new String[]{"Ngày", "Giờ vào", "Giờ ra"}, 0
        );

        table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(52, 152, 219));
        table.getTableHeader().setForeground(Color.WHITE);

        // căn giữa
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class, center);

        // zebra row (dòng xen kẽ màu)
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {

                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 245));
                } else {
                    c.setBackground(new Color(174, 214, 241));
                }

                setHorizontalAlignment(JLabel.CENTER);
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        // ===== ADD =====
        add(title, BorderLayout.NORTH);
        add(top, BorderLayout.CENTER);
        add(scroll, BorderLayout.SOUTH);
    }

    // ===== STYLE BUTTON =====
    private void styleButton(JButton btn, Color color) {
        btn.setFocusPainted(false);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setPreferredSize(new Dimension(120, 35));
        btn.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
    }

    // ===== EVENT =====
    public void onCheckIn(java.awt.event.ActionListener e){
        btnIn.addActionListener(e);
    }

    public void onCheckOut(java.awt.event.ActionListener e){
        btnOut.addActionListener(e);
    }

    // ===== RENDER =====
    public void render(List<AttendanceDTO> list){

        model.setRowCount(0);

        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");

        for(AttendanceDTO a : list){

            String checkOut;

            if (a.getCheckOut() == null || a.getCheckOut().equals(LocalTime.MIDNIGHT)) {
                checkOut = "Chưa check-out";
            } else {
                checkOut = a.getCheckOut().format(timeFormat);
            }

            model.addRow(new Object[]{
                    a.getWorkDate(),
                    a.getCheckIn() == null ? "-" : a.getCheckIn().format(timeFormat),
                    checkOut
            });
        }
    }
}