package com.dearzs.upload.uploadimage.listener.impl;

/**
 * 待上传的实体类
 */
public interface IUploadModel {

    // 处理上传结果,,非UI线程的操作
    int doResult(int uploadResult, String newFilePath, Object resultMessage);

    /** 是否需要上传 *非网络图片，非“提示”图片 */
    boolean isNeedUpload();

    /** 获取要上传的文件路径*/
    String getUploadFilePath();

    /** 设置数据监听接口*/
    void bindUploadProgress(IUploadProgress mIUploadProgress);

    /** 获取进度监听接口*/
    IUploadProgress getUploadProgress();
}
