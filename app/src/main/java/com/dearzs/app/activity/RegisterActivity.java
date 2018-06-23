package com.dearzs.app.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.dearzs.app.R;
import com.dearzs.app.base.BaseActivity;
import com.dearzs.app.entity.EntityBase;
import com.dearzs.app.util.NetConstant;
import com.dearzs.app.util.ReqManager;
import com.dearzs.app.util.ToastUtil;
import com.dearzs.app.util.Utils;
import com.dearzs.app.widget.TitleBarView;
import com.dearzs.commonlib.okhttp.callback.Callback;

import okhttp3.Call;
import okhttp3.Request;

public class RegisterActivity extends BaseActivity {
	private ImageView imvDelPwdIcon, imvSeePwdIcon, imvDelSmsIcon,
			imvDelPhoneIcon;
	private Button btnRegister;
	private TextView txtGetSmsCode;
	private EditText edtSms, edtPwd, edtPhoneNo;
	private boolean pwdHide = true;
	private TimeCountFirst mTimeCount;
	private TextView mLicense;
	private CheckBox mCbAgreeLicence;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentLayout(R.layout.activity_register);
		addLeftBtn(TitleBarView.TITLE_TYPE_LEFT_IV_BACK, "");
		addCenter(TitleBarView.TITLE_TYPE_CENTER_TXT, getString(R.string.register));
		initUi();
		setListener();
		initData();
	}

	private void initUi() {
		imvDelPwdIcon = (ImageView) findViewById(R.id.imvDelPwdIcon);
		imvSeePwdIcon = (ImageView) findViewById(R.id.imvSeePwdIcon);
		imvDelSmsIcon = (ImageView) findViewById(R.id.imvDelSmsIcon);
		imvDelPhoneIcon = (ImageView) findViewById(R.id.imvDelPhoneIcon);
		mCbAgreeLicence = getView(R.id.cb_register_agree_licence);
		mLicense = getView(R.id.tv_register_licence);
		btnRegister = (Button) findViewById(R.id.btnRegister);
		txtGetSmsCode = (TextView) findViewById(R.id.txtGetSmsCode);
		edtSms = (EditText) findViewById(R.id.edtSms);
		edtPwd = (EditText) findViewById(R.id.edtPwd);
		edtPhoneNo = (EditText) findViewById(R.id.edtPhoneNo);
		mLicense.setOnClickListener(this);
	}

	@Override
	public void initData() {
		super.initData();
		mTimeCount = new TimeCountFirst(60000, 1000);
	}

	private void setListener() {
		imvDelPwdIcon.setOnClickListener(this);
		imvDelPhoneIcon.setOnClickListener(this);
		imvSeePwdIcon.setOnClickListener(this);
		imvDelSmsIcon.setOnClickListener(this);
		btnRegister.setOnClickListener(this);
		txtGetSmsCode.setOnClickListener(this);
		edtPhoneNo.addTextChangedListener(new RegisterTextWatcher(0));
		edtPwd.addTextChangedListener(new RegisterTextWatcher(1));
		edtSms.addTextChangedListener(new RegisterTextWatcher(2));

	}

	class RegisterTextWatcher implements TextWatcher {
		private int type = 0;// 手机号，密码，短信验证码，图片验证码

		public RegisterTextWatcher(int type) {
			this.type = type;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			switch (type) {
			case 0:
				if (s.length() > 0) {
					imvDelPhoneIcon.setVisibility(View.VISIBLE);
				} else {
					imvDelPhoneIcon.setVisibility(View.GONE);
				}
				break;
			case 1:
				if (s.length() > 0) {
					imvDelPwdIcon.setVisibility(View.VISIBLE);
					imvSeePwdIcon.setVisibility(View.VISIBLE);
				} else {
					imvDelPwdIcon.setVisibility(View.GONE);
					imvSeePwdIcon.setVisibility(View.GONE);
				}
				break;
			case 2:
				if (s.length() > 0) {
					imvDelSmsIcon.setVisibility(View.VISIBLE);
				} else {
					imvDelSmsIcon.setVisibility(View.GONE);
				}
				break;
			default:
				break;
			}
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imvDelPwdIcon:
			edtPwd.setText("");
			break;
		case R.id.imvSeePwdIcon:
			if (pwdHide) {
				// 如果选中，显示密码
				edtPwd.setTransformationMethod(HideReturnsTransformationMethod
						.getInstance());
				imvSeePwdIcon.setImageResource(R.mipmap.pwd_hide);
			} else {
				// 否则隐藏密码
				edtPwd.setTransformationMethod(PasswordTransformationMethod
						.getInstance());
				imvSeePwdIcon.setImageResource(R.mipmap.pwd_show);
			}
			pwdHide = !pwdHide;
			// 输入框光标一直在输入文本后面
			Editable etable = edtPwd.getText();
			Selection.setSelection(etable, etable.length());
			break;
		case R.id.imvDelPhoneIcon:
			edtPhoneNo.setText("");
			break;
		case R.id.imvDelSmsIcon:
			edtSms.setText("");
			break;
		case R.id.btnRegister:
			if(mCbAgreeLicence.isChecked()){
				if (!isEmptyContent()) {
					showProgressDialog(R.string.wait);
					String phone = edtPhoneNo.getText().toString().trim();
					String smsCode = edtSms.getText().toString().trim();
					String password = edtPwd.getText().toString().trim();
					ReqManager.getInstance().reqUserRegister(reqUserRegisterCall,phone, smsCode, password);
				}
			} else {
				ToastUtil.showLongToast("同意许可协议才可注册");
			}

			break;
		case R.id.txtGetSmsCode:
			if (Utils.isNull(edtPhoneNo.getText())) {
				ToastUtil.showShortToast(R.string.input_phone);
			} else {
				reGetVer();
			}
			break;
			case R.id.tv_register_licence:
				CommonWebViewActivity.startIntent(RegisterActivity.this, NetConstant.getReqUrl(NetConstant.REGISTER_LICENCE), "注册协议");
				break;
		default:
			break;
		}
	}

	public void reGetVer() {
		ReqManager.getInstance().reqValidPhoneCode(reqValidCodeCall, edtPhoneNo.getText().toString().trim(), NetConstant.KEY_REGISTER);
	}

	private boolean isEmptyContent() {
		boolean bl = false;

		String phone = edtPhoneNo.getText().toString().trim();
		String smsCode = edtSms.getText().toString().trim();
		String password = edtPwd.getText().toString().trim();

		if (Utils.isNull(phone)) {
			ToastUtil.showShortToast(R.string.input_phone);
			return true;
		} else if (Utils.isNull(password)) {
			ToastUtil.showShortToast(R.string.input_pwd);
			return true;
		} else if (Utils.isNull(smsCode)) {
			ToastUtil.showShortToast(R.string.input_sms_code);
			return true;
		}
		return bl;
	}

	Callback<EntityBase> reqValidCodeCall = new Callback<EntityBase>() {
		@Override
		public void onError(Call call, Exception e) {
			onFailure(e.toString());
		}

		@Override
		public void onResponse(EntityBase response) {
			if (onSuccess(response)) {
				ToastUtil.showLongToast("发送验证码成功");
				mTimeCount.start();			//开始倒计时
			}
		}

		@Override
		public void onBefore(Request request) {
			super.onBefore(request);
			showProgressDialog();
		}
	};

	Callback<EntityBase> reqUserRegisterCall = new Callback<EntityBase>() {
		@Override
		public void onError(Call call, Exception e) {
            e.printStackTrace();
            onFailure(e.toString());
		}

		@Override
		public void onResponse(EntityBase response) {
			if (onSuccess(response)) {
				ToastUtil.showLongToast("注册成功");
				Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
				startActivity(intent);
				finish();
			}
		}

		@Override
		public void onBefore(Request request) {
			super.onBefore(request);
			showProgressDialog();
		}
	};

	class TimeCountFirst extends CountDownTimer {

		public TimeCountFirst(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			txtGetSmsCode.setClickable(false);
			txtGetSmsCode.setTextColor(getResources().getColor(R.color.gray_light));
			txtGetSmsCode.setText(millisUntilFinished / 1000 + "s后重新获取");
		}

		@Override
		public void onFinish() {
			txtGetSmsCode.setText("获取验证码");
			txtGetSmsCode.setTextColor(getResources().getColor(R.color.sms_code));
			txtGetSmsCode.setClickable(true);

		}
	}


	/**
	 * Activity跳转
	 *
	 * @param
	 */
	public static void startIntent(Context ctx) {
		Intent intent = new Intent();
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.setClass(ctx, RegisterActivity.class);
		ctx.startActivity(intent);
	}
}
