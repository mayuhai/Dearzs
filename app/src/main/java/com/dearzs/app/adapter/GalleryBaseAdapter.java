package com.dearzs.app.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dearzs.app.R;
import com.dearzs.app.adapter.basic.BaseAbstractAdpter;
import com.dearzs.app.util.AsyncIconLoader;
import com.dearzs.app.util.DisplayImageOptionsUtils;
import com.dearzs.app.util.Utils;
import com.dearzs.app.widget.ScaleImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import okhttp3.internal.Util;

public class GalleryBaseAdapter extends BaseAbstractAdpter {
    private static final int MWITH_PHOTO = 640;
    private static final int MHEIGHT_PHOTO = 480;
    private Context mContext;
    private String[] mList;
    private LayoutInflater mInflater = null;
    DisplayImageOptions options = DisplayImageOptionsUtils
            .getImageLoaderBuiderEmptyFail(R.mipmap.pic_null,
                    R.mipmap.pic_failure).build();
    protected AsyncIconLoader mAsyncIconLoader = new AsyncIconLoader(
            MWITH_PHOTO, MHEIGHT_PHOTO);
    ;

    public GalleryBaseAdapter(Context context, String[] list) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mList = list;
    }

    @Override
    public int getCount() {
        if (null != mList) {
            return mList.length;
        } else {
            return 0;
        }
    }

    @Override
    public String getItem(int position) {
        if (null != mList) {
            return mList[position];
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.gallary_adapter_image,
                    null);
            viewHolder = new ViewHolder();
            viewHolder.image = (ScaleImageView) convertView
                    .findViewById(R.id.gallery_image1);
            viewHolder.textView = (TextView) convertView
                    .findViewById(R.id.gallery_pos);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        int pos = position + 1;
        int length = mList.length;
        viewHolder.textView.setText(pos + "/" + length);

//		ImageLoaderManager.getInstance().displayImage(mList[position], viewHolder.image);
//		if (CarDetailActivity.DEF_IMAGE.equals(mList[position])) {
//			Resources res = mContext.getResources();
//			Bitmap bmp = BitmapFactory.decodeResource(res, R.mipmap.pic_null);
//			viewHolder.image.setBitmap(bmp);
//		} else {
//			if (!Utils.isNull(mList[position])
//					&& !mList[position].startsWith("http://")
//					&& !mList[position].startsWith("https://")) {
//				mAsyncIconLoader.loadImageBitmap(mContext, mList[position],
//						true, new AsyncIconLoader.ImageCallback() {
//
//							@Override
//							public void imageLoaded(Bitmap imageBitmap) {
//								viewHolder.image.setDraw(true);
//								if (imageBitmap != null) {
//									viewHolder.image.setBitmap(imageBitmap);
//								}
//							}
//						});
//			} else {
        ImageLoader.getInstance().displayImage(
                mList[position],
                viewHolder.image,
                DisplayImageOptionsUtils.getRoundedBuider(
                        R.mipmap.ic_img_loading, R.mipmap.pic_null,
                        R.mipmap.pic_failure).build(),
                new ImageLoadingListener() {

                    @Override
                    public void onLoadingStarted(String imageUri,
                                                 View view) {
                        Resources res = mContext.getResources();
                        Bitmap bmp = BitmapFactory.decodeResource(res,
                                R.mipmap.ic_img_loading);
                        viewHolder.image.setBitmap(bmp);
                    }

                    @Override
                    public void onLoadingFailed(String imageUri,
                                                View view, FailReason failReason) {
                        Resources res = mContext.getResources();
                        Bitmap bmp = BitmapFactory.decodeResource(res,
                                R.mipmap.pic_null);
                        viewHolder.image.setBitmap(bmp);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri,
                                                  View view, Bitmap loadedImage) {
                        viewHolder.image.setDraw(true);
                        if (loadedImage != null) {
                            viewHolder.image.setBitmap(loadedImage);
                        }
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri,
                                                   View view) {

                    }
                });
//			}
//		}
        return convertView;
    }

    class ViewHolder {
        ScaleImageView image;
        TextView textView;
    }
}
