package com.dearzs.upload.uploadimage.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.dearzs.upload.R;
import com.dearzs.upload.uploadimage.listener.ImageUploadProgressListener;
import com.dearzs.upload.uploadimage.listener.ImageUploadRetryListener;
import com.dearzs.upload.uploadimage.listener.impl.IUploadViewDisplay;
import com.dearzs.upload.uploadimage.utils.BitmapCompressManager;
import com.dearzs.upload.uploadimage.utils.ThreadPool;
import com.dearzs.upload.uploadimage.utils.UploadConstant;

/**
 * 待上传的视图, 与model进行脱离
 * 只负责动态的显示model当前的状态
 */
public class SimpleUploadView extends FrameLayout implements IUploadViewDisplay {

    /**
     * 上传进度提示视图
     */
    private TextView progressView;
    /**
     * 用于显示上传的图片
     */
    private ImageView progressImage;

    private Bitmap progressBitmap;

    /**
     * 用于重试的回调
     */
    private ImageUploadRetryListener retryListener;

    public SimpleUploadView(Context context) {
        this(context, null);
    }

    public SimpleUploadView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.simple_upload_layout, this);

        progressImage = (ImageView) findViewById(R.id.upload_img);

        progressView = (TextView) findViewById(R.id.upload_tip);
        progressView.setVisibility(GONE);
        progressView.setTag(null);

        if (attrs == null) return;

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.UploadView);

        float textSize = typedArray.getDimension(R.styleable.UploadView_textSize_px, 10);
        progressView.setTextSize(textSize);

        int defaultSrc = typedArray.getResourceId(R.styleable.UploadView_default_src, -1);
        if (defaultSrc != -1) {
            progressImage.setImageResource(defaultSrc);
        }

        typedArray.recycle();
    }

    @Override
    public void doResult(final int uploadResult, final Object resultMessage) {
        if (uploadResult == UploadConstant.RESULT_SUCCESS) {
            // 多次设置成功也无用
            if (progressView.getVisibility() == View.GONE) {
                return;
            }
            post(new Runnable() {
                @Override
                public void run() {
                    progressView.setVisibility(View.VISIBLE);
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
        } else if (uploadResult == UploadConstant.RESULT_FAILED) {
            post(new Runnable() {
                @Override
                public void run() {
                    progressView.setVisibility(View.VISIBLE);
                    progressView.setTag(uploadResult);
                    progressView.setText(resultMessage == null ?
                            UploadConstant.UPLOAD_PROGRESS_TIP_FAILED : resultMessage.toString());
                }
            });
        }
    }

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

    /**
     * 获取用于显示图片的实例
     *
     * @return
     */
    public ImageView getProgressImage() {
        return progressImage;
    }

    /**
     * 显示具体路径的图片
     *
     * @param filePath
     */
    public void displayImage(final String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return;
        }
        // 通过异步线程获取Bitmap对象
        ThreadPool.getInstence().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final Bitmap tempBitmap = BitmapCompressManager.getInstance().
                            decodeThumbBitmapForFile(filePath, getWidth(), getHeight());
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
            SimpleUploadView.this.doResult(uploadResult, resultMessage);
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
