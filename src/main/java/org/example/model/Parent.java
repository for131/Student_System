package org.example.model;

public class Parent extends User {
    private String address;
    public Parent(){
        super();
        this.setRole("Parent");
    }

    public Parent(String username, String role, String password, String name, String phone) {
        super(username, role, password, name, phone);
        this.setRole("Parent");
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    @Override
    public String toString(){
        return "家长用户[" + getName() +"] - 地址" + address;
    }

}
