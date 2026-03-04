package org.example.ui;

import org.example.model.Student;
import org.example.model.User;
import org.example.service.UserService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.*;
import java.util.List;

/**
 * 主仪表板窗口
 * ┌────────────┬──────────────────────────────┐
 * │  侧边栏    │  顶部栏                       │
 * │  (导航)    ├──────────────────────────────┤
 * │            │  内容卡片区                   │
 * └────────────┴──────────────────────────────┘
 */
public class DashboardView extends JFrame {

    private final User currentUser;
    private JPanel contentArea;
    private final List<NavItem> navItems = new ArrayList<>();
    private JButton activeNavBtn = null;

    public DashboardView(User user) {
        this.currentUser = user;
        UITheme.applyGlobalFont();

        setTitle("家教匹配系统");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 680);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 580));

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG_MAIN);
        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildMainArea(), BorderLayout.CENTER);

        setContentPane(root);
        setVisible(true);

        // 默认显示第一个面板
        if (!navItems.isEmpty()) {
            navItems.get(0).button.doClick();
        }
    }

    // ── 侧边栏 ──────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(UITheme.BG_SIDEBAR);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setLayout(new BorderLayout());

        // 顶部Logo区
        JPanel logoArea = new JPanel(new GridBagLayout());
        logoArea.setOpaque(false);
        logoArea.setPreferredSize(new Dimension(200, 72));
        JLabel logoIcon = new JLabel("🎓");
        logoIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));
        JLabel logoText = new JLabel("家教系统");
        logoText.setFont(UITheme.font(Font.BOLD, 16));
        logoText.setForeground(Color.WHITE);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(0, 4, 0, 6);
        logoArea.add(logoIcon, g);
        logoArea.add(logoText, g);
        sidebar.add(logoArea, BorderLayout.NORTH);

        // 导航菜单
        JPanel nav = new JPanel();
        nav.setOpaque(false);
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));

        buildNavItems();
        for (NavItem item : navItems) {
            nav.add(item.button);
            nav.add(Box.createVerticalStrut(4));
        }

        sidebar.add(nav, BorderLayout.CENTER);

        // 底部用户信息
        sidebar.add(buildUserInfoPanel(), BorderLayout.SOUTH);

        return sidebar;
    }

    private void buildNavItems() {
        String role = currentUser.getRole();
        switch (role) {
            case "Student" -> {
                navItems.add(new NavItem("🏠", "首页公告", () -> showPanel(MainMenu.buildAnnouncementPanel())));
                navItems.add(new NavItem("👤", "我的信息", () -> showPanel(MainMenu.buildStudentProfilePanel((Student) currentUser))));
                navItems.add(new NavItem("📋", "家教需求", () -> showPanel(MainMenu.buildRequirementListPanel(currentUser))));
            }
            case "Parent" -> {
                navItems.add(new NavItem("🏠", "首页公告", () -> showPanel(MainMenu.buildAnnouncementPanel())));
                navItems.add(new NavItem("➕", "发布需求", () -> showPanel(MainMenu.buildPostRequirementPanel(currentUser))));
                navItems.add(new NavItem("📋", "我的需求", () -> showPanel(MainMenu.buildMyRequirementPanel(currentUser))));
                navItems.add(new NavItem("🔍", "浏览学生", () -> showPanel(MainMenu.buildBrowseStudentsPanel())));
            }
            case "Admin" -> {
                navItems.add(new NavItem("🏠", "系统概况", () -> showPanel(MainMenu.buildAdminOverviewPanel())));
                navItems.add(new NavItem("👥", "用户管理", () -> showPanel(MainMenu.buildUserManagePanel())));
                navItems.add(new NavItem("📣", "发布公告", () -> showPanel(MainMenu.buildPublishAnnouncementPanel())));
                navItems.add(new NavItem("📋", "需求管理", () -> showPanel(MainMenu.buildAllRequirementsPanel())));
                navItems.add(new NavItem("✅", "审核学生", () -> showPanel(MainMenu.buildStudentAuditPanel())));
            }
            default -> {
                navItems.add(new NavItem("🏠", "首页", () -> showPanel(MainMenu.buildAnnouncementPanel())));
            }
        }
    }

    private JPanel buildUserInfoPanel() {
        JPanel p = new JPanel(new BorderLayout(10, 0)) {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(new Color(255, 255, 255, 15));
                g.fillRect(0, 0, getWidth(), 1);
                super.paintComponent(g);
            }
        };
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(12, 16, 16, 16));

        // 头像圆圈
        JPanel avatar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.PRIMARY);
                g2.fillOval(0, 0, 36, 36);
                g2.setColor(Color.WHITE);
                g2.setFont(UITheme.font(Font.BOLD, 15));
                FontMetrics fm = g2.getFontMetrics();
                String ch = currentUser.getName() != null && !currentUser.getName().isEmpty()
                        ? String.valueOf(currentUser.getName().charAt(0)) : "U";
                int x = (36 - fm.stringWidth(ch)) / 2;
                int y = (36 - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(ch, x, y);
                g2.dispose();
            }
        };
        avatar.setOpaque(false);
        avatar.setPreferredSize(new Dimension(36, 36));

        JPanel info = new JPanel(new GridLayout(2, 1));
        info.setOpaque(false);
        JLabel nameLabel = new JLabel(currentUser.getName() != null ? currentUser.getName() : currentUser.getUsername());
        nameLabel.setFont(UITheme.font(Font.BOLD, 13));
        nameLabel.setForeground(Color.WHITE);
        JLabel roleLabel = new JLabel(getRoleText(currentUser.getRole()));
        roleLabel.setFont(UITheme.font(Font.PLAIN, 11));
        roleLabel.setForeground(new Color(255, 255, 255, 150));
        info.add(nameLabel);
        info.add(roleLabel);

        // 退出按钮
        JButton logoutBtn = new JButton("⬅");
        logoutBtn.setFont(UITheme.font(Font.PLAIN, 16));
        logoutBtn.setForeground(new Color(255, 255, 255, 160));
        logoutBtn.setBorderPainted(false);
        logoutBtn.setContentAreaFilled(false);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutBtn.setToolTipText("退出登录");
        logoutBtn.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this, "确定退出登录？", "提示",
                    JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                UserService.currentUser = null;
                dispose();
                new LoginView();
            }
        });

        p.add(avatar, BorderLayout.WEST);
        p.add(info, BorderLayout.CENTER);
        p.add(logoutBtn, BorderLayout.EAST);
        return p;
    }

    // ── 主内容区 ────────────────────────────────────────────
    private JPanel buildMainArea() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(UITheme.BG_MAIN);

        // 顶部栏
        main.add(buildTopBar(), BorderLayout.NORTH);

        // 内容区
        contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(UITheme.BG_MAIN);
        contentArea.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        main.add(contentArea, BorderLayout.CENTER);

        return main;
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(Color.WHITE);
        bar.setPreferredSize(new Dimension(0, 56));
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER),
                BorderFactory.createEmptyBorder(0, 24, 0, 24)
        ));

        JLabel welcome = new JLabel("欢迎，" + (currentUser.getName() != null ? currentUser.getName() : currentUser.getUsername())
                + "  |  " + getRoleText(currentUser.getRole()));
        welcome.setFont(UITheme.font(Font.PLAIN, 13));
        welcome.setForeground(UITheme.TEXT_SUB);

        JLabel dateLabel = new JLabel(java.time.LocalDate.now().toString());
        dateLabel.setFont(UITheme.FONT_SMALL);
        dateLabel.setForeground(UITheme.TEXT_LIGHT);

        bar.add(welcome, BorderLayout.WEST);
        bar.add(dateLabel, BorderLayout.EAST);
        return bar;
    }

    // ── 工具 ────────────────────────────────────────────────
    private void showPanel(JPanel panel) {
        contentArea.removeAll();
        contentArea.add(panel, BorderLayout.CENTER);
        contentArea.revalidate();
        contentArea.repaint();
    }

    private String getRoleText(String role) {
        return switch (role) {
            case "Student" -> "学生";
            case "Parent"  -> "家长";
            case "Admin"   -> "管理员";
            default        -> role;
        };
    }

    // ── 导航按钮 ────────────────────────────────────────────
    private class NavItem {
        final String icon;
        final String label;
        final Runnable action;
        final JButton button;

        NavItem(String icon, String label, Runnable action) {
            this.icon = icon;
            this.label = label;
            this.action = action;
            this.button = createNavButton();
        }

        private JButton createNavButton() {
            JButton btn = new JButton(icon + "  " + label) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    boolean active = (activeNavBtn == this);
                    boolean hover  = getModel().isRollover();
                    Color bg = active ? UITheme.BG_SIDEBAR_ACTIVE
                            : hover  ? UITheme.BG_SIDEBAR_HOVER
                            : UITheme.BG_SIDEBAR;
                    if (active || hover) {
                        g2.setColor(bg);
                        g2.fill(new RoundRectangle2D.Float(8, 0, getWidth()-16, getHeight(), 8, 8));
                    }
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            btn.setFont(UITheme.font(Font.PLAIN, 13));
            btn.setForeground(new Color(255, 255, 255, 200));
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.setHorizontalAlignment(SwingConstants.LEFT);
            btn.setBorder(BorderFactory.createEmptyBorder(9, 20, 9, 12));
            btn.setMaximumSize(new Dimension(200, 42));
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);

            btn.addActionListener(e -> {
                if (activeNavBtn != null) activeNavBtn.repaint();
                activeNavBtn = btn;
                btn.setForeground(Color.WHITE);
                btn.repaint();
                action.run();
            });

            btn.addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) {
                    btn.setForeground(Color.WHITE);
                }
                @Override public void mouseExited(MouseEvent e) {
                    if (activeNavBtn != btn) btn.setForeground(new Color(255,255,255,200));
                }
            });
            return btn;
        }
    }
}
/*

 */