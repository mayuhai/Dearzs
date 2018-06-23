package com.dearzs.commonlib.utils;

public class URLEncoder {

	public static String urlEncoder(char param) {
		try {
			return java.net.URLEncoder.encode(param + "", "UTF-8");
		} catch (Exception e) {
			return String.valueOf(param);
		}
	}

	public static String urlEncoder(String param) {
		try {
			return java.net.URLEncoder.encode(param.trim(), "UTF-8");
		} catch (Exception e) {
			return param;
		}
	}

	public static String urlEncoder(String param, String coding) {
		try {
			return java.net.URLEncoder.encode(param.trim(), coding);
		} catch (Exception e) {
			return param;
		}
	}

	public static String urlDecoder(char param) {
		try {
			return java.net.URLDecoder.decode(param + "", "UTF-8");
		} catch (Exception e) {
			return String.valueOf(param);
		}
	}

	public static String urlDecoder(String param) {
		try {
			return java.net.URLDecoder.decode(param.trim(), "UTF-8");
		} catch (Exception e) {
			return param;
		}
	}
}
