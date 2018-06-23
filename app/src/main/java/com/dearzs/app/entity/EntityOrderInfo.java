package com.dearzs.app.entity;

import java.io.Serializable;

/**
 * Created by mayuhai on 2016/3/25.
 */
public class EntityOrderInfo implements Serializable {

    private String orderNo;  //订单号
    private String orderDate; //订单日期，格式yyyy-MM-dd
    private String orderTime; //订单时间，1上午2下午3夜间4全天
    private String orderPhone;  //订单手机号
    private String remark; //订单备注
    private Double totalFee; //订单金额
    private EntityUserInfo creator;  //订单创建者信息，一般为县级医生
    private EntityUserInfo expert; //专家信息
    private EntityUserInfo patient; //患者信息
    private EntityUserInfo doctor; //县级医生信息
    private Integer payType;  //支付类型，1支付宝2微信
    private Integer payState; //支付状态，0待支付，1已支付
    private String createTime; //订单创建日期，时间戳
    private EntityOrderState orderState;  //订单状态信息
    private Integer type; //订单类型，1会诊2转诊
    private Integer orderTimeHour; //订单时间 小时
    private Integer orderTimeMinute; //订单时间 分钟
    private String expertRemark; //专家接单时的备注
    private EntityComment comment;
    private long consultId;

    public static final int AM = 1;
    public static final int PM = 2;
    public static final int NIGHT = 3;
    public static final int ALLDAY = 4;

    public EntityUserInfo getDoctor() {
        return doctor;
    }

    public void setDoctor(EntityUserInfo doctor) {
        this.doctor = doctor;
    }

    public long getConsultId() {
        return consultId;
    }

    public void setConsultId(long consultId) {
        this.consultId = consultId;
    }

    public EntityComment getComment() {
        return comment;
    }

    public void setComment(EntityComment comment) {
        this.comment = comment;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getOrderPhone() {
        return orderPhone;
    }

    public void setOrderPhone(String orderPhone) {
        this.orderPhone = orderPhone;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Double getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(Double totalFee) {
        this.totalFee = totalFee;
    }

    public EntityUserInfo getCreator() {
        return creator;
    }

    public void setCreator(EntityUserInfo creator) {
        this.creator = creator;
    }

    public EntityUserInfo getExpert() {
        return expert;
    }

    public void setExpert(EntityUserInfo expert) {
        this.expert = expert;
    }

    public EntityUserInfo getPatient() {
        return patient;
    }

    public void setPatient(EntityUserInfo patient) {
        this.patient = patient;
    }

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    public Integer getPayState() {
        return payState;
    }

    public void setPayState(Integer payState) {
        this.payState = payState;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public EntityOrderState getOrderState() {
        return orderState;
    }

    public void setOrderState(EntityOrderState orderState) {
        this.orderState = orderState;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getOrderTimeHour() {
        return orderTimeHour;
    }

    public void setOrderTimeHour(Integer orderTimeHour) {
        this.orderTimeHour = orderTimeHour;
    }

    public Integer getOrderTimeMinute() {
        return orderTimeMinute;
    }

    public void setOrderTimeMinute(Integer orderTimeMinute) {
        this.orderTimeMinute = orderTimeMinute;
    }

    public String getExpertRemark() {
        return expertRemark;
    }

    public void setExpertRemark(String expertRemark) {
        this.expertRemark = expertRemark;
    }

    //去重用的，所有需要下拉刷新上拉加载更多的实体类都需要重写equals方法
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o.getClass() == EntityExpertInfo.class) {
            EntityOrderInfo n = (EntityOrderInfo) o;
            return n.orderNo == orderNo;
        }
        return false;
    }
}
