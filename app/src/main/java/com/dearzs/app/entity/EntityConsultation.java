package com.dearzs.app.entity;

import java.io.Serializable;

public class EntityConsultation implements Serializable {
    private int id;
    private String title;
    private long createTime;
    private String img;
    private long praise;
    private int type;
    private String typeName;
    private int isFav;
    private int isPraise;
    private String shareUrl;

    public int getIsPraise() {
        return isPraise;
    }

    public int getIsFav() {
        return isFav;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setIsFav(int isFav) {
        this.isFav = isFav;
    }

    public void setIsPraise(int isPraise) {
        this.isPraise = isPraise;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public void setPraise(long praise) {
        this.praise = praise;
    }

    public long getCreateTime() {
        return createTime;
    }

    public int getId() {
        return id;
    }

    public String getImg() {
        return img;
    }

    public String getTitle() {
        return title;
    }

    public int getType() {
        return type;
    }

    public long getPraise() {
        return praise;
    }

    public String getTypeName() {
        return typeName;
    }


    //去重用的，所有需要下拉刷新上拉加载更多的实体类都需要重写equals方法
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o.getClass() == EntityConsultation.class) {
            EntityConsultation n = (EntityConsultation) o;
            return n.id == id;
        }
        return false;
    }
}
