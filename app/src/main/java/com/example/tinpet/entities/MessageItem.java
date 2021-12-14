package com.example.tinpet.entities;

public class MessageItem {

    private String id;
    private String name;
    private String content;
    private int count;
    private String picture;
    private String chatID;
    private Long dateSent;


    public MessageItem() {
    }

    public MessageItem(String id, String name, String content, int count, String picture, String chatID, Long dateSent) {
        this.id = id;
        this.name = name;
        this.content = content;
        this.count = count;
        this.picture = picture;
        this.chatID = chatID;
        this.dateSent = dateSent;
    }

    public Long getDateSent() {
        return dateSent;
    }

    public void setDateSent(Long dateSent) {
        this.dateSent = dateSent;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    public int getCount() {
        return count;
    }

    public String getPicture() {
        return picture;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setPicture(String id) {
        this.id = id;
    }

    public String getChatID() {
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }
}

//myImageView.setImageResource(R.drawable.icon);