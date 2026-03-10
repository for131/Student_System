package org.example.ui;

import org.example.model.Admin;
import org.example.model.Parent;
import org.example.model.Student;
import org.example.model.User;
import org.example.service.UserService;
import org.example.utils.Validator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * 登录 / 注册界面
 * 左侧：品牌区（渐变背景 + 系统名称）
 * 右侧：登录/注册表单卡片（Tab切换）
 */
public class LoginView extends JFrame {

    private UserService userService = new UserService();

    // ── 登录控件 ────────────────────────────────────────────
    private JTextField loginUsernameField;
    private JPasswordField loginPasswordField;

    // ── 注册控件 ────────────────────────────────────────────
    private JTextField regUsernameField;
    private JPasswordField regPasswordField;
    private JTextField regNameField;
    private JTextField regPhoneField;
    private JComboBox<String> regRoleBox;
    // Student 专用
    private JPanel studentExtraPanel;
    private JTextField regSchoolField;
    private JTextField regMajorField;
    private JTextField regGradeField;
    // Parent 专用
    private JPanel parentExtraPanel;
    private JTextField regAddressField;

    private JTabbedPane tabbedPane;

    public LoginView() {
        UITheme.applyGlobalFont();
        setTitle("大学生家教服务系统 - 登录");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(860, 560);
        setLocationRelativeTo(null);
        setResizable(false);
        setUndecorated(true);  // 无边框，更美观

        // 添加圆角窗口
        setShape(new RoundRectangle2D.Double(0, 0, 860, 560, 20, 20));

        JPanel root = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
            }
        };
        root.setBackground(UITheme.BG_MAIN);

        root.add(buildLeftPanel(), BorderLayout.WEST);
        root.add(buildRightPanel(), BorderLayout.CENTER);

        // 拖动无边框窗口
        addWindowDrag(root);
        setContentPane(root);
        setVisible(true);
    }

    // ── 左侧品牌区 ──────────────────────────────────────────
    private JPanel buildLeftPanel() {
        JPanel panel = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // 深蓝渐变
                GradientPaint gp = new GradientPaint(0, 0, new Color(0x1E3A8A),
                        0, getHeight(), new Color(0x1D4ED8));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // 装饰圆圈
                g2.setColor(new Color(255, 255, 255, 20));
                g2.fillOval(-60, -60, 260, 260);
                g2.setColor(new Color(255, 255, 255, 10));
                g2.fillOval(80, 350, 320, 320);
                g2.dispose();
            }
        };
        panel.setPreferredSize(new Dimension(310, 560));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(8, 20, 8, 20);
        gbc.anchor = GridBagConstraints.CENTER;

        // Logo图标（emoji替代）
        JLabel icon = new JLabel("^v^");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 52));
        icon.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(icon, gbc);

        JLabel title = new JLabel("大学生家教服务系统");
        title.setFont(UITheme.font(Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        panel.add(title, gbc);

        JLabel subtitle = new JLabel("<html><div style='text-align:center;'>连接优质大学生家教<br/>与有需求的家庭</div></html>");
        subtitle.setFont(UITheme.font(Font.PLAIN, 13));
        subtitle.setForeground(new Color(255, 255, 255, 180));
        subtitle.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(subtitle, gbc);

        // 特性列表
        gbc.insets = new Insets(16, 30, 4, 20);
        String[] features = {"✓  学生实名注册审核", "✓  家长发布需求匹配", "✓  管理员统一管理"};
        for (String f : features) {
            JLabel lbl = new JLabel(f);
            lbl.setFont(UITheme.font(Font.PLAIN, 12));
            lbl.setForeground(new Color(255, 255, 255, 200));
            gbc.insets = new Insets(4, 30, 4, 20);
            panel.add(lbl, gbc);
        }

        return panel;
    }

    // ── 右侧表单区 ─────────────────────────────────────────
    private JPanel buildRightPanel() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(UITheme.BG_MAIN);

        // 关闭按钮（右上角）
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        topBar.setBackground(UITheme.BG_MAIN);
        JButton closeBtn = new JButton("x");
        closeBtn.setFont(UITheme.font(Font.BOLD, 14));
        closeBtn.setForeground(UITheme.TEXT_SUB);
        closeBtn.setBorderPainted(false);
        closeBtn.setContentAreaFilled(false);
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> System.exit(0));
        topBar.add(closeBtn);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(UITheme.BG_MAIN);
        wrapper.add(topBar, BorderLayout.NORTH);

        // Tab面板
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(UITheme.FONT_H3);
        tabbedPane.setForeground(UITheme.TEXT_MAIN);
        tabbedPane.setBackground(UITheme.BG_MAIN);

        tabbedPane.addTab("  登 录  ", buildLoginCard());
        tabbedPane.addTab("  注 册  ", buildRegisterCard());

        wrapper.add(tabbedPane, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1; gbc.weighty = 1;
        gbc.insets = new Insets(20, 20, 20, 30);
        outer.add(wrapper, gbc);

        return outer;
    }

    // ── 登录卡片 ────────────────────────────────────────────
    private JScrollPane buildLoginCard() {
        JPanel card = UITheme.card();
        card.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets = new Insets(8, 0, 8, 0);

        JLabel title = UITheme.titleLabel("欢迎回来");
        title.setBorder(new EmptyBorder(0, 0, 10, 0));
        card.add(title, gbc);

        loginUsernameField = UITheme.roundedField("用户名");
        card.add(UITheme.formRow("用户名", loginUsernameField), gbc);

        loginPasswordField = UITheme.roundedPasswordField();
        card.add(UITheme.formRow("密  码", loginPasswordField), gbc);

        // 登录按钮
        JButton loginBtn = UITheme.primaryButton("立即登录");
        loginBtn.setPreferredSize(new Dimension(300, 42));
        loginBtn.setFont(UITheme.font(Font.BOLD, 15));
        JPanel btnWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        btnWrap.setOpaque(false);
        btnWrap.add(loginBtn);
        card.add(btnWrap, gbc);

        // 提示语
        JLabel hint = new JLabel("还没有账号？点击上方「注册」");
        hint.setFont(UITheme.FONT_SMALL);
        hint.setForeground(UITheme.TEXT_LIGHT);
        hint.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(hint, gbc);

        // 事件
        loginBtn.addActionListener(e -> doLogin());
        loginPasswordField.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) doLogin();
            }
        });

        JScrollPane scroll = new JScrollPane(card);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        return scroll;
    }

    // ── 注册卡片 ────────────────────────────────────────────
    private JScrollPane buildRegisterCard() {
        JPanel card = UITheme.card();
        card.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets = new Insets(5, 0, 5, 0);

        JLabel title = UITheme.titleLabel("创建新账号");
        title.setBorder(new EmptyBorder(0, 0, 6, 0));
        card.add(title, gbc);

        regUsernameField = UITheme.roundedField("登录用户名");
        card.add(UITheme.formRow("用户名", regUsernameField), gbc);

        regPasswordField = UITheme.roundedPasswordField();
        card.add(UITheme.formRow("密  码", regPasswordField), gbc);

        regNameField = UITheme.roundedField("真实姓名");
        card.add(UITheme.formRow("姓  名", regNameField), gbc);

        regPhoneField = UITheme.roundedField("11位手机号");
        card.add(UITheme.formRow("手机号", regPhoneField), gbc);

        // 角色选择
        regRoleBox = new JComboBox<>(new String[]{"Student（学生）", "Parent（家长）", "Admin（管理员）"});
        regRoleBox.setFont(UITheme.FONT_LABEL);
        card.add(UITheme.formRow("角  色", regRoleBox), gbc);

        // Student 额外字段
        studentExtraPanel = buildStudentExtra(gbc);
        studentExtraPanel.setVisible(true);
        card.add(studentExtraPanel, gbc);

        // Parent 额外字段
        parentExtraPanel = buildParentExtra(gbc);
        parentExtraPanel.setVisible(false);
        card.add(parentExtraPanel, gbc);

        // 角色切换联动
        regRoleBox.addActionListener(e -> {
            int idx = regRoleBox.getSelectedIndex();
            studentExtraPanel.setVisible(idx == 0);
            parentExtraPanel.setVisible(idx == 1);
            card.revalidate();
            card.repaint();
        });

        JButton regBtn = UITheme.primaryButton("注册账号");
        regBtn.setPreferredSize(new Dimension(300, 42));
        regBtn.setFont(UITheme.font(Font.BOLD, 15));
        JPanel btnWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 8));
        btnWrap.setOpaque(false);
        btnWrap.add(regBtn);
        card.add(btnWrap, gbc);

        regBtn.addActionListener(e -> doRegister());

        JScrollPane scroll = new JScrollPane(card);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        return scroll;
    }

    private JPanel buildStudentExtra(GridBagConstraints parentGbc) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets = new Insets(5, 0, 5, 0);

        regSchoolField = UITheme.roundedField("所在学校");
        p.add(UITheme.formRow("学  校", regSchoolField), gbc);

        regMajorField = UITheme.roundedField("所学专业");
        p.add(UITheme.formRow("专  业", regMajorField), gbc);

        regGradeField = UITheme.roundedField("例：大三");
        p.add(UITheme.formRow("年  级", regGradeField), gbc);

        return p;
    }

    private JPanel buildParentExtra(GridBagConstraints parentGbc) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets = new Insets(5, 0, 5, 0);

        regAddressField = UITheme.roundedField("家庭住址");
        p.add(UITheme.formRow("地  址", regAddressField), gbc);

        return p;
    }

    // ── 业务逻辑 ────────────────────────────────────────────
    private void doLogin() {
        String username = loginUsernameField.getText().trim();
        String password = new String(loginPasswordField.getPassword());

        if (Validator.isEmpty(username) || Validator.isEmpty(password)) {
            UITheme.showError(this, "用户名和密码不能为空！");
            return;
        }

        if (!userService.login(username, password)) {
            UITheme.showError(this, "用户名或密码错误！");
            return;
        }

        dispose();
        new DashboardView(UserService.currentUser);
    }

    private void doRegister() {
        String username = regUsernameField.getText().trim();
        String password = new String(regPasswordField.getPassword());
        String name     = regNameField.getText().trim();
        String phone    = regPhoneField.getText().trim();
        int    roleIdx  = regRoleBox.getSelectedIndex();

        // ── 基础校验 ──
        if (Validator.isEmpty(username) || Validator.isEmpty(password)
                || Validator.isEmpty(name) || Validator.isEmpty(phone)) {
            UITheme.showError(this, "请填写所有必填项！");
            return;
        }
        if (!Validator.isPasswordStrong(password)) {
            UITheme.showError(this, "密码至少需要6位！");
            return;
        }
        if (!Validator.isPhoneValid(phone)) {
            UITheme.showError(this, "手机号格式不正确（需11位）！");
            return;
        }
        // ── 用户名唯一性检查（交给 UserService 处理）──

        // ── 构建对应角色对象 ──
        User newUser;
        if (roleIdx == 0) {
            // Student
            String school = regSchoolField.getText().trim();
            String major  = regMajorField.getText().trim();
            String grade  = regGradeField.getText().trim();
            if (Validator.isEmpty(school) || Validator.isEmpty(major) || Validator.isEmpty(grade)) {
                UITheme.showError(this, "请填写学生信息（学校/专业/年级）！");
                return;
            }
            Student s = new Student();
            s.setUsername(username); s.setPassword(password);
            s.setName(name); s.setPhone(phone);
            s.setSchool(school); s.setMaior(major); s.setGrade(grade);
            s.setAccept(0); s.setVisible(false);
            newUser = s;
        } else if (roleIdx == 1) {
            // Parent
            String address = regAddressField.getText().trim();
            if (Validator.isEmpty(address)) {
                UITheme.showError(this, "请填写家庭地址！");
                return;
            }
            Parent p = new Parent(username, "Parent", password, name, phone);
            newUser = p;
        } else {
            // Admin
            Admin a = new Admin(username, "Admin", password, name, phone);
            newUser = a;
        }

        // ── 通过 UserService 注册（含唯一性检查）──
        if (!userService.register(newUser)) {
            UITheme.showError(this, "用户名已存在，请换一个！");
            return;
        }

        UITheme.showSuccess(this, "注册成功！" + (roleIdx == 0 ? "\n学生账号需等待管理员审核后方可展示。" : ""));
        tabbedPane.setSelectedIndex(0);
        loginUsernameField.setText(username);
    }

    // ── 拖动无边框窗口 ──────────────────────────────────────
    private void addWindowDrag(JPanel root) {
        final Point[] start = {null};
        root.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) { start[0] = e.getPoint(); }
        });
        root.addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseDragged(MouseEvent e) {
                if (start[0] != null) {
                    Point loc = getLocation();
                    setLocation(loc.x + e.getX() - start[0].x, loc.y + e.getY() - start[0].y);
                }
            }
        });
    }
}
/*

 */