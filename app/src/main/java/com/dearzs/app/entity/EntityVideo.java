package com.dearzs.app.entity;

import java.io.Serializable;

/**
 *  视频实体类
 */
public class EntityVideo implements Serializable {
    private long id;
    private long lectureId;
    private long seq;
    private long times;
    private String url;

    public long getLectureId() {
        return lectureId;
    }

    public long getId() {
        return id;
    }

    public long getSeq() {
        return seq;
    }

    public long getTimes() {
        return times;
    }

    public String getUrl() {
        return url;
    }

    public void setLectureId(long lectureId) {
        this.lectureId = lectureId;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setSeq(long seq) {
        this.seq = seq;
    }

    public void setTimes(long times) {
        this.times = times;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
