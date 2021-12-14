package com.example.tinpet.entities;

public class AdminItem {

    private String email;
    private String name;
    private String password;
    private String adminType;
    private String uid;

    public AdminItem(String uid, String email, String name, String password, String adminType){
        this.email = email;
        this.name = name;
        this.password = password;
        this.adminType = adminType;
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAdminType() {
        return adminType;
    }

    public void setAdminType(String adminType) {
        this.adminType = adminType;
    }
}
