package com.dearzs.app.entity;

import java.io.Serializable;

public class EntityPatientInfo extends EntityBase implements Serializable{

    private String name;
    private String avatar;
    private String cardNo;
    private String phone;
    private String address;
    private int age;
    private String sortLetters;
    private int gender;
    private long id;
    private int isNew;

    public int getIsNew() {
        return isNew;
    }

    public void setIsNew(int isNew) {
        this.isNew = isNew;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCardNo() {
        return cardNo;
    }

    public int getAge() {
        return age;
    }

    public String getAddress() {
        return address;
    }

    public int getGender() {
        return gender;
    }

    public String getPhone() {
        return phone;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }

    public String getSortLetters() {
        return sortLetters;
    }

    //去重用的，所有需要下拉刷新上拉加载更多的实体类都需要重写equals方法
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o.getClass() == EntityExpertInfo.class) {
            EntityPatientInfo n = (EntityPatientInfo) o;
            return n.getId() == this.id;
        }
        return false;
    }
}
