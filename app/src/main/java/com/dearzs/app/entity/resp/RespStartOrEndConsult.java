package com.dearzs.app.entity.resp;

import com.dearzs.app.entity.EntityBase;
import com.dearzs.app.entity.EntityConsultInfo;

import java.io.Serializable;
import java.util.List;

/**
 * 会诊室列表响应类
 */
public class RespStartOrEndConsult extends EntityBase implements Serializable {
    private EntityStartConsultResult result;

    public EntityStartConsultResult getResult() {
        return result;
    }

    public void setResult(EntityStartConsultResult result) {
        this.result = result;
    }

    public class EntityStartConsultResult {
        private EntityConsultInfo consult;

        public EntityConsultInfo getConsult() {
            return consult;
        }

        public void setConsult(EntityConsultInfo consult) {
            this.consult = consult;
        }
    }
}
