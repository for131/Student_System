package org.example;

import org.example.model.Student;
import org.example.model.User;

//TIP 要<b>运行</b>代码，请按 <shortcut actionId="Run"/> 或
// 点击装订区域中的 <icon src="AllIcons.Actions.Execute"/> 图标。
public class Main {
    public static void main(String[] args){

        //TIP 当文本光标位于高亮显示的文本处时按 <shortcut actionId="ShowIntentionActions"/>
        // 查看 IntelliJ IDEA 建议如何修正。
        System.out.printf("Hello and welcome!");

        for (int i = 1; i <= 5; i++) {
            //TIP 按 <shortcut actionId="Debug"/> 开始调试代码。我们已经设置了一个 <icon src="AllIcons.Debugger.Db_set_breakpoint"/> 断点
            // 但您始终可以通过按 <shortcut actionId="ToggleLineBreakpoint"/> 添加更多断点。
            System.out.println("i = " + i);
        }
    }
}
/*
StudentTutorSystem/
├── src/
│   ├── main/
│   │   ├── Application.java             // 程序入口，包含 main 方法
│   │   │
│   │   ├── model/                       // 【模型层】存放实体类（POJO）
│   │   │   ├── User.java                // 用户基类
│   │   │   ├── Student.java             // 大学生子类
│   │   │   ├── Parent.java              // 家长子类
│   │   │   ├── Admin.java               // 管理员类
│   │   │   ├── TutorRequirement.java    // 家教需求实体
│   │   │   ├── ApplicationRecord.java   // 申请记录实体
│   │   │   └── Announcement.java        // 公告实体
│   │   │
│   │   ├── dao/                         // 【数据访问层】直接操作文件或数据库
│   │   │   ├── BaseDao.java             // 通用的增删改查逻辑
│   │   │   ├── UserDao.java
│   │   │   ├── RequirementDao.java
│   │   │   └── AnnouncementDao.java
│   │   │
│   │   ├── service/                     // 【业务逻辑层】处理逻辑（如密码加密、审核过滤）
│   │   │   ├── UserService.java
│   │   │   ├── TutorService.java        // 处理匹配、申请逻辑
│   │   │   └── AdminService.java        // 处理审核、公告逻辑
│   │   │
│   │   ├── ui/                          // 【界面层】控制台交互或 Swing 窗口
│   │   │   ├── MainMenu.java            // 主菜单
│   │   │   ├── LoginView.java           // 登录注册界面
│   │   │   └── DashboardView.java       // 各角色操作面板
│   │   │
│   │   └── utils/                       // 【工具类】
│   │       ├── FileUtil.java            // 文件读写工具（TXT/JSON解析）
│   │       ├── EncryptionUtil.java      // 密码加密（如 MD5 或 Base64）
│   │       └── Validator.java           // 输入合法性检查（手机号、学号格式）
│   │
├── data/                                // 【数据存储目录】
│   ├── users.json                       // 用户数据
│   ├── requirements.json                // 需求数据
│   └── announcements.json               // 公告数据
│
└── lib/                                 // 存放第三方 jar 包（如 Gson 或 Fastjson）
 */


