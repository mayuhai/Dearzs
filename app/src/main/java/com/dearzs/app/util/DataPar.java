package com.dearzs.app.util;

import android.content.Context;

import com.dearzs.app.entity.EntityBase;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class DataPar {

	private static Gson mGson;

	public EntityBase parData(String content, Class<? extends EntityBase> clazz) {
		EntityBase result = null;
		try {
			if (mGson == null) {
				mGson = new Gson();
			}
			result = mGson.fromJson(content, clazz);
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return result == null ? new EntityBase() : result;
	}

	/**
	 * 用于获取assets/fakeMsg文件夹中的模拟报文，用于测试
	 * 
	 * @author 张海龙
	 * @param fileName
	 *            文件名
	 * @param context
	 *            上下文
	 * @return String
	 */
	public String getFromAssetsForTest(String fileName, Context context) {

		InputStreamReader inputReader = null;
		BufferedReader bufReader = null;
		String result = "";
		try {
			inputReader = new InputStreamReader(context.getResources()
					.getAssets().open("fakeMsg/" + fileName));
			bufReader = new BufferedReader(inputReader);
			String line = "";

			while ((line = bufReader.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (bufReader != null) {
					bufReader.close();
					bufReader = null;
				}
				if (inputReader != null) {
					inputReader.close();
					inputReader = null;
				}
			} catch (Exception e) {
			}
		}
		return result;
	}
}
