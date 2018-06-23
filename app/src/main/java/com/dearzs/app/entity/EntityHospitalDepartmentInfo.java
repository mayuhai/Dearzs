package com.dearzs.app.entity;

import java.io.Serializable;

/**
 * Created by luyanlong on 2016/3/25.
 */
public class EntityHospitalDepartmentInfo implements Serializable {
    private long hid;
    private long seq;
    private long id;
    private String name;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getHid() {
        return hid;
    }

    public void setHid(long hid) {
        this.hid = hid;
    }

    public long getSeq() {
        return seq;
    }

    public void setSeq(long seq) {
        this.seq = seq;
    }
}
