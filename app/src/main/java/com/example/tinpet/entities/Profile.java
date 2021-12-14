package com.example.tinpet.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Profile {

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("url")
    @Expose
    private String imageUrl;

    @SerializedName("age")
    @Expose
    private Integer age;

    @SerializedName("location")
    @Expose
    private String location;

    @SerializedName("latitude")
    @Expose
    private String latitude;

    @SerializedName("longitude")
    @Expose
    private String longitude;


    @SerializedName("breed")
    @Expose
    private String breed;

    @SerializedName("gender")
    @Expose
    private String gender;

    @SerializedName("uid")
    @Expose
    private String uid;

    @SerializedName("size")
    @Expose
    private String size;

    @SerializedName("breed2")
    @Expose
    private String breed2;

    @SerializedName("breed3")
    @Expose
    private String breed3;

    @SerializedName("breed4")
    @Expose
    private String breed4;

    @SerializedName("breed5")
    @Expose
    private String breed5;

    public String getBreed2() {
        return breed2;
    }

    public void setBreed2(String breed2) {
        this.breed2 = breed2;
    }

    public String getBreed3() {
        return breed3;
    }

    public void setBreed3(String breed3) {
        this.breed3 = breed3;
    }

    public String getBreed4() {
        return breed4;
    }

    public void setBreed4(String breed4) {
        this.breed4 = breed4;
    }

    public String getBreed5() {
        return breed5;
    }

    public void setBreed5(String breed5) {
        this.breed5 = breed5;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}