package com.dearzs.app.activity.communtity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.dearzs.app.BaseApplication;
import com.dearzs.app.R;
import com.dearzs.app.activity.mine.PersionalDataActivity;
import com.dearzs.app.adapter.GvUploadPicRecyclerViewAdapter;
import com.dearzs.app.base.BaseActivity;
import com.dearzs.app.entity.EntityBase;
import com.dearzs.app.entity.EntityDynamicInfo;
import com.dearzs.app.entity.EntityNetPic;
import com.dearzs.app.entity.EntityPicBase;
import com.dearzs.app.entity.EntityUserInfo;
import com.dearzs.app.entity.PDynamicImg;
import com.dearzs.app.util.Constant;
import com.dearzs.app.util.DimenUtils;
import com.dearzs.app.util.ImageLoaderManager;
import com.dearzs.app.util.ReqManager;
import com.dearzs.app.util.ToastUtil;
import com.dearzs.app.util.Utils;
import com.dearzs.app.widget.TitleBarView;
import com.dearzs.commonlib.okhttp.callback.Callback;
import com.dearzs.commonlib.utils.DisplayUtil;
import com.dearzs.commonlib.utils.LayoutUtil;
import com.dearzs.commonlib.utils.log.LogUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;
import okhttp3.Request;

/**
 * 发表评论界面
 * 目前支持动态评论和名医讲堂评论
 * Created by luyanlong on 2016/6/5.
 */
public class ReleaseCommentActivity extends BaseActivity {
    private String mDynamicComment;
    private EditText mEtComment;
    private long mDynamicId;
    private int mReleaseCommentType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_release_dynamic_comment);
        addLeftBtn(TitleBarView.TITLE_TYPE_LEFT_IV_BACK, null);
        addRightBtn(TitleBarView.TITLE_TYPE_RIGHT_TXT, "完成");
        addCenter(TitleBarView.TITLE_TYPE_CENTER_TXT, "发布评论");
    }

    @Override
    public void initView() {
        super.initView();
        mEtComment = getView(R.id.et_release_comment);
    }

    @Override
    public void initData() {
        super.initData();
        mDynamicId = getIntent().getLongExtra(Constant.KEY_DYNAMIC_ID, 0);
        mReleaseCommentType = getIntent().getIntExtra(Constant.KEY_RELEASE_COMMET_TYPE, 0);
    }

    @Override
    public void onRightBtnClick() {
        super.onRightBtnClick();
        mDynamicComment = mEtComment.getText().toString();
        if(!TextUtils.isEmpty(mDynamicComment)){
            if(mReleaseCommentType == Constant.KEY_RELEASE_COMMET_TYPE_DYNAMIC){
                ReqManager.getInstance().reqReleaseDynamicComment(reqReleaseCommentCallback, mDynamicId, mDynamicComment, Utils.getUserToken(ReleaseCommentActivity.this));
            } else {
                ReqManager.getInstance().reqDoctorForumReleaseComment(reqReleaseCommentCallback, mDynamicId, mDynamicComment, Utils.getUserToken(ReleaseCommentActivity.this));
            }
        } else {
            ToastUtil.showLongToast("请输入评论内容");
            return;
        }
    }

    //发布动态接口回调
    Callback<EntityBase> reqReleaseCommentCallback = new Callback<EntityBase>() {
        @Override
        public void onError(Call call, Exception e) {
            closeProgressDialog();
            ToastUtil.showLongToast("发布评论失败，请重试");
            //请求失败，设置加载和刷新完毕
            onFailure(e.toString());
        }

        @Override
        public void onResponse(EntityBase response) {
            closeProgressDialog();
            //请求成功，设置加载和刷新完毕
            if (onSuccess(response)) {
                ToastUtil.showLongToast("发布评论成功");
                setResult(RESULT_OK);
                finish();
            }
        }

        @Override
        public void onBefore(Request request) {
            showProgressDialog();
            super.onBefore(request);
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            ImageLoaderManager.getInstance().cleanMemoryCache();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.gc();
    }

    @Override
    public void onLeftBtnClick() {
        super.onLeftBtnClick();
    }

    @Override
    public boolean handleBack() {
        return super.handleBack();
    }

    /**
     * Activity跳转
     */
    public static void startIntent(Context ctx, long dynamic, int type) {
        Intent intent = new Intent();
        intent.setClass(ctx, ReleaseCommentActivity.class);
        intent.putExtra(Constant.KEY_DYNAMIC_ID, dynamic);
        intent.putExtra(Constant.KEY_RELEASE_COMMET_TYPE, type);
        ctx.startActivity(intent);
    }
}

