package com.dearzs.app.entity;

/**
 * Created by luyanlong on 2016/12/15.
 * 家庭医生套餐实体类
 */

public class EntityFamilyDoctorPackage {
    private String packageName;
    private boolean isChecked;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
