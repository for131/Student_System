package org.example.model;

public class Student extends User {
    //个人信息
    private String school;
    private String major; // 专业
    private String grade;//年级
    private String address;// 地址
    private String Studentid;
    // 展示到ui界面
    private String subject;//学科
    private String targetGrages;//可辅导年级
    private int experience;//家教经验
    private String price;//收费标准
    private String way; //辅导方式
    private String advantage;//个人优势：获奖，特长
    //系统审核
    private int Accept; //0-待审核，1-审核通过 ，2-审核拒绝
    private boolean isVisible;

    public Student(){
        super();
        this.setRole("Student");
        this.Accept = 0;
        this.isVisible = false;
    }
    public Student(String username, String password, String name, String phone, String school,String major,String grade,String address,String studentid) {
        super(username, password, name, phone, "Parent");
        this.Accept = 0;
        this.isVisible = false;
        this.school = school;
        this.major = major;
        this.grade = grade;
        this.address = address;
        this.Studentid = studentid;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getMaior() {
        return major;
    }

    public void setMaior(String maior) {
        this.major = maior;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getaddress() {
        return address;
    }

    public void setaddress(String pohotPath) {
        this.address = pohotPath;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTargetGrages() {
        return targetGrages;
    }

    public void setTargetGrages(String targetGrages) {
        this.targetGrages = targetGrages;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getWay() {
        return way;
    }

    public void setWay(String way) {
        this.way = way;
    }

    public String getAdvantage() {
        return advantage;
    }

    public void setAdvantage(String advantage) {
        this.advantage = advantage;
    }

    public int getAccept() {
        return Accept;
    }

    public void setAccept(int accept) {
        Accept = accept;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public String getStudentid() {
        return Studentid;
    }

    public void setStudentid(String studentid) {
        Studentid = studentid;
    }

    @Override
    public String toString() {
        return "Student{" +
                "username ='" + getUsername() + '\''+
                ",name = '" +getName() + '\'' +
                ",role='" +getRole()+ '\''+
                ",school ='" + school +'\''+
                ",major = '" + major +'\''+
                '}';
    }
}
