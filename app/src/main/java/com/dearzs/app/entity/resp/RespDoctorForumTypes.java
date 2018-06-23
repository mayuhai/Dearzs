package com.dearzs.app.entity.resp;

import com.dearzs.app.entity.EntityBase;
import com.dearzs.app.entity.EntityDoctorForumTypes;

import java.io.Serializable;
import java.util.List;

/**
 * 名医讲堂类别响应类
 */
public class RespDoctorForumTypes extends EntityBase implements Serializable {
    private EntityDoctorFurumTyoesResult result;

    public EntityDoctorFurumTyoesResult getResult() {
        return result;
    }

    public void setResult(EntityDoctorFurumTyoesResult result) {
        this.result = result;
    }

    public class EntityDoctorFurumTyoesResult {
        private List<EntityDoctorForumTypes> list;

        public List<EntityDoctorForumTypes> getList() {
            return list;
        }

        public void setList(List<EntityDoctorForumTypes> list) {
            this.list = list;
        }
    }
}
