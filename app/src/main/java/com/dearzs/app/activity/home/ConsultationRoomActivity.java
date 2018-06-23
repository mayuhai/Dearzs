package com.dearzs.app.activity.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.dearzs.app.BaseApplication;
import com.dearzs.app.R;
import com.dearzs.app.activity.HomeActivity;
import com.dearzs.app.adapter.LvConsultRoomListAdapter;
import com.dearzs.app.base.BaseActivity;
import com.dearzs.app.entity.EntityConsultInfo;
import com.dearzs.app.entity.resp.RespGetConsultList;
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
 * Created by lx on 2016/6/14.
 * 会诊室
 */
public class ConsultationRoomActivity extends BaseActivity {
    private XRecyclerView mRecyclerView;
    private List<EntityConsultInfo> mDataList;
    private LvConsultRoomListAdapter mListAdapter;
    private int mPageIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_consultation_room);
        addLeftBtn(TitleBarView.TITLE_TYPE_LEFT_IV_BACK, null);
        addCenter(TitleBarView.TITLE_TYPE_CENTER_TXT, "会诊室");
    }

    @Override
    public void initView() {
        super.initView();
        mRecyclerView = getView(R.id.consultation_room_list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(ConsultationRoomActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
        mRecyclerView.setArrowImageView(R.mipmap.ic_down_arrow);

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
    public void onErrorClick() {
        super.onErrorClick();
        mPageIndex = 1;
        reqData(mPageIndex);
    }

    @Override
    public void initData() {
        super.initData();
        mListAdapter = new LvConsultRoomListAdapter(ConsultationRoomActivity.this, mDataList = new ArrayList<EntityConsultInfo>());
        mRecyclerView.setAdapter(mListAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPageIndex = 1;
        reqData(mPageIndex);
    }

    private void reqData(int pageIndex) {
        ReqManager.getInstance().reqConsultList(reqMedicalConsultationListCall, pageIndex, ReqManager.KEY_PAGE_SIZE, Utils.getUserToken(ConsultationRoomActivity.this));
    }

    //会诊室列表接口回调
    Callback<RespGetConsultList> reqMedicalConsultationListCall = new Callback<RespGetConsultList>() {
        @Override
        public void onError(Call call, Exception e) {
            mRecyclerView.refreshComplete();
            mRecyclerView.loadMoreComplete();
            onFailure(e.toString());
            showErrorLayout("数据异常，请检查您的网络", R.mipmap.ic_empty_img);
        }

        @Override
        public void onResponse(RespGetConsultList response) {
            mRecyclerView.refreshComplete();
            mRecyclerView.loadMoreComplete();
            hideErrorLayout();
            if (onSuccess(response)) {
                if (response.getResult() != null && response.getResult().getList() != null) {
                    mDataList = response.getResult().getList();
                    mListAdapter.notifyData(mDataList, mPageIndex == 1);
                    if(mDataList.size() >= ReqManager.KEY_PAGE_SIZE){
                        mRecyclerView.setLoadingMoreEnabled(true);
                    } else {
                        mRecyclerView.setLoadingMoreEnabled(false);
                    }
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
    public void updateData(String update){
        if(Constant.EVENT_UPDATE_CONSUL_STATE.equals(update)){
            mPageIndex = 1;
            reqData(mPageIndex);
        }
    }

    /**
     * Activity跳转
     *
     */
    public static void startIntent(Context ctx) {
        Intent intent = new Intent();
        intent.setClass(ctx, ConsultationRoomActivity.class);
        ctx.startActivity(intent);
    }
}
