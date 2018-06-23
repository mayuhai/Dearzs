package com.dearzs.app.entity.resp;

import com.dearzs.app.entity.EntityBase;
import com.dearzs.app.entity.EntityExpertDetails;
import com.dearzs.app.entity.EntityExpertInfo;

import java.io.Serializable;
import java.util.List;

/**
 * 专家详情响应类
 */
public class RespGetExpertDetails extends EntityBase implements Serializable {
    private EntityExpertDetails result;

    public EntityExpertDetails getResult() {
        return result;
    }

    public void setResult(EntityExpertDetails result) {
        this.result = result;
    }
}
