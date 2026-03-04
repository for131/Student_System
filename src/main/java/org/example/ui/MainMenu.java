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
        JTextField subjectField   = UITheme.roundedField("可辅导的学科");
        JTextField gradeField2    = UITheme.roundedField("可辅导的年级段");
        JTextField priceField     = UITheme.roundedField("收费标准，如：100元/小时");
        JTextField wayField       = UITheme.roundedField("上门/线上/均可");
        JTextField advantageField = UITheme.roundedField("获奖经历、擅长科目等");
        JSpinner expSpinner = new JSpinner(new SpinnerNumberModel(student.getExperience(), 0, 20, 1));
        if (student.getSubject()      != null) subjectField.setText(student.getSubject());
        if (student.getTargetGrages() != null) gradeField2.setText(student.getTargetGrages());
        if (student.getPrice()        != null) priceField.setText(student.getPrice());
        if (student.getWay()          != null) wayField.setText(student.getWay());
        if (student.getAdvantage()    != null) advantageField.setText(student.getAdvantage());
        card.add(UITheme.formRow("辅导科目", subjectField), gbc);
        card.add(UITheme.formRow("辅导年级", gradeField2), gbc);
        card.add(UITheme.formRow("收费标准", priceField), gbc);
        card.add(UITheme.formRow("辅导方式", wayField), gbc);
        card.add(UITheme.formRow("家教经验(年)", expSpinner), gbc);
        card.add(UITheme.formRow("个人优势", advantageField), gbc);
        JButton saveBtn = UITheme.primaryButton("保存信息");
        saveBtn.setPreferredSize(new Dimension(140, 38));
        card.add(centerWrap(saveBtn), gbc);
        saveBtn.addActionListener(e -> {
            student.setSubject(subjectField.getText().trim());
            student.setTargetGrages(gradeField2.getText().trim());
            student.setPrice(priceField.getText().trim());
            student.setWay(wayField.getText().trim());
            student.setAdvantage(advantageField.getText().trim());
            student.setExperience((Integer) expSpinner.getValue());
            student.setVisible(student.getAccept() == 1 && !Validator.isEmpty(student.getSubject()));
            userDao.update(u -> u.getUsername().equals(student.getUsername()), student);
            UserService.currentUser = student;
            UITheme.showSuccess(saveBtn, "保存成功！");
        });
        root.add(scrollPane(card), BorderLayout.CENTER);
        return root;
    }

    public static JPanel buildRequirementListPanel(User currentUser) {
        JPanel root = pageRoot("📋  家教需求列表");
        List<TutorRequirement> list = requireDao.findLits(r -> !r.isClosed());
        if (list.isEmpty()) { root.add(emptyHint("暂无开放的家教需求"), BorderLayout.CENTER); return root; }
        String[] cols = {"需求ID", "发布家长", "科目", "年级", "地址", "薪酬", "时间", "要求"};
        Object[][] data = new Object[list.size()][cols.length];
        for (int i = 0; i < list.size(); i++) {
            TutorRequirement r = list.get(i);
            data[i] = new Object[]{r.getReqID(), r.getParentUsername(), r.getSubject(),
                    r.getGradeLevel(), r.getAddress(), r.getMoney(), r.getDuration(), r.getNeed()};
        }
        JTable table = styledTable(data, cols);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);
        root.add(scroll, BorderLayout.CENTER);
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
        List<User> users = userDao.findLits(u -> u.getRole().equals("Student"));
        List<Student> visible = users.stream()
                .filter(u -> u instanceof Student).map(u -> (Student) u)
                .filter(s -> s.isVisible() && s.getAccept() == 1).toList();
        if (visible.isEmpty()) { root.add(emptyHint("暂无通过审核的学生展示"), BorderLayout.CENTER); return root; }
        JPanel grid = new JPanel(new GridLayout(0, 2, 16, 16));
        grid.setOpaque(false);
        for (Student s : visible) grid.add(buildStudentCard(s));
        root.add(scrollPane(grid), BorderLayout.CENTER);
        return root;
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
        if (s.getAdvantage() != null && !s.getAdvantage().isEmpty())
            card.add(infoRow("个人优势", s.getAdvantage()), gbc);
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

    public static JPanel buildUserManagePanel() {
        JPanel root = pageRoot("👥  用户管理");
        List<User> list = userDao.readAll();
        String[] cols = {"用户名", "姓名", "角色", "手机号", "操作"};
        Object[][] data = new Object[list.size()][cols.length];
        for (int i = 0; i < list.size(); i++) {
            User u = list.get(i);
            data[i] = new Object[]{u.getUsername(), u.getName(), u.getRole(), u.getPhone(), "删除"};
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
                User target = list.get(row);
                int ch = JOptionPane.showConfirmDialog(root,
                        "确定删除用户「" + target.getName() + "」？", "确认删除", JOptionPane.YES_NO_OPTION);
                if (ch == JOptionPane.YES_OPTION) {
                    userDao.del(u -> u.getUsername().equals(target.getUsername()));
                    UITheme.showSuccess(root, "已删除用户");
                    rebuildPanel(root, buildUserManagePanel());
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
}
/*、

 */