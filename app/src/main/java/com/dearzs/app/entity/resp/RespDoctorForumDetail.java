package com.dearzs.app.entity.resp;

import com.dearzs.app.entity.EntityBase;
import com.dearzs.app.entity.EntityDoctorForumInfo;
import com.dearzs.app.entity.EntityDoctorForumLecture;

import java.io.Serializable;
import java.util.List;

/**
 * 名医讲堂详情响应类
 */
public class RespDoctorForumDetail extends EntityBase implements Serializable {
    private EntityDoctorFurumDetailResult result;

    public EntityDoctorFurumDetailResult getResult() {
        return result;
    }

    public void setResult(EntityDoctorFurumDetailResult result) {
        this.result = result;
    }

    public class EntityDoctorFurumDetailResult {
        private EntityDoctorForumLecture lecture;

        public EntityDoctorForumLecture getLecture() {
            return lecture;
        }

        public void setLecture(EntityDoctorForumLecture lecture) {
            this.lecture = lecture;
        }
    }
}
