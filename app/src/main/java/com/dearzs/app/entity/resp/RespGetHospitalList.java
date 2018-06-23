package com.dearzs.app.entity.resp;

import com.dearzs.app.entity.EntityBase;
import com.dearzs.app.entity.EntityHospitalInfo;
import com.dearzs.app.entity.EntityOrderInfo;

import java.io.Serializable;
import java.util.List;

/**
 * 医生列表响应类
 */
public class RespGetHospitalList extends EntityBase implements Serializable {
    private EntityHospitalListResult result;

    public EntityHospitalListResult getResult() {
        return result;
    }

    public void setResult(EntityHospitalListResult result) {
        this.result = result;
    }

    public class EntityHospitalListResult {
        private List<EntityHospitalInfo> list;

        public List<EntityHospitalInfo> getList() {
            return list;
        }

        public void setOrderInfoList(List<EntityHospitalInfo> list) {
            this.list = list;
        }

    }
}
