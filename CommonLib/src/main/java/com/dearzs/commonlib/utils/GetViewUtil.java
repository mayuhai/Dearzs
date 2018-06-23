package com.dearzs.commonlib.utils;

import android.app.Activity;
import android.util.Log;
import android.view.View;

/**
 * 获取控件的工具类<br>
 * @version 1.0
 */
public class GetViewUtil {

	/**
	 * 获得对应的控件
	 *
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static final <E extends View> E getView(Activity activity,int id) {
		try {
			return (E) activity.findViewById(id);
		} catch (ClassCastException ex) {
			Log.e("View", "Could not cast View to concrete class.", ex);
			throw ex;
		}
	}

	/**
	 * 获得对应的控件
	 *
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static final <E extends View> E getView(View v, int id) {
		try {
			return (E) v.findViewById(id);
		} catch (ClassCastException ex) {
			Log.e("View", "Could not cast View to concrete class.", ex);
			throw ex;
		}
	}
}
