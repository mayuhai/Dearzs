package com.dearzs.app.widget;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dearzs.app.R;
import com.dearzs.commonlib.utils.GetViewUtil;

/**
 * 继承自SwipeRefreshLayout,从而实现滑动到底部时上拉加载更多的功能
 */
public class RefreshLayout extends SwipeRefreshLayout implements OnScrollListener, SwipeRefreshLayout.OnRefreshListener {
    /**
     * 滑动到最下面时的上拉操作
     */
    private int mTouchSlop;
    /**
     * listview实例
     */
    private ListView mListView;

    /**
     * 下拉监听器
     */
    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener;

    /**
     * 上拉监听器, 到了最底部的上拉加载操作
     */
    private OnLoadListener mOnLoadListener;
    /**
     * ListView的加载中footer
     */
    private View mListViewFooter;
    private ProgressBar mFooterProgress;
    private TextView mFooterText;

    /**
     * 按下时的y坐标
     */
    private int mYDown;
    /**
     * 抬起时的y坐标, 与mYDown一起用于滑动到底部时判断是上拉还是下拉
     */
    private int mLastY;
    /**
     * 是否在加载中 ( 上拉加载更多 )
     */
    private boolean isLoading = false;

    private OnScrollListener mOnScrollListener;

    public RefreshLayout(Context context) {
        this(context, null);
    }

    public RefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mListViewFooter = LayoutInflater.from(context).inflate(R.layout.listview_footer, null,
                false);
        mFooterProgress = GetViewUtil.getView(mListViewFooter, R.id.pull_to_refresh_load_progress);
        mFooterText = GetViewUtil.getView(mListViewFooter, R.id.pull_to_refresh_loadmore_text);
        super.setOnRefreshListener(this);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (mListView != null || getChildCount() <= 0) return;
        View childView = getChildAt(0);
        if (childView instanceof ListView) {
            mListView = (ListView) childView;
            // 设置滚动监听器给ListView, 使得滚动的情况下也可以自动加载
            mListView.setOnScrollListener(this);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mYDown = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                mLastY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                if (canLoad()) {
                    loadData();
                }
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    /**
     * 首次添加一个空的头试图
     *
     * @param mListView
     * @param mBaseAdapter
     */
    public void setAdapter(ListView mListView, BaseAdapter mBaseAdapter) {

        mListViewFooter.setVisibility(View.GONE);
        mListView.addFooterView(mListViewFooter);
        mListView.setAdapter(mBaseAdapter);
        mListView.setVisibility(View.GONE);
    }

    /**
     * 是否可以加载更多, 条件是到了最底部, listview不在加载中, 且为上拉操作要加载更多
     */
    private boolean canLoad() {
        /** 是否是上拉操作 */
        boolean isPullUp = (mYDown - mLastY) >= mTouchSlop;

        /** 是否到底部*/
        boolean isBottom = false;
        if (mListView != null && mListView.getAdapter() != null) {
            isBottom = mListView.getLastVisiblePosition() == (mListView
                    .getAdapter().getCount() - 1);
        }
        return isBottom && isPullUp && !isLoading;
    }

    /**
     * 如果到了最底部,而且是上拉操作.那么执行onLoad方法
     */
    private void loadData() {
        if (mOnLoadListener == null || mListView == null) {
            return;
        }
        mOnLoadListener.onLoad();

        /**
         * 调用加载更多了
         * 显示 更多 文字
         * 显示 加载进度
         */
        if (mListView.getFooterViewsCount() <= 0) {
            mListView.addFooterView(mListViewFooter);
        }
        if (mListViewFooter.getVisibility() == View.GONE) {
            mListViewFooter.setVisibility(View.VISIBLE);
        }
        mFooterProgress.setVisibility(View.VISIBLE);
        mFooterText.setText("加载中...");

        isLoading = true;
    }

    /**
     * 设置加载状态
     */
    public void updateLoadState(boolean loading) {
        if (!loading) {
            mYDown = 0;
            mLastY = 0;
        }
        isLoading = loading;
    }

    public void setOnRefreshListener(final SwipeRefreshLayout.OnRefreshListener listener) {
        this.mOnRefreshListener = listener;
    }

    @Override
    public void onRefresh() {
        if (mOnRefreshListener == null) {
            return;
        }

        // 下拉刷新时，移除更多按钮
        if (mListView.getFooterViewsCount() > 0) {
            mListView.removeFooterView(mListViewFooter);
        }

        // 回调刷新操作
        mOnRefreshListener.onRefresh();
    }

    /**
     * 设置加载更多的回调
     */
    public void setOnLoadListener(OnLoadListener loadListener) {
        mOnLoadListener = loadListener;
        // 为null并且footer处于显示状态，则将状态改为已加载完全部
        if (loadListener != null) return;
        if (mListViewFooter.getVisibility() == View.GONE) {
            mListView.removeFooterView(mListViewFooter);
        } else {
            mFooterText.setText("已经全部加载完毕");
            mFooterProgress.setVisibility(View.GONE);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (mOnScrollListener != null) {
            mOnScrollListener.onScrollChanged(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
        // 滚动时到了最底部也可以加载更多
        if (canLoad()) {
            loadData();
        }
    }

    /**
     * 加载更多的监听器
     */
    public interface OnLoadListener {
        public void onLoad();
    }

    public void setOnScrollListener(OnScrollListener nnScrollListener) {
        this.mOnScrollListener = nnScrollListener;
    }

    public interface OnScrollListener {
        void onScrollChanged(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount);
    }
}
