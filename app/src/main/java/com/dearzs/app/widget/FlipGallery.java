package com.dearzs.app.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Gallery;

import com.dearzs.app.R;

public class FlipGallery extends Gallery {
	private GestureDetector mGestureScanner;
	private ScaleImageView mImageView;
	public int mScreenWidth;
	public int mScreenHeight;
	private static final String TAG = "FlipGallery";
	private PointF startPoint;
	private static final int ZOOM = 2;
	private static final int DRAG = 1;
	private int mode;
	float lastPointX = 0;
	float lastPointY = 0;

	public FlipGallery(Context context) {
		super(context);
	}

	public FlipGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public FlipGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		Activity tmpActivity = (Activity) context;
		mScreenWidth = tmpActivity.getWindowManager().getDefaultDisplay().getWidth();
		mScreenHeight = tmpActivity.getWindowManager().getDefaultDisplay().getHeight();
		mGestureScanner = new GestureDetector(new MySimpleGesture());
		this.setOnTouchListener(mGalleryOnTouchListener);
	}

	OnTouchListener mGalleryOnTouchListener = new OnTouchListener() {

		float mBaseValue;
		float mOriginalScale;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			ViewGroup viewGroup = (ViewGroup) FlipGallery.this.getSelectedView();
			if (viewGroup == null) {
				return false;
			}
			View view = viewGroup.findViewById(R.id.gallery_image1);
			if (view instanceof ScaleImageView) {
				mImageView = (ScaleImageView) view;
				/*
				 * if (mImageView.getDrawable() != null &&
				 * mImageView.getDrawable() instanceof BitmapDrawable) {
				 */
				if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
					startPoint = new PointF(event.getX(), event.getY());
					mBaseValue = 0;
					lastPointX = 0;
					lastPointY = 0;
					mOriginalScale = mImageView.getScale();
					mode = DRAG;
				} else if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_DOWN) {
					mode = ZOOM;
				} else if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_MOVE) {

					if (event.getPointerCount() == 2 && mode == ZOOM) {
						float x = event.getX(0) - event.getX(1);
						float y = event.getY(0) - event.getY(1);
						float value = (float) Math.sqrt(x * x + y * y);// 计算两点的距离
						if (mBaseValue == 0) {
							mBaseValue = value;
						} else {
							float scale = value / mBaseValue;// 当前两点间的距离除以手指落下时两点间的距离就是需要缩放的比例。
							// scale the image
							mImageView.zoomTo(mOriginalScale * scale,
									// x + event.getX(1), y + event.getY(1));
									(event.getX(1) + event.getX(0)) / 2,
									(event.getY(1) + event.getY(0)) / 2);
						}
					}
					if (mode == DRAG) {

						PointF currentPoint = new PointF(event.getX(), event.getY());
						if (lastPointX == 0) {
							lastPointX = startPoint.x;
						}
						if (lastPointY == 0) {
							lastPointY = startPoint.y;
						}
						float dx = currentPoint.x - lastPointX;// 得到物体在X轴的移动距离
						float dy = currentPoint.y - lastPointY;// 得到物体在X轴的移动距离

						lastPointX = currentPoint.x;
						lastPointY = currentPoint.y;

						Rect r = new Rect();
						mImageView.getGlobalVisibleRect(r);
						/*
						 * Logger.v(TAG, "r.left = " + r.left); Logger.v(TAG,
						 * "r.right = " + r.right);
						 */

						float v1[] = new float[9];
						Matrix m = mImageView.getImageMatrix();
						m.getValues(v1);
						float left, right;
						// 图片的实时宽，高
						float width, height;
						width = mImageView.getScale() * mImageView.getImageWidth();
						height = mImageView.getScale() * mImageView.getImageHeight();

						left = v1[Matrix.MTRANS_X];
						right = left + width;

						if (dx > 0) { // 往左滑动
							if (left <= 0 && r.right >= mScreenWidth) {
								if ((dx + left) > 0) { // 防止边界越出
									dx = -left;
								}
								mImageView.postTranslate(dx, dy);
							}
						} else {
							if (r.left <= 0 && right >= mScreenWidth) {
								if ((dx + right) < mScreenWidth) { // 防止边界越出
									dx = mScreenWidth - right;
								}
								mImageView.postTranslate(dx, dy);
							}
						}

						/*
						 * if(dx < 0) { //往右滑动 if(right < mScreenWidth ) {
						 * mImageView.postTranslate(dx, dy); } } else {
						 * if(r.right <= mScreenWidth) {
						 * mImageView.postTranslate(dx, dy); } }
						 */

					}
				} else if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_UP) {
					mode = 0;
				} else if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
					mode = 0;
				}
				// }
			} else {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					mBaseValue = 0;
					mOriginalScale = mImageView.getScale();
				}
			}

			return false;
		}

	};

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		ViewGroup viewGroup = (ViewGroup) getSelectedView();
		if (viewGroup == null) {
			return false;
		}
		View view = viewGroup.findViewById(R.id.gallery_image1);
		if (view instanceof ScaleImageView) {
			mImageView = (ScaleImageView) view;

			float v[] = new float[9];
			Matrix m = mImageView.getImageMatrix();
			m.getValues(v);
			// 图片实时的上下左右坐标
			float left, right;
			// 图片的实时宽，高
			float width, height;
			width = mImageView.getScale() * mImageView.getImageWidth();
			height = mImageView.getScale() * mImageView.getImageHeight();
			// 一下逻辑为移动图片和滑动gallery换屏的逻辑。如果没对整个框架了解的非常清晰，改动以下的代码前请三思！！！！！！
			if ((int) width <= mScreenWidth && (int) height <= mScreenHeight)// 如果图片当前大小<屏幕大小，直接处理滑屏事件
			{
				super.onScroll(e1, e2, distanceX, distanceY);
			} else {
				left = v[Matrix.MTRANS_X];

				right = left + width;
				Rect r = new Rect();
				mImageView.getGlobalVisibleRect(r);

				if (distanceX > 0)// 向右滑动
				{
					if (r.left > 0) {// 判断当前ImageView是否显示完全
						super.onScroll(e1, e2, distanceX, distanceY);
					} else if (right <= mScreenWidth) {
						super.onScroll(e1, e2, distanceX, distanceY);
					} else {
						// mImageView.postTranslate(-distanceX, -distanceY);
					}
				} else if (distanceX < 0)// 向左滑动
				{
					if (r.right < mScreenWidth) {
						super.onScroll(e1, e2, distanceX, distanceY);
					} else if (left >= 0) {
						super.onScroll(e1, e2, distanceX, distanceY);
					} else {
						// mImageView.postTranslate(-distanceX, -distanceY);
					}
				}
			}
		} else {
			super.onScroll(e1, e2, distanceX, distanceY);
		}
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		// return false;
		// return super.onFling(e1, e2, velocityX, velocityY);
		int kEvent;
		if (e2.getX() > e1.getX()) {
			// Check if scrolling left
			kEvent = KeyEvent.KEYCODE_DPAD_LEFT;
		} else {
			// Otherwise scrolling right
			kEvent = KeyEvent.KEYCODE_DPAD_RIGHT;
		}
		onKeyDown(kEvent, null);
		return true;

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mGestureScanner.onTouchEvent(event);
		switch (event.getAction()) {
		case MotionEvent.ACTION_POINTER_UP:
			break;
		case MotionEvent.ACTION_UP:
			// 判断上下边界是否越界
			ViewGroup viewGroup = (ViewGroup) FlipGallery.this.getSelectedView();
			if (viewGroup == null) {
				return false;
			}
			View view = viewGroup.findViewById(R.id.gallery_image1);
			if (view instanceof ScaleImageView) {
				ScaleImageView imageView = (ScaleImageView) view;
				float width = imageView.getScale() * imageView.getImageWidth();
				float height = imageView.getScale() * imageView.getImageHeight();
				if ((int) width <= mScreenWidth && (int) height <= mScreenHeight)// 如果图片当前大小<屏幕大小，判断边界
				{
					break;
				}
				float v[] = new float[9];
				Matrix m = imageView.getImageMatrix();
				m.getValues(v);
				float top = v[Matrix.MTRANS_Y];
				float bottom = top + height;
				if (top > 0) {
					imageView.postTranslateDur(-top, 200f);
				}
				if (bottom < mScreenHeight) {
					imageView.postTranslateDur(mScreenHeight - bottom, 200f);
				}
			}
			break;
		}
		return super.onTouchEvent(event);
	}

	private class MySimpleGesture extends SimpleOnGestureListener {

		// 按两下的第二下Touch down时触发
		public boolean onDoubleTap(MotionEvent e) {
			ViewGroup viewGroup = (ViewGroup) getSelectedView();
			if (viewGroup == null) {
				return false;
			}
			mImageView = (ScaleImageView) viewGroup.findViewById(R.id.gallery_image1);
			if (mImageView.getDrawable() != null
					&& mImageView.getDrawable() instanceof BitmapDrawable) {
				if (mImageView.getScale() > mImageView.getScaleRate()) {
					mImageView.zoomTo(mImageView.getScaleRate(), mScreenWidth / 2,
							mScreenHeight / 2, 200f);
					// imageView.layoutToCenter();
				} else {
					mImageView.zoomTo(1.0f, mScreenWidth / 2, mScreenHeight / 2, 200f);
				}
			}
			return true;
		}
	}
}
