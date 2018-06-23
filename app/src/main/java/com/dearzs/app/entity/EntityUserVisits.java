package com.dearzs.app.entity;

import java.io.Serializable;

/**
 * 出诊时间实体类
 */
public class EntityUserVisits extends EntityBase implements Serializable {
    private long id;
    private String time;
    private long uid;
    private int week;

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public long getUid() {
        return uid;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public int getWeek() {
        return week;
    }
}
