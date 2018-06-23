package com.dearzs.upload.uploadimage.listener;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.dearzs.upload.uploadimage.utils.UploadConstant;

import java.lang.ref.WeakReference;

/**
 * 处理上传状态的变化
 * 包含上传进度的监听实例
 */
abstract class ImageUploadProgressHandler extends Handler {

    // 初始化(拷贝,压缩,准备上传)
    public static final int INIT = 0x01;
    // 进行中
    public static final int UPDATE = 0x02;
    // 结束
    public static final int FINISH = 0x03;

    //弱引用
    private final WeakReference<ImageUploadProgressListener> mUIProgressListenerWeakReference;

    public ImageUploadProgressHandler(ImageUploadProgressListener uiProgressListener) {
        super(Looper.getMainLooper());
        mUIProgressListenerWeakReference = new WeakReference<ImageUploadProgressListener>(uiProgressListener);
    }

    @Override
    public void handleMessage(Message msg) {
        //获得进度
        String uploadProgress = null;
        String uploadTip = null;
        String uploadFilePath = null;

        // 获得参数
        Bundle mBundle = msg.getData();
        if (mBundle != null) {
            uploadProgress = mBundle.getString(UploadConstant.UPLOAD_PROGRESS);
            uploadTip = mBundle.getString(UploadConstant.UPLOAD_TIP);
            uploadFilePath = mBundle.getString(UploadConstant.UPLOAD_FILE_PATH);
        }
        // 图片上传进度
        ImageUploadProgressListener uiProgressListener = mUIProgressListenerWeakReference.get();

        switch (msg.what) {
            case INIT: {
                if (uiProgressListener != null) {
                    //回调抽象方法
                    init(uiProgressListener, uploadTip, uploadFilePath);
                }
                break;
            }
            case UPDATE: {
                if (uiProgressListener != null) {
                    //回调抽象方法
                    progress(uiProgressListener, uploadProgress);
                }
                break;
            }
            case FINISH:
                if (uiProgressListener != null) {
                    finish(uiProgressListener);
                }
                break;
            default:
                super.handleMessage(msg);
                break;
        }
    }

    public abstract void init(ImageUploadProgressListener uiProgressListener, String tip, String filePath);
    public abstract void progress(ImageUploadProgressListener uiProgressListener, String progress);
    public abstract void finish(ImageUploadProgressListener uiProgressListener);
}
