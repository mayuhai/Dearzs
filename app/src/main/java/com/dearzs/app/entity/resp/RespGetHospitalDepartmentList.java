package com.dearzs.app.entity.resp;

import com.dearzs.app.entity.EntityBase;
import com.dearzs.app.entity.EntityHospitalDepartmentInfo;
import com.dearzs.app.entity.EntityHospitalInfo;

import java.io.Serializable;
import java.util.List;

/**
 * 医院科室列表响应类
 */
public class RespGetHospitalDepartmentList extends EntityBase implements Serializable {
    private EntityHospitalDepartmentListResult result;

    public EntityHospitalDepartmentListResult getResult() {
        return result;
    }

    public void setResult(EntityHospitalDepartmentListResult result) {
        this.result = result;
    }

    public class EntityHospitalDepartmentListResult {
        private List<EntityHospitalDepartmentInfo> list;

        public List<EntityHospitalDepartmentInfo> getList() {
            return list;
        }

        public void setList(List<EntityHospitalDepartmentInfo> list) {
            this.list = list;
        }

    }
}
