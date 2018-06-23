package com.dearzs.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.dearzs.app.BaseApplication;
import com.dearzs.app.R;
import com.dearzs.app.base.BaseActivity;
import com.dearzs.app.entity.resp.RespUserLogin;
import com.dearzs.app.util.Constant;
import com.dearzs.app.util.ReqManager;
import com.dearzs.app.util.ToastUtil;
import com.dearzs.commonlib.okhttp.callback.Callback;
import com.dearzs.commonlib.utils.MCountDownTimer;
import com.dearzs.commonlib.utils.PfUtils;

import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import okhttp3.Call;
import okhttp3.Request;

/**
 * 应用启动页
 */
public class StartActivity extends BaseActivity {
    private MCountDownTimer mTimer;
    private TextView mTvStart;
    private int mPageIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_start);
    }

    @Override
    public void initView() {
        super.initView();
        mTvStart = getView(R.id.start_tv_start);
    }

    @Override
    public void initListener() {
        super.initListener();
        mTvStart.setOnClickListener(this);
    }

    @Override
    public void initData() {
        super.initData();
        mPageIndex = getIntent().getIntExtra(Constant.KEY_CURRENT_PAGE, 0);
        mTimer = new MCountDownTimer(1000, 1000, new MCountDownTimer.CountDownCallBack() {
            @Override
            public void onTickCallBack(long millisUntilFinished) {
            }

            @Override
            public void onFinishCallBack() {
                String tokenid = PfUtils.getStr(StartActivity.this, Constant.DEARZS_SP, Constant.KEY_TOKENID, null);
                if (!TextUtils.isEmpty(tokenid)) {
                    ReqManager.getInstance().reqUserLoginByTokenId(reqUserLoginByTokenCall, tokenid);
                }else {
                    boolean mIsFirshRun = PfUtils.getBoolean(StartActivity.this, Constant.USER_CONFIG, Constant.KEY_IS_FIRST_RUN, true);
//                    int versionCode = PfUtils.getInt(StartActivity.this, Constant.USER_CONFIG, Constant.KEY_APP_VIRSON_CODE, 0);
                    if(mIsFirshRun){ //|| versionCode < Utils.getVersionCode(StartActivity.this)){

                        JPushInterface.setAlias(StartActivity.this, "", new TagAliasCallback() {
                            @Override
                            public void gotResult(int i, String s, Set<String> set) {
//                                ToastUtil.showLongToast("i = " + i + "; s = " + s + ";");
                            }
                        });
                        startActivity(new Intent(StartActivity.this, AppGuideActivity.class));
                        finish();
                    } else {
                        LoginActivity.startIntent(StartActivity.this);
                        finish();
                    }
                }
            }
        });
        mTimer.startTimer();
    }

    Callback<RespUserLogin> reqUserLoginByTokenCall = new Callback<RespUserLogin>() {
        @Override
        public void onError(Call call, Exception e) {
            e.printStackTrace();
            onFailure(e.toString());
            LoginActivity.startIntent(StartActivity.this);
            finish();
        }

        @Override
        public void onResponse(RespUserLogin response) {
            if (onSuccess(response)) {
                ToastUtil.showLongToast("登陆成功");
                String tokenId = response.getResult().getTokenId();
                if (response.getResult() != null) {
                    BaseApplication.getInstance().setLoginInfo(response.getResult());        //将用户信息保存在内存里
                    PfUtils.setStr(StartActivity.this, Constant.DEARZS_SP, Constant.KEY_TOKENID, tokenId);
                    Intent intent = new Intent(StartActivity.this, HomeActivity.class);
                    intent.putExtra(Constant.KEY_CURRENT_PAGE, mPageIndex);
                    startActivity(intent);
                    finish();
                }else {
                    LoginActivity.startIntent(StartActivity.this);
                    finish();
                }
            }
        }

        @Override
        public void onBefore(Request request) {
            super.onBefore(request);
            showProgressDialog();
        }
    };

    @Override
    public void onClick(View v) {
        super.onClick(v);
//        if(v.getId()==R.id.start_tv_start){
//            HomeActivity.startIntent(this,null);
//            finish();
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
    }
}
