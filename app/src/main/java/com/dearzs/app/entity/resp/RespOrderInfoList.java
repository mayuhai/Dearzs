package com.dearzs.app.entity.resp;

import com.dearzs.app.entity.EntityBase;
import com.dearzs.app.entity.EntityOrderInfo;

import java.io.Serializable;
import java.util.List;

/**
 * 订单列表响应类
 */
public class RespOrderInfoList extends EntityBase implements Serializable {
    private EntityOrderListResult result;

    public EntityOrderListResult getResult() {
        return result;
    }

    public void setResult(EntityOrderListResult result) {
        this.result = result;
    }

    public class EntityOrderListResult {
        private List<EntityOrderInfo> list;

        public List<EntityOrderInfo> getList() {
            return list;
        }

        public void setList(List<EntityOrderInfo> list) {
            this.list = list;
        }
    }
}
