package com.dearzs.app.entity;

import java.util.List;

/**
 * 专家实体类
 */
public class EntityExpertDetails {
    private int isFav;
    private EntityExpertInfo user;

    public int getIsFav() {
        return isFav;
    }

    public void setIsFav(int isFav) {
        this.isFav = isFav;
    }

    public void setUser(EntityExpertInfo user) {
        this.user = user;
    }

    public EntityExpertInfo getUser() {
        return user;
    }
}
