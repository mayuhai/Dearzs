package com.dearzs.app.entity.resp;
import com.dearzs.app.entity.EntityBase;
import com.dearzs.app.entity.EntityConsultation;

import java.util.List;

/**
 * 获取收藏的咨询列表实体类
 *
 * @author 鲁延龙
 * @version 1.0
 */
public class RespGetCollectionConsultationList extends EntityBase {
    private EntityConsultationListResult result;

    public class EntityConsultationListResult {
        private List<EntityConsultation> list;
        public List<EntityConsultation> getList() {
            return list;
        }
    }

    public EntityConsultationListResult getResult() {
        return result;
    }

    public void setResult(EntityConsultationListResult result) {
        this.result = result;
    }
}
