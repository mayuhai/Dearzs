package com.dearzs.app.entity;

public class EntityPaymentsBalance {
    private long id;
    private long uid;
    private String detail;
    private long createTime;
    private double money;
    private int type;

    public long getUid() {
        return uid;
    }

    public String getDetail() {
        return detail;
    }

    public long getCreateTime() {
        return createTime;
    }

    public long getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public double getMoney() {
        return money;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public void setMoney(long money) {
        this.money = money;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    //去重用的，所有需要下拉刷新上拉加载更多的实体类都需要重写equals方法
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o.getClass() == EntityPaymentsBalance.class) {
            EntityPaymentsBalance n = (EntityPaymentsBalance) o;
            return n.id == id;
        }
        return false;
    }
}
