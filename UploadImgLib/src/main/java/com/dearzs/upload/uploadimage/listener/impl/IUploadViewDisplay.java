package com.dearzs.upload.uploadimage.listener.impl;

import com.dearzs.upload.uploadimage.listener.ImageUploadRetryListener;

/**
 * 上传数据实体类对应的视图
 */
public interface IUploadViewDisplay extends IUploadProgress {

    /** 设置其视图重传监听*/
    void setOnRetryListener(ImageUploadRetryListener retryListener);

    /** 回收视图*/
    void recyleView();
}
