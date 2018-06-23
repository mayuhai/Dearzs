package com.dearzs.app.entity;

import java.io.Serializable;

/**
 *  名医讲堂推荐实体类
 */
public class EntityLectureRecomment implements Serializable {
    private long id;
    private String img;
    private long typeId;
    private String title;
    private long views;
    private EntityExpertInfo expert;

    public long getId() {
        return id;
    }

    public EntityExpertInfo getExpert() {
        return expert;
    }

    public long getTypeId() {
        return typeId;
    }

    public long getViews() {
        return views;
    }

    public String getImg() {
        return img;
    }

    public String getTitle() {
        return title;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setExpert(EntityExpertInfo expert) {
        this.expert = expert;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTypeId(long typeId) {
        this.typeId = typeId;
    }

    public void setViews(long views) {
        this.views = views;
    }
}
