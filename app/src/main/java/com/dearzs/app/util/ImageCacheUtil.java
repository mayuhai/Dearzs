package com.dearzs.app.util;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class ImageCacheUtil {
	
	/**
	 * 以640X480为图片容器，原图尺寸做如下处理：
若是16:9 以高度为准 缩放到480 宽度多出来的剪切 
若是9:16 以宽度为准 缩放到640 高度多出来的剪切
若是4:3 以宽度为准 缩放到640 高度多出来的剪切
若是3:4 以高度为准 缩放到480 宽度多出来的剪切
若图片不符合如上两种比例，仍以640X480为容器，宽高都未超过容器尺寸的，原图上传
若宽高都超出则或有一个超出则自动按640X480剪切图片.

	 */
	
	private  int M_WIDTH = 640;
	private  int M_HEIGH = 480;
	
	

	public ImageCacheUtil(int width, int height) {
		M_WIDTH = width;
		M_HEIGH = height;
	}

	/**
	 * 获取合适的Bitmap平时获取Bitmap就用这个方法吧.
	 * 
	 * @param path
	 *            路径.
	 * @param data
	 *            byte[]数组.
	 * @param context
	 *            上下文
	 * @param uri
	 *            uri
	 * @return
	 */
	public  Bitmap getResizedBitmap(String path, byte[] data, Context context, Uri uri) {
		Options options = null;
		float ssize = 1.0f;
		int isize = 1;
		int target = 0;
		Options info = new Options();
		// 这里设置true的时候，decode时候Bitmap返回的为空，
		// 将图片宽高读取放在Options里.
		info.inJustDecodeBounds = true;
		decode(path, data, context, uri, info);
		int width = info.outWidth;
		int heigh = info.outHeight;
		double s = 0;
		

		if ((width > M_WIDTH && heigh > M_HEIGH) || (width > M_HEIGH && heigh > M_WIDTH)) {
			s = (double) width / (double) heigh;
			if (round(s) == round((float)16/9)) {
				target = M_HEIGH;
				isize = sampleSize(heigh, target);
				ssize = ((float) target / (float) heigh);
			} 
			else if (round(s) ==round((float)9/16)){
				target = M_WIDTH;
				isize = sampleSize(width, target);
				ssize = ((float) target / (float) width);
			} 
			
			else if(round(s) == round((float)4/3)){
				target = M_WIDTH;
				isize = sampleSize(width, target);
				ssize = (float) target / (float) width;
			}
			else if(round(s) == round((float)3/4)){
				target = M_HEIGH;
				isize = sampleSize(heigh, target);
				ssize = (float) target / (float) heigh;
			}
			else{
				int size = width > heigh ? heigh : width ;
				int size2 = width > heigh ? width : heigh;
				float d = (float)size2/(float)size;
				if(d>=1.5){
					target = M_HEIGH;
				}else{
					target = M_WIDTH;
				}
				isize = sampleSize(size, target);
				ssize = (float) target / (float) size;
			}
		}

		options = new Options();
		options.inSampleSize = isize;
		options.inJustDecodeBounds = false;

		Bitmap bm = null;
		try {
			Bitmap tbm = decode(path, data, context, uri, options);
			// 已缩小的倍数
			if(isize>1){
				if(ssize * isize!=1){
					tbm = scaledDrawable(tbm, ssize * isize);
				}
			}
			bm = cutBitmap(tbm);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bm;
	}
	
	private double round(double value){
	    return Math.round(value*100)/100.0;
	}


	/**
	 * 绘制固定尺寸的图片
	 * 
	 * @param micon
	 * @return
	 */
	private  Bitmap cutBitmap(Bitmap micon) {
		int w = M_WIDTH;
		int h = M_HEIGH;
		int mwidth = micon.getWidth();
		int mheight = micon.getHeight();
		//宽够,高度不够
		if(mwidth>=M_WIDTH && mheight<M_HEIGH){
			h = mheight;
		}
		//宽不够,高度高
		else if(mwidth<M_WIDTH && mheight>=M_HEIGH){
			w = mwidth;
		}
		//都不够,不处理
		else if(mwidth<M_WIDTH && mheight<M_HEIGH){
			return micon;
		}  
		else if(mwidth==M_WIDTH && mheight==M_HEIGH){
			return micon;
		}  
		else{
			//都大于等于,默认M_WIDTH*440 
		}

		Bitmap newb = null;
		try {
			newb = Bitmap.createBitmap(w, h, Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
			Canvas cv = new Canvas(newb);
			cv.drawBitmap(micon, 0, 0, null);
			micon.recycle();
			cv.save(Canvas.ALL_SAVE_FLAG);// 保存
			cv.restore();// 存储
		} catch (Exception e) {
			e.printStackTrace();
		}
		return newb;
	}

	/**
	 * 缩放图片
	 * 
	 * @param dest
	 * @param scale
	 * @return
	 */
	private  Bitmap scaledDrawable(Bitmap dest, float scale) {
		int width = dest.getWidth();
		int height = dest.getHeight();
		Bitmap endImage = Bitmap.createScaledBitmap(dest, (int) (width * scale), (int) (height * scale), true);
		dest.recycle();
		return endImage;
	}

	/**
	 * 解析Bitmap的公用方法.
	 * 
	 * @param path
	 * @param data
	 * @param context
	 * @param uri
	 * @param options
	 * @return
	 */
	private   Bitmap decode(String path, byte[] data, Context context, Uri uri, Options options) {

		Bitmap result = null;
		if (path != null) {
			result = BitmapFactory.decodeFile(path, options);
		} else if (data != null) {
			result = BitmapFactory.decodeByteArray(data, 0, data.length, options);
		} else if (uri != null) {
			// uri不为空的时候context也不要为空.
			ContentResolver cr = context.getContentResolver();
			InputStream inputStream = null;
			try {
				inputStream = cr.openInputStream(uri);
				result = BitmapFactory.decodeStream(inputStream, null, options);
				inputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 获取合适的sampleSize. 这里就简单实现都是2的倍数啦.
	 * 
	 * @param width
	 * @param target
	 * @return
	 */
	private   int sampleSize(int width, int target) {
		int result = 1;
		for (int i = 0; i < 10; i++) {
			if (width < target * 2) {
				break;
			}
			width = width / 2;
			result = result * 2;
		}
		return result;
	}

	public   byte[] bitmapToBytes(Bitmap bitmap) {
		if (bitmap == null) {
			return null;
		}
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
		return os.toByteArray();
	}

	

}
