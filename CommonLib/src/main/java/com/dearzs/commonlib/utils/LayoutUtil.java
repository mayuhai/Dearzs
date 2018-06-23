package com.dearzs.commonlib.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * 布局计算工具
 * @version 1.0
 */
public class LayoutUtil {

	/**
	 * 根据现有宽度获取屏幕适配的高
	 * @param act
	 * @param oldHeight
	 * @param oldWidth
	 * @return
	 */
	public static int getAdaptiveHeight(Activity act, int curWidth,
			int oldHeight, int oldWidth) {
		int curHeight = curWidth * oldHeight / oldWidth;
		return curHeight;
	}

	/**
	 * 根据现有高度获取屏幕适配的宽
	 * @param act
	 * @param oldHeight
	 * @param oldWidth
	 * @return
	 */
	public static int getAdaptiveWidth(Activity act, int curHeight,
			int oldHeight, int oldWidth) {
		int curWidth = curHeight * oldWidth / oldHeight;
		return curWidth;
	}

	/**
	 * 重新计算控件高度
	 * @return
	 */
	public static void reMesureHeight(Activity act, View v, int curWidth,
			int oldHeight, int oldWidth) {
		ViewGroup.LayoutParams layoutParam = v.getLayoutParams();
		layoutParam.height = LayoutUtil.getAdaptiveHeight(act, curWidth,
				oldHeight, oldWidth);
		v.setLayoutParams(layoutParam);
	}


	/**
	 * 重新计算控件高度
	 */
	public static void reMeasureHeight(Context ctx, View v, int mHeight) {
		ViewGroup.LayoutParams layoutParam = v.getLayoutParams();
		layoutParam.height = DisplayUtil.dip2px(ctx, mHeight);
		v.setLayoutParams(layoutParam);
	}

	/**
	 * 重新计算控件高度
	 */
	public static void reMeasureHeight(View v, int mHeight) {
		ViewGroup.LayoutParams layoutParam = v.getLayoutParams();
		layoutParam.height = mHeight;
		v.setLayoutParams(layoutParam);
	}

	/**
	 * 重新计算控件宽度
	 */
	public static void reMeasureWidth(View v, int width) {
		ViewGroup.LayoutParams layoutParam = v.getLayoutParams();
		layoutParam.width = width;
		v.setLayoutParams(layoutParam);
	}

    /**
     * 重新计算控件高度
     * @return
     */
    public static void reMesureHeight(Activity ctx,View v, int mHeight) {
        ViewGroup.LayoutParams layoutParam = v.getLayoutParams();
        layoutParam.height = DisplayUtil.dip2px(ctx,mHeight);
        v.setLayoutParams(layoutParam);
    }

	/**
	 * 重新计算控件宽度
	 * @return
	 */
	public static void reMesureWidth(Activity act, View v, int curHeight,
			int oldHeight, int oldWidth) {
		ViewGroup.LayoutParams layoutParam = v.getLayoutParams();
		layoutParam.height = LayoutUtil.getAdaptiveWidth(act, curHeight,
				oldHeight, oldWidth);
		v.setLayoutParams(layoutParam);
	}

	/**
	 * 设置布局
	 * @param v
	 * @param curHeight
	 * @param curWidth
	 */
	public static void setLayout(View v, int curHeight, int curWidth) {
		ViewGroup.LayoutParams layoutParam = v.getLayoutParams();
		layoutParam.height = curHeight;
		layoutParam.width = curWidth;
		v.setLayoutParams(layoutParam);
	}

	/**
	 * 设置布局高度
	 * @param v
	 * @param curHeight
	 */
	public static void setLayoutHeight(View v, int curHeight) {
		ViewGroup.LayoutParams layoutParam = v.getLayoutParams();
		layoutParam.height = curHeight;
		v.setLayoutParams(layoutParam);
	}

	/**
	 * 设置布局宽度
	 * @param v
	 * @param curWidth
	 */
	public static void setLayoutWidth(View v, int curWidth) {
		ViewGroup.LayoutParams layoutParam = v.getLayoutParams();
		layoutParam.width = curWidth;
		v.setLayoutParams(layoutParam);
	}

}
