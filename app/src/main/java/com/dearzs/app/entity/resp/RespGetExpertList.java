package com.dearzs.app.entity.resp;

import com.dearzs.app.entity.EntityBase;
import com.dearzs.app.entity.EntityHospitalInfo;
import com.dearzs.app.entity.EntityExpertInfo;

import java.io.Serializable;
import java.util.List;

/**
 * 专家列表响应类
 */
public class RespGetExpertList extends EntityBase implements Serializable {
    private EntityExpertListResult result;

    public EntityExpertListResult getResult() {
        return result;
    }

    public void setResult(EntityExpertListResult result) {
        this.result = result;
    }

    public class EntityExpertListResult {
        private List<EntityExpertInfo> list;
        private long total;

        public long getTotal() {
            return total;
        }

        public void setTotal(long total) {
            this.total = total;
        }

        public List<EntityExpertInfo> getList() {
            return list;
        }

        public void setList(List<EntityExpertInfo> list) {
            this.list = list;
        }
    }
}
