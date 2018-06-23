package com.dearzs.app.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.webkit.WebView;

import com.dearzs.commonlib.utils.AsyncInfoLoader;
import com.dearzs.commonlib.utils.log.LogUtil;
import com.dearzs.upload.uploadimage.utils.BitmapCompressManager;
import com.dearzs.upload.uploadimage.utils.ThreadPool;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 相册浏览器管理类
 */
public class AlbumBrowserManager {

    public final static int SCAN_OK = 1;
    private HashMap<String, List<String>> mPicMap;
    private Context mCtx;

    private static AlbumBrowserManager mInstance;

    private AlbumBrowserManager(Context mCtx) {
        this.mCtx = mCtx;
    }


    public static AlbumBrowserManager getInstance(Context mCtx) {
        if (mInstance == null) {
            mInstance = new AlbumBrowserManager(mCtx);
        }
        return mInstance;
    }

    public void getAlbumPicList(final Handler mHandler) {
        try {
            if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                return;
            }
            mPicMap = new HashMap<String, List<String>>();
            AsyncInfoLoader.getInstance().execute(new Runnable() {

                @Override
                public void run() {
                    Uri mUrl = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    ContentResolver mContentResolver = mCtx.getContentResolver();

                    //只查询jpeg和png的图片
                    Cursor mCursor = mContentResolver.query(mUrl, null,
                            MediaStore.Images.Media.MIME_TYPE + "=? or "
                                    + MediaStore.Images.Media.MIME_TYPE + "=? or "
                                    + MediaStore.Images.Media.MIME_TYPE + "=?",
                            new String[]{"image/jpeg", "image/jpg", "image/png"},
                            MediaStore.Images.Media.DATE_MODIFIED + " desc");

                    while (mCursor.moveToNext()) {
                        //获取图片的路径
                        String path = mCursor.getString(mCursor
                                .getColumnIndex(MediaStore.Images.Media.DATA));

                        //获取该图片的父路径名
                        String parentName = new File(path).getParentFile().getName();


                        //根据父路径名将图片放入到mGruopMap中
                        if (!mPicMap.containsKey(parentName)) {
                            List<String> chileList = new ArrayList<String>();
                            chileList.add(path);
                            mPicMap.put(parentName, chileList);
                        } else {
                            mPicMap.get(parentName).add(path);
                        }
                    }
                    mCursor.close();

                    //通知Handler扫描图片完成
                    Message msg = Message.obtain();
                    msg.obj = mPicMap;
                    msg.what = SCAN_OK;
                    mHandler.sendMessage(msg);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            //通知Handler扫描图片失败
            Message msg = Message.obtain();
            msg.obj = null;
            msg.what = SCAN_OK;
            mHandler.sendMessage(msg);
        }
    }

    /**
     * 保存到临时截屏目录，页面关闭时会清空
     */
    public void saveImageToGallery(WebView mWebView, final File file, final String fileName) {
        final Bitmap bmp = captureWebView(mWebView);
        ThreadPool.getInstence().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // 写入之后压缩大小
                    Bitmap newBitmap = BitmapCompressManager.getInstance().resizeImage(bmp);
                    bmp.recycle();

                    // 先写入到本地
                    FileOutputStream fos = new FileOutputStream(file);
                    newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.flush();
                    fos.close();
                    newBitmap.recycle();
                } catch (Exception e) {
                    LogUtil.LogD("share_style", e.toString());
                    return;
                }

                // 其次把文件插入到系统图库
                String tempPath = file.getAbsolutePath();
                try {
                    MediaStore.Images.Media.insertImage(mCtx.getContentResolver(),
                            tempPath, fileName, null);
                } catch (Exception e) {
                }
                tempPath = file.getPath();
                // 最后通知图库更新
                mCtx.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + tempPath)));
            }
        });
    }

    /**
     * 截取webView快照(webView加载的整个内容的大小)
     *
     * @return
     */
    private Bitmap captureWebView(WebView mWebView) {
        Picture snapShot = mWebView.capturePicture();
        final Bitmap bmp = Bitmap.createBitmap(snapShot.getWidth(),
                snapShot.getHeight(), Bitmap.Config.RGB_565);
        mWebView.draw(new Canvas(bmp));
        return bmp;
    }

}
