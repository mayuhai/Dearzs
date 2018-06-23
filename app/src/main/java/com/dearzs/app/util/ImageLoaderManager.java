package com.dearzs.app.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.widget.ImageView;

import com.dearzs.app.R;
import com.nostra13.universalimageloader.cache.disc.impl.ext.LruDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;

/**
 * 图片下载管理类
 * @version 1.0
 *          String imageUri = "http://site.com/image.png"; // from Web
 *          String imageUri = "file:///mnt/sdcard/image.png"; // from SD card
 *          String imageUri = "content://media/external/audio/albumart/13"; // from content provider
 *          String imageUri = "assets://image.png"; // from assets
 *          String imageUri = "drawable://" + R.drawable.image; // from drawables (only images, non-9patch)
 */
public class ImageLoaderManager {
    private static ImageLoaderManager mInstance;
    private Context mContext;

    private ImageLoaderManager(Context mContext) {
        this.mContext = mContext;
        initImageLoader(this.mContext);
    }

    public static ImageLoaderManager getInstance(Context mContext) {
        if (mInstance == null) {
            mInstance = new ImageLoaderManager(mContext);
        }
        return mInstance;
    }

    public static ImageLoaderManager getInstance() {
        if (mInstance == null) {
            throw new IllegalStateException(
                    "mInstance must be init in MainApplication");
        }
        return mInstance;
    }

    /**
     * 显示普通配置图片
     *
     * @param uri
     * @param imageView
     */
    public void displayImage(String uri, ImageView imageView) {
        if (!TextUtils.isEmpty(uri)) {
            displayImage(uri, imageView, (ImageLoadingListener) null);
        }
    }
    public void displayImage(String uri, ImageView imageView, ImageLoadingListener listener) {
        displayImage(uri, imageView, getImageLoaderCustomOptions(true), listener);
    }
    public void displayImage(String uri, ImageView imageView, DisplayImageOptions options) {
        displayImage(uri, imageView, options, null);
    }
    public void displayImage(String uri, ImageView imageView, int defaultImg) {
        displayImage(uri, imageView, defaultImg, defaultImg, defaultImg);
    }
    public void displayImage(String uri, ImageView imageView, int loadingImg, int emptyImg, int failImg) {
        displayImage(uri, imageView, getImageLoaderCustomOptions(loadingImg, emptyImg, failImg), null);
    }

    /**
     * 显示普通常用配置图片
     */
    public void displayImageNoCache(String uri, ImageView imageView) {
        displayImageNoCache(uri, imageView, -1, -1);

    }
    public void displayImageNoCache(String uri, ImageView imageView, int maxWidth, int maxHeight) {
        displayImageNoCache(uri, imageView, maxWidth, maxHeight, null);

    }
    public void displayImageNoCache(String uri, ImageView imageView, int maxWidth, int maxHeight, ImageLoadingListener listener) {
        ImageSize imageSize = null;
        if (maxWidth > 0 && maxHeight > 0) {
            imageSize = new ImageSize(maxWidth, maxHeight);
        }
        displayImage(uri, imageView, getImageLoaderCustomOptions(uri, maxWidth, maxHeight, false), imageSize, listener);
    }

    /**

     * 统一的加载图片方法

     * @param uri
     * @param imageView

     * @param options
     * @param listener

     */

    private void displayImage(String uri, ImageView imageView, DisplayImageOptions options, ImageLoadingListener listener) {
        displayImage(uri, imageView, options, null,listener);
    }
    private void displayImage(String uri, ImageView imageView, DisplayImageOptions options, ImageSize imageSize, ImageLoadingListener listener) {
        try {
            ImageLoader.getInstance().displayImage(uri, new ImageViewAware(imageView), options, imageSize, listener, null);
        } catch (Exception exception){
            ImageLoader.getInstance().clearMemoryCache();
        } catch (Error error) {
            ImageLoader.getInstance().clearMemoryCache();
        }
    }

    public void cleanMemoryCache(){
        ImageLoader.getInstance().clearMemoryCache();
    }

    /**
     * 初始化ImageLoader
     *
     * @param mContext
     */
    private void initImageLoader(Context mContext) {
        File cacheDir = new File(Constant.DEFAULT_CACHE_DIR);
        LruDiskCache lruDiskCache = null;
        try {
            lruDiskCache = new LruDiskCache(cacheDir, new HashCodeFileNameGenerator(), 50 * 1024 * 1024);
        } catch (Exception e) {
        }
        ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(
                mContext)
                .threadPoolSize(3)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .memoryCacheSize(15 * 1024 * 1024)
                //.memoryCacheSizePercentage(13)// default

                .denyCacheImageMultipleSizesInMemory()
                .imageDownloader(new BaseImageDownloader(mContext)); // default
                if (lruDiskCache != null) {
                    builder.diskCache(lruDiskCache);
                } else {
                    builder.diskCacheSize(50 * 1024 * 1024);
                }
        ImageLoader.getInstance().init(builder.build());
    }

    private DisplayImageOptions getImageLoaderCustomOptions(boolean hasCache) {
        return getImageLoaderCustomOptions(null, -1, -1, hasCache);
    }

    private DisplayImageOptions getImageLoaderCustomOptions(String filePath, int reqWidth, int reqHeight, boolean hasCache) {
        return getImageLoaderCustomOptions(filePath, reqWidth, reqHeight, hasCache, R.mipmap.ic_img_loading, R.mipmap.ic_img_empty, R.mipmap.ic_img_fail);
    }

    private DisplayImageOptions getImageLoaderCustomOptions(int loadingImg, int emptyImg, int failImg) {
        return getImageLoaderCustomOptions(null, -1, -1, loadingImg, emptyImg, failImg);
    }

    private DisplayImageOptions getImageLoaderCustomOptions(String filePath, int reqWidth, int reqHeight,
                                                            int loadingImg, int emptyImg, int failImg) {
        return getImageLoaderCustomOptions(filePath, reqWidth, reqHeight, true, loadingImg, emptyImg, failImg);
    }

    private DisplayImageOptions getImageLoaderCustomOptions(String filePath, int reqWidth, int reqHeight,
                                                            boolean hasCache, int loadingImg, int emptyImg, int failImg) {
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        if (!TextUtils.isEmpty(filePath) && filePath.startsWith("file://") && reqWidth != -1 && reqHeight != -1) {
            bitmapOptions = getBitmapFactoryOptions(filePath.substring("file://".length()), reqWidth, reqHeight);
        }
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(hasCache)   //设置图片不缓存于内存中
                .cacheOnDisk(hasCache)

                .bitmapConfig(Bitmap.Config.RGB_565)    //设置图片的质量
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)

                .showImageOnLoading(loadingImg)
                .showImageForEmptyUri(emptyImg)
                .showImageOnFail(failImg)
                .decodingOptions(bitmapOptions)
                .build();
        return options;
    }

    private BitmapFactory.Options getBitmapFactoryOptions(String filePath, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        return options;
    }

    private int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
}
