package com.dearzs.app.entity.resp;

import com.dearzs.app.entity.EntityBase;
import com.dearzs.app.entity.EntityPatientInfo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by luyanlong on 2016/6/19.
 * 患者详情接口
 */
public class RespPatientDetails extends EntityBase {
    private EntityPatientDetails result;
    public EntityPatientDetails getResult() {
        return result;
    }
    public void setResult(EntityPatientDetails resultList) {
        this.result = resultList;
    }

    public static class EntityPatientDetails implements Serializable {
        private EntityPatientInfo user;

        public void setList(EntityPatientInfo user) {
            this.user = user;
        }

        public EntityPatientInfo getUser() {
            return user;
        }
    }
}
