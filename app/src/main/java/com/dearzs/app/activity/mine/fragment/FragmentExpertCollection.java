package com.dearzs.app.activity.mine.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.dearzs.app.R;
import com.dearzs.app.adapter.LvExpertListAdapter;
import com.dearzs.app.base.BaseFragment;
import com.dearzs.app.entity.EntityExpertInfo;
import com.dearzs.app.entity.resp.RespGetCollectionExpertList;
import com.dearzs.app.util.ReqManager;
import com.dearzs.app.util.Utils;
import com.dearzs.commonlib.okhttp.callback.Callback;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Request;

/**
 * Created by luyanlong
 * 专家收藏
 */
public class FragmentExpertCollection extends BaseFragment {

    private List<EntityExpertInfo> mDataList;
    private XRecyclerView mRecyclerView;
    private LvExpertListAdapter mListAdapter;
    private int mPageIndex;

    @Override
    protected int inflateResource() {
        return R.layout.fragment_collection;
    }

    @Override
    protected void initViews(View rootView) {
        mRecyclerView = getView(rootView, R.id.collection_listview);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
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

    @Override
    public void initData() {
        mListAdapter = new LvExpertListAdapter(getActivity(), mDataList = new ArrayList<EntityExpertInfo>());
        mRecyclerView.setAdapter(mListAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPageIndex = 1;
        reqData(mPageIndex);
    }

    @Override
    public void onErrorClick() {
        super.onErrorClick();
        mPageIndex = 1;
        reqData(mPageIndex);
    }

    public static FragmentConsultationCollection newInstance(Bundle args) {
        FragmentConsultationCollection f = new FragmentConsultationCollection();
        f.setArguments(args);
        return f;
    }

    private void reqData(int pageIndex) {
        ReqManager.getInstance().reqCollectionExpertList(reqGetUcarTypeListCallBack, pageIndex, Utils.getUserToken(getActivity()));
    }

    /**
     * 刷新类型待处理数量接口回调
     */
    Callback<RespGetCollectionExpertList> reqGetUcarTypeListCallBack = new Callback<RespGetCollectionExpertList>() {

        @Override
        public void onBefore(Request request) {
            super.onBefore(request);
        }

        @Override
        public void onResponse(RespGetCollectionExpertList response) {
            mRecyclerView.loadMoreComplete();
            mRecyclerView.refreshComplete();
            hideErrorLayout();
            if (onSuccess(response)) {
                if(response.getResult() != null && response.getResult().getList() != null){
                    mDataList = response.getResult().getList();
                    mListAdapter.notifyData(mDataList, mPageIndex == 1);
                } else {
                    mListAdapter.notifyData(new ArrayList<EntityExpertInfo>(), mPageIndex == 1);
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
        public void onError(Call call, Exception e) {
            onFailure(e.toString());
            if(mListAdapter.getItemCount() <= 0){
                showErrorLayout(getString(R.string.error_message), R.mipmap.ic_empty_img);
            }
            mRecyclerView.loadMoreComplete();
            mRecyclerView.refreshComplete();
        }
    };
}
