package com.dearzs.app.entity.resp;
import com.dearzs.app.entity.EntityBase;
import com.dearzs.app.entity.EntityDynamicInfo;
import com.dearzs.app.entity.EntityExpertInfo;

import java.util.List;

/**
 * 获取社区动态列表实体类
 *
 * @author 鲁延龙
 * @version 1.0
 */
public class RespGetDynamicList extends EntityBase {
    private EntityDynamicListResult result;

    public EntityDynamicListResult getResult() {
        return result;
    }

    public void setResult(EntityDynamicListResult result) {
        this.result = result;
    }

    public class EntityDynamicListResult {
        private List<EntityDynamicInfo> list;

        public List<EntityDynamicInfo> getList() {
            return list;
        }

        public void setList(List<EntityDynamicInfo> list) {
            this.list = list;
        }
    }
}
