package com.dearzs.app.entity;

import java.io.Serializable;

/**
 * Created by luyanlong on 2016/3/25.
 */
public class EntityHospitalInfo implements Serializable {
    private long id ;
    private String name;
    private long seq;

    public long getSeq() {
        return seq;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
