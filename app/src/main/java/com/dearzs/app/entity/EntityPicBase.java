package com.dearzs.app.entity;

import android.os.Parcelable;

import com.dearzs.upload.uploadimage.listener.impl.IUploadModel;
import com.dearzs.upload.uploadimage.listener.impl.IUploadProgress;

/**
 * 图片基类
 */
abstract public class EntityPicBase implements IUploadModel, Parcelable {

    public static final int UPLOAD_PIC_TIP = 0;                         // 只用于显示的"更多"
    public static final int UPLOAD_PIC_NORMAL = UPLOAD_PIC_TIP + 1;     // 正常状态
    public static final int UPLOAD_PIC_ING = UPLOAD_PIC_NORMAL + 1;     // 上传中
    public static final int UPLOAD_PIC_SUCCESS = UPLOAD_PIC_ING + 1;    // 上传成功
    public static final int UPLOAD_PIC_FAILED = UPLOAD_PIC_SUCCESS + 1; // 上传失败

    public int getUpload_state() {
        return UPLOAD_PIC_NORMAL;
    }

    @Override
    abstract public int doResult(int uploadResult, String newFilePath, Object resultMessage);

    @Override
    abstract public boolean isNeedUpload();

    @Override
    abstract public String getUploadFilePath();

    @Override
    abstract public void bindUploadProgress(IUploadProgress mIUploadProgress);

    @Override
    abstract public IUploadProgress getUploadProgress();

    abstract public void destory();
}
