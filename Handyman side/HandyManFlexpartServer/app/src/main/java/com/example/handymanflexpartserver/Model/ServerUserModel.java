package com.example.handymanflexpartserver.Model;

public class ServerUserModel {
    private String uid,name,phone,location,key;


    public ServerUserModel()
    {

    }
    public ServerUserModel(String uid,String name,String phone){
        this.uid=uid;
        this.name=name;
        this.phone=phone;

    }

    public String getKey() {
        return key;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

}
