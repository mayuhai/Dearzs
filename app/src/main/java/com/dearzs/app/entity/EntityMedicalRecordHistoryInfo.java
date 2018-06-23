package com.dearzs.app.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 专家实体类
 */
public class EntityMedicalRecordHistoryInfo implements Serializable {
    private long id;
    private long createTime ;
    private EntityMedicalRecordHistoryExpertInfo expert;

    public EntityMedicalRecordHistoryExpertInfo getExpert() {
        return expert;
    }

    public long getId() {
        return id;
    }

    public long getCreateTime() {
        return createTime;
    }

    public class EntityMedicalRecordHistoryExpertInfo implements Serializable {
        private String avatar;
        private String name;
        private String job;
        private int referralState;

        public int getReferralState() {
            return referralState;
        }

        public String getJob() {
            return job;
        }

        public void setJob(String job) {
            this.job = job;
        }

        public void setReferralState(int referralState) {
            this.referralState = referralState;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getAvatar() {
            return avatar;
        }
    }
}
