package com.dearzs.app.entity.resp;


import com.dearzs.app.entity.EntityBase;
import com.dearzs.app.entity.EntityPatientInfo;

import java.io.Serializable;
import java.util.List;

public class RespPatientList extends EntityBase implements Serializable {
    private EntityMyCustomer result;
    public EntityMyCustomer getResult() {
        return result;
    }
    public void setResult(EntityMyCustomer resultList) {
        this.result = resultList;
    }

    public static class EntityMyCustomer implements Serializable {
        private List<EntityPatientInfo> list;

        public void setList(List<EntityPatientInfo> list) {
            this.list = list;
        }

        public List<EntityPatientInfo> getList() {
            return list;
        }
    }
}
