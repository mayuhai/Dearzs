package com.dearzs.app.entity.resp;

import com.dearzs.app.entity.EntityBase;
import com.dearzs.app.entity.EntityUser;
import com.dearzs.app.entity.EntityUserInfo;

/**
 * 用户登陆返回结果
 * @author mayuhai
 * @version 1.0
 */
public class RespUserLogin extends EntityBase {

    private  EntityUserLogin result;

    public EntityUserLogin getResult() {
        return result;
    }

    public void setResult(EntityUserLogin result) {
        this.result = result;
    }

    public class EntityUserLogin {
        private String tokenId;
        private String hotline;
        private EntityUserInfo user;

        public String getHotline() {
            return hotline;
        }

        public String getTokenId() {
            return tokenId;
        }

        public void setTokenId(String tokenId) {
            this.tokenId = tokenId;
        }

        public EntityUserInfo getUser() {
            return user;
        }

        public void setUser(EntityUserInfo user) {
            this.user = user;
        }
    }
}
