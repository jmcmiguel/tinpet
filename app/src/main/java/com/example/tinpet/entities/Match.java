package com.example.tinpet.entities;

public class Match {

    private String name;
    private String picture;
    private String location;
    private String date;
    private String chatID;
    private String otherUserID;
    private Long rawDate;
    private String breed;
    private String bday;
    private String petSize;

    public Match(String name, String picture, String location, String date, String chatID, String otherUserID, Long rawDate, String breed, String bday, String petSize) {
        this.name = name;
        this.picture = picture;
        this.location = location;
        this.date = date;
        this.chatID = chatID;
        this.otherUserID = otherUserID;
        this.rawDate = rawDate;
        this.breed = breed;
        this.bday = bday;
        this.petSize = petSize;
    }

    public String getPetSize() {
        return petSize;
    }

    public void setPetSize(String petSize) {
        this.petSize = petSize;
    }

    public String getBday() {
        return bday;
    }

    public void setBday(String bday) {
        this.bday = bday;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public Long getRawDate() {
        return rawDate;
    }

    public void setRawDate(Long rawDate) {
        this.rawDate = rawDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
    public String getChatID() {
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }
    public String getOtherUserID() {
        return otherUserID;
    }

    public void setOtherUserID(String otherUserID) {
        this.otherUserID = otherUserID;
    }
}
