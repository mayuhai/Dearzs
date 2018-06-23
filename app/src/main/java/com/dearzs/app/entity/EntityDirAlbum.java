package com.dearzs.app.entity;

import java.util.ArrayList;

/**
 * 实体类
 *
 * @author 鲁延龙
 * @version 1.0
 */
public class EntityDirAlbum {
    private String dirName;
    private String topImgPath;
    private ArrayList<String> childList;

    public String getDirName() {
        return dirName;
    }

    public void setDirName(String dirName) {
        this.dirName = dirName;
    }

    public ArrayList<String> getChildList() {
        return childList;
    }

    public void setChildList(ArrayList<String> childList) {
        this.childList = childList;
    }

    public int getChildCount() {
        if (childList != null) {
            return childList.size();
        }
        return 0;
    }

    public String getTopImgPath() {
        return topImgPath;
    }

    public void setTopImgPath(String topImgPath) {
        this.topImgPath = topImgPath;
    }
}
