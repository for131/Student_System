package org.example.service;

import org.example.dao.UserDao;
import org.example.model.Admin;
import org.example.model.User;

import java.util.List;

public class UserService {
    private UserDao userdao = new UserDao();
    private AdminService admin = new AdminService();
    public static User currentUser;
    public boolean login(String name,String password){
        List<User> users = userdao.findLits(u -> u.getUsername().equals(name));
        if(!users.isEmpty()){
            User user = users.get(0);
            if(user.getPassword().equals(password)){
                currentUser = user;
                return true;
            }
        }
        return false;
    }

    public boolean register(User user){
        List<User> users = userdao.findLits(u -> u.getUsername().equals((user.getUsername())));
        if(users.isEmpty()){
                userdao.add(user);
                return true;
        }else{
            return true;
        }
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    /*

 */
}
