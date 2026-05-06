package view;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class TableActionCellRender extends DefaultTableCellRenderer {
    private boolean showDetail;

    public TableActionCellRender(boolean showDetail) {
        this.showDetail = showDetail;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 2));
        actionPanel.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());

        if (showDetail) {
            JButton btnDetail = new JButton("Xem");
            formatButton(btnDetail, new Color(52, 152, 219));
            actionPanel.add(btnDetail);
        }

        JButton btnEdit = new JButton("Sửa");
        formatButton(btnEdit, new Color(241, 196, 15));
        actionPanel.add(btnEdit);

        JButton btnDelete = new JButton("Xóa");
        formatButton(btnDelete, new Color(231, 76, 60));
        actionPanel.add(btnDelete);

        return actionPanel;
    }

    private void formatButton(JButton b, Color bg) {
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setPreferredSize(new Dimension(65, 25));
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
    }
}