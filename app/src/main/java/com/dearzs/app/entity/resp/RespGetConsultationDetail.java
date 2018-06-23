package com.dearzs.app.entity.resp;
import com.dearzs.app.entity.EntityBase;
import com.dearzs.app.entity.EntityConsultation;

import java.io.Serializable;
import java.util.List;

/**
 * 获取咨询详情实体类
 *
 * @author 鲁延龙
 * @version 1.0
 */
public class RespGetConsultationDetail extends EntityBase {
    private EntityConsultationResult result;

    public class EntityConsultationResult implements Serializable {
        private EntityConsultation news;
        public EntityConsultation getNews() {
            return news;
        }
    }

    public EntityConsultationResult getResult() {
        return result;
    }

    public void setResult(EntityConsultationResult result) {
        this.result = result;
    }
}
