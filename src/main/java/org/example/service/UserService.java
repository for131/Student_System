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
            // 检查账户是否被禁用（密码以 !DISABLED! 开头）
            if (user.getPassword() != null && user.getPassword().startsWith("!DISABLED!")) {
                return false; // 禁用账户直接拒绝，调用方可通过 isAccountDisabled() 区分
            }
//            System.out.println("数据库中的密码: [" + user.getPassword() + "]");
//            System.out.println("输入的密码: [" + password + "]");
            if(user.getPassword().equals(password)){
                currentUser = user;
                return true;
            }
        }
        return false;
    }

    /** 检查用户名对应账户是否被禁用 */
    public boolean isAccountDisabled(String username) {
        List<User> users = userdao.findLits(u -> u.getUsername().equals(username));
        if (!users.isEmpty()) {
            String pwd = users.get(0).getPassword();
            return pwd != null && pwd.startsWith("!DISABLED!");
        }
        return false;
    }

    public boolean register(User user){
        List<User> users = userdao.findLits(u -> u.getUsername().equals((user.getUsername())));
        if(users.isEmpty()){
            userdao.add(user);
            return true;
        }else{
            return false;
        }
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    /*

     */
}