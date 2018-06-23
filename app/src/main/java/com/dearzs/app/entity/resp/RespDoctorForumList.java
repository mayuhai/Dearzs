package com.dearzs.app.entity.resp;

import com.dearzs.app.entity.EntityBase;
import com.dearzs.app.entity.EntityDoctorForumInfo;

import java.io.Serializable;
import java.util.List;

/**
 * 名医讲堂列表响应类
 */
public class RespDoctorForumList extends EntityBase implements Serializable {
    private EntityDoctorFurumListResult result;

    public EntityDoctorFurumListResult getResult() {
        return result;
    }

    public void setResult(EntityDoctorFurumListResult result) {
        this.result = result;
    }

    public class EntityDoctorFurumListResult {
        private List<EntityDoctorForumInfo> list;

        public List<EntityDoctorForumInfo> getList() {
            return list;
        }

        public void setList(List<EntityDoctorForumInfo> list) {
            this.list = list;
        }
    }
}
