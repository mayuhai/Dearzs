package com.dearzs.app.activity.family;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.dearzs.app.R;
import com.dearzs.app.activity.home.MessageListActivity;
import com.dearzs.app.adapter.LvFamilyDoctorListAdapter;
import com.dearzs.app.base.BaseActivity;
import com.dearzs.app.entity.resp.RespGetMessageList;
import com.dearzs.app.widget.TitleBarView;
import com.dearzs.commonlib.okhttp.callback.Callback;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Request;

/**
 * Created by luyanlong on 2016/12/14.
 * 家庭医生 列表页
 */

public class FamilyDocListActivity extends BaseActivity {
    private XRecyclerView mRecyclerView;
    private LvFamilyDoctorListAdapter mListAdapter;
    private List<String> mDataList;
    private View mEmptyView;
    private int mPageIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_family_doctor_list);

        addCenter(TitleBarView.TITLE_TYPE_CENTER_TXT, "家庭医生");
        addLeftBtn(TitleBarView.TITLE_TYPE_LEFT_IV_BACK, null);
    }

    @Override
    public void initView() {
        super.initView();
        mRecyclerView = getView(R.id.family_doctor_list);
        mEmptyView = getView( R.id.family_doctor_layout_empty);

        LinearLayoutManager layoutManager = new LinearLayoutManager(FamilyDocListActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
        mRecyclerView.setArrowImageView(R.mipmap.ic_down_arrow);
        mRecyclerView.setEmptyView(mEmptyView);

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

    private void reqData(int pageIndex){

    }

    @Override
    public void initData() {
        super.initData();
        mDataList = new ArrayList<String>();
        mDataList.add("第一个家庭医生方案");
        mDataList.add("第二个家庭医生方案");
        mDataList.add("第三个家庭医生方案");
        mDataList.add("第四个家庭医生方案");

        mListAdapter = new LvFamilyDoctorListAdapter(FamilyDocListActivity.this, mDataList);
        mRecyclerView.setAdapter(mListAdapter);
        mListAdapter.notifyData(mDataList, mPageIndex == 1);
    }

    //消息列表接口回调
    Callback<RespGetMessageList> reqMessageListCall = new Callback<RespGetMessageList>() {
        @Override
        public void onError(Call call, Exception e) {
            mRecyclerView.refreshComplete();
            mRecyclerView.loadMoreComplete();
            onFailure(e.toString());
            if (mListAdapter == null || mListAdapter.getItemCount() <= 0) {
                showErrorLayout(getString(R.string.error_message), R.mipmap.ic_empty_img);
            }
        }

        @Override
        public void onResponse(RespGetMessageList response) {
            mRecyclerView.refreshComplete();
            mRecyclerView.loadMoreComplete();
            if (onSuccess(response)) {
                if (response.getResult() != null && response.getResult().getList() != null) {
                    if (mDataList != null) mDataList.clear();
                    mDataList = new ArrayList<String>();
                    mDataList.add("第一个家庭医生方案");
                    mDataList.add("第二个家庭医生方案");
                    mDataList.add("第三个家庭医生方案");
                    mDataList.add("第四个家庭医生方案");
                    mListAdapter.notifyData(mDataList, mPageIndex == 1);
                }
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
     */
    public static void startIntent(Context ctx) {
        Intent intent = new Intent();
        intent.setClass(ctx, FamilyDocListActivity.class);
        ctx.startActivity(intent);
    }
}
