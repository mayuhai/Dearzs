package com.dearzs.app.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import com.dearzs.app.R;
import com.dearzs.app.adapter.LvMedicalConsultationListAdapter;
import com.dearzs.app.adapter.LvPaymentsBalanceListAdapter;
import com.dearzs.app.base.BaseActivity;
import com.dearzs.app.entity.EntityPaymentsBalance;
import com.dearzs.app.entity.resp.RespGetPaymentsBalanceList;
import com.dearzs.app.util.ReqManager;
import com.dearzs.app.util.Utils;
import com.dearzs.app.widget.TitleBarView;
import com.dearzs.commonlib.okhttp.callback.Callback;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Request;

/**
 * Created by Lyl on 2016/6/12.
 * 收支明细列表
 */
public class PaymentsBalanceListActivity extends BaseActivity {
    private List<EntityPaymentsBalance> mDataList;
    private LvPaymentsBalanceListAdapter mListAdapter;
    private int mPageIndex;
    private XRecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_payments_balance_list);

        addCenter(TitleBarView.TITLE_TYPE_CENTER_TXT, "收支明细");
        addLeftBtn(TitleBarView.TITLE_TYPE_LEFT_IV_BACK, null);
    }

    @Override
    public void initData() {
        super.initData();
        mListAdapter = new LvPaymentsBalanceListAdapter(PaymentsBalanceListActivity.this, mDataList = new ArrayList<EntityPaymentsBalance>());
        mRecyclerView.setAdapter(mListAdapter);
        reqData(1);
    }

    @Override
    public void initView() {
        super.initView();
        mRecyclerView = getView(R.id.payments_balance_list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(PaymentsBalanceListActivity.this);
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
        ReqManager.getInstance().reqPaymentsBalanceList(reqMedicalConsultationListCall, pageIndex, Utils.getUserToken(PaymentsBalanceListActivity.this));
    }

    //专家列表接口回调
    Callback<RespGetPaymentsBalanceList> reqMedicalConsultationListCall = new Callback<RespGetPaymentsBalanceList>() {
        @Override
        public void onError(Call call, Exception e) {
            mRecyclerView.refreshComplete();
            mRecyclerView.loadMoreComplete();
            onFailure(e.toString());
        }

        @Override
        public void onResponse(RespGetPaymentsBalanceList response) {
            mRecyclerView.refreshComplete();
            mRecyclerView.loadMoreComplete();
            if (onSuccess(response)) {
                if(response.getResult() != null && response.getResult().getList() != null){
                    mDataList = response.getResult().getList();
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
     *
     * @param b
     */
    public static void startIntent(Context ctx, Bundle b) {
        Intent intent = new Intent();
        intent.setClass(ctx, PaymentsBalanceListActivity.class);
        if (null != b) {
            intent.putExtras(b);
        }
        ctx.startActivity(intent);
    }
}
