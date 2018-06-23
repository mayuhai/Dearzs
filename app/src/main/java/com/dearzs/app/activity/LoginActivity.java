package com.dearzs.app.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.dearzs.app.BaseApplication;
import com.dearzs.app.R;
import com.dearzs.app.base.BaseActivity;
import com.dearzs.app.entity.resp.RespUserLogin;
import com.dearzs.app.jpush.JpushActivity;
import com.dearzs.app.util.Constant;
import com.dearzs.app.util.ReqManager;
import com.dearzs.app.util.ToastUtil;
import com.dearzs.app.util.Utils;
import com.dearzs.app.widget.TitleBarView;
import com.dearzs.commonlib.okhttp.callback.Callback;
import com.dearzs.commonlib.utils.PfUtils;

import okhttp3.Call;
import okhttp3.Request;

/**
 * 登录界面
 */
public class LoginActivity extends BaseActivity {
    private EditText mUserName;
    private EditText mUserPassword;
    private Button mLogin;
    private TextView mForgetPass;
    private TextView mRegister;
    private ImageView mDelUserName, mDelPwdIcon, mSeePwdIcon;
    private boolean pwdHide = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_login);

        addCenter(TitleBarView.TITLE_TYPE_CENTER_TXT, "登录");
//        addLeftBtn(TitleBarView.TITLE_TYPE_LEFT_IV_BACK, null);
    }

    @Override
    public void initView() {
        super.initView();
        mUserName = getView(R.id.edtUN);
        mUserPassword = getView(R.id.edtPwd);
        mLogin = getView(R.id.btnLogin);
        mForgetPass = getView(R.id.txtForgetPwd);
        mRegister = getView(R.id.txtRegister);
        mDelUserName = getView(R.id.imvDelUNIcon);
        mDelPwdIcon = getView(R.id.imvDelPwdIcon);
        mSeePwdIcon = getView(R.id.imvSeePwdIcon);

        mLogin.setOnClickListener(this);
        mForgetPass.setOnClickListener(this);
        mRegister.setOnClickListener(this);
        mDelUserName.setOnClickListener(this);
        mDelPwdIcon.setOnClickListener(this);
        mSeePwdIcon.setOnClickListener(this);
        mUserName.addTextChangedListener(new RegisterTextWatcher(0));
        mUserPassword.addTextChangedListener(new RegisterTextWatcher(1));

        mUserName.setText(PfUtils.getStr(LoginActivity.this, Constant.DEARZS_SP, Constant.KEY_USERNAME, null));
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        Intent intent;
        switch (v.getId()) {
            case R.id.imvDelUNIcon:
                mUserName.setText("");
                break;
            case R.id.imvDelPwdIcon:
                mUserPassword.setText("");
                break;
            case R.id.imvSeePwdIcon:
                if (pwdHide) {
                    // 如果选中，显示密码
                    mUserPassword.setTransformationMethod(HideReturnsTransformationMethod
                            .getInstance());
                    mSeePwdIcon.setImageResource(R.mipmap.pwd_hide);
                } else {
                    // 否则隐藏密码
                    mUserPassword.setTransformationMethod(PasswordTransformationMethod
                            .getInstance());
                    mSeePwdIcon.setImageResource(R.mipmap.pwd_show);
                }
                pwdHide = !pwdHide;
                // 输入框光标一直在输入文本后面
                Editable etable = mUserPassword.getText();
                Selection.setSelection(etable, etable.length());
                break;
            case R.id.txtRegister:
                intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivityForResult(intent, 999);
                break;
            case R.id.txtForgetPwd:
                intent = new Intent(LoginActivity.this, FindPwdActivity.class);
                startActivityForResult(intent, 999);
                break;
            case R.id.btnLogin:
                if (!isEmptyContent()) {
                    hideInputMethod(mUserName);
                    String username = mUserName.getText().toString().trim();
                    String password = mUserPassword.getText().toString().trim();

                    ReqManager.getInstance().reqUserLogin(reqUserLoginCall, username, password, Utils.getJpushRegistrationID(LoginActivity.this));
                }
                break;
        }
    }

    Callback<RespUserLogin> reqUserLoginCall = new Callback<RespUserLogin>() {
        @Override
        public void onError(Call call, Exception e) {
            e.printStackTrace();
            onFailure(e.toString());
        }

        @Override
        public void onResponse(RespUserLogin response) {
            if (onSuccess(response)) {
                ToastUtil.showLongToast("登陆成功");
                if (response.getResult() != null) {
                    BaseApplication.getInstance().setLoginInfo(response.getResult());        //将用户信息保存在内存里
                    PfUtils.setStr(LoginActivity.this, Constant.DEARZS_SP, Constant.KEY_TOKENID, response.getResult().getTokenId());
                    PfUtils.setLong(LoginActivity.this, Constant.DEARZS_SP, Constant.KEY_USER_ID, response.getResult().getUser().getId());
                    PfUtils.setInt(LoginActivity.this, Constant.DEARZS_SP, Constant.KEY_USER_TYPE, response.getResult().getUser().getType());
                    PfUtils.setStr(LoginActivity.this, Constant.DEARZS_SP, Constant.KEY_HOT_LINE, response.getResult().getHotline());

                    PfUtils.setStr(LoginActivity.this, Constant.DEARZS_SP, Constant.KEY_USERNAME, response.getResult().getUser().getPhone());
                }

                Utils.startIntent(LoginActivity.this, JpushActivity.class, null);
                HomeActivity.startIntent(LoginActivity.this);
                finish();
            }
        }

        @Override
        public void onBefore(Request request) {
            super.onBefore(request);
            showProgressDialog();
        }
    };

    private boolean isEmptyContent() {
        boolean bl = false;
        if (Utils.isNull(mUserName.getText())) {
            ToastUtil.showLongToast(R.string.input_un);
            return true;
        } else if (Utils.isNull(mUserPassword.getText())) {
            ToastUtil.showLongToast(R.string.input_pwd);
            return true;
        }
        return bl;
    }

//    Callback<EntityBase> reqHospitalListCallBack = new Callback<EntityBase>() {
//        @Override
//        public void onError(Call call, Exception e) {
//            onFailure(e.toString());
//        }
//
//        @Override
//        public void onResponse(EntityBase response) {
//            if (onSuccess(response)) {
//                finish();
//            } else {
//            }
//        }
//
//        @Override
//        public void onBefore(Request request) {
//            super.onBefore(request);
//            showProgressDialog();
//        }
//    };

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
                        mDelUserName.setVisibility(View.VISIBLE);
                    } else {
                        mDelUserName.setVisibility(View.GONE);
                    }
                    break;
                case 1:
                    if (s.length() > 0) {
                        mDelPwdIcon.setVisibility(View.VISIBLE);
                        mSeePwdIcon.setVisibility(View.VISIBLE);
                    } else {
                        mDelPwdIcon.setVisibility(View.GONE);
                        mSeePwdIcon.setVisibility(View.GONE);
                    }
                    break;
                default:
                    break;
            }
        }

    }

    @Override
    public void initData() {
        super.initData();
    }

    /**
     * Activity跳转
     *
     */
    public static void startIntent(Context ctx) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setClass(ctx, LoginActivity.class);
        ctx.startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
