package com.dearzs.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dearzs.app.R;
import com.dearzs.app.base.BaseActivity;
import com.dearzs.app.chat.utils.Util;
import com.dearzs.app.entity.EntityBase;
import com.dearzs.app.util.NetConstant;
import com.dearzs.app.util.ReqManager;
import com.dearzs.app.util.ToastUtil;
import com.dearzs.app.util.Utils;
import com.dearzs.app.widget.TitleBarView;
import com.dearzs.commonlib.okhttp.callback.Callback;

import de.greenrobot.event.Subscribe;
import okhttp3.Call;
import okhttp3.Request;

/**
 * Created by lx on 2016/9/6.
 * 忘记密码
 */
public class ForgetPwdActivity extends BaseActivity {
    private EditText mEtPhone;
    private EditText mEtSmsCode;
    private TextView mTvGetCode;
    private Button mBtNext;
    private TimeCountFirst mTimeCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_forget_password);

        addCenter(TitleBarView.TITLE_TYPE_CENTER_TXT, "忘记密码");
        addLeftBtn(TitleBarView.TITLE_TYPE_LEFT_IV_BACK, null);
    }

    @Override
    public void initView() {
        super.initView();
        mBtNext = getView(R.id.btn_next);
        mEtPhone = getView(R.id.edt_Phone_No_forget);
        mEtSmsCode = getView(R.id.edt_Sms_code_forget);
        mTvGetCode = getView(R.id.txtGetSmsCode_forget);

        mBtNext.setOnClickListener(this);
        mTvGetCode.setOnClickListener(this);
    }

    @Override
    public void initData() {
        super.initData();
        mTimeCount = new TimeCountFirst(60000, 1000);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.txtGetSmsCode_forget:
                if (Utils.isNull(mEtPhone.getText())) {
                    ToastUtil.showShortToast(R.string.input_phone);
                } else {
                    reGetVer();
                }
                break;
            case R.id.btn_next:
                String phoneNum = mEtPhone.getText().toString();
                String smsCode = mEtSmsCode.getText().toString();
                if(!Utils.isPhoneNumberValid(phoneNum)){
                    ToastUtil.showLongToast("请输入正确的电话号码");
                    return;
                }
                if(TextUtils.isEmpty(smsCode) || smsCode.length() != 4 || !Utils.isNumber(smsCode) ){
                    ToastUtil.showLongToast("请输入正确的验证码");
                    return;
                }
//                FindPwdActivity.startIntent(ForgetPwdActivity.this, phoneNum, smsCode);
                break;
        }
    }

    public void reGetVer() {
        ReqManager.getInstance().reqValidPhoneCode(reqValidCodeCall, mEtPhone.getText().toString().trim(), NetConstant.KEY_FORGET_PSWD);
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

    //修改密码成功，关闭自己
    @Subscribe
    public void closeSelf(String msg){
        if("close".equals(msg)){
            finish();
        }
    }

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
}
