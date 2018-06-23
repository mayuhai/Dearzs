package com.dearzs.upload.uploadimage;

import android.text.TextUtils;

import com.dearzs.upload.uploadimage.listener.impl.IUploadModel;
import com.dearzs.upload.uploadimage.utils.BitmapCompressManager;
import com.dearzs.upload.uploadimage.utils.ThreadPool;
import com.dearzs.upload.uploadimage.utils.UploadConstant;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 图片上传管理类
 */
public class UploadManager {

    /** 单例对象*/
    private static UploadManager mSingleton;
    /** 具体联网对象*/
    private OkHttpClient mOkHttpClient;
    /** 请求头对象*/
    private String userAgent;
    /** 待请求对列*/
    private ConcurrentHashMap<IUploadModel, UploadRequest> mUploadRequests;

    /** 构造函数初始化 */
    private UploadManager(String userAgent) {
        if (mOkHttpClient == null) {
            // 设置写入、读取和连接的超时时间
            OkHttpClient.Builder mClientBuilder = new OkHttpClient().newBuilder();
            mClientBuilder.writeTimeout(20, TimeUnit.SECONDS);
            mClientBuilder.readTimeout(20, TimeUnit.SECONDS);
            mClientBuilder.connectTimeout(30, TimeUnit.SECONDS);
            mOkHttpClient = mClientBuilder.build();
        }
        this.userAgent = userAgent;
        mUploadRequests = new ConcurrentHashMap<IUploadModel, UploadRequest>();
    }

    /**
     * 退出自身管理器
     */
    synchronized public static void exit() {
        if (mSingleton == null) {
            return;
        }
        if (mSingleton.mOkHttpClient == null) {
            return;
        }
        mSingleton.mOkHttpClient = null;
        mSingleton = null;
    }

    /**
     * 初始化自身管理器
     * @return
     */
    synchronized public static boolean init(String userAgent, String uploadFileDir) {
        if (mSingleton != null) return false;

        mSingleton = new UploadManager(userAgent);
        BitmapCompressManager.init(uploadFileDir);
        return true;
    }

    /**
     * 获取上传管理单例
     * @return
     */
    synchronized public static UploadManager getInstance() {
        return mSingleton;
    }

    /**
     * 清空所有请求
     */
    public void clearAllRequest() {
        cancelAllRequest();
        if (mUploadRequests != null) {
            mUploadRequests.clear();
        }
    }

    /**
     * 取消所有上传任务
     */
    public void cancelAllRequest() {
        if (mUploadRequests == null) return;
        Iterator<IUploadModel> it = mUploadRequests.keySet().iterator();
        while(it.hasNext()){
            try {
                mUploadRequests.get(it.next()).cancel();
            } catch (Exception e) {
            }
        }
    }
    /**
     * 执行上传任务
     *
     * @param uploadUrl
     * @param mIUploadModel
     */
    public void execute(final String uploadUrl, final IUploadModel mIUploadModel) {
        if (TextUtils.isEmpty(uploadUrl)) {
            return;
        }
        if (mIUploadModel == null) {
            return;
        }

        // 若对列中,包含此上传数据时,则强制重试
        final boolean isRetry = mUploadRequests.containsKey(mIUploadModel);

        // 生成请求任务
        final UploadRequest uploadRequest = new UploadRequest(uploadUrl, mIUploadModel);
        // 此处是在线程中执行的
        ThreadPool.getInstence().execute(new Runnable() {
            @Override
            public void run() {
                boolean isInit = uploadRequest.init();
                //初始化失败直接返回,因初始化时，未修改状态
                if (!isInit) {
                    return;
                }
                // 将可执行的任务,加入到对列中
                if (!isRetry) {
                    try {
                        mUploadRequests.put(mIUploadModel, uploadRequest);
                    } catch (Exception e) {
                    }
                }
                uploadRequest.initRequest(userAgent);
                // 获取请求失败时,要回调(因可能修改了UI状态)
                if (uploadRequest.getRealRequest() == null) {
                    doResult(uploadRequest, UploadConstant.RESULT_FAILED, "初始化失败");
                    return;
                }
                // 开始上传
                doRequest(uploadRequest);
            }
        });
    }

    /**
     * 具体执行上传操作
     */
    private void doRequest(final UploadRequest uploadRequest) {
        if (uploadRequest.getRealRequest() == null) return;

        uploadRequest.showProgressListenerTip(UploadConstant.UPLOAD_PROGRESS_TIP_START);
        Call mCall = mOkHttpClient.newCall(uploadRequest.getRealRequest());
        uploadRequest.setRealCall(mCall);
        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                doResult(uploadRequest, UploadConstant.RESULT_FAILED, UploadConstant.UPLOAD_PROGRESS_TIP_FAILED);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (response == null || response.body() == null) {
                        doResult(uploadRequest, UploadConstant.RESULT_FAILED, UploadConstant.UPLOAD_PROGRESS_TIP_ERROR);
                        return;
                    }
                    String resultResponse = BitmapCompressManager.getInstance().
                            readByteInputStream(response.body().byteStream());
                    doResult(uploadRequest, UploadConstant.RESULT_SUCCESS, resultResponse);
                } catch (Exception e) {
                    doResult(uploadRequest, UploadConstant.RESULT_FAILED, UploadConstant.UPLOAD_PROGRESS_TIP_ERROR);
                }
            }
        });
    }

    private void doResult(final UploadRequest uploadRequest,
                          int state,
                          String result) {
        // 只有当任务成功时,从对列中移除，，为了使用失败时,点击可重试
        if (state == UploadConstant.RESULT_SUCCESS) {
            if (uploadRequest != null && !mUploadRequests.isEmpty()) {
                mUploadRequests.remove(uploadRequest.getUploadModel());
            }
        }
        uploadRequest.doResult(state, result);
    }
}
