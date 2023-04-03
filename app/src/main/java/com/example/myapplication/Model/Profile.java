package com.example.myapplication.Model;

import java.io.Serializable;

public class Profile implements Serializable {
    private int id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String gender;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    private String avatar;
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Profile(int id, String name, String email, String phone, String address, String gender, String avatar) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.gender = gender;
        this.avatar = avatar;
    }
    public Profile() {
    }
    public void setProfile(int id, String name, String email, String phone, String address, String gender, String avatar){
        this.setId(id);
        this.setName(name);
        this.setEmail(email);
        this.setPhone(phone);
        this.setAddress(address);
        this.setGender(gender);
        this.setAvatar(avatar);
    }

    @Override
    public String toString() {
        return "Profile{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", gender='" + gender + '\'' +
                ", avatar='" + avatar + '\'' +
                '}';
    }
}
