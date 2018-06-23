package com.dearzs.app.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.dearzs.app.R;
import com.dearzs.app.base.BaseActivity;
import com.dearzs.app.entity.EntityBase;
import com.dearzs.app.entity.resp.RespAppUpdate;
import com.dearzs.app.util.ReqManager;
import com.dearzs.app.util.ToastUtil;
import com.dearzs.app.util.Utils;
import com.dearzs.app.widget.TitleBarView;
import com.dearzs.commonlib.okhttp.callback.Callback;

import okhttp3.Call;
import okhttp3.Request;

/**
 * Created by Lyl on 2016/6/12.
 * 反馈界面
 */
public class FeedbackActivity extends BaseActivity {
    private EditText mEtContent;
    private Button mBtSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_feedback);
        addLeftBtn(TitleBarView.TITLE_TYPE_LEFT_IV_BACK, null);
        addCenter(TitleBarView.TITLE_TYPE_CENTER_TXT, "反馈");
    }

    @Override
    public void initData() {
        super.initData();
    }

    @Override
    public void initView() {
        super.initView();
        mEtContent = getView(R.id.et_feedback_content);
        mBtSubmit = getView(R.id.feedback_submit);
        mBtSubmit.setOnClickListener(this);
    }


    Callback<EntityBase> reqFeedback = new Callback<EntityBase>() {
        @Override
        public void onError(Call call, Exception e) {
            closeProgressDialog();
            ToastUtil.showLongToast("反馈失败，请稍后重试！");
//            onFailure(e.toString());
        }

        @Override
        public void onResponse(EntityBase response) {
            if (onSuccess(response)) {
                ToastUtil.showLongToast("反馈成功，谢谢您的反馈！");
                finish();
            } else {
                ToastUtil.showLongToast("反馈失败，请稍后重试！");
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
        if(checkContent()){
            ReqManager.getInstance().reqFeedback(reqFeedback, Utils.getUserToken(FeedbackActivity.this), mEtContent.getText().toString());
        }
    }

    private boolean checkContent(){
        String feedbackContent = mEtContent.getText().toString();
        if(TextUtils.isEmpty(feedbackContent)){
                ToastUtil.showLongToast("请输入您要反馈的内容");
                return false;
        }else {
            if(feedbackContent.length() < 20 && feedbackContent.length() > 0){
                ToastUtil.showLongToast("请至少输入20个字符");
                return false;
            } else {
                return true;
            }
        }
    }


    /**
     * Activity跳转
     *
     * @param ctx
     */
    public static void startIntent(Context ctx) {
        Intent intent = new Intent();
        intent.setClass(ctx, FeedbackActivity.class);
        ctx.startActivity(intent);
    }
}
