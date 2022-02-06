package com.example.travelapp.model;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Users implements Serializable {
    String uid,image,email, name, dob, gender, phone, address, password;

    @Override
    public String toString() {
        return "Users{" +
                "uid='" + uid + '\'' +
                ", image='" + image + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", dob='" + dob + '\'' +
                ", gender='" + gender + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public Users() {
    }

    public Users(String uid, String image, String email, String name, String dob, String gender, String phone, String address, String password) {
        this.uid = uid;
        this.image = image;
        this.email = email;
        this.name = name;
        this.dob = dob;
        this.gender = gender;
        this.phone = phone;
        this.address = address;
        this.password = password;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
