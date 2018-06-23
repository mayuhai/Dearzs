package com.dearzs.app.entity;

import java.io.Serializable;

/**
 * 病历中的图片实体类
 * **/
public class EntityMedicalRecordImgs implements Serializable {
    private long historyId;
    private long id;
    private String img;

    public void setHistoryId(long historyId) {
        this.historyId = historyId;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public long getHistoryId() {
        return historyId;
    }

    public long getId() {
        return id;
    }

    public String getImg() {
        return img;
    }
}
