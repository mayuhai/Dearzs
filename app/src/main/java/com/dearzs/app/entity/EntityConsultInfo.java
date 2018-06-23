package com.dearzs.app.entity;

import java.io.Serializable;

/**
 * 会诊室实体类
 * @author 鲁延龙
 * @version 1.0
 */
public class EntityConsultInfo implements Serializable {
    private long id;
    private int state;
    private String caseAnalysis;
    private long createTime;
    private String diseaseDiagnosis;
    private String treatmentPrograms;
    private String result;
    private EntityUserInfo creator;
    private EntityExpertInfo expert;
    private EntityPatientInfo patient;
    private EntityExpertInfo doctor;
    private String groupId;
    private long ctime;

    public EntityExpertInfo getDoctor() {
        return doctor;
    }

    public void setDoctor(EntityExpertInfo doctor) {
        this.doctor = doctor;
    }

    public EntityPatientInfo getPatient() {
        return patient;
    }

    public EntityExpertInfo getExpert() {
        return expert;
    }

    public String getDiseaseDiagnosis() {
        return diseaseDiagnosis;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setCaseAnalysis(String caseAnalysis) {
        this.caseAnalysis = caseAnalysis;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public void setDiseaseDiagnosis(String diseaseDiagnosis) {
        this.diseaseDiagnosis = diseaseDiagnosis;
    }

    public String getTreatmentPrograms() {
        return treatmentPrograms;
    }

    public void setTreatmentPrograms(String treatmentPrograms) {
        this.treatmentPrograms = treatmentPrograms;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setCreator(EntityUserInfo creator) {
        this.creator = creator;
    }

    public void setExpert(EntityExpertInfo expert) {
        this.expert = expert;
    }

    public void setPatient(EntityPatientInfo patient) {
        this.patient = patient;
    }

    public EntityUserInfo getCreator() {
        return creator;

    }

    public int getState() {
        return state;
    }

    public long getId() {
        return id;
    }

    public String getCaseAnalysis() {
        return caseAnalysis;
    }

    public long getCreateTime() {
        return createTime;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public long getCtime() {
        return ctime;
    }

    public void setCtime(long ctime) {
        this.ctime = ctime;
    }

    //去重用的，所有需要下拉刷新上拉加载更多的实体类都需要重写equals方法
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o.getClass() == EntityConsultInfo.class) {
            EntityConsultInfo n = (EntityConsultInfo) o;
            return n.id == id;
        }
        return false;
    }
}
