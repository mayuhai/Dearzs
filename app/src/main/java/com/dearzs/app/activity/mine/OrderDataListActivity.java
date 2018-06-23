package com.dearzs.app.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;

import com.dearzs.app.R;
import com.dearzs.app.adapter.LvOrderDataListAdapter;
import com.dearzs.app.base.BaseActivity;
import com.dearzs.app.entity.EntityConsultation;
import com.dearzs.app.entity.EntityEvent;
import com.dearzs.app.entity.EntityOrderInfo;
import com.dearzs.app.entity.resp.RespOrderInfoList;
import com.dearzs.app.util.Constant;
import com.dearzs.app.util.ReqManager;
import com.dearzs.app.util.Utils;
import com.dearzs.app.widget.TitleBarView;
import com.dearzs.commonlib.okhttp.callback.Callback;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import okhttp3.Call;
import okhttp3.Request;

/**
 * Created by mayuhai on 2016/6/8.
 * 我的订单列表
 */
public class OrderDataListActivity extends BaseActivity {
    private List<EntityOrderInfo> mDataList;
    private LvOrderDataListAdapter mListAdapter;
    private int mPageIndex;
    private XRecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_order);

        addCenter(TitleBarView.TITLE_TYPE_CENTER_TXT, "我的订单");
        addLeftBtn(TitleBarView.TITLE_TYPE_LEFT_IV_BACK, null);
    }

    @Override
    public void initData() {
        super.initData();
        mPageIndex = 1;
        reqData(mPageIndex);
        mListAdapter = new LvOrderDataListAdapter(OrderDataListActivity.this, mDataList = new ArrayList<EntityOrderInfo>());
        mRecyclerView.setAdapter(mListAdapter);
    }

    @Override
    public void onLeftBtnClick() {
        super.onLeftBtnClick();
    }

    @Override
    public void initView() {
        super.initView();
        mRecyclerView = getView(R.id.medical_consultation_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(OrderDataListActivity.this);
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
//        ReqManager.getInstance().reqConsultationList(reqMedicalConsultationListCall, pageIndex, ReqManager.KEY_PAGE_SIZE, Utils.getUserToken(OrderDataListActivity.this));
        ReqManager.getInstance().reqOrderInfoList(reqMedicalConsultationListCall, Utils.getUserToken(OrderDataListActivity.this), null, pageIndex);
    }

    //专家列表接口回调
    Callback<RespOrderInfoList> reqMedicalConsultationListCall = new Callback<RespOrderInfoList>() {
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
        public void onResponse(RespOrderInfoList response) {
            mRecyclerView.refreshComplete();
            mRecyclerView.loadMoreComplete();
            if (onSuccess(response)) {
                if (response.getResult() != null && response.getResult().getList() != null) {
                    mDataList = response.getResult().getList();
                    mListAdapter.notifyData(mDataList, mPageIndex == 1);
                    mRecyclerView.setLoadingMoreEnabled(mDataList != null && mDataList.size() >= ReqManager.KEY_PAGE_SIZE);
                } else {
                    mRecyclerView.setLoadingMoreEnabled(false);
                }
                if (mListAdapter.getItemCount() <= 0) {
                    showEmptyLayout(getString(R.string.empty_data_message), R.mipmap.ic_empty_img);
                }
            } else {
                if (mListAdapter.getItemCount() <= 0) {
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

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void eventOrderRefresh(EntityEvent.EventOrderRefresh event) {
        if (event != null && mListAdapter != null) {
            EntityOrderInfo item = event.getEntityOrderInfo();
            if(item == null || TextUtils.isEmpty(item.getOrderNo())) return;
            for(int i=0;i<mListAdapter.getItemCount();i++){
                if(TextUtils.isEmpty(mListAdapter.getItem(i).getOrderNo())) return;
                if(mListAdapter.getItem(i).getOrderNo().equals(item.getOrderNo())){
                    if (event.getType() == EntityEvent.EventOrderRefresh.TYPE_ORDER_DELETE) {//订单删除
                        mListAdapter.delItemView(i);
                    } else if (event.getType() == EntityEvent.EventOrderRefresh.TYPE_ORDER_REFRESH) {//订单更新
                        mListAdapter.refreshItemView(item, i);
                    }
                }
            }
        }
    }

    /**
     * Activity跳转
     *
     * @param b
     */
    public static void startIntent(Context ctx, Bundle b) {
        Intent intent = new Intent();
        intent.setClass(ctx, OrderDataListActivity.class);
        if (null != b) {
            intent.putExtras(b);
        }
        ctx.startActivity(intent);
    }
}
