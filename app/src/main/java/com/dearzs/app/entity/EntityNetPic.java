package com.dearzs.app.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.dearzs.app.entity.resp.RespUploadPic;
import com.dearzs.app.util.DataPar;
import com.dearzs.commonlib.utils.log.LogUtil;
import com.dearzs.upload.uploadimage.listener.impl.IUploadProgress;
import com.dearzs.upload.uploadimage.utils.UploadConstant;

/**
 * 接口返回的图片列表，
 * 以及本地选择的待上传图片
 *
 * @author zhaoyb
 */
public class EntityNetPic extends EntityPicBase {

    // 默认为正常图片
    private int upload_state = UPLOAD_PIC_NORMAL;
    private String url;

    private String defaultTip;
    // 本地图片路径,,有可能发生变化(比如压缩后的新图片路径)
    private String localPath;

    public int getUpload_state() {
        return upload_state;
    }

    public void setUpload_state(int upload_state) {
        this.upload_state = upload_state;
    }

    public String getUrl() {
        return getPictureUrl();
    }

    public String getPictureUrl() {
        LogUtil.LogE(localPath);
        if (TextUtils.isEmpty(url)) {
            return localPath;
        } else {
            return url;
        }
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public String getDefaultTip() {
        return defaultTip;
    }

    public void setDefaultTip(String defaultTip) {
        this.defaultTip = defaultTip;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        // 只要修改了本地图片,则将相应字段清空
        upload_state = UPLOAD_PIC_NORMAL;
        url = null;

        defaultTip = null;
        this.localPath = localPath;
    }

    /**
     * ==================Parcelable接口方法=====================
     */
    public EntityNetPic() {
    }

    public EntityNetPic(Parcel in) {
        upload_state = in.readInt();
        url = in.readString();
        defaultTip = in.readString();
        localPath = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(upload_state);
        dest.writeString(url);
        dest.writeString(defaultTip);
        dest.writeString(localPath);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public EntityNetPic createFromParcel(Parcel source) {
            return new EntityNetPic(source);
        }

        @Override
        public EntityNetPic[] newArray(int size) {
            return new EntityNetPic[size];
        }
    };

    /**
     * ================IUploadModel接口方法============================
     */
    @Override
    public int doResult(int uploadResult, String newFilePath, Object resultMessage) {
        // 默认失败了
        int realUploadSuccess = UploadConstant.RESULT_FAILED;
        // 记录新的图片路径(copy并且压缩后的)
        if (!TextUtils.isEmpty(newFilePath)) {
            this.localPath = newFilePath;
        }
        try {
            if (uploadResult == UploadConstant.RESULT_SUCCESS) {
                RespUploadPic mRespUploadPic = (RespUploadPic) new DataPar().
                        parData(resultMessage.toString(), RespUploadPic.class);
                if (mRespUploadPic != null && mRespUploadPic.isSuccess()) {
                    url = mRespUploadPic.getResult().getUrl();
                    LogUtil.LogE("url = " + url);
                    realUploadSuccess = UploadConstant.RESULT_SUCCESS;
                }
            }
        } catch (Exception e) {
        }
        // 记录最终状态
        if (realUploadSuccess == UploadConstant.RESULT_FAILED) {
            upload_state = UPLOAD_PIC_FAILED;
        } else {
            upload_state = UPLOAD_PIC_SUCCESS;
        }
        return realUploadSuccess;
    }

    /**
     * 验证是否需要上传
     * <p/>
     * 1，网络图不用上传
     * 2, 本地图片路径不存在不用上传
     * 3, 图片当前状态为“更多”，进行中，成功，不用上传
     *
     * @return
     */
    @Override
    public boolean isNeedUpload() {
        if (isNetPic()) {
            return false;
        }
        if (TextUtils.isEmpty(localPath)) {
            return false;
        }
        if (upload_state == UPLOAD_PIC_TIP || upload_state == UPLOAD_PIC_ING
                || upload_state == UPLOAD_PIC_SUCCESS) {
            return false;
        }
        return true;
    }

    @Override
    public String getUploadFilePath() {
        return localPath;
    }

    private IUploadProgress mIUploadProgress;

    @Override
    public void bindUploadProgress(IUploadProgress mIUploadProgress) {
        this.mIUploadProgress = mIUploadProgress;
    }

    @Override
    public IUploadProgress getUploadProgress() {
        return mIUploadProgress;
    }

    @Override
    public void destory() {
        mIUploadProgress = null;
    }

    /**
     * 验证当前图片为网络图片
     * 1, 相应变量不存在
     * 2，相应变量名不是网络地址
     * 3, 图片名称不存在
     * 4，图片名称不包含在路径中
     *
     * @return
     */
    public boolean isNetPic() {
        String url = getPictureUrl();
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        return url.startsWith("http");
    }
}

