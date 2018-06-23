package com.dearzs.app.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dearzs.app.R;
import com.dearzs.app.base.IPicDelListener;
import com.dearzs.app.entity.EntityNetPic;
import com.dearzs.app.util.ImageLoaderManager;
import com.dearzs.upload.uploadimage.listener.ImageUploadProgressListener;
import com.dearzs.upload.uploadimage.listener.ImageUploadRetryListener;
import com.dearzs.upload.uploadimage.listener.impl.IUploadViewDisplay;
import com.dearzs.upload.uploadimage.utils.BitmapCompressManager;
import com.dearzs.upload.uploadimage.utils.ThreadPool;
import com.dearzs.upload.uploadimage.utils.UploadConstant;

/**
 * 行驶证，检测报告等上传视图
 *
 * @author zhaoyb
 */
public class UploadVehiclePicView extends LinearLayout
        implements IUploadViewDisplay, View.OnClickListener {

    private RelativeLayout mIvCarparent;
    // 待显示的图片
    private ImageView progressImage;
    private Bitmap progressBitmap;

    // 图片删除按钮
    private ImageView mIvDel;

    // 上传失败提示
    private TextView mTvProgressError;
    // 上传进度提示语
    private TextView progressView;
    // 单元格底部提示语
    private TextView mTvTip;

    /**
     * 每一个view都对应一个实体类(可能来源于网络,也可能是本地图片)
     */
    private EntityNetPic mEntityPic;
    /**
     * 用于重试的回调
     */
    private ImageUploadRetryListener retryListener;

    public UploadVehiclePicView(Context context) {
        this(context, null);
    }

    public UploadVehiclePicView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.item_gv_insure_car_voucher, this);

        // 图片容器
        mIvCarparent = (RelativeLayout) findViewById(R.id.item_car_voucher_layout_pic);
        // 待显示图片
        progressImage = (ImageView) findViewById(R.id.item_car_voucher_iv_pic);

        // 右上角删除按钮
        mIvDel = (ImageView) findViewById(R.id.item_car_voucher_iv_close);
        mIvDel.setVisibility(View.GONE);
        mIvDel.setOnClickListener(this);

        mTvProgressError = (TextView) findViewById(R.id.item_car_voucher_tv_fail_tip);
        mTvProgressError.setVisibility(View.GONE);
        progressView = (TextView) findViewById(R.id.item_car_voucher_tv_ing_tip);
        progressView.setVisibility(View.GONE);

        mTvTip = (TextView) findViewById(R.id.item_car_voucher_tv_desc);
        mTvTip.setVisibility(View.INVISIBLE);
    }

    public void setIvCarparentSize(int itemWidth) {
        mIvCarparent.setLayoutParams(new LayoutParams(itemWidth, itemWidth));
    }

    /**
     * 设置图片
     *
     * @param mEntityPic
     */
    public void updateEntityPic(EntityNetPic mEntityPic) {
        if (mEntityPic == null) {
            return;
        }
        this.mEntityPic = mEntityPic;
        if (TextUtils.isEmpty(mEntityPic.getDefaultTip())) {
            mTvTip.setVisibility(View.INVISIBLE);
        } else {
            mTvTip.setText(mEntityPic.getDefaultTip());
            mTvTip.setVisibility(View.VISIBLE);
        }

        String picPath = mEntityPic.getUrl();
        // 为空时,删除当前图片,重新调整为默认图
        if (TextUtils.isEmpty(picPath)) {
            // 显示默认图
            progressImage.setImageResource(R.mipmap.ic_car_insure);
            // 隐藏删除按钮
            mIvDel.setVisibility(View.GONE);
            if (progressBitmap != null && !progressBitmap.isRecycled()) {
                progressBitmap.recycle();
                progressBitmap = null;
            }
            return;
        }
        mEntityPic.bindUploadProgress(this);

        // 图片显示,使用tag解决图片刷新问题
        if (picPath.startsWith("http")) {
            //if (!picPath.equals(progressImage.getTag())) {
            //progressImage.setTag(picPath);
            ImageLoaderManager.getInstance().displayImageNoCache(picPath, progressImage);
            //}
        } else {
            displayImage(picPath);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.item_car_voucher_iv_close) {
            progressImage.setTag(null);
            // 显示默认图
            progressImage.setImageResource(R.mipmap.ic_car_insure);
            // 隐藏删除按钮
            mIvDel.setVisibility(View.GONE);
            // 重新调整对列
            if (mDelListener != null) {
                mDelListener.onPicDel(mEntityPic);
            }
        }
    }

    private IPicDelListener mDelListener;

    public void setDelListener(IPicDelListener mDelListener) {
        this.mDelListener = mDelListener;
    }

    /**
     * 显示具体路径的图片
     *
     * @param filePath
     */
    private void displayImage(final String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return;
        }
        // 若是同一个地址，直接忽略
        //if (filePath.equals(progressImage.getTag())) {
        //return;
        //}

        //progressImage.setTag(filePath);
        // 通过异步线程获取Bitmap对象
        ThreadPool.getInstence().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String newFilePath = BitmapCompressManager.getInstance().copyFileUsingFileChannels(filePath);
                    // 压缩图片
                    try {
                        BitmapCompressManager.getInstance().compressImage(newFilePath);
                    } catch (Exception e) {
                        newFilePath = null;
                    } catch (Error error) {
                        newFilePath = null;
                    }
                    final Bitmap tempBitmap = BitmapCompressManager.getInstance().
                            decodeThumbBitmapForFile(newFilePath, getWidth(), getHeight());
                    post(new Runnable() {
                        @Override
                        public void run() {
                            if (progressBitmap != null && !progressBitmap.isRecycled()) {
                                progressBitmap.recycle();
                                progressBitmap = null;
                            }
                            progressBitmap = tempBitmap;
                            progressImage.setImageBitmap(progressBitmap);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mIvDel.setVisibility(View.VISIBLE);
    }

    /**
     * ============= 以下是接口所必须实现的=================
     */
    @Override
    public void setOnRetryListener(ImageUploadRetryListener retryListener) {
        this.retryListener = retryListener;
    }

    @Override
    public void recyleView() {
        if (progressBitmap == null) return;
        if (progressBitmap.isRecycled()) return;
        progressBitmap.recycle();
        progressBitmap = null;
    }

    @Override
    public boolean callOnClick() {
        if (progressView.getTag() != null) {
            // 若有回调，告诉观察者
            if (retryListener != null) {
                retryListener.onRetryListener();
            }
            return false;
        }
        return super.callOnClick();
    }

    @Override
    public void doResult(final int uploadResult, final Object resultMessage) {
        if (uploadResult == UploadConstant.RESULT_SUCCESS) {
            post(new Runnable() {
                @Override
                public void run() {
                    progressView.setText(resultMessage == null ?
                            UploadConstant.UPLOAD_PROGRESS_TIP_SUCCESS : resultMessage.toString());
                }
            });
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressView.setVisibility(GONE);
                }
            }, 1000);
        } else {
            post(new Runnable() {
                @Override
                public void run() {
                    progressView.setTag(uploadResult);
                    progressView.setText(resultMessage == null ? UploadConstant.UPLOAD_PROGRESS_TIP_FAILED : resultMessage.toString());
                }
            });
        }
    }

    @Override
    public void initUploadData(String tip, String filePath) {
        imageUploadProgressListener.initUploadData(tip, filePath);
    }

    @Override
    public void onProgress(long bytesWrite, long contentLength) {
        imageUploadProgressListener.onProgress(bytesWrite, contentLength);
    }

    /**
     * 视图上传进度回调
     */
    private final ImageUploadProgressListener imageUploadProgressListener = new ImageUploadProgressListener() {

        @Override
        public void doResult(int uploadResult, Object resultMessage) {
            UploadVehiclePicView.this.doResult(uploadResult, resultMessage);
        }

        @Override
        public void onUIInit(String tip, String filePath) {
            progressView.setTag(null);
            progressView.setVisibility(VISIBLE);
            if (TextUtils.isEmpty(tip)) {
                tip = UploadConstant.UPLOAD_PROGRESS_TIP_START;
            }
            progressView.setText(tip);
            displayImage(filePath);
        }

        @Override
        public void onUIFinish() {
            progressView.setVisibility(VISIBLE);
            progressView.setText(UploadConstant.UPLOAD_PROGRESS_TIP_FINISH);
        }

        @Override
        public void onUIProgress(String percentStr) {
            progressView.setVisibility(VISIBLE);
            progressView.setText(String.format(UploadConstant.UPLOAD_PROGRESS_TIP_ING, percentStr));
        }
    };
}
