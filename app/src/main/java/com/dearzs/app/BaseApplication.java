package com.dearzs.app;

import android.app.Application;
import android.os.Build;

import com.dearzs.app.base.BaseActivity;
import com.dearzs.app.chat.model.TimManager;
import com.dearzs.app.entity.EntityPicBase;
import com.dearzs.app.entity.EntityUserInfo;
import com.dearzs.app.entity.resp.RespUserLogin;
import com.dearzs.app.util.Constant;
import com.dearzs.app.util.DeviceUtil;
import com.dearzs.app.util.ImageLoaderManager;
import com.dearzs.app.util.ReqManager;
import com.dearzs.app.util.ShareUtil;
import com.dearzs.app.util.Utils;
import com.dearzs.commonlib.okhttp.OkHttpUtils;
import com.dearzs.upload.uploadimage.UploadManager;
import com.dearzs.upload.uploadimage.utils.BitmapCompressManager;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import cn.jpush.android.api.JPushInterface;

/**
 * 基础Application
 */
public class BaseApplication extends Application {
    private RespUserLogin.EntityUserLogin mLoginInfo;
    private EntityUserInfo mUserInfo;
    private static BaseApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);     		// 初始化 JPush
        //分享初始化
        ShareUtil.getInstence(this).init();
        //图片压缩
        BitmapCompressManager.init(Constant.ROOT_APP_DIR);
        ReqManager.init(this);
        DeviceUtil.init(this);
        UploadManager.init(getCurUserAgent(), Constant.ROOT_APP_DIR);

        //初始化OkHttp
        OkHttpUtils.getInstance().debug("testDebug").setConnectTimeout(30000, TimeUnit.MILLISECONDS);
        //初始化初始化ImagerLoader
        ImageLoaderManager.getInstance(getApplicationContext());

        //初始化IM
        TimManager.getInstance(this).init();


        //bugly 建议在测试阶段建议设置成true，发布时设置为false。
        CrashReport.initCrashReport(getApplicationContext(), "1400011690", true);
        //bugly测试崩溃
//        CrashReport.testJavaCrash();
    }

    public void exit() {
        // 应用退出时,关闭所有Activity
        if (mAllActivitys != null) {
            for (BaseActivity item : mAllActivitys) {
                if (item == null || item.isFinishing()) {
                    continue;
                } else {
                    item.finish();
                }
            }
            mAllActivitys.clear();
            mAllActivitys = null;
        }
        //退出程序
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    private ArrayList<BaseActivity> mAllActivitys;

    public BaseActivity getTopActivity() {
        if (mAllActivitys == null || mAllActivitys.isEmpty()) {
            return null;
        }
        int count = mAllActivitys.size();
        for (int index = count - 1; index >= 0; index--) {
            BaseActivity item = mAllActivitys.get(index);
            if (item != null && item.isFinishing() == false) {
                return item;
            }
        }
        return null;
    }

    private String getCurUserAgent() {
        //dearzs/1.2.0/Android 4.1.2; zh-cn; GT-P3100 Build/JZO54K
        String webUserAgent = "%s/%s/Android %s";
        Locale locale = Locale.getDefault();
        StringBuffer buffer = new StringBuffer();
        // Add version
        final String version = Build.VERSION.RELEASE;
        if (version.length() > 0) {
            buffer.append(version);
        } else {
            // default to "1.0"
            buffer.append("1.0");
        }
        buffer.append("; ");
        final String language = locale.getLanguage();
        if (language != null) {
            buffer.append(language.toLowerCase());
            final String country = locale.getCountry();
            if (country != null) {
                buffer.append("-");
                buffer.append(country.toLowerCase());
            }
        } else {
            // default to "en"
            buffer.append("en");
        }
        // add the model for the release build
        if ("REL".equals(Build.VERSION.CODENAME)) {
            final String model = Build.MODEL;
            if (model.length() > 0) {
                buffer.append("; ");
                buffer.append(model);
            }
        }
        final String id = Build.ID;
        if (id.length() > 0) {
            buffer.append(" Build-");
            buffer.append(id);
        }
        return String.format(webUserAgent, "dearzs", Utils.getVersion(this), buffer);
    }

    /**
     * 多线程安全: 获取TaoCheApplication句柄
     */
    synchronized public static final BaseApplication getInstance() {
        return mInstance;
    }

    public void addActivity(BaseActivity mActivity) {
        if (mAllActivitys == null) {
            mAllActivitys = new ArrayList<BaseActivity>();
        }
        mAllActivitys.add(mActivity);
    }

    public void deleteActivity(BaseActivity mActivity) {
        if (mAllActivitys == null) return;
        mAllActivitys.remove(mActivity);
    }

    /**
     * ========= 用于存储当前发送图片的临时照片对列=============
     */
    private ArrayList<? extends EntityPicBase> mPicList;

    public void clearPicList() {
        if (mPicList == null || mPicList.isEmpty()) {
            return;
        }
        for (EntityPicBase item : mPicList) {
            item.destory();
        }
        mPicList.clear();
        mPicList = null;
    }

    public EntityUserInfo getUserInfo() {
        if (mLoginInfo != null) {
            return mLoginInfo.getUser();
        } else {
            return new EntityUserInfo();
        }
    }

    public void setUserInfo(EntityUserInfo mUserInfo) {
        this.mUserInfo = mUserInfo;
    }

    public void setLoginInfo(RespUserLogin.EntityUserLogin userInfo) {
        this.mLoginInfo = userInfo;
        setUserInfo(userInfo.getUser());
    }

    public RespUserLogin.EntityUserLogin getLoginInfo() {
        return mLoginInfo;
    }

    public void setPicList(ArrayList<? extends EntityPicBase> mPicList) {
        this.mPicList = mPicList;
    }

}
