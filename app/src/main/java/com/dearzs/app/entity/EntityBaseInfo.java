package com.dearzs.app.entity;

import java.util.List;

/**
 * Info实体类
 */
public class EntityBaseInfo {
    private long id ;
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
}
