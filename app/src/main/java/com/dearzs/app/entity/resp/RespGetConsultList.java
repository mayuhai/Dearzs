package com.dearzs.app.entity.resp;

import com.dearzs.app.entity.EntityBase;
import com.dearzs.app.entity.EntityComment;
import com.dearzs.app.entity.EntityConsultInfo;

import java.io.Serializable;
import java.util.List;

/**
 * 会诊室列表响应类
 */
public class RespGetConsultList extends EntityBase implements Serializable {
    private EntityConsultListResult result;

    public EntityConsultListResult getResult() {
        return result;
    }

    public void setResult(EntityConsultListResult result) {
        this.result = result;
    }

    public class EntityConsultListResult {
        private List<EntityConsultInfo> list;

        public List<EntityConsultInfo> getList() {
            return list;
        }

        public void setList(List<EntityConsultInfo> list) {
            this.list = list;
        }
    }
}
