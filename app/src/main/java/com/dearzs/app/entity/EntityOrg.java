package com.dearzs.app.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 名医讲坛发布视频的机构实体类
 */
public class EntityOrg implements Serializable {
    private String orgName;
    private String orgIndustry;
    private String orgCity;
    private String img;

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getOrgIndustry() {
        return orgIndustry;
    }

    public void setOrgIndustry(String orgIndustry) {
        this.orgIndustry = orgIndustry;
    }

    public String getOrgCity() {
        return orgCity;
    }

    public void setOrgCity(String orgCity) {
        this.orgCity = orgCity;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
