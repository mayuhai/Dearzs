package com.dearzs.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import java.util.UUID;

/**
 * 设备唯一标识工厂类
 */
public class DeviceUuidFactory {

	protected static final String PREFS_FILE = "device_id.xml";
	protected static final String PREFS_DEVICE_ID = "device_id";
	protected static UUID uuid;

	public static String getClientID(Context context) {
		// 获取imei号
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = telephonyManager.getDeviceId();
		if (!TextUtils.isEmpty(imei)) {
			return imei;
		}

		// 获取mac地址
		WifiManager wifi = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		String mac = wifi.getConnectionInfo().getMacAddress();
		if (!TextUtils.isEmpty(mac)) {
			return mac;
		}

		// 获取uuid
		if (uuid == null) {
			new DeviceUuidFactory(context);
		}

		return uuid.toString();
	}

	public DeviceUuidFactory(Context context) {
		if (uuid == null) {
			synchronized (DeviceUuidFactory.class) {
				if (uuid == null) {
					final SharedPreferences prefs = context
							.getSharedPreferences(PREFS_FILE, 0);
					final String id = prefs.getString(PREFS_DEVICE_ID, null);

					if (id != null) {
						// Use the ids previously computed and stored in the
						// prefs file
						uuid = UUID.fromString(id);

					} else {

						final String androidId = Secure
								.getString(context.getContentResolver(),
										Secure.ANDROID_ID);

						// Use the Android ID unless it's broken, in which case
						// fallback on deviceId,
						// unless it's not available, then fallback on a random
						// number which we store
						// to a prefs file
						try {
							if (!"9774d56d682e549c".equals(androidId)) {
								uuid = UUID.nameUUIDFromBytes(androidId
										.getBytes("utf8"));
							} else {
								final String deviceId = ((TelephonyManager) context
										.getSystemService(Context.TELEPHONY_SERVICE))
										.getDeviceId();
								uuid = deviceId != null ? UUID
										.nameUUIDFromBytes(deviceId
												.getBytes("utf8")) : UUID
										.randomUUID();
							}
						} catch (Exception e) {
							Log.d("DeviceUuidFactory", "RuntimeException");
						}

						// Write the value out to the prefs file
						prefs.edit()
								.putString(PREFS_DEVICE_ID, uuid.toString())
								.commit();

					}
				}
			}
		}
	}
}