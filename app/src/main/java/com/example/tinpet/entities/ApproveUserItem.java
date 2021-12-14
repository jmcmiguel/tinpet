package com.example.tinpet.entities;

public class ApproveUserItem {

    private String email;
    private String name;
    private String gender;
    private String bday;
    private String uid;
    private String medcert;
    private String validID;

    public ApproveUserItem(String uid, String name, String email, String gender, String bday, String medcert, String validID){
        this.email = email;
        this.name = name;
        this.gender = gender;
        this.bday = bday;
        this.uid = uid;
        this.validID = validID;
        this.medcert = medcert;
    }

    public String getMedcert() {
        return medcert;
    }

    public void setMedcert(String medcert) {
        this.medcert = medcert;
    }

    public String getValidID() {
        return validID;
    }

    public void setValidID(String validID) {
        this.validID = validID;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBday() {
        return bday;
    }

    public void setBday(String bday) {
        this.bday = bday;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
