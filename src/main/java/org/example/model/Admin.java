package org.example.model;

public class Admin extends User {
    public Admin(){
        super();
        this.setRole("Admin");
    }
    public Admin(String username, String role, String password, String name, String phone){
        super(username, "Admin", password, name, phone);
    }
}