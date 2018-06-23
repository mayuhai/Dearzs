package com.dearzs.app.entity;

import java.io.Serializable;

/**
 * Created by luyanlong on 2016/6/5.
 */
public class EntityDynamicImage implements Serializable {
    private long dynamicId;
    private long id;
    private String img;
    private int seq;

    public int getSeq() {
        return seq;
    }

    public long getDynamicId() {
        return dynamicId;
    }

    public void setDynamicId(long dynamicId) {
        this.dynamicId = dynamicId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
