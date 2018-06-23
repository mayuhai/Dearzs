package com.dearzs.app.entity;

import java.io.Serializable;

/**
 *  名医讲堂评论实体类
 */
public class EntityLectureComment implements Serializable {
    private String comment;
    private long createTime;
    private long id;
    private long lectureId;
    private long uid;
    private EntityUser user;

    public EntityUser getUser() {
        return user;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public long getCreateTime() {
        return createTime;
    }

    public long getId() {
        return id;
    }

    public long getLectureId() {
        return lectureId;
    }

    public long getUid() {
        return uid;
    }

    public String getComment() {
        return comment;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setLectureId(long lectureId) {
        this.lectureId = lectureId;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public void setUser(EntityUser user) {
        this.user = user;
    }
}
