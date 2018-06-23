package com.dearzs.app.entity.resp;
import com.dearzs.app.entity.EntityBanner;
import com.dearzs.app.entity.EntityBase;

import java.util.List;

/**
 * 获取Banner列表实体类
 *
 * @author 鲁延龙
 * @version 1.0
 */
public class RespGetBannerList extends EntityBase {
    private EntityBannerListResult result;

    public class EntityBannerListResult {
        private List<EntityBanner> list;
        public List<EntityBanner> getList() {
            return list;
        }
    }

    public EntityBannerListResult getResult() {
        return result;
    }

    public void setResult(EntityBannerListResult result) {
        this.result = result;
    }
}
