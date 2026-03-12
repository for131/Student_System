package org.example.ui;

import org.example.dao.AnnouncementDao;
import org.example.dao.RequirementDao;
import org.example.dao.UserDao;
import org.example.model.*;
import org.example.service.AdminService;
import org.example.service.UserService;
import org.example.utils.Validator;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class MainMenu {

    private static final UserDao userDao             = new UserDao();
    private static final RequirementDao requireDao   = new RequirementDao();
    private static final AnnouncementDao announceDao = new AnnouncementDao();
    private static final AdminService adminService   = new AdminService();

    // =====================================================================
    //  advantage 字段编解码工具
    //  存储格式：「个人优势文本||RURL:https://...||VCODE:ABC123」
    // =====================================================================
    static String advText(String raw) {
        if (raw == null) return "";
        int i = raw.indexOf("||RURL:");
        return i >= 0 ? raw.substring(0, i) : raw;
    }
    static String advReportUrl(String raw) {
        if (raw == null) return "";
        int s = raw.indexOf("||RURL:"); if (s < 0) return "";
        String rest = raw.substring(s + 7);
        int e = rest.indexOf("||VCODE:"); return e >= 0 ? rest.substring(0, e) : rest;
    }
    static String advVerifyCode(String raw) {
        if (raw == null) return "";
        int s = raw.indexOf("||VCODE:"); return s >= 0 ? raw.substring(s + 8) : "";
    }
    static String buildAdv(String text, String url, String code) {
        String base = text == null ? "" : text.trim();
        String u    = url  == null ? "" : url.trim();
        String c    = code == null ? "" : code.trim();
        if (u.isEmpty() && c.isEmpty()) return base;
        return base + "||RURL:" + u + (c.isEmpty() ? "" : "||VCODE:" + c);
    }

    public static JPanel buildAnnouncementPanel() {
        JPanel root = pageRoot("📣  系统公告");
        List<Announcement> list = announceDao.readAll();
        if (list.isEmpty()) { root.add(emptyHint("暂无公告"), BorderLayout.CENTER); return root; }
        JPanel grid = new JPanel(new GridLayout(0, 1, 0, 12));
        grid.setOpaque(false);
        for (Announcement a : list) {
            JPanel card = UITheme.card();
            card.setLayout(new BorderLayout(10, 6));
            JLabel title = new JLabel("📌 " + a.getTitle());
            title.setFont(UITheme.FONT_H3); title.setForeground(UITheme.PRIMARY);
            JLabel time = new JLabel(a.getPublishtime());
            time.setFont(UITheme.FONT_SMALL); time.setForeground(UITheme.TEXT_LIGHT);
            JTextArea content = new JTextArea(a.getContent());
            content.setFont(UITheme.FONT_LABEL); content.setForeground(UITheme.TEXT_MAIN);
            content.setWrapStyleWord(true); content.setLineWrap(true);
            content.setEditable(false); content.setOpaque(false);
            JPanel header = new JPanel(new BorderLayout());
            header.setOpaque(false); header.add(title, BorderLayout.WEST); header.add(time, BorderLayout.EAST);
            card.add(header, BorderLayout.NORTH); card.add(content, BorderLayout.CENTER);
            grid.add(card);
        }
        root.add(scrollPane(grid), BorderLayout.CENTER);
        return root;
    }

    /** 以弹窗形式展示公告（进入系统后自动弹出） */
    public static void showAnnouncementDialog(java.awt.Component parent) {
        List<Announcement> list = announceDao.readAll();
        if (list.isEmpty()) return;
        String now = UserService.getCurrentUser().getRole();
        if(now.equals("Admin")) return;

        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(parent), "📣 系统公告", true);
        dialog.setSize(520, 420);
        dialog.setLocationRelativeTo(parent);
        dialog.setResizable(false);

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(UITheme.BG_MAIN);
        root.setBorder(BorderFactory.createEmptyBorder(20, 24, 16, 24));

        JLabel title = UITheme.titleLabel("最新公告");
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        root.add(title, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(0, 1, 0, 10));
        grid.setOpaque(false);
        // 最多展示最近3条
        List<Announcement> recent = list.size() > 3 ? list.subList(list.size() - 3, list.size()) : list;
        for (Announcement a : recent) {
            JPanel card = UITheme.card();
            card.setLayout(new BorderLayout(8, 4));
            JLabel t = new JLabel("📌 " + a.getTitle());
            t.setFont(UITheme.FONT_H3); t.setForeground(UITheme.PRIMARY);
            JLabel time = new JLabel(a.getPublishtime());
            time.setFont(UITheme.FONT_SMALL); time.setForeground(UITheme.TEXT_LIGHT);
            JTextArea content = new JTextArea(a.getContent());
            content.setFont(UITheme.FONT_SMALL); content.setForeground(UITheme.TEXT_MAIN);
            content.setWrapStyleWord(true); content.setLineWrap(true);
            content.setEditable(false); content.setOpaque(false);
            JPanel header = new JPanel(new BorderLayout());
            header.setOpaque(false); header.add(t, BorderLayout.WEST); header.add(time, BorderLayout.EAST);
            card.add(header, BorderLayout.NORTH); card.add(content, BorderLayout.CENTER);
            grid.add(card);
        }
        JScrollPane scroll = new JScrollPane(grid);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false); scroll.getViewport().setOpaque(false);
        root.add(scroll, BorderLayout.CENTER);

        JButton closeBtn = UITheme.primaryButton("知道了");
        closeBtn.setPreferredSize(new Dimension(120, 36));
        closeBtn.addActionListener(e -> dialog.dispose());
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        btnPanel.setOpaque(false);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        btnPanel.add(closeBtn);
        root.add(btnPanel, BorderLayout.SOUTH);

        dialog.setContentPane(root);
        dialog.setVisible(true);
    }

    public static JPanel buildStudentProfilePanel(Student student) {
        JPanel root = pageRoot("👤  我的信息");
        JPanel card = UITheme.card();
        card.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        gbc.insets = new Insets(7, 0, 7, 0);
        String[] statusText = {"待审核", "已通过", "已拒绝"};
        Color[] statusColor = {UITheme.WARNING, UITheme.SUCCESS, UITheme.DANGER};
        int acc = Math.min(student.getAccept(), 2);
        card.add(badge(statusText[acc], statusColor[acc]), gbc);
        card.add(infoRow("用户名", student.getUsername()), gbc);
        card.add(infoRow("姓　名", student.getName()), gbc);
        card.add(infoRow("手机号", student.getPhone()), gbc);
        card.add(infoRow("学　校", student.getSchool()), gbc);
        card.add(infoRow("专　业", student.getMaior()), gbc);
        card.add(infoRow("年　级", student.getGrade()), gbc);
        JLabel editTitle = new JLabel("── 教学信息（对家长可见）──");
        editTitle.setFont(UITheme.font(Font.BOLD, 12)); editTitle.setForeground(UITheme.TEXT_LIGHT);
        card.add(editTitle, gbc);

        JTextField subjectField   = UITheme.roundedField("可辅导的学科，如：数学、英语");
        JTextField gradeField2    = UITheme.roundedField("可辅导的年级段，如：小学、初中");
        JTextField advantageField = UITheme.roundedField("获奖经历、擅长科目等");
        JTextField addressField   = UITheme.roundedField("可辅导的地址，如：海淀区、朝阳区");
        JSpinner expSpinner = new JSpinner(new SpinnerNumberModel(student.getExperience(), 0, 20, 1));

        // ── 辅导方式复选框 + 分价格 ──────────────────────────
        JCheckBox offlineCheck = new JCheckBox("线下上门");
        JCheckBox onlineCheck  = new JCheckBox("线上授课");
        offlineCheck.setFont(UITheme.FONT_LABEL); offlineCheck.setOpaque(false);
        onlineCheck.setFont(UITheme.FONT_LABEL);  onlineCheck.setOpaque(false);

        JTextField offlinePriceField = UITheme.roundedField("线下收费，如：150元/小时");
        JTextField onlinePriceField  = UITheme.roundedField("线上收费，如：100元/小时");
        offlinePriceField.setEnabled(false);
        onlinePriceField.setEnabled(false);

        // 解析已保存 way 字段（格式："线下:150元/小时|线上:100元/小时"）
        if (student.getWay() != null) {
            for (String part : student.getWay().split("\\|")) {
                if (part.startsWith("线下:")) {
                    offlineCheck.setSelected(true);
                    offlinePriceField.setEnabled(true);
                    offlinePriceField.setText(part.substring(3));
                } else if (part.startsWith("线上:")) {
                    onlineCheck.setSelected(true);
                    onlinePriceField.setEnabled(true);
                    onlinePriceField.setText(part.substring(3));
                }
            }
        }
        if (student.getSubject()      != null) subjectField.setText(student.getSubject());
        if (student.getTargetGrages() != null) gradeField2.setText(student.getTargetGrages());
        if (student.getaddress() != null) addressField.setText(student.getaddress());
        // advantage 字段只显示纯文本部分（URL/验证码在下方专区）
        advantageField.setText(advText(student.getAdvantage()));

        offlineCheck.addActionListener(e -> offlinePriceField.setEnabled(offlineCheck.isSelected()));
        onlineCheck.addActionListener(e  -> onlinePriceField.setEnabled(onlineCheck.isSelected()));

        // 辅导方式行：两个复选框并排
        JPanel wayRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        wayRow.setOpaque(false);
        wayRow.add(offlineCheck); wayRow.add(onlineCheck);
        JPanel wayFormRow = new JPanel(new BorderLayout(10, 0));
        wayFormRow.setOpaque(false);
        JLabel wayLbl = new JLabel("辅导方式");
        wayLbl.setFont(UITheme.FONT_LABEL); wayLbl.setForeground(UITheme.TEXT_SUB);
        wayLbl.setPreferredSize(new Dimension(90, 38)); wayLbl.setHorizontalAlignment(SwingConstants.RIGHT);
        wayFormRow.add(wayLbl, BorderLayout.WEST); wayFormRow.add(wayRow, BorderLayout.CENTER);

        card.add(UITheme.formRow("辅导科目", subjectField), gbc);
        card.add(UITheme.formRow("辅导年级", gradeField2), gbc);
        card.add(wayFormRow, gbc);
        card.add(UITheme.formRow("线下价格", offlinePriceField), gbc);
        card.add(UITheme.formRow("线上价格", onlinePriceField), gbc);
        card.add(UITheme.formRow("家教经验(年)", expSpinner), gbc);
        card.add(UITheme.formRow("个人优势", advantageField), gbc);
        card.add(UITheme.formRow("辅导地址", addressField), gbc);

        // ── 学历在线验证专区（新增）──────────────────────────
        card.add(sectionSep("── 学历在线验证（供家长核验）──"), gbc);

        JTextField reportUrlField  = UITheme.roundedField("在线查验网址，如：https://www.chsi.com.cn/...");
        JTextField verifyCodeField = UITheme.roundedField("在线验证码，如：ABCD1234");
        // 读取已保存的查验网址和验证码（从 advantage 字段编码中解析）
        reportUrlField.setText(advReportUrl(student.getAdvantage()));
        verifyCodeField.setText(advVerifyCode(student.getAdvantage()));

        JLabel verifyStatusLbl = buildVerifyStatusLabel(student.getAdvantage());
        card.add(UITheme.formRow("查验网址", reportUrlField), gbc);
        card.add(UITheme.formRow("验证码", verifyCodeField), gbc);
        card.add(verifyStatusLbl, gbc);

        // 提示密码修改入口
        JLabel pwdHintLbl = new JLabel("  🔒 如需修改密码，请点击左侧「账号设置」");
        pwdHintLbl.setFont(UITheme.FONT_SMALL); pwdHintLbl.setForeground(UITheme.TEXT_LIGHT);
        card.add(pwdHintLbl, gbc);

        JButton saveBtn = UITheme.primaryButton("保存信息");
        saveBtn.setPreferredSize(new Dimension(140, 38));
        card.add(centerWrap(saveBtn), gbc);

        saveBtn.addActionListener(e -> {
            if (!offlineCheck.isSelected() && !onlineCheck.isSelected()) {
                UITheme.showError(saveBtn, "请至少选择一种辅导方式！"); return;
            }
            StringBuilder wayBuilder = new StringBuilder();
            if (offlineCheck.isSelected()) {
                String p = offlinePriceField.getText().trim();
                if (p.isEmpty()) { UITheme.showError(saveBtn, "请填写线下收费标准！"); return; }
                wayBuilder.append("线下:").append(p);
            }
            if (onlineCheck.isSelected()) {
                String p = onlinePriceField.getText().trim();
                if (p.isEmpty()) { UITheme.showError(saveBtn, "请填写线上收费标准！"); return; }
                if (wayBuilder.length() > 0) wayBuilder.append("|");
                wayBuilder.append("线上:").append(p);
            }
            StringBuilder priceDisplay = new StringBuilder();
            int offlinePrice = Integer.MAX_VALUE;
            int onlinePrice = Integer.MAX_VALUE;
            offlinePrice = parsePrice(offlinePriceField.getText());
            onlinePrice  = parsePrice(onlinePriceField.getText());

// 取最小值
            int minPrice = Math.min(offlinePrice, onlinePrice);

// 保存最小价格
            student.setPrice(minPrice == Integer.MAX_VALUE ? "" : String.valueOf(minPrice));
            // 将优势文本 + 查验网址 + 验证码合并编码存入 advantage 字段
            String newAdv = buildAdv(
                    advantageField.getText().trim(),
                    reportUrlField.getText().trim(),
                    verifyCodeField.getText().trim());
            student.setSubject(subjectField.getText().trim());
            student.setTargetGrages(gradeField2.getText().trim());
            student.setWay(wayBuilder.toString());
            student.setAdvantage(newAdv);
            student.setaddress(addressField.getText().trim());
            student.setExperience((Integer) expSpinner.getValue());
            student.setVisible(student.getAccept() == 1 && !Validator.isEmpty(student.getSubject()));
            // 调用 BaseDao.update(Predicate, item) 持久化到 users.json
            userDao.update(u -> u.getUsername().equals(student.getUsername()), student);
            UserService.currentUser = student;
            // 刷新验证状态标签
            String urlNow = advReportUrl(newAdv);
            if (urlNow.isEmpty()) {
                verifyStatusLbl.setText("  ⚠ 未填写查验网址，家长暂无法核验学历");
                verifyStatusLbl.setForeground(UITheme.WARNING);
            } else {
                String cp = advVerifyCode(newAdv).isEmpty() ? "（未填验证码）" : " + 验证码";
                verifyStatusLbl.setText("  ✅ 已填写查验网址" + cp);
                verifyStatusLbl.setForeground(UITheme.SUCCESS);
            }
            UITheme.showSuccess(saveBtn, "保存成功！");
        });
        root.add(scrollPane(card), BorderLayout.CENTER);
        return root;
    }

    /** 构建学历验证状态标签 */
    private static JLabel buildVerifyStatusLabel(String raw) {
        String url = advReportUrl(raw);
        JLabel lbl;
        if (url.isEmpty()) {
            lbl = new JLabel("  ⚠ 未填写查验网址，家长暂无法核验学历");
            lbl.setForeground(UITheme.WARNING);
        } else {
            String cp = advVerifyCode(raw).isEmpty() ? "（未填验证码）" : " + 验证码";
            lbl = new JLabel("  ✅ 已填写查验网址" + cp);
            lbl.setForeground(UITheme.SUCCESS);
        }
        lbl.setFont(UITheme.FONT_SMALL);
        return lbl;
    }

    // =====================================================================
    //  「账号设置」面板 —— 学生和家长通用
    //  · 修改姓名 / 手机号  →  userDao.update(Predicate, item)
    //  · 修改密码           →  userDao.findLits(Predicate) 校验旧密码
    //                          userDao.update(Predicate, item) 持久化
    //  · 同步 UserService.currentUser 内存状态
    // =====================================================================
    public static JPanel buildAccountSettingsPanel(User currentUser) {
        JPanel root = pageRoot("🔑  账号设置");
        JPanel card = UITheme.card();
        card.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        gbc.insets = new Insets(8, 0, 8, 0);

        // ── 只读账号信息 ─────────────────────────────────────
        card.add(infoRow("用户名", currentUser.getUsername()), gbc);
        card.add(infoRow("角　色", roleLabel(currentUser.getRole())), gbc);

        // ── 修改基本信息 ─────────────────────────────────────
        card.add(sectionSep("── 修改基本信息 ──"), gbc);
        JTextField nameField  = UITheme.roundedField("真实姓名");
        JTextField phoneField = UITheme.roundedField("手机号");
        if (currentUser.getName()  != null) nameField.setText(currentUser.getName());
        if (currentUser.getPhone() != null) phoneField.setText(currentUser.getPhone());
        card.add(UITheme.formRow("姓　　名 *", nameField), gbc);
        card.add(UITheme.formRow("手　机　号 *", phoneField), gbc);

        JButton saveInfoBtn = UITheme.primaryButton("保存基本信息");
        saveInfoBtn.setPreferredSize(new Dimension(160, 38));
        card.add(centerWrap(saveInfoBtn), gbc);

        saveInfoBtn.addActionListener(e -> {
            String name  = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            if (Validator.isEmpty(name))  { UITheme.showError(saveInfoBtn, "姓名不能为空！"); return; }
            if (Validator.isEmpty(phone)) { UITheme.showError(saveInfoBtn, "手机号不能为空！"); return; }
            currentUser.setName(name);
            currentUser.setPhone(phone);
            // BaseDao.update(Predicate, item) 持久化到 users.json
            userDao.update(u -> u.getUsername().equals(currentUser.getUsername()), currentUser);
            UserService.currentUser = currentUser;
            UITheme.showSuccess(saveInfoBtn, "基本信息已保存！");
        });

        // ── 修改密码 ─────────────────────────────────────────
        card.add(sectionSep("── 修改密码 ──"), gbc);
        JPasswordField oldPwdField  = UITheme.roundedPasswordField();
        JPasswordField newPwdField  = UITheme.roundedPasswordField();
        JPasswordField newPwdField2 = UITheme.roundedPasswordField();
        oldPwdField.setToolTipText("请输入当前密码");
        newPwdField.setToolTipText("新密码（至少6位）");
        newPwdField2.setToolTipText("再次输入新密码确认");
        card.add(UITheme.formRow("当前密码 *", oldPwdField), gbc);
        card.add(UITheme.formRow("新　密　码 *", newPwdField), gbc);
        card.add(UITheme.formRow("确认新密码 *", newPwdField2), gbc);

        // 实时密码强度指示
        JLabel strengthLbl = new JLabel(" ");
        strengthLbl.setFont(UITheme.FONT_SMALL);
        card.add(strengthLbl, gbc);
        newPwdField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            void refresh() {
                String pwd = new String(newPwdField.getPassword());
                if (pwd.isEmpty()) { strengthLbl.setText(" "); return; }
                int score = 0;
                if (pwd.length() >= 6)               score++;
                if (pwd.length() >= 10)              score++;
                if (pwd.matches(".*[A-Z].*"))         score++;
                if (pwd.matches(".*[0-9].*"))         score++;
                if (pwd.matches(".*[^A-Za-z0-9].*")) score++;
                String[] lvl = {"  强度：很弱", "  强度：弱", "  强度：一般", "  强度：较强", "  强度：强"};
                Color[]  clr = {UITheme.DANGER, UITheme.DANGER, UITheme.WARNING, UITheme.SUCCESS, UITheme.SUCCESS};
                int idx = Math.min(score, 4);
                strengthLbl.setText(lvl[idx]);
                strengthLbl.setForeground(clr[idx]);
            }
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e)  { refresh(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e)  { refresh(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { refresh(); }
        });

        JButton changePwdBtn = UITheme.primaryButton("修改密码");
        changePwdBtn.setPreferredSize(new Dimension(140, 38));
        card.add(centerWrap(changePwdBtn), gbc);

        changePwdBtn.addActionListener(e -> {
            String oldPwd  = new String(oldPwdField.getPassword()).trim();
            String newPwd  = new String(newPwdField.getPassword()).trim();
            String newPwd2 = new String(newPwdField2.getPassword()).trim();
            if (Validator.isEmpty(oldPwd))  { UITheme.showError(changePwdBtn, "请输入当前密码！"); return; }
            if (Validator.isEmpty(newPwd))  { UITheme.showError(changePwdBtn, "请输入新密码！"); return; }
            if (newPwd.length() < 6)        { UITheme.showError(changePwdBtn, "新密码不能少于6位！"); return; }
            if (!newPwd.equals(newPwd2))    { UITheme.showError(changePwdBtn, "两次密码输入不一致！"); return; }
            if (newPwd.equals(oldPwd))      { UITheme.showError(changePwdBtn, "新密码不能与当前密码相同！"); return; }
            // 调用 BaseDao.findLits(Predicate) 校验旧密码是否正确
            List<User> matched = userDao.findLits(
                    u -> u.getUsername().equals(currentUser.getUsername())
                            && u.getPassword().equals(oldPwd));
            if (matched.isEmpty()) { UITheme.showError(changePwdBtn, "当前密码不正确！"); return; }
            // 调用 BaseDao.update(Predicate, item) 持久化新密码
            currentUser.setPassword(newPwd);
            userDao.update(u -> u.getUsername().equals(currentUser.getUsername()), currentUser);
            UserService.currentUser = currentUser;
            oldPwdField.setText(""); newPwdField.setText(""); newPwdField2.setText("");
            strengthLbl.setText(" ");
            UITheme.showSuccess(changePwdBtn, "密码修改成功！");
        });

        root.add(scrollPane(card), BorderLayout.CENTER);
        return root;
    }

    private static String roleLabel(String role) {
        return switch (role) {
            case "Student" -> "学生";
            case "Parent"  -> "家长";
            case "Admin"   -> "管理员";
            default        -> role;
        };
    }

    public static JPanel buildRequirementListPanel(User currentUser) {
        JPanel root = pageRoot("📋  家教需求列表");
        List<TutorRequirement> allList = requireDao.findLits(r -> !r.isClosed());

        // ── 搜索栏 ──────────────────────────────────────────
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchBar.setOpaque(false);
        searchBar.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        JTextField gradeSearch = UITheme.roundedField("");
        gradeSearch.setPreferredSize(new Dimension(160, 34));
        gradeSearch.setToolTipText("按年级搜索，如：小学、初中、高中");
        gradeSearch.setText("按年级搜索…");
        gradeSearch.setForeground(UITheme.TEXT_LIGHT);
        gradeSearch.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (gradeSearch.getText().equals("按年级搜索…")) { gradeSearch.setText(""); gradeSearch.setForeground(UITheme.TEXT_MAIN); }
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                if (gradeSearch.getText().isEmpty()) { gradeSearch.setText("按年级搜索…"); gradeSearch.setForeground(UITheme.TEXT_LIGHT); }
            }
        });

        // ✨ 新增：地址搜索框
        JTextField addressSearch = UITheme.roundedField("");
        addressSearch.setPreferredSize(new Dimension(160, 34));
        addressSearch.setToolTipText("按地址搜索，如：海淀区、朝阳区");
        addressSearch.setText("按地址搜索…");
        addressSearch.setForeground(UITheme.TEXT_LIGHT);
        addressSearch.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (addressSearch.getText().equals("按地址搜索…")) { addressSearch.setText(""); addressSearch.setForeground(UITheme.TEXT_MAIN); }
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                if (addressSearch.getText().isEmpty()) { addressSearch.setText("按地址搜索…"); addressSearch.setForeground(UITheme.TEXT_LIGHT); }
            }
        });

        JTextField priceSearch = UITheme.roundedField("");
        priceSearch.setPreferredSize(new Dimension(160, 34));
        priceSearch.setToolTipText("输入最高薪酬数字，筛选 ≤ 该值的需求，如：150");
        priceSearch.setText("薪酬上限（如：150）…");
        priceSearch.setForeground(UITheme.TEXT_LIGHT);
        priceSearch.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (priceSearch.getText().equals("薪酬上限（如：150）…")) { priceSearch.setText(""); priceSearch.setForeground(UITheme.TEXT_MAIN); }
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                if (priceSearch.getText().isEmpty()) { priceSearch.setText("薪酬上限（如：150）…"); priceSearch.setForeground(UITheme.TEXT_LIGHT); }
            }
        });

        JButton searchBtn = UITheme.primaryButton("🔍 搜索");
        searchBtn.setPreferredSize(new Dimension(90, 34));
        JButton resetBtn  = UITheme.secondaryButton("重置");
        resetBtn.setPreferredSize(new Dimension(70, 34));

        // ✨ 修改提示文本
        JLabel hintLbl = new JLabel("  年级/地址：模糊匹配；薪酬：输入数字，展示 ≤ 该值的所有需求");
        hintLbl.setFont(UITheme.FONT_SMALL); hintLbl.setForeground(UITheme.TEXT_LIGHT);

        // ✨ 添加地址搜索框到搜索栏
        searchBar.add(gradeSearch);
        searchBar.add(addressSearch);  // ← 新增
        searchBar.add(priceSearch);
        searchBar.add(searchBtn);
        searchBar.add(resetBtn);
        searchBar.add(hintLbl);

        // ── 表格区（可动态刷新） ────────────────────────────
        String[] cols = {"需求ID", "发布家长", "科目", "年级", "地址", "薪酬", "时间", "要求"};
        JPanel tableHolder = new JPanel(new BorderLayout());
        tableHolder.setOpaque(false);

        Runnable refreshTable = () -> {
            String gKw = gradeSearch.getText().equals("按年级搜索…") ? "" : gradeSearch.getText().trim().toLowerCase();
            // ✨ 新增：获取地址关键词
            String aKw = addressSearch.getText().equals("按地址搜索…") ? "" : addressSearch.getText().trim().toLowerCase();
            String pRaw = priceSearch.getText().equals("薪酬上限（如：150）…") ? "" : priceSearch.getText().trim();

            // 解析价格上限
            int priceMax = Integer.MAX_VALUE;
            if (!pRaw.isEmpty()) {
                try { priceMax = Integer.parseInt(pRaw.replaceAll("[^0-9]", "")); }
                catch (NumberFormatException ignored) {}
            }
            final int maxVal = priceMax;

            List<TutorRequirement> filtered = allList.stream()
                    .filter(r -> gKw.isEmpty() || (r.getGradeLevel() != null && r.getGradeLevel().toLowerCase().contains(gKw)))
                    // ✨ 新增：按地址过滤
                    .filter(r -> aKw.isEmpty() || (r.getAddress() != null && r.getAddress().toLowerCase().contains(aKw)))
                    .filter(r -> {
                        if (maxVal == Integer.MAX_VALUE) return true;
                        if (r.getMoney() == null) return false;
                        try {
                            int val = Integer.parseInt(r.getMoney().replaceAll("[^0-9]", ""));
                            return val <= maxVal;
                        } catch (NumberFormatException e) { return false; }
                    })
                    .toList();

            tableHolder.removeAll();
            if (filtered.isEmpty()) {
                tableHolder.add(emptyHint("没有符合条件的需求，请调整搜索条件"), BorderLayout.CENTER);
            } else {
                Object[][] data = new Object[filtered.size()][cols.length];
                for (int i = 0; i < filtered.size(); i++) {
                    TutorRequirement r = filtered.get(i);
                    data[i] = new Object[]{r.getReqID(), r.getParentUsername(), r.getSubject(),
                            r.getGradeLevel(), r.getAddress(), r.getMoney(), r.getDuration(), r.getNeed()};
                }
                JTable table = styledTable(data, cols);
                JScrollPane scroll = new JScrollPane(table);
                scroll.setBorder(BorderFactory.createEmptyBorder());
                scroll.getViewport().setBackground(Color.WHITE);
                tableHolder.add(scroll, BorderLayout.CENTER);
            }
            tableHolder.revalidate(); tableHolder.repaint();
        };

        searchBtn.addActionListener(e -> refreshTable.run());
        resetBtn.addActionListener(e -> {
            gradeSearch.setText("按年级搜索…"); gradeSearch.setForeground(UITheme.TEXT_LIGHT);
            addressSearch.setText("按地址搜索…"); addressSearch.setForeground(UITheme.TEXT_LIGHT);  // ← 新增
            priceSearch.setText("薪酬上限（如：150）…"); priceSearch.setForeground(UITheme.TEXT_LIGHT);
            refreshTable.run();
        });

        // ✨ 支持回车触发搜索
        gradeSearch.addActionListener(e -> refreshTable.run());
        addressSearch.addActionListener(e -> refreshTable.run());  // ← 新增
        priceSearch.addActionListener(e -> refreshTable.run());

        if (allList.isEmpty()) {
            tableHolder.add(emptyHint("暂无开放的家教需求"), BorderLayout.CENTER);
        } else {
            refreshTable.run();
        }

        // 把搜索栏加到 pageRoot 的 NORTH 下方
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setOpaque(false);
        Component titleBar = root.getComponent(0);
        root.remove(titleBar);
        northPanel.add(titleBar, BorderLayout.NORTH);
        northPanel.add(searchBar, BorderLayout.SOUTH);
        root.add(northPanel, BorderLayout.NORTH);
        root.add(tableHolder, BorderLayout.CENTER);
        return root;
    }

    /** 家长编辑个人信息 */
    public static JPanel buildParentEditPanel(User currentUser) {
        JPanel root = pageRoot("👤  我的信息");
        JPanel card = UITheme.card();
        card.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        gbc.insets = new Insets(8, 0, 8, 0);

        card.add(infoRow("用户名", currentUser.getUsername()), gbc);
        card.add(infoRow("当前角色", "家长"), gbc);

        JLabel sep = new JLabel("── 可修改信息 ──");
        sep.setFont(UITheme.font(Font.BOLD, 12)); sep.setForeground(UITheme.TEXT_LIGHT);
        card.add(sep, gbc);

        JTextField nameField  = UITheme.roundedField("真实姓名");
        JTextField phoneField = UITheme.roundedField("手机号");

        if (currentUser.getName()  != null) nameField.setText(currentUser.getName());
        if (currentUser.getPhone() != null) phoneField.setText(currentUser.getPhone());

        card.add(UITheme.formRow("姓　　名", nameField), gbc);
        card.add(UITheme.formRow("手　机　号", phoneField), gbc);

        // 提示密码修改入口
        JLabel pwdHintLbl = new JLabel("  🔒 如需修改密码，请点击左侧「账号设置」");
        pwdHintLbl.setFont(UITheme.FONT_SMALL); pwdHintLbl.setForeground(UITheme.TEXT_LIGHT);
        card.add(pwdHintLbl, gbc);

        JButton saveBtn = UITheme.primaryButton("保存修改");
        saveBtn.setPreferredSize(new Dimension(140, 38));
        card.add(centerWrap(saveBtn), gbc);

        saveBtn.addActionListener(e -> {
            String name  = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            if (Validator.isEmpty(name))  { UITheme.showError(saveBtn, "姓名不能为空！"); return; }
            if (Validator.isEmpty(phone)) { UITheme.showError(saveBtn, "手机号不能为空！"); return; }
            currentUser.setName(name);
            currentUser.setPhone(phone);
            userDao.update(u -> u.getUsername().equals(currentUser.getUsername()), currentUser);
            UserService.currentUser = currentUser;
            UITheme.showSuccess(saveBtn, "信息修改成功！");
        });

        root.add(scrollPane(card), BorderLayout.CENTER);
        return root;
    }

    public static JPanel buildPostRequirementPanel(User currentUser) {
        JPanel root = pageRoot("➕  发布家教需求");
        JPanel card = UITheme.card();
        card.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        gbc.insets = new Insets(8, 0, 8, 0);
        JTextField subjectField  = UITheme.roundedField("如：数学、英语");
        JTextField gradeField    = UITheme.roundedField("如：小学三年级");
        JTextField addressField  = UITheme.roundedField("上课地点");
        JTextField moneyField    = UITheme.roundedField("如：150元/小时");
        JTextField durationField = UITheme.roundedField("如：每周三次，每次2小时");
        JTextField needField     = UITheme.roundedField("对家教的要求");
        card.add(UITheme.formRow("辅导科目*", subjectField), gbc);
        card.add(UITheme.formRow("学生年级*", gradeField), gbc);
        card.add(UITheme.formRow("上课地址*", addressField), gbc);
        card.add(UITheme.formRow("薪　　酬*", moneyField), gbc);
        card.add(UITheme.formRow("课程时间*", durationField), gbc);
        card.add(UITheme.formRow("其他要求", needField), gbc);
        JButton submitBtn = UITheme.primaryButton("发布需求");
        submitBtn.setPreferredSize(new Dimension(140, 40));
        card.add(centerWrap(submitBtn), gbc);
        submitBtn.addActionListener(e -> {
            if (Validator.isEmpty(subjectField.getText()) || Validator.isEmpty(gradeField.getText())
                    || Validator.isEmpty(addressField.getText()) || Validator.isEmpty(moneyField.getText())
                    || Validator.isEmpty(durationField.getText())) {
                UITheme.showError(submitBtn, "请填写所有带*的必填项！"); return;
            }
            TutorRequirement req = new TutorRequirement(false, needField.getText().trim(),
                    moneyField.getText().trim(), addressField.getText().trim(),
                    durationField.getText().trim(), gradeField.getText().trim(),
                    subjectField.getText().trim(), currentUser.getUsername());
            requireDao.add(req);
            UITheme.showSuccess(submitBtn, "需求发布成功！");
            subjectField.setText(""); gradeField.setText(""); addressField.setText("");
            moneyField.setText(""); durationField.setText(""); needField.setText("");
        });
        root.add(scrollPane(card), BorderLayout.CENTER);
        return root;
    }

    public static JPanel buildMyRequirementPanel(User currentUser) {
        JPanel root = pageRoot("📋  我的需求");
        List<TutorRequirement> list = requireDao.findLits(
                r -> r.getParentUsername().equals(currentUser.getUsername()));
        if (list.isEmpty()) { root.add(emptyHint("您还没有发布任何需求"), BorderLayout.CENTER); return root; }
        String[] cols = {"需求ID", "科目", "年级", "地址", "薪酬", "状态", "操作"};
        Object[][] data = new Object[list.size()][cols.length];
        for (int i = 0; i < list.size(); i++) {
            TutorRequirement r = list.get(i);
            data[i] = new Object[]{r.getReqID(), r.getSubject(), r.getGradeLevel(),
                    r.getAddress(), r.getMoney(), r.isClosed() ? "已关闭" : "开放中", "关闭"};
        }
        DefaultTableModel model = new DefaultTableModel(data, cols) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = styledTable(null, null);
        table.setModel(model);
        int opCol = table.getColumnModel().getColumnIndex("操作");
        table.getColumnModel().getColumn(opCol).setCellRenderer(new BtnRenderer("关闭", UITheme.DANGER));
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (row < 0 || col != opCol) return;
                TutorRequirement target = list.get(row);
                if (target.isClosed()) { UITheme.showError(root, "该需求已关闭"); return; }
                int choice = JOptionPane.showConfirmDialog(root, "确定关闭该需求？", "确认", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    target.setClosed(true);
                    requireDao.update(r -> r.getReqID().equals(target.getReqID()), target);
                    UITheme.showSuccess(root, "需求已关闭");
                    rebuildPanel(root, buildMyRequirementPanel(currentUser));
                }
            }
        });
        table.addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseMoved(MouseEvent e) {
                table.setCursor(table.columnAtPoint(e.getPoint()) == opCol
                        ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());
            }
        });
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        root.add(scroll, BorderLayout.CENTER);
        return root;
    }

    public static JPanel buildBrowseStudentsPanel() {
        JPanel root = pageRoot("🔍  浏览大学生家教");
        List<Student> allVisible = userDao.readAll().stream()
                .filter(u -> u instanceof Student).map(u -> (Student) u)
                .filter(s -> s.isVisible() && s.getAccept() == 1).toList();

        // ── 搜索栏 ──────────────────────────────────────────
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchBar.setOpaque(false);
        searchBar.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        JTextField gradeSearch = UITheme.roundedField("");
        gradeSearch.setPreferredSize(new Dimension(160, 34));
        gradeSearch.setToolTipText("按可辅导年级搜索，如：小学、初中、高中");
        gradeSearch.setText("按可辅导年级搜索…");
        gradeSearch.setForeground(UITheme.TEXT_LIGHT);
        gradeSearch.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (gradeSearch.getText().equals("按可辅导年级搜索…")) { gradeSearch.setText(""); gradeSearch.setForeground(UITheme.TEXT_MAIN); }
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                if (gradeSearch.getText().isEmpty()) { gradeSearch.setText("按可辅导年级搜索…"); gradeSearch.setForeground(UITheme.TEXT_LIGHT); }
            }
        });

        JTextField priceSearch = UITheme.roundedField("");
        priceSearch.setPreferredSize(new Dimension(160, 34));
        priceSearch.setToolTipText("输入最高价格，筛选 ≤ 该值的学生，如：100");
        priceSearch.setText("价格上限（如：100）…");
        priceSearch.setForeground(UITheme.TEXT_LIGHT);
        priceSearch.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (priceSearch.getText().equals("价格上限（如：100）…")) { priceSearch.setText(""); priceSearch.setForeground(UITheme.TEXT_MAIN); }
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                if (priceSearch.getText().isEmpty()) { priceSearch.setText("价格上限（如：100）…"); priceSearch.setForeground(UITheme.TEXT_LIGHT); }
            }
        });

        JButton searchBtn = UITheme.primaryButton("🔍 搜索");
        searchBtn.setPreferredSize(new Dimension(90, 34));
        JButton resetBtn  = UITheme.secondaryButton("重置");
        resetBtn.setPreferredSize(new Dimension(70, 34));

        JLabel hintLbl = new JLabel("  年级：模糊匹配；价格：输入数字，展示 ≤ 该值的所有学生");
        hintLbl.setFont(UITheme.FONT_SMALL); hintLbl.setForeground(UITheme.TEXT_LIGHT);
        JTextField addressSearch = UITheme.roundedField("");
        addressSearch.setPreferredSize(new Dimension(160, 34));
        addressSearch.setToolTipText("按辅导地址搜索，如：海淀区、朝阳区");
        addressSearch.setText("按辅导地址搜索…");
        addressSearch.setForeground(UITheme.TEXT_LIGHT);
        addressSearch.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (addressSearch.getText().equals("按辅导地址搜索…")) {
                    addressSearch.setText("");
                    addressSearch.setForeground(UITheme.TEXT_MAIN);
                }
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                if (addressSearch.getText().isEmpty()) {
                    addressSearch.setText("按辅导地址搜索…");
                    addressSearch.setForeground(UITheme.TEXT_LIGHT);
                }
            }
        });
        searchBar.add(gradeSearch); searchBar.add(priceSearch);
        searchBar.add(addressSearch);
        searchBar.add(searchBtn);   searchBar.add(resetBtn);
        searchBar.add(hintLbl);

        // ── 结果区（动态刷新） ──────────────────────────────
        JPanel resultHolder = new JPanel(new BorderLayout());
        resultHolder.setOpaque(false);

        Runnable refreshGrid = () -> {

            String gKw = gradeSearch.getText().equals("按可辅导年级搜索…")
                    ? "" : gradeSearch.getText().trim().toLowerCase();

            String aKw = addressSearch.getText().equals("按辅导地址搜索…")
                    ? "" : addressSearch.getText().trim().toLowerCase();

            String pRaw = priceSearch.getText().equals("价格上限（如：100）…")
                    ? "" : priceSearch.getText().trim();

            int priceMax = Integer.MAX_VALUE;

            if (!pRaw.isEmpty()) {
                try {
                    priceMax = Integer.parseInt(pRaw.replaceAll("[^0-9]", ""));
                } catch (NumberFormatException ignored) {}
            }

            final int maxVal = priceMax;

            List<Student> filtered = allVisible.stream()

                    // 年级过滤
                    .filter(s ->
                            gKw.isEmpty() ||
                                    (s.getTargetGrages() != null &&
                                            s.getTargetGrages().toLowerCase().contains(gKw))
                    )

                    // 地址过滤
                    .filter(s ->
                            aKw.isEmpty() ||
                                    (s.getaddress() != null &&
                                            s.getaddress().toLowerCase().contains(aKw))
                    )

                    // 价格过滤
                    .filter(s -> {
                        if (maxVal == Integer.MAX_VALUE) return true;

                        if (s.getPrice() == null) return false;

                        try {
                            int val = Integer.parseInt(s.getPrice().replaceAll("[^0-9]", ""));
                            return val <= maxVal;
                        } catch (NumberFormatException e) {
                            return false;
                        }
                    })

                    .toList();

            resultHolder.removeAll();

            if (filtered.isEmpty()) {
                resultHolder.add(emptyHint("没有符合条件的学生，请调整搜索条件"), BorderLayout.CENTER);
            } else {

                JPanel grid = new JPanel(new GridLayout(0, 2, 16, 16));
                grid.setOpaque(false);

                for (Student s : filtered) {
                    grid.add(buildStudentCard(s));
                }

                resultHolder.add(scrollPane(grid), BorderLayout.CENTER);
            }

            resultHolder.revalidate();
            resultHolder.repaint();
        };

        searchBtn.addActionListener(e -> refreshGrid.run());
        resetBtn.addActionListener(e -> {
            gradeSearch.setText("按可辅导年级搜索…"); gradeSearch.setForeground(UITheme.TEXT_LIGHT);
            addressSearch.setText("按辅导地址搜索…"); addressSearch.setForeground(UITheme.TEXT_LIGHT);
            priceSearch.setText("价格上限（如：100）…"); priceSearch.setForeground(UITheme.TEXT_LIGHT);
            refreshGrid.run();
        });
        gradeSearch.addActionListener(e -> refreshGrid.run());
        priceSearch.addActionListener(e -> refreshGrid.run());
        addressSearch.addActionListener(e -> refreshGrid.run());

        if (allVisible.isEmpty()) {
            resultHolder.add(emptyHint("暂无通过审核的学生展示"), BorderLayout.CENTER);
        } else {
            refreshGrid.run();
        }

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setOpaque(false);
        Component titleBar = root.getComponent(0);
        root.remove(titleBar);
        northPanel.add(titleBar, BorderLayout.NORTH);
        northPanel.add(searchBar, BorderLayout.SOUTH);
        root.add(northPanel, BorderLayout.NORTH);
        root.add(resultHolder, BorderLayout.CENTER);
        return root;
    }

    /**
     * 搜索结果面板：按关键词匹配学生（姓名/学校/科目）和家长（姓名/用户名）
     */
    public static JPanel buildSearchResultPanel(String keyword, User currentUser) {
        String kw = keyword.toLowerCase();
        JPanel root = pageRoot("🔍  搜索结果：「" + keyword + "」");

        // 搜索已通过审核的学生
        List<Student> students = userDao.readAll().stream()
                .filter(u -> u instanceof Student).map(u -> (Student) u)
                .filter(s -> s.isVisible() && s.getAccept() == 1)
                .filter(s -> matchKw(kw, s.getName(), s.getSchool(), s.getMaior(),
                        s.getSubject(), s.getTargetGrages(), s.getAdvantage()))
                .toList();

        // 搜索家长（用户名/姓名）
        List<User> parents = userDao.readAll().stream()
                .filter(u -> u.getRole().equals("Parent"))
                .filter(u -> matchKw(kw, u.getName(), u.getUsername(), u.getPhone()))
                .toList();

        if (students.isEmpty() && parents.isEmpty()) {
            root.add(emptyHint("未找到与「" + keyword + "」相关的用户"), BorderLayout.CENTER);
            return root;
        }

        JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        resultPanel.setOpaque(false);

        if (!students.isEmpty()) {
            JLabel sectionLbl = new JLabel("  👨‍🎓 匹配的学生家教（" + students.size() + " 位）");
            sectionLbl.setFont(UITheme.FONT_H2); sectionLbl.setForeground(UITheme.TEXT_SUB);
            sectionLbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
            sectionLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            resultPanel.add(sectionLbl);
            JPanel grid = new JPanel(new GridLayout(0, 2, 16, 16));
            grid.setOpaque(false);
            grid.setAlignmentX(Component.LEFT_ALIGNMENT);
            for (Student s : students) grid.add(buildStudentCard(s));
            resultPanel.add(grid);
            resultPanel.add(Box.createVerticalStrut(20));
        }

        if (!parents.isEmpty()) {
            JLabel sectionLbl = new JLabel("  👨‍👩‍👧 匹配的家长（" + parents.size() + " 位）");
            sectionLbl.setFont(UITheme.FONT_H2); sectionLbl.setForeground(UITheme.TEXT_SUB);
            sectionLbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
            sectionLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            resultPanel.add(sectionLbl);
            JPanel grid = new JPanel(new GridLayout(0, 2, 16, 16));
            grid.setOpaque(false);
            grid.setAlignmentX(Component.LEFT_ALIGNMENT);
            for (User p : parents) {
                JPanel card = UITheme.card();
                card.setLayout(new GridBagLayout());
                GridBagConstraints gbc2 = new GridBagConstraints();
                gbc2.gridx = 0; gbc2.gridy = GridBagConstraints.RELATIVE;
                gbc2.fill = GridBagConstraints.HORIZONTAL; gbc2.weightx = 1;
                gbc2.insets = new Insets(3, 0, 3, 0);
                JLabel name = new JLabel("👨‍👩‍👧 " + (p.getName() != null ? p.getName() : p.getUsername()));
                name.setFont(UITheme.FONT_H3); name.setForeground(UITheme.TEXT_MAIN);
                card.add(name, gbc2);
                card.add(infoRow("用户名", p.getUsername()), gbc2);
                card.add(infoRow("手机号", p.getPhone() != null ? p.getPhone() : "-"), gbc2);
                grid.add(card);
            }
            resultPanel.add(grid);
        }

        root.add(scrollPane(resultPanel), BorderLayout.CENTER);
        return root;
    }

    /** 多字段关键词匹配辅助 */
    private static boolean matchKw(String kw, String... fields) {
        for (String f : fields) {
            if (f != null && f.toLowerCase().contains(kw)) return true;
        }
        return false;
    }


    private static JPanel buildStudentCard(Student s) {
        JPanel card = UITheme.card();
        card.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        gbc.insets = new Insets(3, 0, 3, 0);
        JLabel name = new JLabel("👨‍🎓 " + s.getName());
        name.setFont(UITheme.FONT_H3); name.setForeground(UITheme.TEXT_MAIN);
        card.add(name, gbc);
        card.add(infoRow("学校专业", s.getSchool() + " · " + s.getMaior()), gbc);
        card.add(infoRow("辅导科目", s.getSubject() != null ? s.getSubject() : "-"), gbc);
        card.add(infoRow("辅导年级", s.getTargetGrages() != null ? s.getTargetGrages() : "-"), gbc);
        card.add(infoRow("收费标准", s.getPrice() != null ? s.getPrice() : "-"), gbc);
        card.add(infoRow("辅导方式", s.getWay() != null ? s.getWay() : "-"), gbc);
        card.add(infoRow("辅导地址", s.getaddress() != null ? s.getaddress() : "-"), gbc);

        card.add(infoRow("联系电话", s.getPhone() != null ? s.getPhone() : "-"), gbc);
        // 只展示优势文本（不含URL/验证码）
        String advTxt = advText(s.getAdvantage());
        if (advTxt != null && !advTxt.isEmpty())
            card.add(infoRow("个人优势", advTxt), gbc);
        // 学历查验网址（供家长点击核验）
        String reportUrl = advReportUrl(s.getAdvantage());
        if (reportUrl != null && !reportUrl.isEmpty()) {
            String verifyCode = advVerifyCode(s.getAdvantage());
            JLabel verifyLbl = new JLabel("  🎓 已提供学历查验 — 点击核验");
            verifyLbl.setFont(UITheme.FONT_SMALL);
            verifyLbl.setForeground(UITheme.PRIMARY);
            verifyLbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            verifyLbl.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    String msg = "学历在线查验网址：\n" + reportUrl
                            + (verifyCode.isEmpty() ? "" : "\n\n验证码：" + verifyCode);
                    int choice = JOptionPane.showOptionDialog(card, msg, "学历核验",
                            JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
                            new String[]{"打开网址", "关闭"}, "打开网址");
                    if (choice == 0) {
                        try { java.awt.Desktop.getDesktop().browse(new java.net.URI(reportUrl)); }
                        catch (Exception ex) { UITheme.showError(card, "无法打开浏览器：" + ex.getMessage()); }
                    }
                }
            });
            card.add(verifyLbl, gbc);
        }
        return card;
    }

    public static JPanel buildAdminOverviewPanel() {
        JPanel root = pageRoot("🏠  系统概况");
        int totalUsers    = userDao.readAll().size();
        int totalStudents = userDao.findLits(u -> u.getRole().equals("Student")).size();
        int totalParents  = userDao.findLits(u -> u.getRole().equals("Parent")).size();
        int pendingAudit  = (int) userDao.findLits(u -> u instanceof Student s && s.getAccept() == 0).stream().count();
        int totalReqs     = requireDao.readAll().size();
        int openReqs      = requireDao.findLits(r -> !r.isClosed()).size();
        JPanel stats = new JPanel(new GridLayout(2, 3, 16, 16));
        stats.setOpaque(false);
        stats.add(statCard("总用户数",   String.valueOf(totalUsers),    UITheme.PRIMARY));
        stats.add(statCard("学生数量",   String.valueOf(totalStudents), UITheme.SUCCESS));
        stats.add(statCard("家长数量",   String.valueOf(totalParents),  new Color(0x8B5CF6)));
        stats.add(statCard("待审核学生", String.valueOf(pendingAudit),  UITheme.WARNING));
        stats.add(statCard("总需求数",   String.valueOf(totalReqs),     new Color(0xEC4899)));
        stats.add(statCard("开放需求",   String.valueOf(openReqs),      UITheme.SUCCESS));
        root.add(stats, BorderLayout.NORTH);
        return root;
    }

    private static JPanel statCard(String label, String value, Color color) {
        JPanel card = UITheme.card();
        card.setLayout(new BorderLayout(0, 8));
        JLabel val = new JLabel(value, SwingConstants.CENTER);
        val.setFont(UITheme.font(Font.BOLD, 36)); val.setForeground(color);
        JLabel lbl = new JLabel(label, SwingConstants.CENTER);
        lbl.setFont(UITheme.FONT_LABEL); lbl.setForeground(UITheme.TEXT_SUB);
        card.add(val, BorderLayout.CENTER); card.add(lbl, BorderLayout.SOUTH);
        return card;
    }

    /** 判断账户是否已被禁用（密码以 !DISABLED! 开头） */
    private static boolean isDisabled(User u) {
        return u.getPassword() != null && u.getPassword().startsWith("!DISABLED!");
    }

    public static JPanel buildUserManagePanel() {
        JPanel root = pageRoot("👥  用户管理");
        List<User> list = userDao.readAll();
        String[] cols = {"用户名", "姓名", "角色", "手机号", "状态", "操作"};
        Object[][] data = new Object[list.size()][cols.length];
        for (int i = 0; i < list.size(); i++) {
            User u = list.get(i);
            boolean dis = isDisabled(u);
            data[i] = new Object[]{u.getUsername(), u.getName(), u.getRole(), u.getPhone(),
                    dis ? "🔴 已禁用" : "🟢 正常",
                    dis ? "启用" : "禁用"};
        }
        DefaultTableModel model = new DefaultTableModel(data, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = styledTable(null, null);
        table.setModel(model);
        final int opCol = table.getColumnModel().getColumnIndex("操作");
        // 动态渲染：禁用账户显示绿色"启用"，正常账户显示橙色"禁用"
        table.getColumnModel().getColumn(opCol).setCellRenderer((t, val, sel, focus, row, col) -> {
            boolean dis = isDisabled(list.get(row));
            JButton btn = new JButton(dis ? "启用" : "禁用") {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(dis ? UITheme.SUCCESS : UITheme.WARNING);
                    g2.fillRoundRect(4, 4, getWidth()-8, getHeight()-8, 8, 8);
                    g2.dispose(); super.paintComponent(g);
                }
            };
            btn.setFont(UITheme.font(Font.BOLD, 12));
            btn.setForeground(Color.WHITE);
            btn.setContentAreaFilled(false); btn.setBorderPainted(false); btn.setFocusPainted(false);
            return btn;
        });
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (row < 0 || col != opCol) return;
                User target = list.get(row);
                boolean dis = isDisabled(target);
                if (dis) {
                    // 启用：去掉 !DISABLED! 前缀
                    target.setPassword(target.getPassword().substring("!DISABLED!".length()));
                    // 若是学生且原先通过审核，恢复可见
                    if (target instanceof Student s && s.getAccept() == 1) s.setVisible(true);
                    userDao.update(u -> u.getUsername().equals(target.getUsername()), target);
                    UITheme.showSuccess(root, "已启用账户「" + target.getName() + "」");
                } else {
                    int ch = JOptionPane.showConfirmDialog(root,
                            "确定禁用「" + target.getName() + "」？\n该用户将无法登录，相关信息将下架（数据保留）。",
                            "确认禁用", JOptionPane.YES_NO_OPTION);
                    if (ch != JOptionPane.YES_OPTION) return;
                    // 禁用：密码前加 !DISABLED! 前缀
                    target.setPassword("!DISABLED!" + target.getPassword());
                    // 下架学生信息
                    if (target instanceof Student s) s.setVisible(false);
                    userDao.update(u -> u.getUsername().equals(target.getUsername()), target);
                    // 下架该用户的所有未关闭需求（家长）
                    requireDao.findLits(r -> r.getParentUsername().equals(target.getUsername()) && !r.isClosed())
                            .forEach(r -> {
                                r.setClosed(true);
                                requireDao.update(req -> req.getReqID().equals(r.getReqID()), r);
                            });
                    UITheme.showSuccess(root, "已禁用账户「" + target.getName() + "」，相关信息已下架");
                }
                rebuildPanel(root, buildUserManagePanel());
            }
        });
        table.addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseMoved(MouseEvent e) {
                table.setCursor(table.columnAtPoint(e.getPoint()) == opCol
                        ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());
            }
        });
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        root.add(scroll, BorderLayout.CENTER);
        return root;
    }

    public static JPanel buildPublishAnnouncementPanel() {
        JPanel root = pageRoot("📣  发布公告");
        JPanel card = UITheme.card();
        card.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        gbc.insets = new Insets(8, 0, 8, 0);
        JTextField titleField = UITheme.roundedField("公告标题");
        JTextArea contentArea = new JTextArea(6, 30);
        contentArea.setFont(UITheme.FONT_LABEL);
        contentArea.setLineWrap(true); contentArea.setWrapStyleWord(true);
        JScrollPane contentScroll = new JScrollPane(contentArea);
        contentScroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));
        card.add(UITheme.formRow("标　题*", titleField), gbc);
        card.add(UITheme.formRow("内　容*", contentScroll), gbc);
        JButton publishBtn = UITheme.primaryButton("发布公告");
        publishBtn.setPreferredSize(new Dimension(140, 40));
        card.add(centerWrap(publishBtn), gbc);
        publishBtn.addActionListener(e -> {
            String t = titleField.getText().trim();
            String c = contentArea.getText().trim();
            if (Validator.isEmpty(t) || Validator.isEmpty(c)) {
                UITheme.showError(publishBtn, "标题和内容不能为空！"); return;
            }
            Announcement ann = new Announcement();
            ann.setTitle(t); ann.setContent(c);
            announceDao.add(ann);
            UITheme.showSuccess(publishBtn, "公告发布成功！");
            titleField.setText(""); contentArea.setText("");
        });
        root.add(scrollPane(card), BorderLayout.CENTER);
        return root;
    }

    public static JPanel buildAllRequirementsPanel() {
        JPanel root = pageRoot("📋  需求管理");
        List<TutorRequirement> list = requireDao.readAll();
        if (list.isEmpty()) { root.add(emptyHint("暂无任何需求"), BorderLayout.CENTER); return root; }
        String[] cols = {"需求ID", "家长", "科目", "年级", "地址", "薪酬", "状态", "操作"};
        Object[][] data = new Object[list.size()][cols.length];
        for (int i = 0; i < list.size(); i++) {
            TutorRequirement r = list.get(i);
            data[i] = new Object[]{r.getReqID(), r.getParentUsername(), r.getSubject(),
                    r.getGradeLevel(), r.getAddress(), r.getMoney(),
                    r.isClosed() ? "已关闭" : "开放中", "删除"};
        }
        DefaultTableModel model = new DefaultTableModel(data, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = styledTable(null, null);
        table.setModel(model);
        int opCol = table.getColumnModel().getColumnIndex("操作");
        table.getColumnModel().getColumn(opCol).setCellRenderer(new BtnRenderer("删除", UITheme.DANGER));
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (row < 0 || col != opCol) return;
                TutorRequirement target = list.get(row);
                requireDao.del(r -> r.getReqID().equals(target.getReqID()));
                UITheme.showSuccess(root, "已删除");
                rebuildPanel(root, buildAllRequirementsPanel());
            }
        });
        table.addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseMoved(MouseEvent e) {
                table.setCursor(table.columnAtPoint(e.getPoint()) == opCol
                        ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());
            }
        });
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        root.add(scroll, BorderLayout.CENTER);
        return root;
    }

    // ★ 核心修复：学生审核面板，完全使用 MouseListener
    public static JPanel buildStudentAuditPanel() {
        JPanel root = pageRoot("✅  学生审核");
        List<User> users = userDao.findLits(u -> u.getRole().equals("Student"));
        List<Student> students = users.stream()
                .filter(u -> u instanceof Student).map(u -> (Student) u).toList();
        if (students.isEmpty()) { root.add(emptyHint("暂无学生账号"), BorderLayout.CENTER); return root; }

        String[] cols = {"用户名", "姓名", "学校", "专业", "经验(年)", "审核状态", "通过", "拒绝"};
        String[] statusText = {"待审核", "已通过", "已拒绝"};
        Object[][] data = new Object[students.size()][cols.length];
        for (int i = 0; i < students.size(); i++) {
            Student s = students.get(i);
            int acc = Math.min(s.getAccept(), 2);
            data[i] = new Object[]{s.getUsername(), s.getName(), s.getSchool(), s.getMaior(),
                    s.getExperience(), statusText[acc], "通过", "拒绝"};
        }

        DefaultTableModel model = new DefaultTableModel(data, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = styledTable(null, null);
        table.setModel(model);

        // 注意：必须在 setModel 之后才能 getColumnIndex
        final int approveCol = table.getColumnModel().getColumnIndex("通过");
        final int rejectCol  = table.getColumnModel().getColumnIndex("拒绝");

        table.getColumnModel().getColumn(approveCol).setCellRenderer(new BtnRenderer("通过", UITheme.SUCCESS));
        table.getColumnModel().getColumn(rejectCol).setCellRenderer(new BtnRenderer("拒绝", UITheme.DANGER));

        // ★ MouseListener：点哪行哪列就操作哪个学生，无歧义
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (row < 0) return;

                Student s = students.get(row);

                if (col == approveCol) {
                    boolean ok = adminService.approveStudent(s.getUsername());
                    UITheme.showSuccess(root, ok
                            ? "已通过「" + s.getName() + "」的审核"
                            : "操作失败");
                    rebuildPanel(root, buildStudentAuditPanel());

                } else if (col == rejectCol) {
                    boolean ok = adminService.rejectStudent(s.getUsername());
                    UITheme.showSuccess(root, ok
                            ? "已拒绝「" + s.getName() + "」的审核"
                            : "操作失败");
                    rebuildPanel(root, buildStudentAuditPanel());
                }
            }
        });

        // 悬浮在按钮列时显示手型光标
        table.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int col = table.columnAtPoint(e.getPoint());
                table.setCursor((col == approveCol || col == rejectCol)
                        ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                        : Cursor.getDefaultCursor());
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        root.add(scroll, BorderLayout.CENTER);
        return root;
    }

    // ── 工具方法 ─────────────────────────────────────────────
    private static JPanel pageRoot(String pageTitle) {
        JPanel root = new JPanel(new BorderLayout(0, 16));
        root.setBackground(UITheme.BG_MAIN);
        root.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setOpaque(false);
        JLabel title = new JLabel(pageTitle);
        title.setFont(UITheme.FONT_TITLE); title.setForeground(UITheme.TEXT_MAIN);
        titleBar.add(title, BorderLayout.WEST);
        root.add(titleBar, BorderLayout.NORTH);
        return root;
    }

    private static JTable styledTable(Object[][] data, String[] cols) {
        JTable table = data != null ? new JTable(data, cols) : new JTable();
        table.setFont(UITheme.FONT_LABEL);
        table.setRowHeight(38);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setBackground(Color.WHITE);
        table.setForeground(UITheme.TEXT_MAIN);
        table.setSelectionBackground(new Color(0xEFF6FF));
        table.setSelectionForeground(UITheme.TEXT_MAIN);
        table.getTableHeader().setFont(UITheme.FONT_BOLD);
        table.getTableHeader().setBackground(new Color(0xF8FAFC));
        table.getTableHeader().setForeground(UITheme.TEXT_SUB);
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        if (data != null) table.setDefaultEditor(Object.class, null);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object val,
                                                                     boolean sel, boolean focus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                if (!sel) c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(0xFAFAFA));
                setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                return c;
            }
        });
        return table;
    }

    private static JPanel infoRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        JLabel lbl = new JLabel(label + "：");
        lbl.setFont(UITheme.FONT_LABEL); lbl.setForeground(UITheme.TEXT_LIGHT);
        lbl.setPreferredSize(new Dimension(90, 28)); lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        JLabel val = new JLabel(value != null ? value : "-");
        val.setFont(UITheme.FONT_LABEL); val.setForeground(UITheme.TEXT_MAIN);
        row.add(lbl, BorderLayout.WEST); row.add(val, BorderLayout.CENTER);
        return row;
    }

    /** 分区分隔标签 */
    private static JLabel sectionSep(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UITheme.font(Font.BOLD, 12));
        lbl.setForeground(UITheme.TEXT_LIGHT);
        return lbl;
    }

    private static JLabel badge(String text, Color color) {
        JLabel lbl = new JLabel("  " + text + "  ") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 25));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose(); super.paintComponent(g);
            }
        };
        lbl.setFont(UITheme.font(Font.BOLD, 12)); lbl.setForeground(color); lbl.setOpaque(false);
        return lbl;
    }

    private static JPanel centerWrap(JComponent comp) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        p.setOpaque(false); p.add(comp); return p;
    }

    private static JScrollPane scrollPane(JComponent comp) {
        JScrollPane sp = new JScrollPane(comp);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.setOpaque(false); sp.getViewport().setOpaque(false); return sp;
    }

    private static JLabel emptyHint(String msg) {
        JLabel lbl = new JLabel(msg, SwingConstants.CENTER);
        lbl.setFont(UITheme.font(Font.PLAIN, 15)); lbl.setForeground(UITheme.TEXT_LIGHT); return lbl;
    }

    private static void rebuildPanel(JPanel target, JPanel newContent) {
        target.removeAll();
        for (Component c : newContent.getComponents()) {
            target.add(c, ((BorderLayout) newContent.getLayout()).getConstraints(c));
        }
        target.revalidate(); target.repaint();
    }

    // 只负责渲染，不参与编辑
    static class BtnRenderer extends DefaultTableCellRenderer {
        private final JButton btn;
        BtnRenderer(String text, Color color) {
            btn = new JButton(text) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(color);
                    g2.fillRoundRect(4, 4, getWidth() - 8, getHeight() - 8, 8, 8);
                    g2.dispose(); super.paintComponent(g);
                }
            };
            btn.setFont(UITheme.font(Font.BOLD, 12));
            btn.setForeground(Color.WHITE);
            btn.setContentAreaFilled(false); btn.setBorderPainted(false); btn.setFocusPainted(false);
        }
        @Override public Component getTableCellRendererComponent(JTable t, Object val,
                                                                 boolean sel, boolean focus, int row, int col) { return btn; }
    }
    private static int parsePrice(String p){
        if(p == null) return Integer.MAX_VALUE;
        try{
            return Integer.parseInt(p.replaceAll("[^0-9]", ""));
        }catch(Exception e){
            return Integer.MAX_VALUE;
        }
    }
}