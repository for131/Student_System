package org.example.service;

import org.example.dao.UserDao;
import org.example.model.User;

import java.util.List;

public class UserService {
    private UserDao userdao = new UserDao();
    public static User currentUser;
    public boolean login(String name,String password){
        List<User> users = userdao.findLits(u -> u.getUsername().equals(name));
        if(!users.isEmpty()){
            User user = users.get(0);
            if(user.getPassword() == password){
                currentUser = user;
                return true;
            }

        }
        return false;
    }

}
