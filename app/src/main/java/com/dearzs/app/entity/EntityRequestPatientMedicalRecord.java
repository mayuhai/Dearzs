package com.dearzs.app.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by luyanlong on 2016/3/25.
 * 创建患者病历，病历实体类信息
 */
public class EntityRequestPatientMedicalRecord implements Serializable {
    private List<String> aidImgs;
    private String aidLabel;
    private String aidText;
    private List<String> diagImgs;
    private String diagResult;
    private List<String> labImgs;
    private String labResult;
    private String past;
    private List<String> pastImgs;
    private String present;
    private String special;
    private List<String> specialImgs;
    private List<String> presentImgs;

    public void setAidLabel(String aidLabel) {
        this.aidLabel = aidLabel;
    }

    public void setAidText(String aidText) {
        this.aidText = aidText;
    }


    public void setDiagImgs(List<String> diagImgs) {
        this.diagImgs = diagImgs;
    }

    public void setDiagResult(String diagResult) {
        this.diagResult = diagResult;
    }


    public void setLabImgs(List<String> labImgs) {
        this.labImgs = labImgs;
    }

    public void setLabResult(String labResult) {
        this.labResult = labResult;
    }

    public void setPast(String past) {
        this.past = past;
    }

    public void setPastImgs(List<String> pastImgs) {
        this.pastImgs = pastImgs;
    }

    public void setPresent(String present) {
        this.present = present;
    }

    public void setPresentImgs(List<String> presentImgs) {
        this.presentImgs = presentImgs;
    }

    public void setSpecial(String special) {
        this.special = special;
    }

    public void setSpecialImgs(List<String> specialImgs) {
        this.specialImgs = specialImgs;
    }

    public List<String> getPresentImgs() {
        return presentImgs;
    }

    public void setAidImgs(List<String> aidImgs) {
        this.aidImgs = aidImgs;
    }

    public List<String> getAidImgs() {
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

    public List<String> getDiagImgs() {
        return diagImgs;
    }

    public List<String> getLabImgs() {
        return labImgs;
    }

    public List<String> getPastImgs() {
        return pastImgs;
    }

    public List<String> getSpecialImgs() {
        return specialImgs;
    }

}
