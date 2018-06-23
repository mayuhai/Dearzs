package com.dearzs.app.entity.resp;

import com.dearzs.app.entity.EntityBase;
import com.dearzs.app.entity.EntityConsultInfo;
import com.dearzs.app.entity.EntityDiseaseDpmtInfo;

import java.io.Serializable;
import java.util.List;

/**
 * 疾病类别列表响应类
 */
public class RespGetDiseaseCategory extends EntityBase implements Serializable {
    private EntityDiseaseCateGoryResult result;

    public EntityDiseaseCateGoryResult getResult() {
        return result;
    }

    public void setResult(EntityDiseaseCateGoryResult result) {
        this.result = result;
    }

    public class EntityDiseaseCateGoryResult {
        private List<EntityDiseaseDpmtInfo> list;

        public List<EntityDiseaseDpmtInfo> getList() {
            return list;
        }

        public void setList(List<EntityDiseaseDpmtInfo> list) {
            this.list = list;
        }
    }
}
