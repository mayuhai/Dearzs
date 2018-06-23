package com.dearzs.app.entity.resp;
import com.dearzs.app.entity.EntityBase;
import com.dearzs.app.entity.EntityConsultation;
import com.dearzs.app.entity.EntityPaymentsBalance;

import java.util.List;

/**
 * 获取收支明细列表实体类
 *
 * @author 鲁延龙
 * @version 1.0
 */
public class RespGetPaymentsBalanceList extends EntityBase {
    private EntityPaymentsBalanceListResult result;

    public class EntityPaymentsBalanceListResult {
        private List<EntityPaymentsBalance> list;
        public List<EntityPaymentsBalance> getList() {
            return list;
        }
    }

    public EntityPaymentsBalanceListResult getResult() {
        return result;
    }

    public void setResult(EntityPaymentsBalanceListResult result) {
        this.result = result;
    }
}
