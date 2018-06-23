package com.dearzs.app.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.dearzs.app.BaseApplication;
import com.dearzs.app.R;
import com.dearzs.app.base.BaseActivity;
import com.dearzs.app.chat.utils.Util;
import com.dearzs.app.entity.EntityUserInfo;
import com.dearzs.app.entity.resp.RespUserLogin;
import com.dearzs.app.util.BankInfoUtil;
import com.dearzs.app.util.Constant;
import com.dearzs.app.util.ReqManager;
import com.dearzs.app.util.ToastUtil;
import com.dearzs.app.util.Utils;
import com.dearzs.app.widget.TitleBarView;
import com.dearzs.commonlib.okhttp.callback.Callback;

import okhttp3.Call;
import okhttp3.Request;

public class BankCardActivity extends BaseActivity implements TextWatcher {
    private final static int KEY_VIEW_MODEL = 0;
    private final static int KEY_EDIT_MODEL = 1;
    private EditText mEtUserName;
    private EditText mEtUserCard;
    private EditText mEtBankName;
    private EditText mEtOpenCardBankName;
    private Button mSaveBtn;
    private EntityUserInfo mModifyedUserInfo;
    private int mModel = KEY_VIEW_MODEL;

    @Override
    public void onRightBtnClick() {
        super.onRightBtnClick();
        if(mModel == KEY_VIEW_MODEL){
            mModel = KEY_EDIT_MODEL;
        } else {
            if(TextUtils.isEmpty(mEtUserName.getText())){
                ToastUtil.showLongToast("请输入姓名");
                return;
            }
            if(TextUtils.isEmpty(mEtUserCard.getText())){
                ToastUtil.showLongToast("请输入银行卡号");
                return;
            } else if(!Utils.isBankCardNum(mEtUserCard.getText().toString())){
                ToastUtil.showLongToast("银行卡号格式不正确");
                return;
            }
            if(TextUtils.isEmpty(mEtBankName.getText())){
                ToastUtil.showLongToast("请输入银行名称");
                return;
            }
            if(TextUtils.isEmpty(mEtOpenCardBankName.getText())){
                ToastUtil.showLongToast("请输入开户行");
                return;
            }
            mModifyedUserInfo.setBankAccount(mEtBankName.getText().toString());
            mModifyedUserInfo.setBankAccountBranch(mEtOpenCardBankName.getText().toString());
            mModifyedUserInfo.setBankAccountName(mEtUserName.getText().toString());
            mModifyedUserInfo.setBankAccountNumber(mEtUserCard.getText().toString());
            ReqManager.getInstance().reqUpdateUserInfo(reqPersionalDataUpdate, mModifyedUserInfo, Utils.getUserToken(BankCardActivity.this));
        }
        onModelChange(mModel);
    }

    private void onModelChange(int model){
        addRightBtn(TitleBarView.TITLE_TYPE_RIGHT_TXT, model == KEY_EDIT_MODEL ? "保存" : "修改");
        mEtBankName.setEnabled(model == KEY_EDIT_MODEL);
        mEtUserName.setEnabled(model == KEY_EDIT_MODEL);
        mEtUserCard.setEnabled(model == KEY_EDIT_MODEL);
        mEtOpenCardBankName.setEnabled(model == KEY_EDIT_MODEL);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_bank_card);
        addLeftBtn(TitleBarView.TITLE_TYPE_LEFT_IV_BACK, null);
        addCenter(TitleBarView.TITLE_TYPE_CENTER_TXT, "银行卡信息");
    }

    @Override
    public void initView() {
        super.initView();
        mSaveBtn = getView(R.id.btn_save);
        mEtUserName = getView(R.id.et_bank_card_user_name);
        mEtUserCard = getView(R.id.et_bank_card_user_number);
        mEtBankName = getView(R.id.et_bank_card_back_name);
        mEtOpenCardBankName = getView(R.id.et_bank_card_open_card_back_name);
        mEtUserCard.addTextChangedListener(this);
        mSaveBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
        }
    }

    @Override
    public void initData() {
        super.initData();
        if(getIntent() != null){
            mModifyedUserInfo = BaseApplication.getInstance().getUserInfo();
            if(mModifyedUserInfo != null
                    && !TextUtils.isEmpty(mModifyedUserInfo.getBankAccount())
                    && !TextUtils.isEmpty(mModifyedUserInfo.getBankAccountBranch())
                    && !TextUtils.isEmpty(mModifyedUserInfo.getBankAccountName())
                    && !TextUtils.isEmpty(mModifyedUserInfo.getBankAccountNumber())){
                mEtBankName.setText(mModifyedUserInfo.getBankAccount());
                mEtUserName.setText(mModifyedUserInfo.getBankAccountName());
                mEtUserCard.setText(mModifyedUserInfo.getBankAccountNumber());
                mEtOpenCardBankName.setText(mModifyedUserInfo.getBankAccountBranch());
                mModel = KEY_VIEW_MODEL;
            } else {
                mModel = KEY_EDIT_MODEL;
            }
            onModelChange(mModel);
        }
    }

    Callback<RespUserLogin> reqPersionalDataUpdate = new Callback<RespUserLogin>() {
        @Override
        public void onError(Call call, Exception e) {
            onFailure(e.toString());
            ToastUtil.showLongToast("保存失败，请重试");
        }

        @Override
        public void onResponse(RespUserLogin response) {
            if (onSuccess(response)) {
                mModel = KEY_VIEW_MODEL;
                onModelChange(mModel);
                if (response != null && response.getResult() != null) {
                    BaseApplication.getInstance().setLoginInfo(response.getResult());
                }
                ToastUtil.showLongToast("银行卡信息保存成功");
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
     */
    public static void startIntent(Context ctx) {
        Intent intent = new Intent();
        intent.setClass(ctx, BankCardActivity.class);
        ctx.startActivity(intent);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(!TextUtils.isEmpty(s) && s.length() >= 6){
            String backName = BankInfoUtil.getNameOfBank(s.toString().toCharArray());
            if(mModel == KEY_EDIT_MODEL)
            mEtBankName.setText(backName);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
