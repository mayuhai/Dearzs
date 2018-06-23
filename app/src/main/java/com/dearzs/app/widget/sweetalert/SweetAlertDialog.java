package com.dearzs.app.widget.sweetalert;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dearzs.app.R;

import java.util.List;

public class SweetAlertDialog extends Dialog implements View.OnClickListener {
	private View mDialogView;
	private AnimationSet mModalInAnim;
	private Animation mErrorInAnim;
	private AnimationSet mErrorXInAnim;
	private AnimationSet mSuccessLayoutAnimSet;
	private Animation mSuccessBowAnim;
	private TextView mTitleTextView;
	private TextView mContentTextView;
	private View mProgressDialog;
	private View mInputTextDialog;
	private EditText mInputTextView;
	private TextView mProgressDialogText;
	private String mTitleText;
	private String mEditText;
	private String mContentText;
	private boolean mShowCancel;
	private String mCancelText;
	private String mConfirmText;
	private int mAlertType;
	private FrameLayout mErrorFrame;
	private FrameLayout mSuccessFrame;
	private SuccessTickView mSuccessTick;
	private ImageView mErrorX;
	private View mSuccessLeftMask;
	private View mSuccessRightMask;
	private Drawable mCustomImgDrawable;
	private ImageView mCustomImage;
	private Button mConfirmButton;
	private Button mCancelButton;
	private View mDialogLine;
	private FrameLayout mWarningFrame;
	private OnSweetClickListener mCancelClickListener;
	private OnSweetClickListener mConfirmClickListener;
	private int mInputType = -1;
	private int mMinHeight = 50;
	private int mGravity = -1;

	public static final int NORMAL_TYPE = 0;
	public static final int ERROR_TYPE = NORMAL_TYPE + 1;
	public static final int SUCCESS_TYPE = ERROR_TYPE + 1;
	public static final int WARNING_TYPE = SUCCESS_TYPE + 1;
	public static final int CUSTOM_IMAGE_TYPE = WARNING_TYPE + 1;
	public static final int PROGRESS_BAR_TYPE = CUSTOM_IMAGE_TYPE + 1;
	public static final int INPUT_TEXT_TYPE = PROGRESS_BAR_TYPE + 1;

	public SweetAlertDialog(Context context) {
		this(context, NORMAL_TYPE);
		setOwnerActivity((Activity) context);
	}

	public SweetAlertDialog(Context context, int alertType) {
		super(context, R.style.alert_dialog);
		setOwnerActivity((Activity) context);
		setCancelable(true);
		setCanceledOnTouchOutside(false);
		mAlertType = alertType;
		mErrorInAnim = OptAnimationLoader.loadAnimation(getContext(),
				R.anim.error_frame_in);
		mErrorXInAnim = (AnimationSet) OptAnimationLoader.loadAnimation(
				getContext(), R.anim.error_x_in);
		// 2.3.x system don't support alpha-animation on layer-list drawable
		// remove it from animation set
		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
			List<Animation> childAnims = mErrorXInAnim.getAnimations();
			int idx = 0;
			for (; idx < childAnims.size(); idx++) {
				if (childAnims.get(idx) instanceof AlphaAnimation) {
					break;
				}
			}
			if (idx < childAnims.size()) {
				childAnims.remove(idx);
			}
		}
		mSuccessBowAnim = OptAnimationLoader.loadAnimation(getContext(),
				R.anim.success_bow_roate);
		mSuccessLayoutAnimSet = (AnimationSet) OptAnimationLoader
				.loadAnimation(getContext(), R.anim.success_mask_layout);
		mModalInAnim = (AnimationSet) OptAnimationLoader.loadAnimation(
				getContext(), R.anim.modal_in);
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alert_dialog);
		mDialogView = getWindow().getDecorView().findViewById(
				android.R.id.content);
		mTitleTextView = (TextView) findViewById(R.id.title_text);
		mContentTextView = (TextView) findViewById(R.id.content_text);
		mInputTextDialog = findViewById(R.id.input_text_dialog);
		mInputTextView = (EditText)findViewById(R.id.input_text_view);
		mProgressDialog = findViewById(R.id.progress_dialog);
		mProgressDialogText = (TextView) findViewById(R.id.progress_dialog_text);
		mErrorFrame = (FrameLayout) findViewById(R.id.error_frame);
		mErrorX = (ImageView) mErrorFrame.findViewById(R.id.error_x);
		mSuccessFrame = (FrameLayout) findViewById(R.id.success_frame);
		mSuccessTick = (SuccessTickView) mSuccessFrame
				.findViewById(R.id.success_tick);
		mSuccessLeftMask = mSuccessFrame.findViewById(R.id.mask_left);
		mSuccessRightMask = mSuccessFrame.findViewById(R.id.mask_right);
		mCustomImage = (ImageView) findViewById(R.id.custom_image);
		mWarningFrame = (FrameLayout) findViewById(R.id.warning_frame);
		mConfirmButton = (Button) findViewById(R.id.confirm_button);
		mCancelButton = (Button) findViewById(R.id.cancel_button);
		mDialogLine = (View)findViewById(R.id.dialog_line);

		mConfirmButton.setOnClickListener(this);
		mCancelButton.setOnClickListener(this);

		setInputText(mEditText);
		setInputType(mInputType);
		setTitleText(mTitleText);
		setContentText(mContentText);
		showCancelButton(mShowCancel);
		setCancelText(mCancelText);
		setConfirmText(mConfirmText);
		changeAlertType(mAlertType, true);
		setTextMinHeight(mMinHeight);
		setTextGravity(mGravity);
	}

	private void restore() {
		mCustomImage.setVisibility(View.GONE);
		mErrorFrame.setVisibility(View.GONE);
		mSuccessFrame.setVisibility(View.GONE);
		mWarningFrame.setVisibility(View.GONE);
		mProgressDialog.setVisibility(View.GONE);
		mInputTextDialog.setVisibility(View.GONE);
		mTitleTextView.setVisibility(View.VISIBLE);
		((View)mConfirmButton.getParent().getParent()).setVisibility(View.VISIBLE);
		
		mConfirmButton
				.setBackgroundResource(R.drawable.sweet_alert_blue_button_background);
		mErrorFrame.clearAnimation();
		mErrorX.clearAnimation();
		mSuccessTick.clearAnimation();
		mSuccessLeftMask.clearAnimation();
		mSuccessRightMask.clearAnimation();
	}

	private void playAnimation() {
		if (mAlertType == ERROR_TYPE) {
			mErrorFrame.startAnimation(mErrorInAnim);
			mErrorX.startAnimation(mErrorXInAnim);
		} else if (mAlertType == SUCCESS_TYPE) {
			mSuccessTick.startTickAnim();
			mSuccessRightMask.startAnimation(mSuccessBowAnim);
		}
	}

	private void changeAlertType(int alertType, boolean fromCreate) {
		mAlertType = alertType;
		// call after created views
		if (mDialogView != null) {
			if (!fromCreate) {
				// restore all of views state before switching alert type
				restore();
			}
			switch (mAlertType) {
			case ERROR_TYPE:
				mErrorFrame.setVisibility(View.VISIBLE);
				break;
			case SUCCESS_TYPE:
				mSuccessFrame.setVisibility(View.VISIBLE);
				// initial rotate layout of success mask
				mSuccessLeftMask.startAnimation(mSuccessLayoutAnimSet
						.getAnimations().get(0));
				mSuccessRightMask.startAnimation(mSuccessLayoutAnimSet
						.getAnimations().get(1));
				break;
			case WARNING_TYPE:
				mConfirmButton
						.setBackgroundResource(R.drawable.sweet_alert_red_button_background);
				mWarningFrame.setVisibility(View.VISIBLE);
				break;
			case CUSTOM_IMAGE_TYPE:
				setCustomImage(mCustomImgDrawable);
				break;
			case PROGRESS_BAR_TYPE:
				mProgressDialog.setVisibility(View.VISIBLE);
				mTitleTextView.setVisibility(View.GONE);
				((View)mConfirmButton.getParent().getParent()).setVisibility(View.GONE);
				break;
			case INPUT_TEXT_TYPE:
				mInputTextDialog.setVisibility(View.VISIBLE);
				break;
			}
			if (!fromCreate) {
				playAnimation();
			}
		}
	}

	public int getAlerType() {
		return mAlertType;
	}

	public void changeAlertType(int alertType) {
		changeAlertType(alertType, false);
	}

	public void setProgressDialogText(String progressText) {
		if (mProgressDialogText != null && progressText != null) {
			mProgressDialogText.setText(progressText);
		}
	}

	public EditText getInputTextView() {
		return mInputTextView;
	}

	public SweetAlertDialog setInputText(String editText){
		mEditText = editText;
		if(mInputTextView != null && editText != null){
			mInputTextView.setText(editText);
		}
		return this;
	}

	public SweetAlertDialog setInputType(int inputType){
		mInputType = inputType;
		if(mInputTextView != null && inputType >= 0){
			mInputTextView.setInputType(inputType);
		}
		return this;
	}

	public SweetAlertDialog setTextMinHeight(int minHeight){
		mMinHeight = minHeight;
		if(mInputTextView != null && mMinHeight > 50){
			LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mInputTextView.getLayoutParams();
			layoutParams.height = 500;
			mInputTextView.setPadding(5,3,3,5);
			mInputTextView.setSingleLine(false);
			mInputTextView.setMaxLines(4);
			mInputTextView.setLayoutParams(layoutParams);
		}
		return this;
	}

	public SweetAlertDialog setTextGravity(int gravity){
		mGravity = gravity;
		if(mInputTextView != null && gravity != -1){
			mInputTextView.setGravity(gravity);
		}
		return this;
	}



	public String getTitleText() {
		return mTitleText;
	}

	public SweetAlertDialog setTitleText(String text) {
		mTitleText = text;
		if (mTitleTextView != null && mTitleText != null) {
			mTitleTextView.setText(mTitleText);
		}
		return this;
	}

	public SweetAlertDialog setCustomImage(Drawable drawable) {
		mCustomImgDrawable = drawable;
		if (mCustomImage != null && mCustomImgDrawable != null) {
			mCustomImage.setVisibility(View.VISIBLE);
			mCustomImage.setImageDrawable(mCustomImgDrawable);
		}
		return this;
	}

	public SweetAlertDialog setCustomImage(int resourceId) {
		return setCustomImage(getContext().getResources().getDrawable(
				resourceId));
	}

	public String getContentText() {
		return mContentText;
	}

	public SweetAlertDialog setContentText(String text) {
		mContentText = text;
		if (mContentTextView != null && mContentText != null) {
			mContentTextView.setVisibility(View.VISIBLE);
			mContentTextView.setText(Html.fromHtml(mContentText));
		}
		return this;
	}

	public boolean isShowCancelButton() {
		return mShowCancel;
	}

	public SweetAlertDialog showCancelButton(boolean isShow) {
		mShowCancel = isShow;
		if (mCancelButton != null) {
			mCancelButton.setVisibility(mShowCancel ? View.VISIBLE : View.GONE);
		}
		if(mDialogLine != null){
			mDialogLine.setVisibility(mShowCancel ? View.VISIBLE : View.GONE);
		}
		return this;
	}

	public String getCancelText() {
		return mCancelText;
	}

	public SweetAlertDialog setCancelText(String text) {
		mCancelText = text;
		if (mCancelButton != null && mCancelText != null) {
			mCancelButton.setText(mCancelText);
		}
		return this;
	}

	public String getConfirmText() {
		return mConfirmText;
	}

	public SweetAlertDialog setConfirmText(String text) {
		mConfirmText = text;
		if (mConfirmButton != null && mConfirmText != null) {
			mConfirmButton.setText(mConfirmText);
		}
		return this;
	}

	public SweetAlertDialog setCancelClickListener(OnSweetClickListener listener) {
		mCancelClickListener = listener;
		return this;
	}

	public SweetAlertDialog setConfirmClickListener(
			OnSweetClickListener listener) {
		mConfirmClickListener = listener;
		return this;
	}

	protected void onStart() {
		try {
			mDialogView.startAnimation(mModalInAnim);
			playAnimation();
		} catch (Exception e) {
		}
	}

	public void dismiss() {
		try {
			if (getOwnerActivity() == null) return;
			if (getOwnerActivity().isFinishing() == false) {
				SweetAlertDialog.super.dismiss();
			}
		} catch (Exception e) {
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.cancel_button) {
			if (mCancelClickListener != null) {
				mCancelClickListener.onClick(SweetAlertDialog.this);
			} else {
				dismiss();
			}
		} else if (v.getId() == R.id.confirm_button) {
			if (mConfirmClickListener != null) {
				mConfirmClickListener.onClick(SweetAlertDialog.this);
			} else {
				dismiss();
			}
		}
	}
}
