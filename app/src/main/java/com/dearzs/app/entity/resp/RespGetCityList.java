package com.dearzs.app.entity.resp;

import com.dearzs.app.entity.EntityBase;
import com.dearzs.app.entity.EntityCityInfo;
import com.dearzs.app.entity.EntityHospitalInfo;

import java.io.Serializable;
import java.util.List;

/**
 * 城市列表响应类
 */
public class RespGetCityList extends EntityBase implements Serializable {
    private EntityCityListResult result;

    public EntityCityListResult getResult() {
        return result;
    }

    public void setResult(EntityCityListResult result) {
        this.result = result;
    }

    public class EntityCityListResult {
        private List<EntityCityInfo> list;

        public List<EntityCityInfo> getList() {
            return list;
        }

        public void setOrderInfoList(List<EntityCityInfo> list) {
            this.list = list;
        }

    }
}
