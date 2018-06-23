package com.dearzs.app.base;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ListView;

import com.dearzs.app.R;
import com.dearzs.app.adapter.basic.QuickAdapter;
import com.dearzs.app.util.Constant;
import com.dearzs.app.widget.RefreshLayout;

import java.util.List;

/**
 * 通用下拉刷新，上拉加载更多的页面(Activity)
 * 只用于单一列表页实现
 */
abstract public class BaseRefreshActivity extends BaseActivity implements RefreshLayout.OnLoadListener {

    /** 下拉刷新控件*/
    protected RefreshLayout mRefreshLayout;
    /** 普通的listView */
    protected ListView mBaseListView;
    /** 列表适配器*/
    protected QuickAdapter mBaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 通用下拉刷新，上拉加载更多的布局
        setContentLayout(getContentLayoutId());
        initTitleBar();
    }

    public int getContentLayoutId() {
        return R.layout.activity_base_refresh;
    }

    abstract public void initTitleBar();

    @Override
    public void initView() {
        super.initView();

        // 初始化列表相关数据
        mBaseListView = getView(R.id.base_refresh_activity_lv);
        mBaseListView.setDivider(getResources().getDrawable(R.color.line));
        mBaseListView.setDividerHeight((int) getResources().getDimension(R.dimen.dimens_0_5));
        mRefreshLayout = (RefreshLayout) mBaseListView.getParent();
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setColorSchemeResources(R.color.holo_blue_bright,
                R.color.holo_green_light, R.color.holo_orange_light,
                R.color.holo_red_light);

        initAdapter();
    }

    abstract public void initAdapter();

    @Override
    public void initData() {
        super.initData();
        onRefresh();
    }

    /**
     * 发布加载数据的事件
     *
     * @param getType 数据加载类型
     */
    private void sendLoadDataEvent(int getType) {
        Message msg = new Message();
        msg.arg2 = getType;
        msg.what = Constant.PAGE_LOAD_ING;
        mUIHandler.sendMessage(msg);
    }

    protected final Handler mUIHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            BaseRefreshActivity.this.handleMessage(msg);
        }
    };

    /** =========更新列表数据=============== */
    private void updateListViewData(int pagecount, List<?> mDatas, int getType) {
        if (mBaseListView == null)
            return;

        hideErrorLayout();
        mRefreshLayout.updateLoadState(false);
        // 数据加载失败时，不考虑修改相应值
        if (mDatas == null || mDatas.isEmpty()) {
            if ((pagecount != Constant.INVALID && mBaseAdapter != null && mBaseAdapter.getCount() > 0)) {
                BaseRefreshActivity.this.showErrorDialog("获取数据为空...");
            } else {
                showEmptyLayout(getEmptyStr(), getEmptySrc());
                //mMyBuySellInfoAdapter.addData(null, true);
                //mRecordListView.setVisibility(View.GONE);
            }
        }
        // 数据加载成功时, 根据获取类型判断
        else {
            // 若是刷新数据,则将页码调整为1,否则页码自动加1
            if (getType == Constant.REFRESH_DATA) {
                curPageIndex = 1;
            } else {
                ++curPageIndex;
            }
            // 若页码大于等于总页数时,下拉刷新功能将隐藏
            if (curPageIndex >= pagecount) {
                mRefreshLayout.setOnLoadListener(null);
            } else {
                mRefreshLayout.setOnLoadListener(this);
            }

            if (curPageIndex == 1) {
                mBaseAdapter.replaceAll(mDatas);
            } else {
                mBaseAdapter.addAll(mDatas);
            }
            mBaseListView.setVisibility(View.VISIBLE);
            // 若为刷新数据，则置顶
            if (curPageIndex == 1) {
                mBaseListView.setSelection(0);
            }
        }
    }

    public String getEmptyStr() {
        return "暂无记录";
    }

    public int getEmptySrc(){
        return -1;
    }

    /** 默认当前为第一页数据*/
    private int curPageIndex = 1;
    private void handleMessage(Message msg) {
        if (msg.what == Constant.PAGE_LOAD_RESULT) {
            updateListViewData(msg.arg1, (List<?>)msg.obj, msg.arg2);
        } else if (msg.what == Constant.PAGE_LOAD_ING) {
            // 调出加载动画
            mRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mRefreshLayout.setRefreshing(true);
                }
            });
            // 若是加载更多,默认请求下一页
            int curPageIndex = 1;
            if (msg.arg2 == Constant.GET_MORE_DATA) {
                curPageIndex = this.curPageIndex + 1;
            } else if (msg.arg2 == Constant.REFRESH_DATA) {
            } else {
                curPageIndex = this.curPageIndex;
            }
            Message msg2 = new Message();
            msg2.arg1 = curPageIndex;
            msg2.arg2 = msg.arg2;
            msg2.what = Constant.PAGE_LOAD_DELAY_ING;
            mUIHandler.sendMessageDelayed(msg2, 300);
        } else if (msg.what == Constant.PAGE_LOAD_DELAY_ING) {
            handleReq(msg.arg1, msg.arg2);
        }
    }

    /** 拼装请求参数*/
    abstract public void handleReq(int curPageIndex, int getType);

    /** 进度条销毁的同时,下拉刷新状态也要销毁*/
    public void closeProgressDialog() {
        super.closeProgressDialog();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mRefreshLayout == null) return;
                mRefreshLayout.setRefreshing(false);
                mRefreshLayout.updateLoadState(false);
            }
        });
    }

    @Override
    public void onRefresh() {
        sendLoadDataEvent(Constant.REFRESH_DATA);
    }

    @Override
    public void onLoad() {
        sendLoadDataEvent(Constant.GET_MORE_DATA);
    }
}