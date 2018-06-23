package com.dearzs.app.util;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions.Builder;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

public class DisplayImageOptionsUtils {

	public static Builder getImageLoaderBuider() {
		Builder builder = new Builder()
				.imageScaleType(ImageScaleType.NONE)
				.bitmapConfig(Bitmap.Config.RGB_565).cacheOnDisk(true)
				.cacheInMemory(true).displayer(new SimpleBitmapDisplayer());
		return builder;
	}

	public static Builder getImageLoaderBuider(int cornerRadiusPixels) {
		Builder builder = new Builder()
				.imageScaleType(ImageScaleType.NONE)
				.bitmapConfig(Bitmap.Config.RGB_565).cacheOnDisk(true)
				.cacheInMemory(true).displayer(new RoundedBitmapDisplayer(cornerRadiusPixels));
		return builder;
	}

	public static Builder getImageLoaderBuiderLoading(int loading) {
		Builder builder = getImageLoaderBuider();
		builder.showImageOnLoading(loading);
		return builder;
	}

	public static Builder getImageLoaderBuiderEmpty(int empty) {
		Builder builder = getImageLoaderBuider();
		builder.showImageForEmptyUri(empty);
		return builder;
	}

	public static Builder getImageLoaderBuider(int loading, int empty) {
		Builder builder = getImageLoaderBuider();
		builder.showImageOnLoading(loading).showImageForEmptyUri(empty);
		return builder;
	}

	public static Builder getImageLoaderBuiderEmptyFail(int empty, int fail) {
		Builder builder = new Builder()
				.showImageForEmptyUri(empty).showImageOnFail(fail);
		return builder;
	}

	public static Builder getImageLoaderBuider(int loading, int empty, int fail) {
		Builder builder = getImageLoaderBuider();
		builder.showImageOnLoading(loading).showImageForEmptyUri(empty)
				.showImageOnFail(fail);
		return builder;
	}

	public static Builder getRoundedBuider() {
		Builder builder = new Builder()
				.cacheInMemory(true).cacheOnDisk(true)
				.imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.displayer(new RoundedBitmapDisplayer(8));
		return builder;
	}

	public static Builder getRoundedBuider(int loading, int empty, int fail) {
		Builder builder = getRoundedBuider();
		builder.showImageOnLoading(loading).showImageForEmptyUri(empty)
				.showImageOnFail(fail);
		return builder;
	}
}
