package com.dearzs.commonlib.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

/**
 * Android大小单位转换工具类
 *
 * @author wader
 */
public class DisplayUtil {

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @param fontScale （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int px2sp(float pxValue, float fontScale) {
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @param fontScale （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(float spValue, float fontScale) {
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 获取屏幕宽高
     *
     * @param act Activity对象
     * @return DisplayMetrics
     */
    public static DisplayMetrics getDisplayMetrics(Context act) {
        WindowManager manager = getWindowManager(act);
        DisplayMetrics dm = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(dm);
        return dm;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 获取屏幕宽度
     *
     * @param ctx
     * @return
     */
    public static int getScreenWidth(Context ctx) {
        try {
            DisplayMetrics dm = getDisplayMetrics(ctx);
            if (dm != null) {
                return dm.widthPixels;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取屏幕高度
     *
     * @param ctx
     * @return
     */
    public static int getScreenHeight(Context ctx) {
        try {
            DisplayMetrics dm = getDisplayMetrics(ctx);
            if (dm != null) {
                return dm.heightPixels;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取WindowManager
     *
     * @param ctx
     * @return
     */
    private static WindowManager getWindowManager(Context ctx) {
        try {
            if (ctx != null) {
                return (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取控件的高度
     *
     * @param view
     * @return
     */
    public static int getRealHeight(View view) {
        if (view == null) return 0;
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(width, height);
        height = view.getMeasuredHeight();
        return height;
    }
}