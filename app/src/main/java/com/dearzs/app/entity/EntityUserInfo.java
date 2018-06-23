package com.dearzs.app.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户 包括登陆用户，患者，医生，专家
 */
public class EntityUserInfo extends EntityBase implements Serializable{
    private String username ;
    private String password ;
    private long id;
    private int age;
    private int medicalAge;
    private double balance;
    private String cardNo;
    private String address;
    private long createTime;
    private long lastLoginTime;
    private String avatar;
    private EntityHospitalDepartmentInfo department;
    private String email;
    private String intro;
    private String job;
    private String name;
    private int orderNumber;
    private String phone;
    private double referralMoney;
    private Integer referralState;
    private float star;
    private int type;
    private ArrayList<EntityUserVisits> userVisits;
    private double visitMoney;
    private Integer visitState;
    private EntityHospitalInfo hospital;
    private int gender;
    private int comments;
    private int week;
    private String time;
    private String area;
    private String sig;
    private int state;
    private String label;
    private String djob;
    private String bankAccount;
    private String bankAccountName;
    private String bankAccountNumber;
    private String bankAccountBranch;
    private String inviteCode;
    private String qr;

    // 医生认证状态，
    // 0待审核，可以提交
    // 1审核通过，不可再次提交
    // 2审核失败，可以再次提交
    // 3审核中，不可提交
    public static Integer WAIT_VERIFY = 0;
    public static Integer VERIFY_SUCC = 1;
    public static Integer VERIFY_FAIL = 2;
    public static Integer VERIFY_ING = 3;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    //类型，1用户2医生
    public static Integer NORMALUSER = 1;
    public static Integer DOCTORUSER = 2;

    public String getDjob() {
        return djob;
    }

    public void setDjob(String djob) {
        this.djob = djob;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public int getGender() {
        return gender;
    }

    public EntityHospitalInfo getHospital() {
        return hospital;
    }

    public void setHospital(EntityHospitalInfo hospital) {
        this.hospital = hospital;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getReferralMoney() {
        return referralMoney;
    }

    public void setReferralMoney(double referralMoney) {
        this.referralMoney = referralMoney;
    }

    public double getVisitMoney() {
        return visitMoney;
    }

    public void setVisitMoney(double visitMoney) {
        this.visitMoney = visitMoney;
    }

    public EntityHospitalDepartmentInfo getDepartment() {
        return department;
    }

    public void setDepartment(EntityHospitalDepartmentInfo department) {
        this.department = department;
    }

    public ArrayList<EntityUserVisits> getUserVisits() {
        return userVisits;
    }

    public void setUserVisits(ArrayList<EntityUserVisits> userVisits) {
        this.userVisits = userVisits;
    }

    public float getStar() {
        return star;
    }

    public void setStar(float star) {
        this.star = star;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getMedicalAge() {
        return medicalAge;
    }

    public void setMedicalAge(int medicalAge) {
        this.medicalAge = medicalAge;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
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

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }



    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getSig() {
        return sig;
    }

    public void setSig(String sig) {
        this.sig = sig;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getBankAccountBranch() {
        return bankAccountBranch;
    }

    public void setBankAccountBranch(String bankAccountBranch) {
        this.bankAccountBranch = bankAccountBranch;
    }

    public String getBankAccountName() {
        return bankAccountName;
    }

    public void setBankAccountName(String bankAccountName) {
        this.bankAccountName = bankAccountName;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

    public String getQr() {
        return qr;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    @Override
    public String toString() {
        return "EntityUser{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
