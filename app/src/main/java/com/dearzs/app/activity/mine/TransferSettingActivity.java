package com.dearzs.app.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.dearzs.app.R;
import com.dearzs.app.base.BaseActivity;
import com.dearzs.app.entity.EntityUserInfo;
import com.dearzs.app.util.ToastUtil;
import com.dearzs.app.widget.TitleBarView;

/**
 * Created by mayuhai on 2016/5/10.
 */
public class TransferSettingActivity extends BaseActivity implements View.OnClickListener {

    private TextView mTView;
    private EditText mEditCost;
    private CheckBox mCheckBox;
    private EntityUserInfo userInfo;
    private TextView mStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentLayout(R.layout.activity_transfer_setting);
        addCenter(TitleBarView.TITLE_TYPE_CENTER_TXT, "转诊设置");
        addLeftBtn(TitleBarView.TITLE_TYPE_LEFT_IV_BACK, null);

        findViewById(R.id.btn_ok).setOnClickListener(this);
        addRightBtn(TitleBarView.TITLE_TYPE_RIGHT_TXT, "确定");

    }    @Override
    public void onRightBtnClick() {
        super.onRightBtnClick();

        Bundle bundle = new Bundle();
        boolean isOn = mCheckBox.isChecked();
        if (isOn) {
            if (mEditCost.getText() != null && !TextUtils.isEmpty(mEditCost.getText().toString())) {
                bundle.putString("cost", mEditCost.getText().toString());//给 bundle 写入数据

            }else {
                ToastUtil.showLongToast("费用不能为空");
            }
        }
        bundle.putBoolean("isOn", mCheckBox.isChecked());
        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }



    @Override
    public void initView() {
        super.initView();
//        mTView = (TextView) findViewById(R.id.tv_value);
        mEditCost = (EditText) findViewById(R.id.edt_cost);
        mCheckBox = (CheckBox) findViewById(R.id.checkbox);
        mCheckBox.setOnClickListener(this);
        mStatus = getView(R.id.tv_status);

        Intent intent = getIntent();
        if (intent != null) {
            userInfo = (EntityUserInfo) intent.getExtras().getSerializable("userInfo");
        }

        if (userInfo != null) {
            mEditCost.setText(userInfo.getReferralMoney() + "");
            mCheckBox.setChecked(userInfo.getReferralState() == 1 ? true : false);

            if (userInfo.getReferralState() == 1) {
                mStatus.setText("转诊中");
            }else{
                mStatus.setText("停诊中");
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.checkbox:
                if (mCheckBox.isChecked()) {
                    mStatus.setText("转诊中");
                }else{
                    mStatus.setText("停诊中");
                }
                break;
            case R.id.btn_ok:
                Bundle bundle = new Bundle();
                boolean isOn = mCheckBox.isChecked();
                if (isOn) {
                    if (mEditCost.getText() != null && !TextUtils.isEmpty(mEditCost.getText().toString())) {
                        bundle.putString("cost", mEditCost.getText().toString());//给 bundle 写入数据

                    }else {
                        ToastUtil.showLongToast("费用不能为空");
                    }
                }
                bundle.putBoolean("isOn", mCheckBox.isChecked());
                Intent intent = new Intent();
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();


                break;
        }
    }

    @Override
    public void initData() {
        super.initData();
    }

    /**
     * 获取我的钱包回调
     */
//    public class RespOrderInfoCallback extends Callback<RespOrderInfoList> {
//
//        public RespOrderInfoCallback() {
//        }
//
//        @Override
//        public void onBefore(Request request) {
//            super.onBefore(request);
//            showProgressDialog();
//        }
//
//        @Override
//        public void onError(Call call, Exception e) {
//            closeProgressDialog();
//            onFailure(e.toString());
//        }
//
//        @Override
//        public void onResponse(RespOrderInfoList response) {
//            closeProgressDialog();
//            if (response != null && response.getResult() != null && response.getResult().getOrderInfoList() != null) {
////                mDataList = response.getResult().getList();
////                mListAdapter = new LvOrderListAdapter(MyWalletActivity.this, R.layout.item_order_list_layout, mDataList);
////                mOrderListView.setAdapter(mListAdapter);
//            }
//        }
//    }

    /**
     * Activity跳转
     *
     * @param b
     */
    public static void startIntent(Context ctx, Bundle b) {
        Intent intent = new Intent();
        intent.setClass(ctx, TransferSettingActivity.class);
        if (null != b) {
            intent.putExtras(b);
        }
        ctx.startActivity(intent);
    }
}
