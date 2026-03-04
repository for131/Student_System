package org.example.ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * 全局UI主题 - 统一色调、字体、圆角风格
 */
public class UITheme {

    // ── 主色板 ──────────────────────────────────────────────
    public static final Color BG_MAIN      = new Color(0xF5F7FA);   // 页面背景
    public static final Color BG_CARD      = Color.WHITE;            // 卡片背景
    public static final Color BG_SIDEBAR   = new Color(0x1E293B);   // 侧边栏深色
    public static final Color BG_SIDEBAR_HOVER = new Color(0x334155);
    public static final Color BG_SIDEBAR_ACTIVE= new Color(0x3B82F6);

    public static final Color PRIMARY      = new Color(0x3B82F6);   // 蓝色主调
    public static final Color PRIMARY_DARK = new Color(0x2563EB);
    public static final Color SUCCESS      = new Color(0x10B981);
    public static final Color DANGER       = new Color(0xEF4444);
    public static final Color WARNING      = new Color(0xF59E0B);
    public static final Color TEXT_MAIN    = new Color(0x1E293B);
    public static final Color TEXT_SUB     = new Color(0x64748B);
    public static final Color TEXT_LIGHT   = new Color(0x94A3B8);
    public static final Color BORDER       = new Color(0xE2E8F0);

    // ── 字体 ────────────────────────────────────────────────
    public static Font font(int style, float size) {
        return new Font("微软雅黑", style, (int) size);
    }
    public static final Font FONT_TITLE  = font(Font.BOLD, 22);
    public static final Font FONT_LABEL  = font(Font.PLAIN, 13);
    public static final Font FONT_BOLD   = font(Font.BOLD, 13);
    public static final Font FONT_SMALL  = font(Font.PLAIN, 12);
    public static final Font FONT_H2     = font(Font.BOLD, 16);
    public static final Font FONT_H3     = font(Font.BOLD, 14);

    // ── 工厂方法 ────────────────────────────────────────────

    /** 主操作按钮（蓝色填充） */
    public static JButton primaryButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c = getModel().isPressed() ? PRIMARY_DARK
                        : getModel().isRollover() ? PRIMARY_DARK : PRIMARY;
                g2.setColor(c);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(Color.WHITE);
        btn.setFont(FONT_BOLD);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(120, 38));
        return btn;
    }

    /** 次要按钮（灰色描边） */
    public static JButton secondaryButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isRollover() ? new Color(0xF1F5F9) : BG_CARD;
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setColor(BORDER);
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth()-1, getHeight()-1, 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(TEXT_MAIN);
        btn.setFont(FONT_LABEL);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(100, 34));
        return btn;
    }

    /** 危险按钮（红色） */
    public static JButton dangerButton(String text) {
        JButton btn = primaryButton(text);
        btn.setBackground(DANGER);
        // 重绘颜色
        btn.putClientProperty("color", DANGER);
        return btn;
    }

    /** 圆角输入框 */
    public static JTextField roundedField(String placeholder) {
        JTextField field = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
            @Override protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hasFocus() ? PRIMARY : BORDER);
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(new RoundRectangle2D.Float(0.75f, 0.75f, getWidth()-1.5f, getHeight()-1.5f, 8, 8));
                g2.dispose();
            }
        };
        field.setFont(FONT_LABEL);
        field.setForeground(TEXT_MAIN);
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        field.setPreferredSize(new Dimension(200, 38));
        return field;
    }

    /** 圆角密码框 */
    public static JPasswordField roundedPasswordField() {
        JPasswordField field = new JPasswordField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
            @Override protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hasFocus() ? PRIMARY : BORDER);
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(new RoundRectangle2D.Float(0.75f, 0.75f, getWidth()-1.5f, getHeight()-1.5f, 8, 8));
                g2.dispose();
            }
        };
        field.setFont(FONT_LABEL);
        field.setForeground(TEXT_MAIN);
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        field.setPreferredSize(new Dimension(200, 38));
        return field;
    }

    /** 卡片面板（白色圆角阴影） */
    public static JPanel card() {
        JPanel panel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // 阴影
                g2.setColor(new Color(0, 0, 0, 15));
                g2.fill(new RoundRectangle2D.Float(3, 4, getWidth()-4, getHeight()-4, 14, 14));
                // 卡片本体
                g2.setColor(BG_CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth()-3, getHeight()-3, 14, 14));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        return panel;
    }

    /** 统一应用全局字体（FlatLaf或Nimbus降级方案） */
    public static void applyGlobalFont() {
        Font f = font(Font.PLAIN, 13);
        UIManager.put("Label.font", f);
        UIManager.put("Button.font", f);
        UIManager.put("TextField.font", f);
        UIManager.put("PasswordField.font", f);
        UIManager.put("TextArea.font", f);
        UIManager.put("ComboBox.font", f);
        UIManager.put("Table.font", f);
        UIManager.put("TableHeader.font", font(Font.BOLD, 13));
        UIManager.put("TabbedPane.font", f);
        UIManager.put("List.font", f);
        UIManager.put("Panel.background", BG_MAIN);
        UIManager.put("OptionPane.background", BG_MAIN);
        UIManager.put("OptionPane.messageForeground", TEXT_MAIN);
    }

    /** 带标签的表单行 */
    public static JPanel formRow(String label, JComponent field) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(FONT_LABEL);
        lbl.setForeground(TEXT_SUB);
        lbl.setPreferredSize(new Dimension(90, 38));
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        row.add(lbl, BorderLayout.WEST);
        row.add(field, BorderLayout.CENTER);
        return row;
    }

    /** 显示成功提示 */
    public static void showSuccess(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "成功", JOptionPane.INFORMATION_MESSAGE);
    }

    /** 显示错误提示 */
    public static void showError(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "错误", JOptionPane.ERROR_MESSAGE);
    }

    /** 标题标签 */
    public static JLabel titleLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_TITLE);
        lbl.setForeground(TEXT_MAIN);
        return lbl;
    }
}
/*

 */