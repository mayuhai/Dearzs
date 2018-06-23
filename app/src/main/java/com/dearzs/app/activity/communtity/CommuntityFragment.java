package com.dearzs.app.activity.communtity;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dearzs.app.BaseApplication;
import com.dearzs.app.R;
import com.dearzs.app.activity.mine.PersionalDataActivity;
import com.dearzs.app.adapter.LvCommuntityDynamicListAdapter;
import com.dearzs.app.base.BaseFragment;
import com.dearzs.app.entity.EntityDynamicInfo;
import com.dearzs.app.entity.EntityUserInfo;
import com.dearzs.app.entity.resp.RespGetDynamicList;
import com.dearzs.app.util.Constant;
import com.dearzs.app.util.ReqManager;
import com.dearzs.app.util.Utils;
import com.dearzs.commonlib.okhttp.callback.Callback;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.tencent.connect.UserInfo;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Request;

/**
 * 社区界面fragment
 */
public class CommuntityFragment extends BaseFragment{
    private int mPageIndex = 1;
    private XRecyclerView mRecyclerView;
    private List<EntityDynamicInfo> mDataList;
    private LvCommuntityDynamicListAdapter mListAdapter;
    private ImageView mReleaseIv;

    @Override
    protected int inflateResource() {
        return R.layout.activity_communtity;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPageIndex = 1;
        reqData(mPageIndex);
    }

    @Override
    protected void initViews(View rootView) {
        mReleaseIv = getView(rootView, R.id.iv_release_dynamic);
        mRecyclerView = getView(rootView, R.id.community_dynamic_listview);
        mReleaseIv.setOnClickListener(this);
//        mEnptyLayout = getView(rootView, R.id.lv_empty_layout);

        //实例化RecyclerView的layoutManager 如果是GridView 则用GridLayoutManager 来实例化
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
        mRecyclerView.setArrowImageView(R.mipmap.ic_down_arrow);

        //设置数据为空时的页面展示
//        mRecyclerView.setEmptyView(mEnptyLayout);
//        mRecyclerView.getEmptyView().setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                reqData(mPageIndex);
//            }
//        });

        //设置是否可以下拉刷新和是否可以上拉加载更多
        mRecyclerView.setLoadingMoreEnabled(true);
        mRecyclerView.setPullRefreshEnabled(true);

        //设置下拉刷新和上拉加载更多的回调
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
    public void onErrorClick() {
        super.onErrorClick();
        mPageIndex = 1;
        reqData(mPageIndex);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == getActivity().RESULT_OK){
            if(requestCode == Constant.REQUEST_CODE_HOME_ACTIVITY){
                mPageIndex = 1;
                reqData(mPageIndex);
            }
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.iv_release_dynamic:
                Intent intent = new Intent(getActivity(), ReleaseDynamicActivity.class);
                startActivityForResult(intent, Constant.REQUEST_CODE_HOME_ACTIVITY);
                break;
        }
    }

    @Override
    public void initData() {
        mPageIndex = 1;
        reqData(mPageIndex);
        mListAdapter = new LvCommuntityDynamicListAdapter(getActivity(), mDataList = new ArrayList<EntityDynamicInfo>());
        mRecyclerView.setAdapter(mListAdapter);
    }

    private void reqData(int pageIndex){
        ReqManager.getInstance().reqCommuntityList(reqCommentListCall, Utils.getUserToken(getActivity()), pageIndex);
    }

    //专家评论列表接口回调
    Callback<RespGetDynamicList> reqCommentListCall = new Callback<RespGetDynamicList>() {
        @Override
        public void onError(Call call, Exception e) {
            //网络错误，
            //加载更多时，如果网络错误，请求页数应该减一以防漏掉数据
            if(mPageIndex > 1){
                mPageIndex --;
            }

            //请求失败，设置加载和刷新完毕
            mRecyclerView.loadMoreComplete();
            mRecyclerView.refreshComplete();
//            if(mListAdapter == null || mListAdapter.getItemCount() <= 0){
//                showErrorLayout(getString(R.string.error_message), R.mipmap.ic_empty_img);
//            }
            onFailure(e.toString());
        }

        @Override
        public void onResponse(RespGetDynamicList response) {
            //请求成功，设置加载和刷新完毕
            mRecyclerView.loadMoreComplete();
            mRecyclerView.refreshComplete();
            hideErrorLayout();
            if (onSuccess(response)) {
                mDataList = response.getResult().getList();
                mListAdapter.notifyData(mDataList, mPageIndex == 1);
//                if(mListAdapter.getItemCount() <= 0){
//                    showEmptyLayout(getString(R.string.empty_data_message), R.mipmap.ic_empty_img);
//                }
//            } else {
//                if(mListAdapter.getItemCount() <= 0) {
//                    showEmptyLayout(getString(R.string.error_message), R.mipmap.ic_empty_img);
//                }
            }
        }

        @Override
        public void onBefore(Request request) {
            super.onBefore(request);
        }
    };
}
