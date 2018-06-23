package com.dearzs.app.entity;

/**
 * 软件更新实体类
 *
 * @author mayuhai
 * @version 1.0
 */
public class EntityAppUpdateInfo {

//    "version": {
//                "appName": "珊瑚灵御冰盾",
//                "appVersion": "1.0.1",
//                "createTime": "2015-06-17 16:34:12",
//                "filePath": "https://dev.emm.coralsec.com:443/upload/2015/06/17/ZxiTJJHH/coralsec.apk",
//                "id": 26,
//                "packageName": "com.coralsec.emm",
//                "state": 1,
//                "versionCode": 1
//    }

    private String appName;
    private String appVersion;
    private String createTime;
    private String filePath;
    private int id;
    private String packageName;
    private int state;
    private int versionCode;
    private String info;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
