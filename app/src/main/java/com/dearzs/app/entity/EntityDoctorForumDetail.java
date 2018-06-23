package com.dearzs.app.entity;

/**
 * 名医讲堂详情实体类
 */
public class EntityDoctorForumDetail extends EntityBase {
    private EntityExpertInfo expert;
    private long id;
    private String img;
    private String title;
    private long typeId;
    private long views;

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
