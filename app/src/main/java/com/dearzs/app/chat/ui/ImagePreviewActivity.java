package com.dearzs.app.chat.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.dearzs.app.R;
import com.dearzs.app.base.BaseActivity;
import com.dearzs.app.widget.TitleBarView;
import com.dearzs.commonlib.utils.GetViewUtil;

import java.io.File;
import java.io.IOException;

/**
 * 图片预览页
 */
public class ImagePreviewActivity extends BaseActivity {
    public final static String PATH_KEY = "path";
    public final static String ISORI_KEY = "isOri";

    private String mPath;
    private CheckBox mCbOri;
    private ImageView mImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_image_preview);
        addLeftBtn(TitleBarView.TITLE_TYPE_LEFT_IV_BACK, null);
        addCenter(TitleBarView.TITLE_TYPE_CENTER_TXT, "图片预览");
        addRightBtn(TitleBarView.TITLE_TYPE_RIGHT_TXT, "发送");
    }

    @Override
    public void initData() {
        super.initData();
        mPath = getIntent().getStringExtra(PATH_KEY);
        mImage = GetViewUtil.getView(this, R.id.image);
        mCbOri = GetViewUtil.getView(this, R.id.isOri);
        showImage();
    }

    @Override
    public void onRightBtnClick() {
        super.onRightBtnClick();
        Intent intent = new Intent();
        intent.putExtra(PATH_KEY, mPath);
        intent.putExtra(ISORI_KEY, mCbOri.isChecked());
        setResult(RESULT_OK, intent);
        finish();
    }

    private void showImage() {
        if (TextUtils.isEmpty(mPath)) return;
        File file = new File(mPath);
        if (file.exists() && file.length() > 0) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(mPath, options);
            int reqWidth, reqHeight, width = options.outWidth, height = options.outHeight;
            if (width > height) {
                reqWidth = getWindowManager().getDefaultDisplay().getWidth();
                reqHeight = (reqWidth * height) / width;
            } else {
                reqHeight = getWindowManager().getDefaultDisplay().getHeight();
                reqWidth = (width * reqHeight) / height;
            }
            int inSampleSize = 1;
            if (height > reqHeight || width > reqWidth) {
                final int halfHeight = height / 2;
                final int halfWidth = width / 2;
                while ((halfHeight / inSampleSize) > reqHeight
                        && (halfWidth / inSampleSize) > reqWidth) {
                    inSampleSize *= 2;
                }
            }
            try {
                options.inSampleSize = inSampleSize;
                options.inJustDecodeBounds = false;
                float scaleX = (float) reqWidth / (float) (width / inSampleSize);
                float scaleY = (float) reqHeight / (float) (height / inSampleSize);
                Matrix mat = new Matrix();
                mat.postScale(scaleX, scaleY);
                Bitmap bitmap = BitmapFactory.decodeFile(mPath, options);
                ExifInterface ei = new ExifInterface(mPath);
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        mat.postRotate(90);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        mat.postRotate(180);
                        break;
                }
                mImage.setImageBitmap(Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mat, true));
            } catch (IOException e) {
                Toast.makeText(this, getString(R.string.chat_image_preview_load_err), Toast.LENGTH_SHORT).show();
            }
        } else {
            finish();
        }
    }


}
