package com.dearzs.commonlib.utils.log;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * 日志工具类，项目中所有的日志都应用此工具类输出，便于统一管理
 *
 * @version 1.0
 */
public class LogUtil {
    /**
     * log开关;默认为false表示关闭日志开关。true表示打开日志开关
     **/
    public static boolean DEBUG = true;

    /**
     * 设置日志输出状态<br/>
     * logDebug;默认为false表示关闭日志开关。true表示打开日志开关
     *
     * @param logDebug
     */
    public static void initLogDebug(boolean logDebug) {
        DEBUG = logDebug;
    }

    /**
     * Toast提示代理，需求中的Toast应使用本方法
     *
     * @param ctx 上下文
     * @param msg 提示消息
     */
    public static void showToast(Context ctx, String msg) {
        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 日志输出（d级别）
     *
     * @param tag 标签 默认为TaoChe
     * @param msg 日志信息
     */
    public static void LogD(String tag, String msg) {
        if (!DEBUG) {
            return;
        }
        if (tag == null || "".equals(tag)) {
            tag = "Dearzs";
        }
        int length = msg.length();
        int offset = 1000;
        if (length > offset) {// 解决报文过长，打印不全的问题！
            int n = 0;
            for (int i = 0; i < length; i += offset) {
                n += offset;
                if (n > length)
                    n = length;
                android.util.Log.d(tag, msg.substring(i, n));
            }
        } else {
            android.util.Log.d(tag, msg);
        }
    }

    /**
     * 日志输出（d级别）
     *
     * @param tag 标签 默认为TaoChe
     * @param msg 日志信息
     */
    public static void LogFormat(String tag, String msg, String title) {
        if (!DEBUG) {
            return;
        }
        if (tag == null || "".equals(tag)) {
            tag = "Dearzs";
        }
        Logger.d(tag, msg, title);
    }

    /**
     * 日志输出（e级别）
     *
     * @param msg 日志信息
     */
    public static void LogE(String msg) {
        if (!DEBUG) {
            return;
        }

        if (TextUtils.isEmpty(msg)) {
            return;
        }
        int length = msg.length();
        int offset = 1000;
        if (length > offset) {// 解决报文过长，打印不全的问题！
            int n = 0;
            for (int i = 0; i < length; i += offset) {
                n += offset;
                if (n > length)
                    n = length;
                android.util.Log.e("DearzsError", msg.substring(i, n));
            }
        } else {
            android.util.Log.e("DearzsError", msg);
        }
    }

    /**
     * 日志输出（i级别）
     *
     * @param msg 日志信息
     */
    public static void LogI(String msg) {
        if (!DEBUG) {
            return;
        }
        int length = msg.length();
        int offset = 1000;
        if (length > offset) {// 解决报文过长，打印不全的问题！
            int n = 0;
            for (int i = 0; i < length; i += offset) {
                n += offset;
                if (n > length)
                    n = length;
                android.util.Log.i("DearzsError", msg.substring(i, n));
            }
        } else {
            android.util.Log.i("DearzsError", msg);
        }
    }

    /**
     * 输出请求参数
     *
     * @param reqParams
     */
    public static void logReqParams(String reqParams, String title) {
        if (!DEBUG) {
            return;
        }
        if (reqParams.contains("&")) {
            String[] logArray = reqParams.split("&");
            StringBuffer sb = new StringBuffer();

            for (int i = 0; i < logArray.length; i++) {
                sb.append("" + logArray[i]);
                sb.append("\n");
            }
            LogFormat("json", sb.toString(), "接口请求报文------" + title);

        } else {
            LogUtil.LogD("json", "=================请-求-参-数================");
            LogUtil.LogD("json", "！！！请求参数：" + reqParams);
            LogUtil.LogD("json", "=================请-求-参-数================");
        }

    }

    /**
     * 输出POST请求参数
     *
     * @param reqParams
     */
    public static void logPostReqParams(String reqParams, String title) {
        if (!DEBUG) {
            return;
        }
        if (reqParams.contains("&")) {
            String[] logArray = reqParams.split("&");
            StringBuffer sb = new StringBuffer();

            for (int i = 0; i < logArray.length; i++) {
                sb.append("" + logArray[i]);
                sb.append("\n");
            }
            LogUtil.LogD("json", "=================POST-请-求-参-数================");
            LogFormat("json", sb.toString(), title);
            LogUtil.LogD("json", "=================POST-请-求-参-数================");
        }
    }
    public static void s(String msg){
        if(DEBUG){
            System.out.println(msg);
        }
    }
}
