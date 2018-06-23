package com.dearzs.app.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.ImageView;

public class ScaleImageView extends ImageView {
	// This matrix is recomputed when we go from the thumbnail image to
	// the full size image.
	protected Matrix mBaseMatrix = new Matrix();

	private static final String TAG = "ScaleImageView";

	// This is the supplementary transformation which reflects what
	// the user has done in terms of zooming and panning.
	//
	// This matrix remains the same when we go from the thumbnail image
	// to the full size image.
	protected Matrix mSuppMatrix = new Matrix();
	private final Matrix mDisplayMatrix = new Matrix();

	// Temporary buffer used for getting the values out of a matrix.
	private final float[] mMatrixValues = new float[9];

	protected Bitmap mImage = null;

	int mThisWidth = -1, mThisHeight = -1;

	float mMaxZoom = 4.0f;// 最大缩放比例
	float mMinZoom;// 最小缩放比例

	private int mImageWidth;// 图片的原始宽度
	private int mImageHeight;// 图片的原始高度

	private float mScaleRate;// 图片适应屏幕的缩放比例
	public int mScreenWidth;
	public int mScreenHeight;

	private boolean draw = false;

	public ScaleImageView(Context context, AttributeSet set) {
		super(context, set);
		Activity tmpActivity = (Activity) context;
		mScreenWidth = tmpActivity.getWindowManager().getDefaultDisplay()
				.getWidth();
		mScreenHeight = tmpActivity.getWindowManager().getDefaultDisplay()
				.getHeight();
		this.mImageHeight = 0;
		this.mImageWidth = 0;
		init();
	}

	public void setDraw(boolean draw) {
		this.draw = draw;
	}

	public ScaleImageView(Context context) {
		super(context);
		Activity tmpActivity = (Activity) context;
		mScreenWidth = tmpActivity.getWindowManager().getDefaultDisplay()
				.getWidth();
		mScreenHeight = tmpActivity.getWindowManager().getDefaultDisplay()
				.getHeight();
		this.mImageHeight = 0;
		this.mImageWidth = 0;
		init();
	}

	public ScaleImageView(Context context, AttributeSet attrs, int imageWidth,
						  int imageHeight) {
		super(context, attrs);
		this.mImageHeight = imageHeight;
		this.mImageWidth = imageWidth;
		init();
	}

	/**
	 * 计算图片要适应屏幕需要缩放的比例
	 */
	private void arithScaleRate() {
		float scaleWidth = mScreenWidth / (float) mImageWidth;
		float scaleHeight = mScreenHeight / (float) mImageHeight;
		mScaleRate = Math.min(scaleWidth, scaleHeight);
	}

	public float getScaleRate() {
		return mScaleRate;
	}

	public int getImageWidth() {
		return mImageWidth;
	}

	public void setImageWidth(int imageWidth) {
		this.mImageWidth = imageWidth;
	}

	public int getImageHeight() {
		return mImageHeight;
	}

	public void setImageHeight(int imageHeight) {
		this.mImageHeight = imageHeight;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			event.startTracking();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.isTracking()
				&& !event.isCanceled()) {
			if (getScale() > 1.0f) {
				// If we're zoomed in, pressing Back jumps out to show the
				// entire image, otherwise Back returns the user to the gallery.
				zoomTo(1.0f);
				return true;
			}
		}
		return super.onKeyUp(keyCode, event);
	}

	public Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SET_IMAGE:
				setBitmap((Bitmap) msg.obj);
				break;
			}
		}
	};

	public void setRefresh() {
		this.zoomTo(getScaleRate(), mScreenWidth / 2, mScreenHeight / 2, 200f);
	}

	public static final int SET_IMAGE = 1000;

	public void setBitmap(Bitmap bitmap) {
		if (bitmap != null) {
			draw = true;
			init();
			Drawable drawable = this.getDrawable();
			super.setImageBitmap(bitmap);
			if (drawable != null && drawable instanceof BitmapDrawable) {
				BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
				if (bitmapDrawable != null
						&& bitmapDrawable.getBitmap() != null) {
					bitmapDrawable.getBitmap().recycle();
				}
			}
			if (mImage != null) {
				try {
					mImage.recycle();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			mImage = bitmap;
			this.mImageHeight = mImage.getHeight();
			this.mImageWidth = mImage.getWidth();
			arithScaleRate();
			layoutToCenter();
			zoomTo(mScaleRate, mScreenWidth / 2f, mScreenHeight / 2f);
		}
	}

	protected void center(boolean horizontal, boolean vertical) {
		if (mImage == null) {
			Drawable drawable = this.getDrawable();
			if (drawable != null) {
				Matrix m = getImageViewMatrix();
				RectF rect = new RectF(0, 0, drawable.getIntrinsicWidth(),
						drawable.getIntrinsicHeight());
				m.mapRect(rect);
				float height = rect.height();
				float width = rect.width();
				float deltaX = 0, deltaY = 0;

				if (vertical) {
					int viewHeight = getHeight();
					if (height < viewHeight) {
						deltaY = (viewHeight - height) / 2 - rect.top;
					} else if (rect.top > 0) {
						deltaY = -rect.top;
					} else if (rect.bottom < viewHeight) {
						deltaY = getHeight() - rect.bottom;
					}
				}

				if (horizontal) {
					int viewWidth = getWidth();
					if (width < viewWidth) {
						deltaX = (viewWidth - width) / 2 - rect.left;
					} else if (rect.left > 0) {
						deltaX = -rect.left;
					} else if (rect.right < viewWidth) {
						deltaX = viewWidth - rect.right;
					}
				}
				postTranslate(deltaX, deltaY);
				setImageMatrix(getImageViewMatrix());
			}
			return;
		} else {
			Matrix m = getImageViewMatrix();
			RectF rect = new RectF(0, 0, mImage.getWidth(), mImage.getHeight());
			// RectF rect = new RectF(0, 0, imageWidth*getScale(),
			// imageHeight*getScale());

			m.mapRect(rect);

			float height = rect.height();
			float width = rect.width();

			/*
			 * Logger.v(TAG, "mImage.getWidth() = " + mImage.getWidth());
			 * Logger.v(TAG, "width = " + rect.width()); Logger.v(TAG,
			 * "getWidth() = " + getWidth());
			 */

			float deltaX = 0, deltaY = 0;

			if (vertical) {
				int viewHeight = getHeight();
				if (height < viewHeight) {
					deltaY = (viewHeight - height) / 2 - rect.top;
				} else if (rect.top > 0) {
					deltaY = -rect.top;
				} else if (rect.bottom < viewHeight) {
					deltaY = getHeight() - rect.bottom;
				}
			}

			/*
			 * Logger.v(TAG, "rect.right = " + rect.right); Logger.v(TAG,
			 * "rect.left = " + rect.left);
			 */
			if (horizontal) {
				int viewWidth = getWidth();
				if (width < viewWidth) {
					deltaX = (viewWidth - width) / 2 - rect.left;
				} else if (rect.left > 0) {
					deltaX = -rect.left;
				} else if (rect.right < viewWidth) {
					deltaX = viewWidth - rect.right;
				}
			} else {
				/*
				 * if (rect.left > 0) { deltaX = -rect.left; }
				 */
				// Logger.v(TAG, "deltaX = " + deltaX);
			}

			// Logger.v(TAG, "deltaY = " + deltaY);
			postTranslate(deltaX, deltaY);
			setImageMatrix(getImageViewMatrix());
		}

	}

	private void init() {
		// Gallery.LayoutParams params = new
		// Gallery.LayoutParams(Gallery.LayoutParams.FILL_PARENT,
		// Gallery.LayoutParams.FILL_PARENT);
		// setLayoutParams(params);
		setScaleType(ScaleType.MATRIX);
	}

	/**
	 * 设置图片居中显示
	 */
	public void layoutToCenter() {
		// 正在显示的图片实际宽高
		float width = mImageWidth * getScale();
		float height = mImageHeight * getScale();

		// 空白区域宽高
		float fill_width = mScreenWidth - width;
		float fill_height = mScreenHeight - height;
		// 需要移动的距离
		float tran_width = 0f;
		float tran_height = 0f;

		if (fill_width > 0)
			tran_width = fill_width / 2;
		if (fill_height > 0)
			tran_height = fill_height / 2;

		postTranslate(tran_width, tran_height);
		setImageMatrix(getImageViewMatrix());
	}

	protected float getValue(Matrix matrix, int whichValue) {
		matrix.getValues(mMatrixValues);
		// mMinZoom = (Main.screenWidth / 2f) / imageWidth;
		mMinZoom = (mScreenWidth / 1.01f) / mImageWidth;
		return mMatrixValues[whichValue];
	}

	// Get the scale factor out of the matrix.
	protected float getScale(Matrix matrix) {
		return getValue(matrix, Matrix.MSCALE_X);
	}

	protected float getScale() {
		return getScale(mSuppMatrix);
	}

	// Combine the base matrix and the supp matrix to make the final matrix.
	protected Matrix getImageViewMatrix() {
		// The final matrix is computed as the concatentation of the base matrix
		// and the supplementary matrix.
		mDisplayMatrix.set(mBaseMatrix);
		mDisplayMatrix.postConcat(mSuppMatrix);
		return mDisplayMatrix;
	}

	static final float SCALE_RATE = 1.25F;

	// Sets the maximum zoom, which is a scale relative to the base matrix. It
	// is calculated to show the image at 400% zoom regardless of screen or
	// image orientation. If in the future we decode the full 3 megapixel image,
	// rather than the current 1024x768, this should be changed down to 200%.
	protected float maxZoom() {
		float max = 1f;
		if (mImage != null) {
			float fw = (float) mImage.getWidth() / (float) mThisWidth;
			float fh = (float) mImage.getHeight() / (float) mThisHeight;
			float maxValue = Math.max(fw, fh);
			if (maxValue > 1024) {
				max = maxValue * 2;
			} else {
				max = Math.max(fw, fh) * 4;
			}
		}
		return max;
	}

	protected void zoomTo(float scale, float centerX, float centerY) {
		if (scale > mMaxZoom) {
			scale = mMaxZoom;
		} else if (scale < mMinZoom) {
			scale = mMinZoom;
		}
		draw = true;
		float oldScale = getScale();
		float deltaScale = scale / oldScale;

		mSuppMatrix.postScale(deltaScale, deltaScale, centerX, centerY);
		setImageMatrix(getImageViewMatrix());
		draw = true;
		center(true, true);
	}

	protected void zoomTo(final float scale, final float centerX,
			final float centerY, final float durationMs) {
		final float incrementPerMs = (scale - getScale()) / durationMs;
		final float oldScale = getScale();
		final long startTime = System.currentTimeMillis();
		mHandler.post(new Runnable() {
			public void run() {
				long now = System.currentTimeMillis();
				float currentMs = Math.min(durationMs, now - startTime);
				float target = oldScale + (incrementPerMs * currentMs);
				zoomTo(target, centerX, centerY);
				if (currentMs < durationMs) {
					mHandler.post(this);
				}
			}
		});
	}

	protected void zoomTo(float scale) {
		float cx = getWidth() / 2F;
		float cy = getHeight() / 2F;

		zoomTo(scale, cx, cy);
	}

	protected void onDraw(Canvas canvas) {
		try {
			if (draw) {
				float width = mImageWidth * getScale();
				/*
				 * Logger.v(TAG, "width = " + width); Logger.v(TAG,
				 * "mScreenWidth = " + mScreenWidth);
				 */
				if (width > mScreenWidth) {
					// 如果图宽大于屏宽，就不用水平居中
					center(false, true);
				} else {
					center(true, true);
				}

				draw = false;

			}

			super.onDraw(canvas);

		} catch (Error e) {
			e.printStackTrace();
			// TODO: handle exception
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		// 正在显示的图片实际宽高
	}

	protected void zoomOut(float rate) {
		if (mImage == null) {
			return;
		}

		float cx = getWidth() / 2F;
		float cy = getHeight() / 2F;
		// Zoom out to at most 1x.
		Matrix tmp = new Matrix(mSuppMatrix);
		tmp.postScale(1F / rate, 1F / rate, cx, cy);

		if (getScale(tmp) < 1F) {
			mSuppMatrix.setScale(1F, 1F, cx, cy);
		} else {
			mSuppMatrix.postScale(1F / rate, 1F / rate, cx, cy);
		}
		setImageMatrix(getImageViewMatrix());
		center(true, true);
	}

	public void postTranslate(float dx, float dy) {
		draw = true;
		// Logger.v(TAG, "dy = " + dy);
		mSuppMatrix.postTranslate(dx, dy);
		setImageMatrix(getImageViewMatrix());
	}

	float _dy = 0.0f;

	protected void postTranslateDur(final float dy, final float durationMs) {
		_dy = 0.0f;
		final float incrementPerMs = dy / durationMs;
		final long startTime = System.currentTimeMillis();
		mHandler.post(new Runnable() {
			public void run() {
				long now = System.currentTimeMillis();
				float currentMs = Math.min(durationMs, now - startTime);
				/*
				 * Logger.v(TAG, "durationMs = " + durationMs); Logger.v(TAG,
				 * "startTime = " + startTime); Logger.v(TAG, "now = " + now);
				 */
				postTranslate(0, incrementPerMs * currentMs - _dy);
				_dy = incrementPerMs * currentMs;
				if (currentMs < durationMs) {
					mHandler.post(this);
				}
			}
		});
	}

}
