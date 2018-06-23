package com.dearzs.app.entity.resp;
import com.dearzs.app.entity.EntityBase;
import com.dearzs.app.entity.EntityMedicalRecordHistoryInfo;
import com.dearzs.app.entity.EntityPatientMedicalRecord;

import java.util.List;

/**
 * 获取患者病历更新列表实体类
 *
 * @author 鲁延龙
 * @version 1.0
 */
public class RespGetMedicalRecordHistoryList extends EntityBase {
    private EntityMedicalRecordHistoryList result;

    public EntityMedicalRecordHistoryList getResult() {
        return result;
    }

    public void setResult(EntityMedicalRecordHistoryList result) {
        this.result = result;
    }

    public class EntityMedicalRecordHistoryList {
        private List<EntityMedicalRecordHistoryInfo> list;
        public List<EntityMedicalRecordHistoryInfo> getList() {
            return list;
        }

        public void setList(List<EntityMedicalRecordHistoryInfo> list) {
            this.list = list;
        }
    }
}
