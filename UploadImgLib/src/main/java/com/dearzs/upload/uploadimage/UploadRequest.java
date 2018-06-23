package com.dearzs.upload.uploadimage;

import android.content.ContentValues;
import android.text.TextUtils;
import android.util.Base64;

import com.dearzs.upload.uploadimage.listener.impl.IUploadModel;
import com.dearzs.upload.uploadimage.progress.ProgressRequestBody;
import com.dearzs.upload.uploadimage.utils.BitmapCompressManager;
import com.dearzs.upload.uploadimage.utils.UploadConstant;

import java.io.File;
import java.io.FileInputStream;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * 上传请求
 * 1，目标上传地址
 * 2，上传对应的model
 * 3，自身请求需要添加超时机型,在超过指定时间后，若还未执行完,则自动销毁上传操作
 */
public class UploadRequest {

    // 数据上传的目标地址(服务端地址)
    private String uploadUrl;
    // 上传请求携带的数据
    private IUploadModel mIUploadModel;
    // 默认为二进制上传
    private boolean uploadBinary = true;

    // 对原始照片进行压缩和处理后,会得到临时的数据和临时图片路径
    private String tempFilePath;
    private String tempData;

    // 请求对象,方便cancel
    private Request mRequest;
    private Call mCall;

    public UploadRequest(String uploadUrl,
                         IUploadModel mIUploadModel) {
        this.uploadUrl = uploadUrl;
        this.mIUploadModel = mIUploadModel;
    }

    /**
     * 对上传操作进行初始化
     * 不会影响视图的状态
     */
    public boolean init() {
        // 上传地址不存在，直接忽略
        if (TextUtils.isEmpty(uploadUrl)) {
            return false;
        }
        // 上传数据不存在，或不需要上传时,忽略
        if (mIUploadModel == null || !mIUploadModel.isNeedUpload()) {
            return false;
        }
        return true;
    }

    public Request getRealRequest() {
        return mRequest;
    }

    public void setRealCall(Call mCall) {
        this.mCall = mCall;
    }

    public void cancel() {
        if (mCall == null) return;
        try {
            mCall.cancel();
        } catch (Exception e){
        }
    }

    /**
     *
     * 生成请求任务
     * @return
     */
    public void initRequest(String userAgent) {
        if (mRequest != null) {
            mRequest = null;
        }
        // 复制、压缩待上传的图片
        String newFilePath = compress(mIUploadModel.getUploadFilePath());

        File targetFile = null;
        if (!TextUtils.isEmpty(newFilePath)) {
            targetFile = new File(newFilePath);
        }
        // 新图片路径不存在时,无法上传
        if (targetFile == null || !(targetFile.exists())) {
            return;
        }
        // 封装请求参数
        RequestBody mRequestBody = getRequestBody(targetFile);
        if (mRequestBody == null) {
            return;
        }
        // 记录可用的临时文件路径
        tempFilePath = newFilePath;
        mRequest = new Request
                .Builder()
                .url(uploadUrl)
                .addHeader("User-Agent", userAgent)
                .post(new ProgressRequestBody(mRequestBody, mIUploadModel))
                .build();
    }

    /**
     * 此方法调用是在线程中触发的,所以此处可做耗时的操作，比如解析返回的数据
     * @param uploadResult
     * @param resultMessage
     */
    public void doResult(int uploadResult, Object resultMessage) {
        int realUploadResult = uploadResult;
        // 未取消才解析数据等
        if (mIUploadModel != null) {
            realUploadResult = mIUploadModel.doResult(uploadResult, tempFilePath, resultMessage);
        }
        if (mIUploadModel.getUploadProgress() != null) {
            mIUploadModel.getUploadProgress().doResult(realUploadResult,
                    realUploadResult == UploadConstant.RESULT_SUCCESS ? null: resultMessage);
        }

        // 上传成功后,将临时数据删除,,失败时保留，方便重传
        if (realUploadResult == UploadConstant.RESULT_SUCCESS) {
            tempData = null;
        }
    }

    /**
     * 显示具体上传状态
     *
     * @param tip
     */
    public void showProgressListenerTip(String tip) {
        if (mIUploadModel.getUploadProgress() == null) {
            return;
        }
        mIUploadModel.getUploadProgress().initUploadData(tip, tempFilePath);
    }

    /** 获取请求中的实体类*/
    public IUploadModel getUploadModel() {
        return mIUploadModel;
    }

    /**
     * 获取上传参数
     * @return
     */
    private RequestBody getRequestBody(File uploadFilePath) {
        RequestBody mRequestBody;
        // 以二进制的方式上传
        if (uploadBinary) {
            // 注意 必须指定 filename 这个参数
            mRequestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", uploadFilePath.getName(),
                            RequestBody.create(MultipartBody.FORM, uploadFilePath))
                    .build();
        }
        // 以key=value的方式(不推荐使用此方式)
        else {
            // 获取图片二制进
            try {
                // 读取指定文件,获取其base64编码
                String fileData = BitmapCompressManager.getInstance().readByteInputStream(
                        new FileInputStream(uploadFilePath));
                tempData = Base64.encodeToString(fileData.getBytes(), Base64.DEFAULT);
            } catch (Exception e) {
            } catch (Error error) {
            }
            if (TextUtils.isEmpty(tempData)) {
                return null;
            }
            ContentValues uploadParams = new ContentValues();
            uploadParams.put("onlyBase64", "1");
            uploadParams.put("pic_base64", tempData);
            FormBody.Builder mBuilder = new FormBody.Builder();
            for (String key : uploadParams.keySet()) {
                mBuilder.add(key, uploadParams.get(key).toString());
            }
            mRequestBody = mBuilder.build();
        }
        return mRequestBody;
    }

    /** 对指定文件进行校验（copy和压缩)*/
    private String compress(String uploadFilePath) {
        tempFilePath = uploadFilePath;
        // 复制图片
        showProgressListenerTip(UploadConstant.UPLOAD_PROGRESS_TIP_COPY);
        String newFilePath = BitmapCompressManager.getInstance().copyFileUsingFileChannels(uploadFilePath);

        // 压缩图片
        showProgressListenerTip(UploadConstant.UPLOAD_PROGRESS_TIP_COMPRESS);
        try {
            BitmapCompressManager.getInstance().compressImage(newFilePath);
        } catch (Exception e) {
            newFilePath = null;
        } catch (Error error) {
            newFilePath = null;
        }
        return newFilePath;
    }
}
