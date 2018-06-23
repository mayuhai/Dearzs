package com.dearzs.app.entity.resp;

import com.dearzs.app.entity.EntityBase;
import com.dearzs.app.entity.EntityComment;
import com.dearzs.app.entity.EntityDynamicComment;

import java.io.Serializable;
import java.util.List;

/**
 * 社区动态评论列表响应类
 */
public class RespGetDynamicCommentList extends EntityBase implements Serializable {
    private EntityDynamicCommentListResult result;

    public EntityDynamicCommentListResult getResult() {
        return result;
    }

    public void setResult(EntityDynamicCommentListResult result) {
        this.result = result;
    }

    public class EntityDynamicCommentListResult {
        private List<EntityDynamicComment> list;
        private int total;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public List<EntityDynamicComment> getList() {
            return list;
        }

        public void setList(List<EntityDynamicComment> list) {
            this.list = list;
        }
    }
}
