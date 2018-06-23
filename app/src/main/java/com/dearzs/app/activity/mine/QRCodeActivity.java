package com.dearzs.app.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.dearzs.app.BaseApplication;
import com.dearzs.app.R;
import com.dearzs.app.base.BaseActivity;
import com.dearzs.app.util.Constant;
import com.dearzs.app.util.ImageLoaderManager;
import com.dearzs.app.util.ShareUtil;
import com.dearzs.app.widget.TitleBarView;

/**
 * 二维码页面
 */

public class QRCodeActivity extends BaseActivity {

    private String mQRCodeUrl;
    private String mInviteCode;
    private TextView mTvInviteCode;
    private ImageView mIvQrCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentLayout(R.layout.activity_qr_code);

        addLeftBtn(TitleBarView.TITLE_TYPE_LEFT_IV_BACK, null);
        addCenter(TitleBarView.TITLE_TYPE_CENTER_TXT, "我的二维码");
        addRightBtn(TitleBarView.TITLE_TYPE_RIGHT_IV_SINGLE_BUTTON, null);
        getTitleBar().setRightIvResource(R.mipmap.ic_webview_share);
    }

    @Override
    public void initData() {
        super.initData();
        mQRCodeUrl = getIntent().getStringExtra(Constant.KEY_QR_CODE_URL);
        mInviteCode = getIntent().getStringExtra(Constant.KEY_QR_INVITE_CODE);

        if(!TextUtils.isEmpty(mQRCodeUrl)){
            ImageLoaderManager.getInstance().displayImage(mQRCodeUrl, mIvQrCode);
        }
        mTvInviteCode.setText(TextUtils.isEmpty(mInviteCode) ? "" : mInviteCode);
    }

    @Override
    public void onRightBtnClick() {
        super.onRightBtnClick();
        String content = "";
        if(BaseApplication.getInstance().getUserInfo() != null ){
            content = BaseApplication.getInstance().getUserInfo().getName() + "邀请您注册第二诊室";
        }
        String clickUrl = "https://dev.api.dearzs.com/service/h5/invite/" + mInviteCode;
        ShareUtil.getInstence(QRCodeActivity.this).openShare("第二诊室", content,clickUrl, mQRCodeUrl);
    }

    @Override
    public void initView() {
        super.initView();
        mIvQrCode = getView(R.id.iv_qr_code);
        mTvInviteCode= getView(R.id.tv_invite_code);
    }

    public static void startIntent(Context context, String qrCodeUrl, String inviteCode){
        Intent intent = new Intent(context, QRCodeActivity.class);
        intent.putExtra(Constant.KEY_QR_CODE_URL, qrCodeUrl);
        intent.putExtra(Constant.KEY_QR_INVITE_CODE, inviteCode);
        context.startActivity(intent);
    }

}
