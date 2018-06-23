package com.dearzs.app.entity.resp;

import com.dearzs.app.entity.EntityAppUpdateInfo;
import com.dearzs.app.entity.EntityBase;

import java.util.List;

/**
 * 软件更新返回结果
 * @author mayuhai
 * @version 1.0
 */
public class RespAppUpdate extends EntityBase {

    private EntityAppUpdate result;

    public EntityAppUpdate getResult() {
        return result;
    }

    public void setResult(EntityAppUpdate result) {
        this.result = result;
    }

    public class EntityAppUpdate {
        private EntityAppUpdateInfo version;

        public EntityAppUpdateInfo getVersion() {
            return version;
        }

        public void setVersion(EntityAppUpdateInfo version) {
            this.version = version;
        }
    }
}
