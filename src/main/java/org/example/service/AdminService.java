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
    public boolean check_name(String name){
        List<User> list = userDao.findLits(u -> u.getRole().equals("Name"));
        if(!list.isEmpty()){
            User s1 = list.get(0);
            if(s1.getRole().equals("Student")){
                Student student = (Student) s1;
                if(student.getExperience() >= 3){
                    student.setAccept(1);
                    userDao.update(u -> u.getName().equals(student.getName()),student);
                    return true;
                }else{
                    return false;
                }
            }else{
                return true;
            }
        }
        return false;
    }
}
