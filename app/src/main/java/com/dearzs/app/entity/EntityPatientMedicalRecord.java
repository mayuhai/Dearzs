package com.dearzs.app.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by luyanlong on 2016/3/25.
 * 患者病历信息
 */
public class EntityPatientMedicalRecord implements Serializable {
    private List<EntityMedicalRecordImgs> aidImgs;
    private String aidLabel;
    private String aidText;
    private long createTime;
    private List<EntityMedicalRecordImgs> diagImgs;
    private String diagResult;
    private long expertId;
    private long id;
    private List<EntityMedicalRecordImgs> labImgs;
    private String labResult;
    private String past;
    private List<EntityMedicalRecordImgs> pastImgs;
    private long patientId;
    private String present;
    private String special;
    private List<EntityMedicalRecordImgs> specialImgs;
    private List<EntityMedicalRecordImgs> presentImgs;
//    private EntityPatientInfo patient;

//    public EntityPatientInfo getPatient() {
//        return patient;
//    }
//
//    public void setPatient(EntityPatientInfo patient) {
//        this.patient = patient;
//    }

    public void setAidLabel(String aidLabel) {
        this.aidLabel = aidLabel;
    }

    public void setAidText(String aidText) {
        this.aidText = aidText;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public void setDiagImgs(List<EntityMedicalRecordImgs> diagImgs) {
        this.diagImgs = diagImgs;
    }

    public void setDiagResult(String diagResult) {
        this.diagResult = diagResult;
    }

    public void setExpertId(long expertId) {
        this.expertId = expertId;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setLabImgs(List<EntityMedicalRecordImgs> labImgs) {
        this.labImgs = labImgs;
    }

    public void setLabResult(String labResult) {
        this.labResult = labResult;
    }

    public void setPast(String past) {
        this.past = past;
    }

    public void setPastImgs(List<EntityMedicalRecordImgs> pastImgs) {
        this.pastImgs = pastImgs;
    }

    public void setPatientId(long patientId) {
        this.patientId = patientId;
    }

    public void setPresent(String present) {
        this.present = present;
    }

    public void setPresentImgs(List<EntityMedicalRecordImgs> presentImgs) {
        this.presentImgs = presentImgs;
    }

    public void setSpecial(String special) {
        this.special = special;
    }

    public void setSpecialImgs(List<EntityMedicalRecordImgs> specialImgs) {
        this.specialImgs = specialImgs;
    }

    public List<EntityMedicalRecordImgs> getPresentImgs() {
        return presentImgs;
    }

    public void setAidImgs(List<EntityMedicalRecordImgs> aidImgs) {
        this.aidImgs = aidImgs;
    }

    public long getCreateTime() {
        return createTime;
    }

    public long getExpertId() {
        return expertId;
    }

    public long getId() {
        return id;
    }

    public long getPatientId() {
        return patientId;
    }

    public List<EntityMedicalRecordImgs> getAidImgs() {
        return aidImgs;
    }

    public String getAidText() {
        return aidText;
    }

    public String getDiagResult() {
        return diagResult;
    }

    public String getLabResult() {
        return labResult;
    }

    public String getPast() {
        return past;
    }

    public String getPresent() {
        return present;
    }

    public String getSpecial() {
        return special;
    }

    public String getAidLabel() {
        return aidLabel;
    }

    public List<EntityMedicalRecordImgs> getDiagImgs() {
        return diagImgs;
    }

    public List<EntityMedicalRecordImgs> getLabImgs() {
        return labImgs;
    }

    public List<EntityMedicalRecordImgs> getPastImgs() {
        return pastImgs;
    }

    public List<EntityMedicalRecordImgs> getSpecialImgs() {
        return specialImgs;
    }

}
