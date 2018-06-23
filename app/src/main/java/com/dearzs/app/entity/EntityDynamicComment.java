package com.dearzs.app.entity;

/**
 * 动态评论
 */
public class EntityDynamicComment extends EntityBase {
    private String comment ;
    private long createTime ;
    private long dynamicId;
    private int id;
    private int uid ;
    private EntityUser user ;

    public long getDynamicId() {
        return dynamicId;
    }

    public void setDynamicId(long dynamicId) {
        this.dynamicId = dynamicId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public EntityUser getUser() {
        return user;
    }

    public void setUser(EntityUser user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "EntityComment{" +
                "comment='" + comment + '\'' +
                ", createTime='" + createTime + '\'' +
                '}';
    }
}
