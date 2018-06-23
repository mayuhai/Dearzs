package com.dearzs.app.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dearzs.app.BaseApplication;
import com.dearzs.app.R;
import com.dearzs.app.activity.CommonWebViewActivity;
import com.dearzs.app.base.BaseActivity;
import com.dearzs.app.entity.EntityUserInfo;
import com.dearzs.app.util.NetConstant;
import com.dearzs.app.widget.TitleBarView;

/**
 * Created by luyanlong on 2016/4/10.
 */
public class MyWalletActivity extends BaseActivity {
    private TextView mBalance;
    private LinearLayout mWalletHelp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_my_wallet);
        addCenter(TitleBarView.TITLE_TYPE_CENTER_TXT, "我的钱包");
        addLeftBtn(TitleBarView.TITLE_TYPE_LEFT_IV_BACK, null);
        addRightBtn(TitleBarView.TITLE_TYPE_RIGHT_TXT, "收支明细");
    }

    @Override
    public void onRightBtnClick() {
        super.onRightBtnClick();
        PaymentsBalanceListActivity.startIntent(MyWalletActivity.this, new Bundle());
    }

    @Override
    public void initData() {
        super.initData();
        EntityUserInfo userInfo = BaseApplication.getInstance().getUserInfo();
        if(userInfo != null){
            mBalance.setText("￥" + userInfo.getBalance());
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.lin_wallet_help:
                CommonWebViewActivity.startIntent(MyWalletActivity.this, NetConstant.getReqUrl(NetConstant.WALLET_HELP), "钱包助手");
                break;
        }
    }

    @Override
    public void initView() {
        super.initView();
        mBalance = getView(R.id.wallet_yue);
        mWalletHelp = getView(R.id.lin_wallet_help);
        mWalletHelp.setOnClickListener(this);
    }

    /**
     * Activity跳转
     *
     * @param b
     */
    public static void startIntent(Context ctx, Bundle b) {
        Intent intent = new Intent();
        intent.setClass(ctx, MyWalletActivity.class);
        if (null != b) {
            intent.putExtras(b);
        }
        ctx.startActivity(intent);
    }
}
