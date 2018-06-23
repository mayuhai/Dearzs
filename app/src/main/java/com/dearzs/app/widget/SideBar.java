package com.dearzs.app.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.dearzs.app.util.DimenUtils;

public class SideBar extends View {
	private static final int LETTER_TEXT_SIZE = 12;
	private Context mContext;
	// 触摸事件
	private OnTouchingLetterChangedListener onTouchingLetterChangedListener;
	// 26个字母
	public static String[] mLetters = { "A", "B", "C", "D", "E", "F", "G", "H", "I",
			"J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
			"W", "X", "Y", "Z", "#" };
	private int mChoosed = -1;// 选中
	private Paint mPaint = new Paint();

	private TextView mTextDialog;

	public void setTextView(TextView mTextDialog) {
		this.mTextDialog = mTextDialog;
	}


	public SideBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
	}

	public SideBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	public SideBar(Context context) {
		super(context);
		mContext = context;
	}

	/**
	 * 重写这个方法
	 */
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// 获取焦点改变背景颜色.
		int height = getHeight();// 获取对应高度
		int width = getWidth(); // 获取对应宽度
		int singleHeight = height / mLetters.length;// 获取每一个字母的高度

		for (int i = 0; i < mLetters.length; i++) {
			mPaint.setColor(Color.parseColor("#0da052"));
			mPaint.setTypeface(Typeface.DEFAULT);
			mPaint.setAntiAlias(true);
			mPaint.setTextSize(DimenUtils.dip2px(mContext, LETTER_TEXT_SIZE));
			// 选中的状态
			if (i == mChoosed) {
				mPaint.setFakeBoldText(true);
			}
			// x坐标等于中间-字符串宽度的一半.
			float xPos = width / 2 - mPaint.measureText(mLetters[i]) / 2;
			float yPos = singleHeight * i + singleHeight;
			canvas.drawText(mLetters[i], xPos, yPos, mPaint);
			mPaint.reset();// 重置画笔
		}

	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		final int action = event.getAction();
		final float y = event.getY();// 点击y坐标
		final int oldChoose = mChoosed;
		final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
		final int c = (int) (y / getHeight() * mLetters.length);// 点击y坐标所占总高度的比例*b数组的长度就等于点击b中的个数.

		switch (action) {
			case MotionEvent.ACTION_UP:
				setBackgroundDrawable(new ColorDrawable(0x00000000));
				mChoosed = -1;//
				invalidate();
				if (mTextDialog != null) {
					mTextDialog.setVisibility(View.INVISIBLE);
				}
				break;

			default:
//				setBackgroundResource(R.drawable.sidebar_background);
				if (oldChoose != c) {
					if (c >= 0 && c < mLetters.length) {
						if (listener != null) {
							listener.onTouchingLetterChanged(mLetters[c]);
						}
						if (mTextDialog != null) {
							mTextDialog.setText(mLetters[c]);
							mTextDialog.setVisibility(View.VISIBLE);
						}

						mChoosed = c;
						invalidate();
					}
				}

				break;
		}
		return true;
	}

	/**
	 * 向外公开的方法
	 *
	 * @param onTouchingLetterChangedListener
	 */
	public void setOnTouchingLetterChangedListener(
			OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
		this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
	}

	/**
	 * 接口
	 *
	 * @author coder
	 *
	 */
	public interface OnTouchingLetterChangedListener {
		public void onTouchingLetterChanged(String s);
	}

}
