package com.dearzs.app.entity.resp;

import com.dearzs.app.entity.EntityBase;
import com.dearzs.app.entity.EntityExpertInfo;
import com.dearzs.app.entity.EntityOrderInfo;

import java.io.Serializable;
import java.util.List;

/**
 * 提交订单响应类
 */
public class RespOrderCommit extends EntityBase implements Serializable {

    private EntityOrderCommitResult result;


    public EntityOrderCommitResult getResult() {
        return result;
    }

    public void setResult(EntityOrderCommitResult result) {
        this.result = result;
    }

    public class EntityOrderCommitResult {
        private EntityOrderInfo order;

        //微信相关:
        private String appid;
        private String noncestr;
        private String partnerid;
        private String prepayid; //微信预支付交易会话ID，支付类型为2（微信支付）返回此字段
        private String sign;
        private String timestamp;

        public EntityOrderInfo getOrder() {
            return order;
        }

        public void setOrder(EntityOrderInfo order) {
            this.order = order;
        }

        public String getAppid() {
            return appid;
        }

        public void setAppid(String appid) {
            this.appid = appid;
        }

        public String getNoncestr() {
            return noncestr;
        }

        public void setNoncestr(String noncestr) {
            this.noncestr = noncestr;
        }

        public String getPartnerid() {
            return partnerid;
        }

        public void setPartnerid(String partnerid) {
            this.partnerid = partnerid;
        }

        public String getPrepayid() {
            return prepayid;
        }

        public void setPrepayid(String prepayid) {
            this.prepayid = prepayid;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }
    }
}
