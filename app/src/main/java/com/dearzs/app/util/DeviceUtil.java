package com.dearzs.app.util;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.dearzs.commonlib.utils.PfUtils;

import java.io.DataOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * 管理设备相关信息，设备编号及设备当前时间
 */
public class DeviceUtil {

    private static DeviceUtil mInstance;
    private Context cxt;

    private DeviceUtil(Context cxt) {
        this.cxt = cxt;
    }

    synchronized static public void init(Context cxt) {
        if (mInstance != null) return;
        mInstance = new DeviceUtil(cxt);
    }

    public static DeviceUtil getInstance() {
        return mInstance;
    }

    private static final SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmss");

    /**
     * 获取当前时间
     */
    public String getCurTime() {
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        return sdf2.format(curDate);
    }

    /**
     * 获取当前时间
     *
     * @return
     */
    public String getCurTimeMil() {
        return System.currentTimeMillis() / 1000 + "";
    }

    // 小于0时代表用户设置的时间太超前了
    private long timeDifference;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm");
    private static final String SynchronizationTime = "SynchronizationTime";

    /**
     * 同步系统时间
     */
    public void SynchronizationTime(Context mContext, String serverTime) {
        timeDifference = PfUtils.getLong(mContext, Constant.USER_CONFIG, SynchronizationTime, 0);
        try {
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
            long ld = sdf.parse(serverTime).getTime();
            timeDifference = (ld - System.currentTimeMillis()) / 1000;
            PfUtils.setLong(mContext, Constant.USER_CONFIG, SynchronizationTime, timeDifference);
        } catch (Exception e) {
        }
    }

    // 获取当前设备的设备号
    private String deviceToken;

    public String getDeviceToken() {
        if (TextUtils.isEmpty(deviceToken)) {
            try {
                deviceToken = DeviceUuidFactory.getClientID(cxt);
            } catch (Exception e) {
            }
        }
        return deviceToken;
    }

    /**
     * 获取系统版本
     *
     * @return
     */
    public String getSystemReleaseVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 获取系统SDK
     *
     * @return
     */
    public int getSystemSdkVersion() {
        return Build.VERSION.SDK_INT;
    }

    /**
     * 判断机器 是否已经root，即是否获取root权限
     */
    public synchronized boolean getRootAuthority() {
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("exit\n");
            os.flush();
            int exitValue = process.waitFor();
            if (exitValue == 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (process != null) {
                    process.destroy();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
