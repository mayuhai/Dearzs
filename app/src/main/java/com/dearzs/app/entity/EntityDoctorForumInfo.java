package com.dearzs.app.entity;

public class EntityDoctorForumInfo extends EntityBase {
    private EntityExpertInfo expert;
    private EntityOrg org;
    private long id;
    private String img;
    private String title;
    private long typeId;
    private long views;

    public EntityOrg getOrg() {
        return org;
    }

    public void setOrg(EntityOrg org) {
        this.org = org;
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
