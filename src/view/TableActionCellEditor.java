package view;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;

public abstract class TableActionCellEditor extends AbstractCellEditor implements TableCellEditor {
    private final JPanel panel;
    private int rowPointer;

    public abstract void onDetail(int row);

    public abstract void onEdit(int row);

    public abstract void onDelete(int row);

    public TableActionCellEditor(boolean showDetail) {
        panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 2));

        if (showDetail) {
            JButton btnDetail = createBtn("Xem", new Color(52, 152, 219));
            btnDetail.addActionListener(e -> {
                stopCellEditing();
                onDetail(rowPointer);
            });
            panel.add(btnDetail);
        }

        JButton btnEdit = createBtn("Sửa", new Color(241, 196, 15));
        btnEdit.addActionListener(e -> {
            stopCellEditing();
            onEdit(rowPointer);
        });
        panel.add(btnEdit);

        JButton btnDelete = createBtn("Xóa", new Color(231, 76, 60));
        btnDelete.addActionListener(e -> {
            stopCellEditing();
            onDelete(rowPointer);
        });
        panel.add(btnDelete);
    }

    private JButton createBtn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setPreferredSize(new Dimension(65, 25));
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBorderPainted(false);
        return b;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.rowPointer = row;
        panel.setBackground(table.getSelectionBackground());
        return panel;
    }

    @Override
    public Object getCellEditorValue() {
        return "";
    }
}