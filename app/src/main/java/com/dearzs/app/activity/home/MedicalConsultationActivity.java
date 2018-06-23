package com.dearzs.app.activity.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.dearzs.app.R;
import com.dearzs.app.adapter.LvMedicalConsultationListAdapter;
import com.dearzs.app.base.BaseActivity;
import com.dearzs.app.entity.EntityConsultation;
import com.dearzs.app.entity.resp.RespGetConsultationList;
import com.dearzs.app.util.ReqManager;
import com.dearzs.app.util.Utils;
import com.dearzs.app.widget.ErrorLayoutView;
import com.dearzs.app.widget.TitleBarView;
import com.dearzs.commonlib.okhttp.callback.Callback;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Request;

/**
 * Created by luyanlong on 2016/6/8.
 * 医学资讯列表
 */
public class MedicalConsultationActivity extends BaseActivity {
    private List<EntityConsultation> mDataList;
    private LvMedicalConsultationListAdapter mListAdapter;
    private int mPageIndex;
    private XRecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_medical_consultation);

        addCenter(TitleBarView.TITLE_TYPE_CENTER_TXT, "医学资讯");
        addLeftBtn(TitleBarView.TITLE_TYPE_LEFT_IV_BACK, null);
    }

    @Override
    public void initData() {
        super.initData();
        mListAdapter = new LvMedicalConsultationListAdapter(MedicalConsultationActivity.this, mDataList = new ArrayList<EntityConsultation>());
        mRecyclerView.setAdapter(mListAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPageIndex = 1;
        reqData(mPageIndex);
    }

    @Override
    public void onLeftBtnClick() {
        super.onLeftBtnClick();
    }

    @Override
    public void initView() {
        super.initView();
        mRecyclerView = getView(R.id.medical_consultation_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(MedicalConsultationActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
        mRecyclerView.setArrowImageView(R.mipmap.ic_down_arrow);

        mRecyclerView.setLoadingMoreEnabled(true);
        mRecyclerView.setPullRefreshEnabled(true);

        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                mPageIndex = 1;
                reqData(mPageIndex);
            }

            @Override
            public void onLoadMore() {
                reqData(++mPageIndex);
            }
        });
    }

    private void reqData(int pageIndex) {
        ReqManager.getInstance().reqConsultationList(reqMedicalConsultationListCall, pageIndex, ReqManager.KEY_PAGE_SIZE, Utils.getUserToken(MedicalConsultationActivity.this));
    }

    //专家列表接口回调
    Callback<RespGetConsultationList> reqMedicalConsultationListCall = new Callback<RespGetConsultationList>() {
        @Override
        public void onError(Call call, Exception e) {
            mRecyclerView.refreshComplete();
            mRecyclerView.loadMoreComplete();
            onFailure(e.toString());
            if(mListAdapter == null || mListAdapter.getItemCount() <= 0){
                showErrorLayout(getString(R.string.error_message), R.mipmap.ic_empty_img);
            }
        }

        @Override
        public void onResponse(RespGetConsultationList response) {
            mRecyclerView.refreshComplete();
            mRecyclerView.loadMoreComplete();
            hideErrorLayout();
            if (onSuccess(response)) {
                if(response.getResult() != null && response.getResult().getList() != null){
                    mDataList = response.getResult().getList();
                    mListAdapter.notifyData(mDataList, mPageIndex == 1);
                }
                if(mListAdapter.getItemCount() <= 0){
                    showEmptyLayout(getString(R.string.empty_data_message), R.mipmap.ic_empty_img);
                }
            } else {
                if(mListAdapter.getItemCount() <= 0){
                    showEmptyLayout(getString(R.string.error_message), R.mipmap.ic_empty_img);
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
    public void onErrorClick() {
        super.onErrorClick();
        mPageIndex = 1;
        reqData(mPageIndex);
    }

    /**
     * Activity跳转
     *
     * @param b
     */
    public static void startIntent(Context ctx, Bundle b) {
        Intent intent = new Intent();
        intent.setClass(ctx, MedicalConsultationActivity.class);
        if (null != b) {
            intent.putExtras(b);
        }
        ctx.startActivity(intent);
    }
}
