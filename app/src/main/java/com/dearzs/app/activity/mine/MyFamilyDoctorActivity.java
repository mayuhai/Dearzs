package com.dearzs.app.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.dearzs.app.R;
import com.dearzs.app.adapter.LvFamilyDoctorListAdapter;
import com.dearzs.app.adapter.LvMessageListAdapter;
import com.dearzs.app.base.BaseActivity;
import com.dearzs.app.chat.model.EntityEvent;
import com.dearzs.app.chat.model.NomalConversation;
import com.dearzs.app.chat.model.TimManager;
import com.dearzs.app.entity.EntityMessage;
import com.dearzs.app.entity.resp.RespGetMessageList;
import com.dearzs.app.util.ReqManager;
import com.dearzs.app.util.Utils;
import com.dearzs.app.widget.MyTabBar;
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
 * Created by luyanlong on 2017/2/09.
 * 我的家庭医生页
 */
public class MyFamilyDoctorActivity extends BaseActivity implements MyTabBar.OnTabClickListener{
    private final String[] mTabTitles = new String[]{"医生订单", "未参加", "已参加"};
    private List<String> mDataList;
    private LvFamilyDoctorListAdapter mListAdapter;
    private int mPageIndex;
    private XRecyclerView mRecyclerView;
    private MyTabBar mTabBar;
    private View mEmptyView;

    private int mType = 0;
//    private View mEnptyLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_message);

        addCenter(TitleBarView.TITLE_TYPE_CENTER_TXT, "家庭医生");
        addLeftBtn(TitleBarView.TITLE_TYPE_LEFT_IV_BACK, null);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void initData() {
        super.initData();
        mListAdapter = new LvFamilyDoctorListAdapter(MyFamilyDoctorActivity.this, mDataList = new ArrayList<String>());
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
        initTabBarView();
        mRecyclerView = getView(R.id.message_list);
        mEmptyView = getView( R.id.mine_custom_layout_empty);

        LinearLayoutManager layoutManager = new LinearLayoutManager(MyFamilyDoctorActivity.this);
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
                if(mType == 0){ //只有系统消息才发请求
                    reqData(mPageIndex);
                }
            }

            @Override
            public void onLoadMore() {
                if(mType == 0){ //只有系统消息才发请求
                    reqData(++mPageIndex);
                }
            }
        });
    }

    private void initTabBarView() {
        mTabBar = getView(R.id.message_tabbar);
        mTabBar.setNormalBgColor(R.color.white);
        mTabBar.setSelectedBgColor(R.color.holo_orange_dark);
        mTabBar.setSelectedTextColor(R.color.green);
        mTabBar.setNormalTextColor(R.color.gray_1);
        mTabBar.setIsScrollable(true);              //设置可滚动
        mTabBar.setMode(MyTabBar.MODE_LINE);//设置下面有标签的模式
        mTabBar.setTabClickListener(this);//文字显示
        mTabBar.setTitles(mTabTitles);
    }

    private void reqData(int pageIndex) {
        ReqManager.getInstance().reqMessageList(reqMessageListCall, pageIndex, ReqManager.KEY_PAGE_SIZE, Utils.getUserToken(MyFamilyDoctorActivity.this));
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
                    mDataList.add("育儿科名医指导，让宝宝更健康");
                    mDataList.add("产科名医指导，让宝宝更健康");
                    mDataList.add("妇产科名医指导，让妈妈更美丽");
//                    mListAdapter.notifyData(mDataList, mPageIndex == 1);
                    mListAdapter.notifyData(mDataList, false);
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
        intent.setClass(ctx, MyFamilyDoctorActivity.class);
        ctx.startActivity(intent);
    }


    @Override
    public void onTabClick(int position) {
        mType = position;
        if(mRecyclerView != null){
            mRecyclerView.setLoadingMoreEnabled(true);
            mRecyclerView.setPullRefreshEnabled(true);
            mPageIndex = 1;
            reqData(mPageIndex);
        }
    }
}
