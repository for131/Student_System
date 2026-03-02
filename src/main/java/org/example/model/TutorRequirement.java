package org.example.model;
import java.io.Serializable;
import java.util.UUID;

public class TutorRequirement implements Serializable{
    private String reqID; //需求ID
    private String parentUsername;//跟UserDao匹配数据
    private String subject;//辅导的学科
    private String gradeLevel;//需要什么等级
    private String duration;//时间
    private String address;//地址
    private String money;//价钱
    private String need;//要求
    private boolean isClosed;//是否找到家教

    public TutorRequirement(){
        this.reqID = UUID.randomUUID().toString().substring(0,8);
        this.isClosed = false;
    }

    public TutorRequirement(boolean isClosed, String need, String money, String address, String duration, String gradeLevel, String subject, String parentUsername) {
        this.isClosed = isClosed;
        this.need = need;
        this.money = money;
        this.address = address;
        this.duration = duration;
        this.gradeLevel = gradeLevel;
        this.subject = subject;
        this.parentUsername = parentUsername;
        this.reqID = UUID.randomUUID().toString().substring(0,8);;
    }

    public String getReqID() {
        return reqID;
    }

    public void setReqID(String reqID) {
        this.reqID = reqID;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean closed) {
        isClosed = closed;
    }

    public String getNeed() {
        return need;
    }

    public void setNeed(String need) {
        this.need = need;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getGradeLevel() {
        return gradeLevel;
    }

    public void setGradeLevel(String gradeLevel) {
        this.gradeLevel = gradeLevel;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getParentUsername() {
        return parentUsername;
    }

    public void setParentUsername(String parentUsername) {
        this.parentUsername = parentUsername;
    }
}
