package com.dearzs.app.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;

import com.dearzs.app.R;
import com.dearzs.app.activity.CommonWebViewActivity;
import com.dearzs.app.activity.LoginActivity;
import com.dearzs.app.activity.RegisterActivity;
import com.dearzs.app.base.BaseActivity;
import com.dearzs.app.entity.EntityAppUpdateInfo;
import com.dearzs.app.entity.EntityBase;
import com.dearzs.app.entity.resp.RespAppUpdate;
import com.dearzs.app.util.Constant;
import com.dearzs.app.util.DataCleanManager;
import com.dearzs.app.util.NetConstant;
import com.dearzs.app.util.ReqManager;
import com.dearzs.app.util.ShareUtil;
import com.dearzs.app.util.ToastUtil;
import com.dearzs.app.util.Utils;
import com.dearzs.app.widget.CustomCellSwitchView;
import com.dearzs.app.widget.CustomCellView;
import com.dearzs.app.widget.TitleBarView;
import com.dearzs.commonlib.okhttp.callback.Callback;
import com.dearzs.commonlib.utils.AsyncInfoLoader;
import com.dearzs.commonlib.utils.PfUtils;
import com.dearzs.commonlib.utils.log.LogUtil;

import okhttp3.Call;
import okhttp3.Request;

/**
 * Created by luyanlong on 2016/5/29.
 */
public class SettingActivity extends BaseActivity {
    private CustomCellSwitchView mPushCell;
    private CustomCellView mPrivacyCell;
    private CustomCellView mSecutiryCell;
    private CustomCellView mShareCell;
    private CustomCellView mClearCacheCell;
    private CustomCellView mPayHelpCell;
    private CustomCellView mFeedbackCell;
    private CustomCellView mCheckUpdateCell;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_setting);
        addCenter(TitleBarView.TITLE_TYPE_CENTER_TXT, "设置");
        addLeftBtn(TitleBarView.TITLE_TYPE_LEFT_IV_BACK, null);
    }

    @Override
    public void initView() {
        super.initView();
        mPushCell = getView(R.id.setting_item_push);
        mPrivacyCell = getView(R.id.setting_item_privicy_agreenment);
        mSecutiryCell = getView(R.id.setting_item_secutiry_agreenment);
        mShareCell = getView(R.id.setting_item_share);
        mClearCacheCell = getView(R.id.setting_item_clear_cache);
        mPayHelpCell = getView(R.id.setting_item_pay_help);
        mFeedbackCell = getView(R.id.setting_item_feedback);
        mCheckUpdateCell = getView(R.id.setting_item_check_update);
        mPushCell.setClickable(true);
        mPrivacyCell.setClickable(true);
        mSecutiryCell.setClickable(true);
        mShareCell.setClickable(true);
        mClearCacheCell.setClickable(true);
        mPayHelpCell.setClickable(true);
        mFeedbackCell.setClickable(true);
        mCheckUpdateCell.setClickable(true);
        mPushCell.setOnClickListener(this);
        mPrivacyCell.setOnClickListener(this);
        mSecutiryCell.setOnClickListener(this);
        mShareCell.setOnClickListener(this);
        mClearCacheCell.setOnClickListener(this);
        mPayHelpCell.setOnClickListener(this);
        mFeedbackCell.setOnClickListener(this);
        mCheckUpdateCell.setOnClickListener(this);

        mPushCell.setOnSwitchListener(new CustomCellSwitchView.OnSwitchClickListener() {
            @Override
            public void onSwitchClick(boolean on) {
                PfUtils.setBoolean(getApplicationContext(), Constant.DEARZS_SP, Constant.KEY_PUSH_SWITCH, on);
            }
        });
    }

    @Override
    public void initData() {
        super.initData();
        String cacheSize = null;
        try {
            cacheSize = DataCleanManager.getTotalCacheSize(getApplicationContext());
        } catch (Exception e) {
        }
        if (!TextUtils.isEmpty(cacheSize)) {
            mClearCacheCell.setDesc(cacheSize);
            mClearCacheCell.setTag(cacheSize);
        } else {
            mClearCacheCell.setTag(null);
            mClearCacheCell.setDesc("0.00KB");
        }

        String currentVersionName = Utils.getVersionName(SettingActivity.this);
        String newVersionName = PfUtils.getStr(SettingActivity.this, Constant.DEARZS_SP,
                Constant.KEY_VERSIONNAME, null);
        if (!TextUtils.isEmpty(newVersionName)) {
            mCheckUpdateCell.setDesc("最新版本：" + newVersionName);
        }else {
            mCheckUpdateCell.setDesc("当前版本：" + currentVersionName);
        }

        boolean isPush = PfUtils.getBoolean(getApplicationContext(), Constant.DEARZS_SP, Constant.KEY_PUSH_SWITCH, true);
        mPushCell.setRadiaButtonState(isPush);

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.setting_item_push:

                break;
            case R.id.setting_item_privicy_agreenment:
                CommonWebViewActivity.startIntent(SettingActivity.this, NetConstant.getReqUrl(NetConstant.PRIVACY_LICENCE), "隐私协议");
                break;
            case R.id.setting_item_secutiry_agreenment:
//                CommonWebViewActivity.startIntent(SettingActivity.this, NetConstant.getReqUrl(NetConstant.SECURITY_LICENCE), "安全协议");
                CommonWebViewActivity.startIntent(SettingActivity.this, NetConstant.getReqUrl(NetConstant.REGISTER_LICENCE), "许可协议");
                break;
            case R.id.setting_item_share:
                ShareUtil.getInstence(SettingActivity.this).openShare("第二诊室", "我在用第二诊室，里面有好多专家，通过会诊我找到了更好的治疗方案，赶快来用吧！！！", "http://a.app.qq.com/o/simple.jsp?pkgname=com.dearzs.app",String.valueOf(R.mipmap.icon_share));
                break;
            case R.id.setting_item_clear_cache:
                clearCache();
                break;
            case R.id.setting_item_pay_help:
                CommonWebViewActivity.startIntent(SettingActivity.this, NetConstant.getReqUrl(NetConstant.PAY_HELP_LICENCE), "支付帮助");
                break;
            case R.id.setting_item_feedback:
                FeedbackActivity.startIntent(SettingActivity.this);
                break;
            case R.id.setting_item_check_update:
                if (Utils.mIsAPPUpdataing) {
                    ToastUtil.showShortToast(R.string.download_news_title);
                }else {
                    ReqManager.getInstance().reqCheckAppUpdate(reqCheckAppUpdate);
                }
                break;
        }
    }

    private void clearCache(){
        if (mClearCacheCell.getTag() == null) {
            LogUtil.showToast(SettingActivity.this, "暂时不用清理...");
            return;
        }

        showConfirmDialog(SettingActivity.this, "会清除所有缓存，离线的内容及图片", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog("正在清理,请稍后...");
                AsyncInfoLoader.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {
                        DataCleanManager.clearAllCache(getApplicationContext());
                        mClearCacheCell.setTag(null);
                        mUIHandler.sendEmptyMessageDelayed(0, 1000);
                    }
                });
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }


    private final Handler mUIHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            closeProgressDialog();
            mClearCacheCell.setDesc("0.00KB");
            LogUtil.showToast(SettingActivity.this, "清理完毕...");
        }
    };

    Callback<RespAppUpdate> reqCheckAppUpdate = new Callback<RespAppUpdate>() {
        @Override
        public void onError(Call call, Exception e) {
            onFailure(e.toString());
        }

        @Override
        public void onResponse(RespAppUpdate response) {
            if (onSuccess(response)) {
                Utils.checkSoftwareUpdate(SettingActivity.this, response, true);
            }
        }

        @Override
        public void onBefore(Request request) {
            super.onBefore(request);
            showProgressDialog();
        }
    };

    /**
     * Activity跳转
     *
     * @param ctx
     */
    public static void startIntent(Context ctx) {
        Intent intent = new Intent();
        intent.setClass(ctx, SettingActivity.class);
        ctx.startActivity(intent);
    }
}
