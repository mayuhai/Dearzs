package com.dearzs.app.entity;

import android.os.Parcel;
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
public class EntityUploadPic extends EntityBase {
    private EntityResult result;

    public EntityResult getResult() {
        return result;
    }

    public void setResult(EntityResult result) {
        this.result = result;
    }

    public class EntityResult{
        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}

