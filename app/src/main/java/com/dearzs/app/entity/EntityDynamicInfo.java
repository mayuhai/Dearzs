package com.dearzs.app.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 社区 -- 动态实体类
 */
public class EntityDynamicInfo implements Serializable{
    private String content;
    private long createTime;
    private long id;
    private long comments;
    private List<EntityDynamicImage> images;
    private int isPraise;
    private long praise;
    private long uid;
    private EntityDynamicUser user;

    public void setIsPraise(int isPraise) {
        this.isPraise = isPraise;
    }

    public void setPraise(long praise) {
        this.praise = praise;
    }

    public String getContent() {
        return content;
    }

    public long getCreateTime() {
        return createTime;
    }

    public long getId() {
        return id;
    }

    public List<EntityDynamicImage> getImages() {
        return images;
    }

    public int getIsPraise() {
        return isPraise;
    }

    public long getPraise() {
        return praise;
    }

    public long getUid() {
        return uid;
    }

    public long getComments() {
        return comments;
    }

    public void setComments(long comments) {
        this.comments = comments;
    }

    public EntityDynamicUser getUser() {
        return user;
    }

    public class EntityDynamicUser implements Serializable{
        private String avatar;
        private String name;
        private String job;
        private long id;

        public String getJob() {
            return job;
        }

        public void setJob(String job) {
            this.job = job;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getAvatar() {
            return avatar;
        }
    }

    //去重用的，所有需要下拉刷新上拉加载更多的实体类都需要重写equals方法
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o.getClass() == EntityDynamicInfo.class) {
            EntityDynamicInfo n = (EntityDynamicInfo) o;
            return n.id == id;
        }
        return false;
    }
}
