package com.dearzs.app.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 专家实体类
 */
public class EntityExpertInfo implements Serializable {
    private long id;
    private String address ;
    private int age ;
    private int comments ;
    private String avatar ;
    private double balance ;
    private String cardNo ;
    private long createTime ;
    private EntityHospitalDepartmentInfo department ;
    private String email ;
    private int gender ;
    private EntityHospitalInfo hospital ;
    private String intro ;
    private String job ;
    private long lastLoginTime ;
    private int medicalAge ;
    private String name ;
    private int orderNumber ;
    private String phone ;
    private double referralMoney ;
    private Integer referralState ;
    private double star ;
    private int type ;
    private List<EntityUserVisits> userVisits ;
    private double visitMoney ;
    private String label;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    private Integer visitState ;

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getAddress() {
        return address;
    }

    public EntityHospitalDepartmentInfo getDepartment() {
        return department;
    }

    public EntityHospitalInfo getHospital() {
        return hospital;
    }

    public long getId() {
        return id;
    }

    public int getAge() {
        return age;
    }

    public double getBalance() {
        return balance;
    }

    public int getGender() {
        return gender;
    }

    public int getMedicalAge() {
        return medicalAge;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public double getReferralMoney() {
        return referralMoney;
    }

    public Integer getReferralState() {
        return referralState;
    }

    public void setReferralState(Integer referralState) {
        this.referralState = referralState;
    }

    public Integer getVisitState() {
        return visitState;
    }

    public void setVisitState(Integer visitState) {
        this.visitState = visitState;
    }

    public double getStar() {
        return star;
    }

    public int getType() {
        return type;
    }

    public double getVisitMoney() {
        return visitMoney;
    }

    public List<EntityUserVisits> getUserVisits() {
        return userVisits;
    }

    public long getCreateTime() {
        return createTime;
    }

    public long getLastLoginTime() {
        return lastLoginTime;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getCardNo() {
        return cardNo;
    }

    public String getEmail() {
        return email;
    }

    public String getIntro() {
        return intro;
    }

    public String getJob() {
        return job;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public int getComments() {
        return comments;
    }

    //去重用的，所有需要下拉刷新上拉加载更多的实体类都需要重写equals方法
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o.getClass() == EntityExpertInfo.class) {
            EntityExpertInfo n = (EntityExpertInfo) o;
            return n.id == id;
        }
        return false;
    }
}
