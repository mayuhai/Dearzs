package com.dearzs.app.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
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

import de.greenrobot.event.EventBus;
import okhttp3.Call;
import okhttp3.Request;

/**
 * Created by lx on 2016/9/6.
 * 忘记密码
 */
public class FindPwdActivity extends BaseActivity {
    public static final String KEY_PHONE_NUM = "phone_num";
    public static final String KEY_SMS_CODE = "sms_code";
    private TimeCountFirst mTimeCount;
//    private TextView mTvPhoneNum;
    private EditText mEtPhoneNum;
    private TextView mTvGetCode;
    private EditText mEtSmsCode;
    private EditText mEtNewPwd;
    private EditText mEtReNewPwd;
    private Button mBtFinish;
    private ImageView mImSeePwdNew;
    private ImageView mImSeePwdReNew;
    private ImageView mImDelPwdNew;
    private ImageView mImDelPwdReNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_find_password);

        addCenter(TitleBarView.TITLE_TYPE_CENTER_TXT, "找回密码");
        addLeftBtn(TitleBarView.TITLE_TYPE_LEFT_IV_BACK, null);

    }

    @Override
    public void initView() {
        super.initView();
//        mTvPhoneNum = getView(R.id.tv_phone_num_find);
        mTvGetCode = getView(R.id.txtGetSmsCode_forget);
        mEtSmsCode = getView(R.id.edt_Sms_code_forget);
        mEtPhoneNum = getView(R.id.edt_Phone_No_forget);
        mEtNewPwd = getView(R.id.edt_Phone_No_find);
        mEtReNewPwd = getView(R.id.edt_Sms_code_find);
        mBtFinish = getView(R.id.btn_find);
        mImDelPwdNew = (ImageView) findViewById(R.id.imvDelPwdIcon_find_new);
        mImSeePwdNew = (ImageView) findViewById(R.id.imvSeePwdIcon_find_new);
        mImDelPwdReNew = (ImageView) findViewById(R.id.imvDelPwdIcon_find_renew);
        mImSeePwdReNew = (ImageView) findViewById(R.id.imvSeePwdIcon_find_renew);

        mEtNewPwd.addTextChangedListener(new RegisterTextWatcher(1));
        mEtReNewPwd.addTextChangedListener(new RegisterTextWatcher(2));
        mBtFinish.setOnClickListener(this);
        mImDelPwdNew.setOnClickListener(this);
        mImSeePwdNew.setOnClickListener(this);
        mImDelPwdReNew.setOnClickListener(this);
        mImSeePwdReNew.setOnClickListener(this);
        mTvGetCode.setOnClickListener(this);
    }

    @Override
    public void initData() {
        super.initData();
        mTimeCount = new TimeCountFirst(60000, 1000);
    }

    public void reGetVer() {
        ReqManager.getInstance().reqValidPhoneCode(reqValidCodeCall, mEtPhoneNum.getText().toString().trim(), NetConstant.KEY_FORGET_PSWD);
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

    class TimeCountFirst extends CountDownTimer {

        public TimeCountFirst(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            mTvGetCode.setClickable(false);
            mTvGetCode.setTextColor(getResources().getColor(R.color.gray_light));
            mTvGetCode.setText(millisUntilFinished / 1000 + "s后重新获取");
        }

        @Override
        public void onFinish() {
            mTvGetCode.setText("获取验证码");
            mTvGetCode.setTextColor(getResources().getColor(R.color.sms_code));
            mTvGetCode.setClickable(true);

        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.txtGetSmsCode_forget:
                String phone = mEtPhoneNum.getText().toString();
                if (Utils.isNull(phone)) {
                    ToastUtil.showShortToast("请输入正确的手机号");
                } else {
                    reGetVer();
                }
                break;
            case R.id.btn_find:
                String pwdStr = mEtNewPwd.getText().toString();
                String rePwdStr = mEtReNewPwd.getText().toString();
                String phoneNum = mEtPhoneNum.getText().toString();
                String smsCode = mEtSmsCode.getText().toString();
                if (Utils.isNull(phoneNum)) {
                    ToastUtil.showShortToast("请输入正确的手机号");
                    return;
                }
                if(TextUtils.isEmpty(smsCode) || !Utils.isNumber(smsCode)){
                    ToastUtil.showShortToast("请输入正确的验证码");
                    return;
                }
                if(TextUtils.isEmpty(pwdStr)){
                    ToastUtil.showLongToast("请输入新密码");
                    return;
                }
                if(TextUtils.isEmpty(rePwdStr)){
                    ToastUtil.showLongToast("请输如确认新密码");
                    return;
                }
                if(!rePwdStr.equals(pwdStr)){
                    ToastUtil.showLongToast("两次密码不一致，请重新输入");
                    return;
                }
                ReqManager.getInstance().reqResetPwd(reqForgetPwdCall, phoneNum, pwdStr, smsCode);
                break;
            case R.id.imvDelPwdIcon_find_new:
                mEtNewPwd.setText("");
                break;
            case R.id.imvDelPwdIcon_find_renew:
                mEtReNewPwd.setText("");
                break;
        }

    }

    Callback<EntityBase> reqForgetPwdCall = new Callback<EntityBase>() {
        @Override
        public void onError(Call call, Exception e) {
            onFailure(e.toString());
        }

        @Override
        public void onResponse(EntityBase response) {
            if (onSuccess(response) && "000000".equals(response.getCode())) {
                ToastUtil.showLongToast("修改密码成功");
                EventBus.getDefault().post("close");
//                LoginActivity.startIntent(FindPwdActivity.this);
                finish();
            } else {
                if(response != null && !TextUtils.isEmpty(response.getMsg())){
                    ToastUtil.showLongToast(response.getMsg());
                } else {
                    ToastUtil.showLongToast("修改密码失败，请稍后重试");
                }
            }
        }

        @Override
        public void onBefore(Request request) {
            super.onBefore(request);
            showProgressDialog();
        }
    };

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
                case 1:
                    if (s.length() > 0) {
                        mImSeePwdNew.setVisibility(View.GONE);
                        mImDelPwdNew.setVisibility(View.VISIBLE);
                    } else {
                        mImSeePwdNew.setVisibility(View.GONE);
                        mImDelPwdNew.setVisibility(View.GONE);
                    }
                    break;
                case 2:
                    if (s.length() > 0) {
                        mImSeePwdReNew.setVisibility(View.GONE);
                        mImDelPwdReNew.setVisibility(View.VISIBLE);
                    } else {
                        mImSeePwdReNew.setVisibility(View.GONE);
                        mImDelPwdReNew.setVisibility(View.GONE);
                    }
                    break;
                default:
                    break;
            }
        }

    }

    /**
     * Activity跳转
     */
    public static void startIntent(Context ctx) {
        Intent intent = new Intent();
        intent.setClass(ctx, FindPwdActivity.class);
        ctx.startActivity(intent);
    }

}
