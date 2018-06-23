package com.dearzs.app.activity.family;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dearzs.app.R;
import com.dearzs.app.adapter.LvFamilyDoctorPackageListAdapter;
import com.dearzs.app.adapter.LvPatientEvaluationListAdapter;
import com.dearzs.app.base.BaseActivity;
import com.dearzs.app.entity.EntityComment;
import com.dearzs.app.entity.EntityFamilyDoctorPackage;
import com.dearzs.app.entity.resp.RespGetExpertCommentList;
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

import okhttp3.Call;
import okhttp3.Request;

/**
 * Created by luyanlong on 2016/12/14.
 * 家庭医生详情页
 */

public class FamilyDocDetailActivity extends BaseActivity implements MyTabBar.OnTabClickListener{
    private String[] mTabTitles = new String[]{"产品详情", "评论", "购买须知"};
    private XRecyclerView mRecyclerView;
    private LvPatientEvaluationListAdapter mListAdapter;
    private View mEmptyView;
    private int mPageIndex;
    private View mHeadView;
    private ImageView mProductImage;

    private TextView mProductTitle;
    private TextView mProductCurPrice;
    private TextView mProductOrgPrice;
    private TextView mProductBuyedCount;
    private TextView mProductButton;
    private MyTabBar mTabBar;
    private XRecyclerView mPackageRecyclerView;
    private LvFamilyDoctorPackageListAdapter mPackageListAdapter;
    private List<EntityFamilyDoctorPackage> mPackageData;
    private int mType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_family_doctor_detail);

        addCenter(TitleBarView.TITLE_TYPE_CENTER_TXT, "家庭医生详情");
        addLeftBtn(TitleBarView.TITLE_TYPE_LEFT_IV_BACK, null);
    }

    @Override
    public void initView() {
        super.initView();
        mRecyclerView = getView(R.id.family_doctor_detail_list);
        mEmptyView = getView( R.id.family_doctor_layout_empty);

        mHeadView = LayoutInflater.from(FamilyDocDetailActivity.this).inflate(R.layout.view_family_doctor_details_list_header_layout, null);
        initHeadView(mHeadView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(FamilyDocDetailActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addHeaderView(mHeadView);

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

    private void initTabBarView() {
        mTabBar = getView(mHeadView, R.id.family_doctor_detail_tabbar);
        mTabBar.setNormalBgColor(R.color.white);
        mTabBar.setSelectedBgColor(R.color.holo_orange_dark);
        mTabBar.setSelectedTextColor(R.color.green);
        mTabBar.setNormalTextColor(R.color.gray_1);
        mTabBar.setIsScrollable(true);              //设置可滚动
        mTabBar.setMode(MyTabBar.MODE_LINE);//设置下面有标签的模式
        mTabBar.setTabClickListener(this);//文字显示
        mTabBar.setTitles(mTabTitles);
    }

    private void initHeadView(View headView){
        initTabBarView();
        mPackageRecyclerView = getView(headView, R.id.rv_family_doctor_package);
        mProductImage = getView(headView, R.id.iv_item_family_doctor);
        mProductTitle = getView(headView, R.id.tv_item_family_doctor_title);
        mProductOrgPrice = getView(headView, R.id.tv_item_family_doctor_org_price);
        mProductCurPrice = getView(headView, R.id.tv_item_family_doctor_cur_price);
        mProductBuyedCount = getView(headView, R.id.tv_item_family_doctor_buy_count);
        mProductButton = getView(headView, R.id.btn_item_family_doctor);

        mProductImage.setOnClickListener(this);

        GridLayoutManager layoutManager = new GridLayoutManager(FamilyDocDetailActivity.this, 2);
        mPackageRecyclerView.setLayoutManager(layoutManager);
        mPackageRecyclerView.addFootView(new View(FamilyDocDetailActivity.this));
        mPackageRecyclerView.addHeaderView(new View(FamilyDocDetailActivity.this));

        mPackageRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mPackageRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
        mPackageRecyclerView.setArrowImageView(R.mipmap.ic_down_arrow);
    }

    private void bindHeadViewData(){

    }

    private void reqData(int pageIndex){
        mPackageListAdapter = new LvFamilyDoctorPackageListAdapter(FamilyDocDetailActivity.this, new ArrayList<EntityFamilyDoctorPackage>());
        mPackageRecyclerView.setAdapter(mPackageListAdapter);
        mPackageData = new ArrayList<EntityFamilyDoctorPackage>();
        EntityFamilyDoctorPackage entity1 = new EntityFamilyDoctorPackage();
        entity1.setPackageName("一个月名医护理");
        entity1.setChecked(false);
        EntityFamilyDoctorPackage entity2 = new EntityFamilyDoctorPackage();
        entity2.setPackageName("三个月名医护理");
        entity2.setChecked(false);
        EntityFamilyDoctorPackage entity3 = new EntityFamilyDoctorPackage();
        entity3.setPackageName("六个月名医护理");
        entity3.setChecked(false);
        mPackageData.add(entity1);
        mPackageData.add(entity2);
        mPackageData.add(entity3);
        mPackageListAdapter.notifyData(mPackageData);
        ReqManager.getInstance().reqExpertCommentList(reqCommentListCallback, Utils.getUserToken(FamilyDocDetailActivity.this), "1", pageIndex);
    }

    @Override
    public void initData() {
        super.initData();
        mListAdapter = new LvPatientEvaluationListAdapter(FamilyDocDetailActivity.this, new ArrayList<EntityComment>(), 0);
        mRecyclerView.setAdapter(mListAdapter);
        mPageIndex = 1;
        reqData(mPageIndex);
    }

    //家庭医生评价列表接口回调
    Callback<RespGetExpertCommentList> reqCommentListCallback = new Callback<RespGetExpertCommentList>() {
        @Override
        public void onError(Call call, Exception e) {
            closeProgressDialog();
            mRecyclerView.loadMoreComplete();
            //请求失败，设置加载和刷新完毕
            onFailure(e.toString());
        }

        @Override
        public void onResponse(RespGetExpertCommentList response) {
            closeProgressDialog();
            mRecyclerView.loadMoreComplete();
            mRecyclerView.refreshComplete();
            //请求成功，设置加载和刷新完毕
            if (onSuccess(response)) {
                hideErrorLayout();
                final List<EntityComment> list = new ArrayList<EntityComment>();
//                        response.getResult().getList();
                if(list !=null){
//                    list.clear();
                    list.add(response.getResult().getList().get(0));
                    mListAdapter.notifyData(list, mPageIndex == 1);
                    mRecyclerView.setLoadingMoreEnabled(list.size() >= ReqManager.KEY_PAGE_SIZE);
                } else {
                    mRecyclerView.setLoadingMoreEnabled(false);
                }
            }
        }

        @Override
        public void onBefore(Request request) {
            showProgressDialog();
            super.onBefore(request);
        }
    };

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.iv_item_family_doctor:
                FamilyDocOrderConfirmActivity.startIntent(FamilyDocDetailActivity.this, null, 0 ,false);
                break;
        }
    }

    //消息列表接口回调
    Callback<RespGetMessageList> reqMessageListCall = new Callback<RespGetMessageList>() {
        @Override
        public void onError(Call call, Exception e) {
            mRecyclerView.refreshComplete();
            mRecyclerView.loadMoreComplete();
            onFailure(e.toString());
        }

        @Override
        public void onResponse(RespGetMessageList response) {
            mRecyclerView.refreshComplete();
            mRecyclerView.loadMoreComplete();
            if (onSuccess(response)) {
                if (response.getResult() != null && response.getResult().getList() != null) {
                }
            }
        }

        @Override
        public void onBefore(Request request) {
            super.onBefore(request);
            showProgressDialog();
        }
    };


    @Override
    public void onTabClick(int position) {
        mType = (position == 1) ? 0 : 1;
        mRecyclerView.setLoadingMoreEnabled(position == 1);
        mRecyclerView.setPullRefreshEnabled(position == 1);
        List<EntityComment> list = new ArrayList<EntityComment>();
        list.add(new EntityComment());
//        list.add(new EntityComment());
//        list.add(new EntityComment());
        if(mListAdapter != null){
            mListAdapter.notifyData(list, mPageIndex == 1, mType);
        }
    }

    /**
     * Activity跳转
     */
    public static void startIntent(Context ctx, long id) {
        Intent intent = new Intent();
        intent.setClass(ctx, FamilyDocDetailActivity.class);
        ctx.startActivity(intent);
    }

}
