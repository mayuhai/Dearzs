package com.dearzs.app.entity;

import java.util.List;

/**
 * 名医讲堂实体类
 */
public class EntityDoctorForumLecture extends EntityBase {
    private EntityExpertInfo expert;
    private EntityOrg org;
    private long id;
    private String img;
    private String title;
    private long typeId;
    private long views;
    private int isPraise;
    private long praises;
    private List<EntityVideo> videos;
    private List<EntityDoctorForumInfo> recommend;

    public EntityOrg getOrg() {
        return org;
    }

    public void setOrg(EntityOrg org) {
        this.org = org;
    }

    public long getPraise() {
        return praises;
    }

    public void setPraise(long praise) {
        this.praises = praise;
    }

    public int getIsPraise() {
        return isPraise;
    }

    public void setIsPraise(int isPraise) {
        this.isPraise = isPraise;
    }

    public List<EntityDoctorForumInfo> getRecommend() {
        return recommend;
    }

    public void setRecommend(List<EntityDoctorForumInfo> recommend) {
        this.recommend = recommend;
    }

    public List<EntityVideo> getVideos() {
        return videos;
    }

    public void setVideos(List<EntityVideo> videos) {
        this.videos = videos;
    }

    public long getTypeId() {
        return typeId;
    }

    public EntityExpertInfo getExpert() {
        return expert;
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

    public void setTypeId(long typeId) {
        this.typeId = typeId;
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

    public void setViews(long views) {
        this.views = views;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

}
