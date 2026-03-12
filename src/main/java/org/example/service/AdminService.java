package org.example.service;

import org.example.dao.AnnouncementDao;
import org.example.dao.UserDao;
import org.example.model.Announcement;
import org.example.model.Student;
import org.example.model.TutorRequirement;
import org.example.model.User;

import java.util.List;

public class AdminService {
    private UserDao userDao= new UserDao();
    private AnnouncementDao announcementDao = new AnnouncementDao();

    public void publish(Announcement ann) {
        announcementDao.add(ann);
    }
    public boolean approveStudent(String username) {
        List<User> list = userDao.findLits(u -> u.getUsername().equals(username));
        if (!list.isEmpty() && list.get(0) instanceof Student student) {
            student.setAccept(1);   // 1 = 审核通过
            student.setVisible(true);
            userDao.update(u -> u.getUsername().equals(username), student);
            return true;
        }
        return false;
    }
    //进行审核
    public boolean rejectStudent(String username) {
        List<User> list = userDao.findLits(u -> u.getUsername().equals(username));
        if (!list.isEmpty() && list.get(0) instanceof Student student) {
            student.setAccept(2);   // 2 = 审核拒绝
            student.setVisible(false);
            userDao.update(u -> u.getUsername().equals(username), student);
            return true;
        }
        return false;
    }
    public List<User> getPendingStudents() {
        return userDao.findLits(u -> {
            if (u instanceof Student s) return s.getAccept() == 0;
            return false;
        });
    }
    public List<User> getAllStudents() {
        return userDao.findLits(u -> u.getRole().equals("Student"));
    }
}
