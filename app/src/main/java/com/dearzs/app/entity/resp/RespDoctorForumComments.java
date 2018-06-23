package com.dearzs.app.entity.resp;

import com.dearzs.app.entity.EntityBase;
import com.dearzs.app.entity.EntityLectureComment;

import java.io.Serializable;
import java.util.List;

/**
 * 名医讲堂评论列表响应类
 */
public class RespDoctorForumComments extends EntityBase implements Serializable {
    private EntityDoctorFurumCommentsListResult result;

    public EntityDoctorFurumCommentsListResult getResult() {
        return result;
    }

    public void setResult(EntityDoctorFurumCommentsListResult result) {
        this.result = result;
    }

    public class EntityDoctorFurumCommentsListResult {
        private List<EntityLectureComment> list;
        private long total;

        public long getTotal() {
            return total;
        }

        public void setTotal(long total) {
            this.total = total;
        }

        public List<EntityLectureComment> getList() {
            return list;
        }

        public void setList(List<EntityLectureComment> list) {
            this.list = list;
        }
    }
}
