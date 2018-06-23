package com.dearzs.app.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AsyncIconLoader {
	private ImageCacheUtil mImageCacheUtil;

	private ThreadPoolExecutor executor;

	private HashMap<String, SoftReference<Bitmap>> imageCache;

	public AsyncIconLoader(int width, int height) {
		imageCache = new HashMap<String, SoftReference<Bitmap>>();
		mImageCacheUtil = new ImageCacheUtil(width, height);
		LinkedBlockingQueue queue = new LinkedBlockingQueue();
		executor = new ThreadPoolExecutor(1, 5, 180, TimeUnit.SECONDS, queue);
	}

	public Bitmap loadImageBitmap(final Context context, final String url, boolean isRemove,
			final ImageCallback imageCallback) {

		if (Utils.isNull(url))
			return null;
		if (imageCache.containsKey(url)) {
			if (isRemove) {
				imageCache.remove(url);
			} else {
				SoftReference<Bitmap> softReference = imageCache.get(url);
				Bitmap bicon = softReference.get();
				if (bicon != null) {
					return bicon;
				} else {
					imageCache.remove(url);
				}
			}
		}

		final Handler handler = new Handler() {
			public void handleMessage(Message message) {
				imageCallback.imageLoaded((Bitmap) message.obj);
			}
		};

		executor.execute(new Runnable() {
			@Override
			public void run() {
				Bitmap bitmap = mImageCacheUtil.getResizedBitmap(url, null, null, null);
				if (bitmap != null) {
					imageCache.put(url, new SoftReference<Bitmap>(bitmap));
				}
				Message message = handler.obtainMessage(0, bitmap);
				handler.sendMessage(message);
			}
		});

		return null;
	}

	public interface ImageCallback {
		public void imageLoaded(Bitmap imageBitmap);
	}

	public void recyled() {
		if (imageCache != null && !imageCache.isEmpty()) {
			for (Map.Entry<String, SoftReference<Bitmap>> entry : imageCache.entrySet()) {
				SoftReference<Bitmap> bitmaps = entry.getValue();
				Bitmap bitmap = bitmaps.get();
				if (bitmap != null && !bitmap.isRecycled()) {
					bitmap.recycle();
					bitmap = null;
				}
			}
		}
	}
}
