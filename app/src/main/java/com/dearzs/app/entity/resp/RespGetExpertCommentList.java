package com.dearzs.app.entity.resp;

import com.dearzs.app.entity.EntityBase;
import com.dearzs.app.entity.EntityComment;
import com.dearzs.app.entity.EntityUser;

import java.io.Serializable;
import java.util.List;

/**
 * 专家评论列表响应类
 */
public class RespGetExpertCommentList extends EntityBase implements Serializable {
    private EntityExpertCommentListResult result;
    private int total;

    public EntityExpertCommentListResult getResult() {
        return result;
    }

    public void setResult(EntityExpertCommentListResult result) {
        this.result = result;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public class EntityExpertCommentListResult {
        private List<EntityComment> list;

        public List<EntityComment> getList() {
            return list;
        }

        public void setList(List<EntityComment> list) {
            this.list = list;
        }
    }
}
