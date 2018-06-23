package com.dearzs.app.entity.resp;
import com.dearzs.app.entity.EntityBase;
import com.dearzs.app.entity.EntityExpertInfo;
import com.dearzs.app.entity.EntityPatientInfo;
import com.dearzs.app.entity.EntityPatientMedicalRecord;

import java.util.List;

/**
 * 获取患者病历实体类
 *
 * @author 鲁延龙
 * @version 1.0
 */
public class RespGetPatientMedicalRecord extends EntityBase {
    private EntityMedicalRecordResult result;

    public EntityMedicalRecordResult getResult() {
        return result;
    }

    public void setResult(EntityMedicalRecordResult result) {
        this.result = result;
    }

    public class EntityMedicalRecordResult {
        private EntityPatientMedicalRecord history;
        private EntityPatientInfo user;

        public EntityPatientInfo getUser() {
            return user;
        }

        public void setUser(EntityPatientInfo user) {
            this.user = user;
        }

        public EntityPatientMedicalRecord getHistory() {
            return history;
        }

        public void setHistory(EntityPatientMedicalRecord history) {
            this.history = history;
        }
    }
}
