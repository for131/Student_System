package org.example.model;

public class Admin extends User {
    public Admin(){
        super();
    }
    public Admin(String username, String role, String password, String name, String phone){
        super(username, role, password, name, phone);
    }
}
