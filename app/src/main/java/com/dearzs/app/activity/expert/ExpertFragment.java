package com.dearzs.app.activity.expert;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dearzs.app.R;
import com.dearzs.app.activity.home.AppointmentConsultationActivity;
import com.dearzs.app.adapter.CustomSpinerAdapter;
import com.dearzs.app.adapter.LvExpertListAdapter;
import com.dearzs.app.base.BaseFragment;
import com.dearzs.app.entity.EntityBase;
import com.dearzs.app.entity.EntityBaseInfo;
import com.dearzs.app.entity.EntityExpertInfo;
import com.dearzs.app.entity.EntityHospitalDepartmentInfo;
import com.dearzs.app.entity.EntityHospitalInfo;
import com.dearzs.app.entity.EntityUser;
import com.dearzs.app.entity.resp.RespGetExpertCommentList;
import com.dearzs.app.entity.resp.RespGetExpertList;
import com.dearzs.app.entity.resp.RespGetHospitalDepartmentList;
import com.dearzs.app.entity.resp.RespGetHospitalList;
import com.dearzs.app.entity.resp.RespUserLogin;
import com.dearzs.app.util.Constant;
import com.dearzs.app.util.ReqManager;
import com.dearzs.app.util.ToastUtil;
import com.dearzs.app.util.Utils;
import com.dearzs.app.wheel.test.DateSelector;
import com.dearzs.app.widget.CustomSpinerPopWindow;
import com.dearzs.commonlib.okhttp.callback.Callback;
import com.dearzs.commonlib.utils.PfUtils;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.internal.Util;

/**
 * 子页面fragment
 */
public class ExpertFragment extends BaseFragment{
    private static final String SORT_BY_ORDER_NUMBER = "orderNumber";
    private static final String SORT_BY_SCORE = "star";
    private static final String SORT_BY_PRICE = "visitMoney";
    private static final String SORT_ASC = "asc";
    private static final String SORT_DESC = "desc";

    private CustomSpinerPopWindow mHospitalSpinerPopWindow;
    private CustomSpinerPopWindow mDepartmentSpinerPopWindow;
    private CustomSpinerPopWindow mTimeSpinerPopWindow;
    private List<EntityHospitalInfo> mHospitalList = new ArrayList<EntityHospitalInfo>();  //类型列表
    private List<EntityHospitalDepartmentInfo> mDepartmentList = new ArrayList<EntityHospitalDepartmentInfo>();  //类型列表
    private CustomSpinerAdapter mHospitalSpinnerAdapter;
    private CustomSpinerAdapter mExpartmentSpinnerAdapter;
    private CustomSpinerAdapter mTimeSpinnerAdapter;
    private TextView mTvHosiptalSelected;
    private TextView mTvDepartmentSelected;
    private TextView mTvTimeSelected;
    private ImageView mIvHosiptalSelected;
    private ImageView mIvDepartmentlSelected;
    private ImageView mIvTimeSelected;
    private RelativeLayout mLinHosiptalSelected;
    private RelativeLayout mLinDepartmentlSelected;
    private RelativeLayout mLinTimeSelected;
    private XRecyclerView mRecyclerView;
    private LvExpertListAdapter mListAdapter;
    private List<EntityExpertInfo> mDataList;
    private int mPageIndex;
    private long mSelectedHospitalId = -1;
    private long mSelectedDepartmentId = -1;
    private List<Long> mHospitalIds;
    private List<Long> mDepartmentIds;
    private String mSelectedDate;
    private int mSelectedTimeId;
    private String mSelectedSortName;
    private String mSelectedSortOrder;
    private TextView mTvSortByOrderCount;
    private TextView mTvSortByPrice;
    private TextView mTvSortByScore;
    private ImageView mIvSortByPrice ;
    private boolean mIsDesc = false;
    private View mEmptyView;

    @Override
    protected int inflateResource() {
        return R.layout.activity_appintment_consultation;
    }

    @Override
    protected void initViews(View rootView) {
        mTvHosiptalSelected = getView(rootView, R.id.tv_selected_hospital);
        mTvDepartmentSelected = getView(rootView, R.id.tv_selected_department);
        mTvTimeSelected = getView(rootView, R.id.tv_selected_time);
        mIvHosiptalSelected = getView(rootView, R.id.iv_selected_hospital);
        mIvDepartmentlSelected = getView(rootView, R.id.iv_selected_department);
        mIvTimeSelected = getView(rootView, R.id.iv_selected_time);
        mLinHosiptalSelected = getView(rootView, R.id.lin_selected_hospital);
        mLinDepartmentlSelected = getView(rootView, R.id.lin_selected_department);
        mLinTimeSelected = getView(rootView, R.id.lin_selected_time);
        mRecyclerView = getView(rootView, R.id.mine_customer_list);
        mTvSortByOrderCount = getView(rootView, R.id.tv_sort_by_count);
        mTvSortByPrice = getView(rootView, R.id.tv_sort_by_price);
        mTvSortByScore = getView(rootView, R.id.tv_sort_by_score);
        mIvSortByPrice = getView(rootView, R.id.iv_sort_by_price);
        mTvHosiptalSelected = getView(rootView, R.id.tv_selected_hospital);
        mTvDepartmentSelected = getView(rootView, R.id.tv_selected_department);
        mIvSortByPrice.setImageResource(R.mipmap.ic_sort_normal);
        mEmptyView = getView(rootView, R.id.mine_custom_layout_empty);

        mLinHosiptalSelected.setClickable(true);
        mLinDepartmentlSelected.setClickable(true);
        mLinTimeSelected.setClickable(true);
        mLinHosiptalSelected.setOnClickListener(this);
        mLinDepartmentlSelected.setOnClickListener(this);
        mLinTimeSelected.setOnClickListener(this);

        mTvSortByOrderCount.setOnClickListener(this);
        mTvSortByPrice.setOnClickListener(this);
        mTvSortByScore.setOnClickListener(this);

        initRecylerView();
    }

    private void initRecylerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
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
        ReqManager.getInstance().reqHospitalList(reqHospitalListCall, "");
        mPageIndex = 1;
        reqData(mPageIndex);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String dateStr = (String) msg.obj;
            switch (msg.what) {
                case AppointmentConsultationActivity.CONSULTATION_TIME:
                    mTimeSpinerPopWindow.showAsDropDown(mLinTimeSelected);
                    mSelectedDate = dateStr;
                    break;
            }
        }
    };

    private void reqData(int pageIndex){
        ReqManager.getInstance().reqExpertList(reqExpertListCall, pageIndex, Utils.getUserToken(getActivity())
                , mSelectedHospitalId, mSelectedDepartmentId, mSelectedDate, ""
                , mSelectedTimeId, mSelectedSortName, mSelectedSortOrder, "", ReqManager.KEY_PAGE_SIZE, false);
    }

    @Override
    public void initData() {
        mListAdapter = new LvExpertListAdapter(getActivity(), mDataList = new ArrayList<EntityExpertInfo>());
        mRecyclerView.setAdapter(mListAdapter);
        //专家列表
        mPageIndex = 1;
        reqData(mPageIndex);

        initTimePopwindow();

        ReqManager.getInstance().reqHospitalList(reqHospitalListCall, "");
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.lin_selected_hospital:
                if(mHospitalSpinnerAdapter != null){
                    mHospitalSpinerPopWindow.showAsDropDown(mLinHosiptalSelected);
                } else {
                    ToastUtil.showLongToast("数据还没获取到，请稍后重试");
                }
                break;
            case R.id.lin_selected_department:
                ReqManager.getInstance().reqHospitalDepartmentList(reqHospitalDpmtListCall, mSelectedHospitalId);
                break;
            case R.id.lin_selected_time:
                DateSelector dateSelector = new DateSelector();
                dateSelector.init(getActivity(), mHandler, AppointmentConsultationActivity.CONSULTATION_TIME, false);
                dateSelector.showDaySelectorDialog();
                break;
            case R.id.tv_sort_by_count:
                delSortView(1);
                mSelectedSortName = SORT_BY_ORDER_NUMBER;
                mSelectedSortOrder = SORT_DESC;
                mPageIndex = 1;
                reqData(mPageIndex);
                break;
            case R.id.tv_sort_by_price:
                delSortView(2);
                mIsDesc = !mIsDesc;
                mSelectedSortName =  SORT_BY_PRICE;
                mSelectedSortOrder = mIsDesc ? SORT_DESC : SORT_ASC;
                mPageIndex = 1;
                reqData(mPageIndex);
                break;
            case R.id.tv_sort_by_score:
                delSortView(3);
                mSelectedSortName =  SORT_BY_SCORE;
                mSelectedSortOrder = SORT_DESC;
                mPageIndex = 1;
                reqData(mPageIndex);
                break;
        }
    }


    private void delSortView(int type){
        switch (type){
            case 1:
                mTvSortByOrderCount.setTextColor(getResources().getColor(R.color.green));
                mTvSortByPrice.setTextColor(getResources().getColor(R.color.gray_3));
                mTvSortByScore.setTextColor(getResources().getColor(R.color.gray_3));
                mIvSortByPrice.setImageResource(R.mipmap.ic_sort_normal);
                break;
            case 2:
                mTvSortByOrderCount.setTextColor(getResources().getColor(R.color.gray_3));
                mTvSortByPrice.setTextColor(getResources().getColor(R.color.green));
                mTvSortByScore.setTextColor(getResources().getColor(R.color.gray_3));
                mIvSortByPrice.setImageResource(mIsDesc ? R.mipmap.ic_sort_down_selected : R.mipmap.ic_sort_up_selected);
                break;
            case 3:
                mTvSortByOrderCount.setTextColor(getResources().getColor(R.color.gray_3));
                mTvSortByPrice.setTextColor(getResources().getColor(R.color.gray_3));
                mTvSortByScore.setTextColor(getResources().getColor(R.color.green));
                mIvSortByPrice.setImageResource(R.mipmap.ic_sort_normal);
                break;
        }
    }

    private List<String> getHospitalNamesListFromEntity(List<EntityHospitalInfo> list){
        List<String> listStr = new ArrayList<String>();
        if(mHospitalIds != null){
            mHospitalIds.clear();
        } else {
            mHospitalIds = new ArrayList<Long>();
        }
        mHospitalIds.add((long)-1);
        listStr.add("全部医院");
        for (int i=0;i<list.size() ; i++){
            listStr.add(list.get(i).getName());
            mHospitalIds.add(list.get(i).getId());
        }
        return listStr;
    }

    private List<String> getStrListFromDepartmentEntity(List<EntityHospitalDepartmentInfo> list){
        List<String> listStr = new ArrayList<String>();
        if(mDepartmentIds != null){
            mDepartmentIds.clear();
        } else {
            mDepartmentIds = new ArrayList<Long>();
        }
        mDepartmentIds.add((long)-1);
        listStr.add("全部科室");
        for (int i=0;i<list.size() ; i++){
            mDepartmentIds.add(list.get(i).getId());
            listStr.add(list.get(i).getName());
        }
        return listStr;
    }

    public void initHospitalPopwindow(final List<String> dataList){
        mHospitalSpinnerAdapter = new CustomSpinerAdapter(getActivity(), dataList);
        mHospitalSpinnerAdapter.refreshData(dataList, 0);

        //初始化PopWindow
        mHospitalSpinerPopWindow = new CustomSpinerPopWindow(getActivity());
        mHospitalSpinerPopWindow.setAdatper(mHospitalSpinnerAdapter);
        mHospitalSpinerPopWindow.setItemListener(new CustomSpinerAdapter.IOnItemSelectListener() {
            @Override
            public void onItemClick(int pos) {
                mSelectedDepartmentId = -1;
                mTvDepartmentSelected.setText("全部科室");
                mTvDepartmentSelected.setTextColor(getResources().getColor(R.color.gray_3));
                mIvDepartmentlSelected.setImageResource(R.mipmap.ic_screen_unselected);
                mTvHosiptalSelected.setText(dataList.get(pos));
                mTvHosiptalSelected.setTextColor(pos == 0 ? getResources().getColor(R.color.gray_3) : getResources().getColor(R.color.green));
                mIvHosiptalSelected.setImageResource(pos == 0 ? R.mipmap.ic_screen_unselected : R.mipmap.ic_screen_selected);
                if(mHospitalIds != null && mHospitalIds.size() > pos){
                    mSelectedHospitalId = mHospitalIds.get(pos);
                }
                mPageIndex = 1;
                reqData(mPageIndex);
            }
        });
    }

    public void initExpertmentPopwindow(final List<String> dataList){
        mExpartmentSpinnerAdapter = new CustomSpinerAdapter(getActivity(), dataList);
        mExpartmentSpinnerAdapter.refreshData(dataList, 0);

        //初始化PopWindow
        mDepartmentSpinerPopWindow = new CustomSpinerPopWindow(getActivity());
        mDepartmentSpinerPopWindow.setAdatper(mExpartmentSpinnerAdapter);
        mDepartmentSpinerPopWindow.setItemListener(new CustomSpinerAdapter.IOnItemSelectListener() {
            @Override
            public void onItemClick(int pos) {
                mTvDepartmentSelected.setText(dataList.get(pos));
                mTvDepartmentSelected.setTextColor(pos == 0 ? getResources().getColor(R.color.gray_3) : getResources().getColor(R.color.green));
                mIvDepartmentlSelected.setImageResource(pos == 0 ? R.mipmap.ic_screen_unselected : R.mipmap.ic_screen_selected);
                if(mDepartmentIds != null && mDepartmentIds.size() > pos){
                    mSelectedDepartmentId = mDepartmentIds.get(pos);
                }
                mPageIndex = 1;
                reqData(mPageIndex);
            }
        });
        mDepartmentSpinerPopWindow.showAsDropDown(mLinDepartmentlSelected);
    }


    public void initTimePopwindow(){
        final List<String> dataList = new ArrayList<String>();
        dataList.add("取消");
        dataList.add("上午(08:00 - 11:00)");
        dataList.add("下午(13:00 - 18:00)");
        dataList.add("晚上(20:00 - 24:00)");
        dataList.add("全天");
        mTimeSpinnerAdapter = new CustomSpinerAdapter(getActivity(), dataList);
        mTimeSpinnerAdapter.refreshData(dataList, 0);

        //初始化PopWindow
        mTimeSpinerPopWindow = new CustomSpinerPopWindow(getActivity());
        mTimeSpinerPopWindow.setAdatper(mTimeSpinnerAdapter);
        mTimeSpinerPopWindow.setItemListener(new CustomSpinerAdapter.IOnItemSelectListener() {
            @Override
            public void onItemClick(int pos) {
                if(pos == 0){
                    mTvTimeSelected.setText("全部时间");
                    mSelectedTimeId = -1;
                    mSelectedDate = "";
                } else {
                    mTvTimeSelected.setText(mSelectedDate + " " + dataList.get(pos));
                    mSelectedTimeId = pos;
                }
                mTvTimeSelected.setTextColor(pos == 0 ? getResources().getColor(R.color.gray_3) : getResources().getColor(R.color.green));
                mIvTimeSelected.setImageResource(pos == 0 ? R.mipmap.ic_screen_unselected : R.mipmap.ic_screen_selected);
                mPageIndex = 1;
                reqData(mPageIndex);
            }
        });
    }

    //医院科室列表接口回调
    Callback<RespGetHospitalDepartmentList> reqHospitalDpmtListCall = new Callback<RespGetHospitalDepartmentList>() {
        @Override
        public void onError(Call call, Exception e) {
            onFailure(e.toString());
        }

        @Override
        public void onResponse(RespGetHospitalDepartmentList response) {
            if (onSuccess(response)) {
                mDepartmentList = response.getResult().getList();
                initExpertmentPopwindow(getStrListFromDepartmentEntity(mDepartmentList));
            }
        }

        @Override
        public void onBefore(Request request) {
            super.onBefore(request);
            showProgressDialog();
        }
    };

    //专家列表接口回调
    Callback<RespGetExpertList> reqExpertListCall = new Callback<RespGetExpertList>() {
        @Override
        public void onError(Call call, Exception e) {
            mRecyclerView.refreshComplete();
            mRecyclerView.loadMoreComplete();
            onFailure(e.toString());
            if(mListAdapter == null || mListAdapter.getItemCount() <= 0){
                showErrorLayout(getString(R.string.empty_data_message), R.mipmap.ic_empty_img);
            }
        }

        @Override
        public void onResponse(RespGetExpertList response) {
            mRecyclerView.refreshComplete();
            mRecyclerView.loadMoreComplete();
            hideErrorLayout();
            if (onSuccess(response)) {
                if(response.getResult() != null && response.getResult().getList() != null){
                    mDataList = response.getResult().getList();
                    mListAdapter.notifyData(mDataList, mPageIndex == 1);
                } else {
                    mListAdapter.notifyData(new ArrayList<EntityExpertInfo>(), mPageIndex == 1);
                }
                if(mDataList.size() >= ReqManager.KEY_PAGE_SIZE){
                    mRecyclerView.setLoadingMoreEnabled(true);
                } else {
                    mRecyclerView.setLoadingMoreEnabled(false);
                }
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

    //医院列表接口回调
    Callback<RespGetHospitalList> reqHospitalListCall = new Callback<RespGetHospitalList>() {
        @Override
        public void onError(Call call, Exception e) {
            onFailure(e.toString());
        }

        @Override
        public void onResponse(RespGetHospitalList response) {
            if (onSuccess(response)) {
                mHospitalList = response.getResult().getList();
                initHospitalPopwindow(getHospitalNamesListFromEntity(mHospitalList));
            }
        }

        @Override
        public void onBefore(Request request) {
            super.onBefore(request);
            showProgressDialog();
        }
    };

    //专家详情接口回调
    Callback<RespUserLogin> reqExpertDetialCall = new Callback<RespUserLogin>() {
        @Override
        public void onError(Call call, Exception e) {
            onFailure(e.toString());
        }

        @Override
        public void onResponse(RespUserLogin response) {
            if (onSuccess(response)) {

            }
        }

        @Override
        public void onBefore(Request request) {
            super.onBefore(request);
            showProgressDialog();
        }
    };

    //专家评论列表接口回调
    Callback<RespGetExpertCommentList> reqExpertCommentListCall = new Callback<RespGetExpertCommentList>() {
        @Override
        public void onError(Call call, Exception e) {
            onFailure(e.toString());
        }

        @Override
        public void onResponse(RespGetExpertCommentList response) {
            if (onSuccess(response)) {

            }
        }

        @Override
        public void onBefore(Request request) {
            super.onBefore(request);
            showProgressDialog();
        }
    };
}
