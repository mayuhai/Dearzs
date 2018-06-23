package com.dearzs.app.activity.forum;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.dearzs.app.R;
import com.dearzs.app.adapter.GvDoctorForumListAdapter;
import com.dearzs.app.base.BaseActivity;
import com.dearzs.app.entity.EntityDoctorForumInfo;
import com.dearzs.app.entity.EntityDoctorForumTypes;
import com.dearzs.app.entity.resp.RespDoctorForumList;
import com.dearzs.app.entity.resp.RespDoctorForumTypes;
import com.dearzs.app.util.ReqManager;
import com.dearzs.app.util.Utils;
import com.dearzs.app.widget.DividerGridItemDecoration;
import com.dearzs.app.widget.MyTabBar;
import com.dearzs.app.widget.TitleBarView;
import com.dearzs.commonlib.okhttp.callback.Callback;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Request;

/**
 * Created by luyanlong on 2016/10/24.
 * 专家讲堂首页面
 */

public class DoctorForumActivity extends BaseActivity implements MyTabBar.OnTabClickListener {
    private MyTabBar mTabBar;
    private XRecyclerView mRecyclerView;
    private GvDoctorForumListAdapter mDataListAdapter;
    private String[] mTabTitles;
    private long[] mTabTitleIds;
    private int mPageIndex;
    private long mTypeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_doctor_forum);
        addCenter(TitleBarView.TITLE_TYPE_CENTER_TXT, "名医讲堂");
        addLeftBtn(TitleBarView.TITLE_TYPE_LEFT_IV_BACK, null);
    }

    private void initTabBarView() {
        mTabTitles = new String[]{};
        mTabBar = getView(R.id.tab_expert_forum);
        mTabBar.setNormalBgColor(R.color.white);
        mTabBar.setSelectedBgColor(R.color.holo_orange_dark);
        mTabBar.setSelectedTextColor(R.color.green);
        mTabBar.setNormalTextColor(R.color.gray_1);
        mTabBar.setIsScrollable(true);              //设置可滚动
        mTabBar.setMode(MyTabBar.MODE_LINE);//设置下面有标签的模式
        mTabBar.setTabClickListener(this);//文字显示
        mTabBar.setTitles(mTabTitles);
    }

    @Override
    public void onRightBtnClick() {
        super.onRightBtnClick();
    }

    @Override
    public void initView() {
        super.initView();
        initTabBarView();
        initRecyclerView();
    }

    private void initRecyclerView() {
        mRecyclerView = getView(R.id.rv_expert_forum);

        GridLayoutManager layoutManager = new GridLayoutManager(DoctorForumActivity.this, 2);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
        mRecyclerView.setArrowImageView(R.mipmap.ic_down_arrow);
        mRecyclerView.setPullRefreshEnabled(true);
        mRecyclerView.setLoadingMoreEnabled(true);
        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                mPageIndex = 1;
                reqListData(mTypeId, mPageIndex);
            }

            @Override
            public void onLoadMore() {
                reqListData(mTypeId, ++mPageIndex);
            }
        });
    }

    @Override
    public void onTabClick(int position) {
        mTypeId = mTabTitleIds[position];
        mPageIndex = 1;
        reqListData(mTypeId, mPageIndex);
    }

    private void reqListData(long typeId, int pageIndex) {
        ReqManager.getInstance().reqDoctorForumList(reqDoctorForumListCallback, pageIndex, Utils.getUserToken(DoctorForumActivity.this), typeId);
    }

    @Override
    public void initData() {
        super.initData();
        mDataListAdapter = new GvDoctorForumListAdapter(DoctorForumActivity.this, new ArrayList<EntityDoctorForumInfo>());
        mRecyclerView.setAdapter(mDataListAdapter);
        ReqManager.getInstance().reqDoctorForumTypes(reqTypesCallback, Utils.getUserToken(DoctorForumActivity.this));
    }

    //名医讲堂Type接口回调
    Callback<RespDoctorForumTypes> reqTypesCallback = new Callback<RespDoctorForumTypes>() {
        @Override
        public void onError(Call call, Exception e) {
            closeProgressDialog();
            //请求失败，设置加载和刷新完毕
            onFailure(e.toString());
            showErrorDialog("数据发生错误,请检查网络", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ReqManager.getInstance().reqDoctorForumTypes(reqTypesCallback, Utils.getUserToken(DoctorForumActivity.this));
                }
            });
        }

        @Override
        public void onResponse(RespDoctorForumTypes response) {
            closeProgressDialog();
            //请求成功，设置加载和刷新完毕
            if (onSuccess(response)) {
                if (response.getResult() != null && response.getResult().getList() != null && response.getResult().getList().size() > 0) {
                    hideErrorLayout();
                    List<EntityDoctorForumTypes> list = response.getResult().getList();
                    EntityDoctorForumTypes types = new EntityDoctorForumTypes();
                    types.setId(0);
                    types.setName("全部");
                    list.add(0, types);
                    setTabTitles(list);
                    mPageIndex = 1;
                    reqListData(mTabTitleIds[0], mPageIndex);
                } else {
                    showErrorDialog("数据发生错误,请检查网络", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ReqManager.getInstance().reqDoctorForumTypes(reqTypesCallback, Utils.getUserToken(DoctorForumActivity.this));
                        }
                    });
                }
            }
        }

        @Override
        public void onBefore(Request request) {
            showProgressDialog();
            super.onBefore(request);
        }
    };

    //名医讲堂列表接口回调
    Callback<RespDoctorForumList> reqDoctorForumListCallback = new Callback<RespDoctorForumList>() {
        @Override
        public void onError(Call call, Exception e) {
            mRecyclerView.refreshComplete();
            mRecyclerView.loadMoreComplete();
            closeProgressDialog();
            //请求失败，设置加载和刷新完毕
            onFailure(e.toString());
            showErrorDialog("数据发生错误,请检查网络", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reqListData(mTypeId, mPageIndex);
                }
            });
        }

        @Override
        public void onResponse(RespDoctorForumList response) {
            closeProgressDialog();
            mRecyclerView.refreshComplete();
            mRecyclerView.loadMoreComplete();
            //请求成功，设置加载和刷新完毕
            if (onSuccess(response)) {
                if (response.getResult() != null && response.getResult().getList() != null) {
                    hideErrorLayout();
                    List<EntityDoctorForumInfo> list = response.getResult().getList();
                    mDataListAdapter.notifyData(list, mPageIndex == 1);
                    mRecyclerView.setLoadingMoreEnabled(list.size() >= 10);
                } else {
                    showErrorDialog("数据发生错误,请检查网络", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            reqListData(mTypeId, mPageIndex);
                        }
                    });
                }
            }
        }

        @Override
        public void onBefore(Request request) {
            showProgressDialog();
            super.onBefore(request);
        }
    };

    public void setTabTitles(List<EntityDoctorForumTypes> tabTitles) {
        String[] titles = new String[tabTitles.size()];
        long[] ids = new long[tabTitles.size()];
        for (int i = 0; i < tabTitles.size(); i++) {
            ids[i] = tabTitles.get(i).getId();
            titles[i] = tabTitles.get(i).getName();
        }
        mTabTitles = titles;
        mTabTitleIds = ids;
        mTabBar.setTitles(mTabTitles);
    }

    public static void startIntent(Context context) {
        Intent intent = new Intent(context, DoctorForumActivity.class);
        context.startActivity(intent);
    }
}
