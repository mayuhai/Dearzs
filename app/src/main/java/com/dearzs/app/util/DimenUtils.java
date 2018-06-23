package com.dearzs.app.util;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
/**
 * 尺寸单位转换 工具类  add by tjt
 * */
public class DimenUtils {
	
	/**
	 * sp 转px
	 * */
	public static int sp2px(Context context, float spValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
	/**
	 * px 转sp
	 * */
    public static int px2sp(Context context, float pxValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }
	/**
	 * dip 转px
	 * */
    public static int dip2px(Context context, int dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
	/**
	 * px 转dip
	 * */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
    /**
	 * @desc 获取屏幕高度
	 */
	public static int getScreenH(Context context) {

		DisplayMetrics dm = new DisplayMetrics();
		// 获取屏幕信息
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		int screenWidth = dm.heightPixels;
		return screenWidth;
	}
	public static int getScreenW(Context context) {

		DisplayMetrics dm = new DisplayMetrics();
		// 获取屏幕信息
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		int screenWidth = dm.widthPixels;
		return screenWidth;
	}
}
