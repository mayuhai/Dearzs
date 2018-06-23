package com.dearzs.upload.uploadimage.listener;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.dearzs.upload.uploadimage.listener.impl.IUploadProgress;
import com.dearzs.upload.uploadimage.utils.UploadConstant;

import java.text.DecimalFormat;

/**
 * 图片上传进度回调
 */
public abstract class ImageUploadProgressListener implements IUploadProgress {

    //处理UI层的Handler子类
    private static class UIHandler extends ImageUploadProgressHandler {
        public UIHandler(ImageUploadProgressListener uiProgressListener) {
            super(uiProgressListener);
        }

        @Override
        public void init(ImageUploadProgressListener uiProgressListener, String tip, String filePath) {
            if (uiProgressListener!=null) {
                uiProgressListener.onUIInit(tip, filePath);
            }
        }

        @Override
        public void progress(ImageUploadProgressListener uiProgressListener, String progress) {
            if (uiProgressListener!=null){
                uiProgressListener.onUIProgress(progress);
            }
        }

        @Override
        public void finish(ImageUploadProgressListener uiProgressListener) {
            if (uiProgressListener!=null){
                uiProgressListener.onUIFinish();
            }
            removeCallbacksAndMessages(null);
        }
    }

    //主线程Handler
    private final Handler mHandler = new UIHandler(this);

    @Override
    public void initUploadData(String tip, String filePath) {
        Message start = Message.obtain();
        Bundle mBundle = new Bundle();
        mBundle.putString(UploadConstant.UPLOAD_TIP, tip);
        mBundle.putString(UploadConstant.UPLOAD_FILE_PATH, filePath);
        start.setData(mBundle);

        start.what = ImageUploadProgressHandler.INIT;
        mHandler.sendMessage(start);
    }

    @Override
    public void onProgress(long bytesWrite, long contentLength) {
        //通过Handler发送进度消息
        Message message = Message.obtain();
        Bundle mBundle = new Bundle();
        mBundle.putString(UploadConstant.UPLOAD_PROGRESS, getPercent(bytesWrite, contentLength));
        message.setData(mBundle);

        message.what = ImageUploadProgressHandler.UPDATE;
        mHandler.sendMessage(message);

        if(bytesWrite >= contentLength) {
            Message finish = Message.obtain();
            finish.what = ImageUploadProgressHandler.FINISH;
            mHandler.sendMessage(finish);
        }
    }

    /**
     * 根据当前值和总长度,获取其百分比
     * @param currentBytes
     * @param contentLength
     * @return
     */
    private String getPercent(long currentBytes, long contentLength){
        String result = "";//接受百分比的值
        double x_double = currentBytes * 1.0;
        DecimalFormat df1 = new DecimalFormat("0.00%");    //##.00%   百分比格式，后面不足2位的用0补齐
        if (contentLength <= 0) {
            result = "90.00%";
        } else {
            result = df1.format(x_double / contentLength);
        }
        return result;
    }

    /**
     * UI层开始初始化数据(可能包括copy,压缩，读取等)
     */
    public void onUIInit(String tip, String filePath) {}

    /**
     * UI层回调抽象方法
     *
     * @param progress 计算好的百分比
     */
    public abstract void onUIProgress(String progress);

    public void onUIFinish() {};
}