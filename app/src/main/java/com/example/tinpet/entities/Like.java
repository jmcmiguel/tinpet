package com.example.tinpet.entities;

public class Like {

    private String name;
    private String picture;
    private String chatID;
    private Long rawDate;


    public Like(String name, String chatID ,String picture, Long rawDate) {
        this.name = name;
        this.picture = picture;
        this.chatID = chatID;
        this.rawDate = rawDate;
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

    public String getPicture() {
        return picture;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
    public String getChatID() {
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }
}
