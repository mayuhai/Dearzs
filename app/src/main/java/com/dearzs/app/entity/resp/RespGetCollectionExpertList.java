package com.dearzs.app.entity.resp;
import com.dearzs.app.entity.EntityBase;
import com.dearzs.app.entity.EntityExpertInfo;

import java.util.List;

/**
 * 获取收藏的专家列表实体类
 *
 * @author 鲁延龙
 * @version 1.0
 */
public class RespGetCollectionExpertList extends EntityBase {
    private EntityExpertListResult result;

    public EntityExpertListResult getResult() {
        return result;
    }

    public void setResult(EntityExpertListResult result) {
        this.result = result;
    }

    public class EntityExpertListResult {
        private List<EntityExpertInfo> list;

        public List<EntityExpertInfo> getList() {
            return list;
        }

        public void setList(List<EntityExpertInfo> list) {
            this.list = list;
        }
    }
}
