package com.dearzs.commonlib.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.LinkedList;

/**
 * 轻量级的存储类管理 
 * 1,支持str,int,boolean,long的读取和设置 
 */
public class PfUtils {

	/**
	 * 保存字符串值
	 * 
	 * @param mContext
	 * @param name
	 * @param key
	 * @param value
	 */
	public static void setStr(Context mContext, String name, String key,
			String value) {
		SharedPreferences sharedPreferences = mContext.getSharedPreferences(
				name, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	/**
	 * 获取字符串值
	 * @param mContext
	 * @param name
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static String getStr(Context mContext, String name, String key,
			String defaultValue) {
		SharedPreferences sharedPreferences = mContext.getSharedPreferences(
				name, Context.MODE_PRIVATE);
		return sharedPreferences.getString(key, defaultValue);
	}

	/**
	 * 保存整数值
	 * @param mContext
	 * @param name
	 * @param key
	 * @param value
	 */
	public static void setInt(Context mContext, String name, String key,
			int value) {
		SharedPreferences sharedPreferences = mContext.getSharedPreferences(
				name, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	/**
	 * 获取整数值
	 * @param mContext
	 * @param name
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static int getInt(Context mContext, String name, String key,
			int defaultValue) {
		SharedPreferences sharedPreferences = mContext.getSharedPreferences(name,
				Context.MODE_PRIVATE);
		return sharedPreferences.getInt(key, defaultValue);
	}

	/**
	 * 保存长整数型值
	 * 
	 * @param mContext
	 * @param name
	 * @param key
	 * @param value
	 */
	public static void setLong(Context mContext, String name, String key,
			long value) {
		SharedPreferences sharedPreferences = mContext.getSharedPreferences(
				name, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putLong(key, value);
		editor.commit();
	}

	/**
	 * 获取长整型值
	 * @param mContext
	 * @param name
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static long getLong(Context mContext, String name, String key,
			long defaultValue) {
		SharedPreferences sharedPreferences = mContext.getSharedPreferences(
				name, Context.MODE_PRIVATE);
		return sharedPreferences.getLong(key, defaultValue);
	}

	/**
	 * 保存布尔类型值
	 * 
	 * @param mContext
	 * @param name
	 * @param key
	 * @param value
	 */
	public static void setBoolean(Context mContext, String name, String key,
			Boolean value) {
		SharedPreferences sharedPreferences = mContext.getSharedPreferences(
				name, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	/**
	 * 获取布尔类型值
	 * 
	 * @param mContext
	 * @param name
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static Boolean getBoolean(Context mContext, String name, String key,
			Boolean defaultValue) {
		SharedPreferences sharedPreferences = mContext
				.getSharedPreferences(name, Context.MODE_PRIVATE);
		return sharedPreferences.getBoolean(key, defaultValue);
	}

	/**
	 * 移除指定区域的键值
	 * @param mContext
	 * @param name
	 * @param key
	 */
	public static void removeString(Context mContext, String name, String key) {
		SharedPreferences sharedPreferences = mContext.getSharedPreferences(
				name, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.remove(key);
		editor.commit();
	}

	/**
	 * 移除SharedPreference文件
	 * @param mContext
	 * @param name
	 */
	public static void remove(Context mContext, String name) {
		SharedPreferences sharedPreferences = mContext.getSharedPreferences(
				name, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.clear();
		editor.commit();
	}

	/**
	 * 判断指定key是否存在
	 * @param mContext
	 * @param name
	 * @param key
	 * @return
	 */
	public static boolean isExist(Context mContext, String name, String key) {
		SharedPreferences sharedPreferences = mContext.getSharedPreferences(
				name, Context.MODE_PRIVATE);
		return sharedPreferences.contains(key);
	}

	/**
	 * 保存List型值
	 *
	 * @param context
	 * @param preferenceName
	 * @param listkey
	 * @param listSizeKey
	 */
	public static boolean setArray(Context context, String preferenceName, String listkey,String listSizeKey, LinkedList<String> valueList) {
		SharedPreferences sharedPreferences = context
				.getSharedPreferences(preferenceName,
						Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putInt(listSizeKey, valueList.size());

		for(int i=0;i<valueList.size();i++) {
			editor.remove(listkey + "_" + i);
			editor.putString(listkey + "_" + i, valueList.get(i));
		}

		return editor.commit();
	}

	/**
	 * 获取list型值
	 *
	 * @param context
	 * @param preferenceName
	 * @param listkey
	 * @param listSizeKey
	 */
	public static LinkedList<String> getArray(Context context, String preferenceName, String listkey,String listSizeKey) {
		LinkedList<String> valueList = new LinkedList<String>();
		SharedPreferences sharedPreferences = context
				.getSharedPreferences(preferenceName,
						Context.MODE_PRIVATE);
		valueList.clear();
		int size = sharedPreferences.getInt(listSizeKey, 0);
		for(int i=0;i<size;i++) {
			valueList.add(sharedPreferences.getString(listkey + "_" + i, null));
		}
		return valueList;
	}
}
