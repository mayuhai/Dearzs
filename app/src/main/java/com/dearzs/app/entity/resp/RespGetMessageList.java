package com.dearzs.app.entity.resp;
import com.dearzs.app.entity.EntityBase;
import com.dearzs.app.entity.EntityMessage;

import java.io.Serializable;
import java.util.List;

/**
 * 获取消息列表实体类
 *
 * @author 鲁延龙
 * @version 1.0
 */
public class RespGetMessageList extends EntityBase {
    private EntityMessageListResult result;

    public class EntityMessageListResult implements Serializable {
        private List<EntityMessage> list;
        public List<EntityMessage> getList() {
            return list;
        }
    }

    public EntityMessageListResult getResult() {
        return result;
    }

    public void setResult(EntityMessageListResult result) {
        this.result = result;
    }
}
