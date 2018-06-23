package com.dearzs.app.activity.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dearzs.app.R;
import com.dearzs.app.adapter.CustomSpinerAdapter;
import com.dearzs.app.adapter.LvExpertListAdapter;
import com.dearzs.app.base.BaseActivity;
import com.dearzs.app.entity.EntityCityInfo;
import com.dearzs.app.entity.EntityExpertInfo;
import com.dearzs.app.entity.EntityHospitalDepartmentInfo;
import com.dearzs.app.entity.EntityHospitalInfo;
import com.dearzs.app.entity.resp.RespGetCityList;
import com.dearzs.app.entity.resp.RespGetExpertList;
import com.dearzs.app.entity.resp.RespGetHospitalDepartmentList;
import com.dearzs.app.entity.resp.RespGetHospitalList;
import com.dearzs.app.util.Constant;
import com.dearzs.app.util.ReqManager;
import com.dearzs.app.util.Utils;
import com.dearzs.app.wheel.test.DateSelector;
import com.dearzs.app.widget.CustomSpinerPopWindow;
import com.dearzs.app.widget.TitleBarView;
import com.dearzs.commonlib.okhttp.callback.Callback;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.Subscribe;
import okhttp3.Call;
import okhttp3.Request;

/**
 * Created by lyl on 2016/5/30.
 * 预约会诊界面
 */
public class AppointmentConsultationActivity extends BaseActivity implements View.OnClickListener {
    public static final int CONSULTATION_TIME = 1000;
    private static final String SORT_BY_ORDER_NUMBER = "orderNumber";
    private static final String SORT_BY_SCORE = "star";
    private static final String SORT_BY_PRICE = "visitMoney";
    private static final String SORT_ASC = "asc";
    private static final String SORT_DESC = "desc";
    //设置PopWindow
    private CustomSpinerPopWindow mCitySpinerPopWindow;
    private CustomSpinerPopWindow mHospitalSpinerPopWindow;
    private CustomSpinerPopWindow mDepartmentSpinerPopWindow;
    private CustomSpinerPopWindow mTimeSpinerPopWindow;
    private List<EntityHospitalInfo> mHospitalList;  //类型列表
    private List<EntityCityInfo> mCityList;  //类型列表
    private List<EntityHospitalDepartmentInfo> mDepartmentList;  //类型列表
    private List<String> mTimeList = new ArrayList<String>();  //类型列表
    private CustomSpinerAdapter mCitySpinnerAdapter;
    private CustomSpinerAdapter mHospitalSpinnerAdapter;
    private CustomSpinerAdapter mExpartmentSpinnerAdapter;
    private CustomSpinerAdapter mTimeSpinnerAdapter;
    private TextView mTvHosiptalSelected;
    private TextView mTvCitySelected;
    private TextView mTvDepartmentSelected;
    private TextView mTvTimeSelected;
    private ImageView mIvCitySelected;
    private ImageView mIvHosiptalSelected;
    private ImageView mIvDepartmentlSelected;
    private ImageView mIvTimeSelected;
    private RelativeLayout mLinHosiptalSelected;
    private RelativeLayout mLinDepartmentlSelected;
    private RelativeLayout mLinTimeSelected;
    private RelativeLayout mLinCitySelected;
    private View mMiddleLine;
    private XRecyclerView mRecyclerView;
    private LvExpertListAdapter mListAdapter;
    private List<EntityExpertInfo> mDataList;
    private int mPageIndex;
    private long mSelectedHospitalId = -1;
    private long mSelectedDepartmentId = -1;
    private String mSelectedCityName = "";
    private List<Long> mHospitalIds;
    private List<Long> mDepartmentIds;
    private String mSelectedDate;
    private int mSelectedTimeId;
    private String mSelectedSortName;
    private String mSelectedSortOrder;
    private TextView mTvSortByOrderCount;
    private TextView mTvSortByPrice;
    private LinearLayout mLinSortByPrice;
    private TextView mTvSortByScore;
    private ImageView mIvSortByPrice;
    private boolean mIsDesc = false;
    private String mSearchCode;
    private boolean mIsSelectDoctor;
    private long mSelectedExpertId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_appintment_consultation);
        addLeftBtn(TitleBarView.TITLE_TYPE_LEFT_IV_BACK, null);
        addRightBtn(TitleBarView.TITLE_TYPE_RIGHT_IV_SEARCH, null);
    }

    @Override
    public void onRightBtnClick() {
        super.onRightBtnClick();
        SearchActivity.startIntent(AppointmentConsultationActivity.this, mIsSelectDoctor, mSelectedExpertId);
    }

    @Override
    public void initView() {
        super.initView();
        mTvSortByOrderCount = getView(R.id.tv_sort_by_count);
        mTvSortByPrice = getView(R.id.tv_sort_by_price);
        mLinSortByPrice = getView(R.id.lin_sort_by_price);
        mTvSortByScore = getView(R.id.tv_sort_by_score);
        mIvSortByPrice = getView(R.id.iv_sort_by_price);
        mTvCitySelected = getView(R.id.tv_selected_city);
        mTvHosiptalSelected = getView(R.id.tv_selected_hospital);
        mTvDepartmentSelected = getView(R.id.tv_selected_department);
        mTvTimeSelected = getView(R.id.tv_selected_time);
        mIvCitySelected = getView(R.id.iv_selected_city);
        mIvHosiptalSelected = getView(R.id.iv_selected_hospital);
        mIvDepartmentlSelected = getView(R.id.iv_selected_department);
        mIvTimeSelected = getView(R.id.iv_selected_time);
        mLinHosiptalSelected = getView(R.id.lin_selected_hospital);
        mLinDepartmentlSelected = getView(R.id.lin_selected_department);
        mLinTimeSelected = getView(R.id.lin_selected_time);
        mLinCitySelected = getView(R.id.lin_selected_city);
        mMiddleLine = getView(R.id.view_midele_line);
        mIvSortByPrice.setImageResource(R.mipmap.ic_sort_normal);

        mLinHosiptalSelected.setClickable(true);
        mLinDepartmentlSelected.setClickable(true);
        mLinTimeSelected.setClickable(true);
        mLinCitySelected.setClickable(true);
        mLinHosiptalSelected.setOnClickListener(this);
        mLinDepartmentlSelected.setOnClickListener(this);
        mLinTimeSelected.setOnClickListener(this);
        mLinCitySelected.setOnClickListener(this);
        mRecyclerView = getView(R.id.mine_customer_list);

        mTvSortByOrderCount.setOnClickListener(this);
        mLinSortByPrice.setOnClickListener(this);
        mTvSortByScore.setOnClickListener(this);

        getErrorLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPageIndex = 1;
                reqData(mPageIndex);
            }
        });

        initRecylerView();
    }

    private void delSortView(int type) {
        switch (type) {
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

    private void initRecylerView() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(AppointmentConsultationActivity.this);
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
        ReqManager.getInstance().reqExpertList(reqExpertListCall, pageIndex, Utils.getUserToken(AppointmentConsultationActivity.this)
                , mSelectedHospitalId, mSelectedDepartmentId, mSelectedDate, mSelectedCityName
                , mSelectedTimeId, mSelectedSortName, mSelectedSortOrder, mSearchCode, ReqManager.KEY_PAGE_SIZE, mIsSelectDoctor);
    }

    @Override
    public void initData() {
        super.initData();
        Intent intent = getIntent();
        if (intent != null) {
            mSearchCode = intent.getStringExtra(Constant.KEY_SEARCH_CODE);
            mIsSelectDoctor = intent.getBooleanExtra(Constant.KEY_IS_SELECT_DOCTOR, false);
            mSelectedExpertId = intent.getLongExtra(Constant.KEY_EXPERT_ID, 0);
            mLinSortByPrice.setVisibility(mIsSelectDoctor ? View.GONE : View.VISIBLE);
            mLinTimeSelected.setVisibility(mIsSelectDoctor ? View.GONE : View.VISIBLE);
            mLinCitySelected.setVisibility(mIsSelectDoctor ? View.VISIBLE : View.GONE);
            mMiddleLine.setVisibility(mIsSelectDoctor ? View.VISIBLE : View.GONE);
            addCenter(TitleBarView.TITLE_TYPE_CENTER_TXT, mIsSelectDoctor ? "选择主治医生" : "预约会诊");
        }
        mListAdapter = new LvExpertListAdapter(AppointmentConsultationActivity.this, mDataList = new ArrayList<EntityExpertInfo>(), mIsSelectDoctor, mSelectedExpertId);
        mRecyclerView.setAdapter(mListAdapter);

        initTimePopwindow();

        mPageIndex = 1;
        reqData(mPageIndex);
    }

    //城市列表接口回调
    Callback<RespGetCityList> reqCityListCall = new Callback<RespGetCityList>() {
        @Override
        public void onError(Call call, Exception e) {
            onFailure(e.toString());
        }

        @Override
        public void onResponse(RespGetCityList response) {
            if (onSuccess(response)) {
                mCityList = response.getResult().getList();
                initCityPopwindow(getCityNamesListFromEntity(mCityList));
            }
        }

        @Override
        public void onBefore(Request request) {
            super.onBefore(request);
            showProgressDialog();
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

    private List<String> getCityNamesListFromEntity(List<EntityCityInfo> list) {
        List<String> listStr = new ArrayList<String>();
        EntityCityInfo cityInfo = new EntityCityInfo();
        cityInfo.setCity("全国");
        listStr.add("全国");
        for (int i = 0; i < list.size(); i++) {
            listStr.add(list.get(i).getCity());
        }
        return listStr;
    }

    private List<String> getHospitalNamesListFromEntity(List<EntityHospitalInfo> list) {
        List<String> listStr = new ArrayList<String>();
        if (mHospitalIds != null) {
            mHospitalIds.clear();
        } else {
            mHospitalIds = new ArrayList<Long>();
        }
        mHospitalIds.add((long) -1);
        listStr.add("全部医院");
        for (int i = 0; i < list.size(); i++) {
            listStr.add(list.get(i).getName());
            mHospitalIds.add(list.get(i).getId());
        }
        return listStr;
    }

    private List<String> getStrListFromDepartmentEntity(List<EntityHospitalDepartmentInfo> list) {
        List<String> listStr = new ArrayList<String>();
        if (mDepartmentIds != null) {
            mDepartmentIds.clear();
        } else {
            mDepartmentIds = new ArrayList<Long>();
        }
        mDepartmentIds.add((long) -1);
        listStr.add("全部科室");
        for (int i = 0; i < list.size(); i++) {
            mDepartmentIds.add(list.get(i).getId());
            listStr.add(list.get(i).getName());
        }
        return listStr;
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
            if (mListAdapter.getItemCount() <= 0) {
                showErrorLayout(getString(R.string.error_message), R.mipmap.ic_empty_img);
            }
            onFailure(e.toString());
        }

        @Override
        public void onResponse(RespGetExpertList response) {
            mRecyclerView.refreshComplete();
            mRecyclerView.loadMoreComplete();
            hideErrorLayout();
            if (onSuccess(response)) {
                if (response.getResult() != null && response.getResult().getList() != null) {
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

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.lin_selected_hospital:
                ReqManager.getInstance().reqHospitalList(reqHospitalListCall, mSelectedCityName);
                break;
            case R.id.lin_selected_city:
                ReqManager.getInstance().reqCityList(reqCityListCall);
                break;
            case R.id.lin_selected_department:
                ReqManager.getInstance().reqHospitalDepartmentList(reqHospitalDpmtListCall, mSelectedHospitalId);
                break;
            case R.id.lin_selected_time:
                DateSelector dateSelector = new DateSelector();
                dateSelector.init(AppointmentConsultationActivity.this, mHandler, CONSULTATION_TIME, false);
                dateSelector.showDaySelectorDialog();
                break;
            case R.id.tv_sort_by_count:
                mSelectedSortName = SORT_BY_ORDER_NUMBER;
                mSelectedSortOrder = SORT_DESC;
                delSortView(1);
                mPageIndex = 1;
                reqData(mPageIndex);
                break;
            case R.id.lin_sort_by_price:
                delSortView(2);
                mIsDesc = !mIsDesc;
                mSelectedSortName = SORT_BY_PRICE;
                mSelectedSortOrder = mIsDesc ? SORT_DESC : SORT_ASC;
                mPageIndex = 1;
                reqData(mPageIndex);
                break;
            case R.id.tv_sort_by_score:
                delSortView(3);
                mSelectedSortName = SORT_BY_SCORE;
                mSelectedSortOrder = SORT_DESC;
                mPageIndex = 1;
                reqData(mPageIndex);
                break;
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String dateStr = (String) msg.obj;
            switch (msg.what) {
                case CONSULTATION_TIME:
                    mTimeSpinerPopWindow.showAsDropDown(mLinTimeSelected);
                    mSelectedDate = dateStr;
                    break;
            }
        }
    };

    public void initHospitalPopwindow(final List<String> dataList) {
        mHospitalSpinnerAdapter = new CustomSpinerAdapter(this, dataList);
        mHospitalSpinnerAdapter.refreshData(dataList, 0);

        //初始化PopWindow
        mHospitalSpinerPopWindow = new CustomSpinerPopWindow(this);
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
                if (mHospitalIds != null && mHospitalIds.size() > pos) {
                    mSelectedHospitalId = mHospitalIds.get(pos);
                }
                mPageIndex = 1;
                reqData(mPageIndex);
            }
        });
        mHospitalSpinerPopWindow.showAsDropDown(mLinHosiptalSelected);
    }

    //初始化科室popwindow
    public void initExpertmentPopwindow(final List<String> dataList) {
        mExpartmentSpinnerAdapter = new CustomSpinerAdapter(this, dataList);
        mExpartmentSpinnerAdapter.refreshData(dataList, 0);

        //初始化PopWindow
        mDepartmentSpinerPopWindow = new CustomSpinerPopWindow(this);
        mDepartmentSpinerPopWindow.setAdatper(mExpartmentSpinnerAdapter);
        mDepartmentSpinerPopWindow.setItemListener(new CustomSpinerAdapter.IOnItemSelectListener() {
            @Override
            public void onItemClick(int pos) {
                mTvDepartmentSelected.setText(dataList.get(pos));
                mTvDepartmentSelected.setTextColor(pos == 0 ? getResources().getColor(R.color.gray_3) : getResources().getColor(R.color.green));
                mIvDepartmentlSelected.setImageResource(pos == 0 ? R.mipmap.ic_screen_unselected : R.mipmap.ic_screen_selected);
                if (mDepartmentIds != null && mDepartmentIds.size() > pos) {
                    mSelectedDepartmentId = mDepartmentIds.get(pos);
                }
                mPageIndex = 1;
                reqData(mPageIndex);

            }
        });
        mDepartmentSpinerPopWindow.showAsDropDown(mLinDepartmentlSelected);
    }

    //初始化城市popwindow
    public void initCityPopwindow(final List<String> dataList) {
        mCitySpinnerAdapter = new CustomSpinerAdapter(this, dataList);
        mCitySpinnerAdapter.refreshData(dataList, 0);

        //初始化PopWindow
        mCitySpinerPopWindow = new CustomSpinerPopWindow(this);
        mCitySpinerPopWindow.setAdatper(mCitySpinnerAdapter);
        mCitySpinerPopWindow.setItemListener(new CustomSpinerAdapter.IOnItemSelectListener() {
            @Override
            public void onItemClick(int pos) {
                mSelectedDepartmentId = -1;
                mSelectedHospitalId = -1;
                mTvDepartmentSelected.setText("全部科室");
                mTvDepartmentSelected.setTextColor(getResources().getColor(R.color.gray_3));
                mIvDepartmentlSelected.setImageResource(R.mipmap.ic_screen_unselected);
                mTvHosiptalSelected.setText("全部医院");
                mTvHosiptalSelected.setTextColor(getResources().getColor(R.color.gray_3));
                mIvHosiptalSelected.setImageResource(R.mipmap.ic_screen_unselected);

                mTvCitySelected.setText(dataList.get(pos));
                mTvCitySelected.setTextColor(pos == 0 ? getResources().getColor(R.color.gray_3) : getResources().getColor(R.color.green));
                mIvCitySelected.setImageResource(pos == 0 ? R.mipmap.ic_screen_unselected : R.mipmap.ic_screen_selected);
                if (mCityList != null && mCityList.size() >= pos) {

                    if (pos != 0) {
                        EntityCityInfo info = mCityList.get(pos - 1);
                        if (info != null) {
                            mSelectedCityName = info.getCity();
                        } else {
                            mSelectedCityName = "";
                        }
                    } else {
                        mSelectedCityName = "";
                    }
                }
                mPageIndex = 1;
                reqData(mPageIndex);

            }
        });
        mCitySpinerPopWindow.showAsDropDown(mLinCitySelected);
    }

    public void initTimePopwindow() {
        final List<String> dataList = new ArrayList<String>();
        dataList.add("取消");
        dataList.add("上午(08:00 - 11:00)");
        dataList.add("下午(13:00 - 18:00)");
        dataList.add("晚上(20:00 - 24:00)");
        dataList.add("全天");
        mTimeSpinnerAdapter = new CustomSpinerAdapter(this, dataList);
        mTimeSpinnerAdapter.refreshData(dataList, 0);

        //初始化PopWindow
        mTimeSpinerPopWindow = new CustomSpinerPopWindow(this);
        mTimeSpinerPopWindow.setAdatper(mTimeSpinnerAdapter);
        mTimeSpinerPopWindow.setItemListener(new CustomSpinerAdapter.IOnItemSelectListener() {
            @Override
            public void onItemClick(int pos) {
                if (pos == 0) {
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

    @Subscribe
    public void handlSelectDoc(EntityExpertInfo doctorInfo) {
        //处理选择主治医生时，搜索到医生，直接选择，需要关闭此页面直接返回订单确认页面
        if (mIsSelectDoctor && doctorInfo != null) {
            finish();
        }
    }

    /**
     * Activity跳转
     *
     * @param isSelected 是否是选择主治医师，是则为true
     */
    public static void startIntent(Context ctx, boolean isSelected, long selectedExpertId) {
        Intent intent = new Intent();
        intent.setClass(ctx, AppointmentConsultationActivity.class);
        intent.putExtra(Constant.KEY_IS_SELECT_DOCTOR, isSelected);
        intent.putExtra(Constant.KEY_EXPERT_ID, selectedExpertId);
        ctx.startActivity(intent);
    }

    /**
     * Activity跳转
     *
     * @param isSelected 是否是选择主治医师，是则为true
     */
    public static void startIntent(Context ctx, boolean isSelected) {
        Intent intent = new Intent();
        intent.setClass(ctx, AppointmentConsultationActivity.class);
        intent.putExtra(Constant.KEY_IS_SELECT_DOCTOR, isSelected);
        ctx.startActivity(intent);
    }

    public static void startIntent(Context ctx, String searchCode) {
        Intent intent = new Intent();
        intent.setClass(ctx, AppointmentConsultationActivity.class);
        intent.putExtra(Constant.KEY_SEARCH_CODE, searchCode);
        ctx.startActivity(intent);
    }
}
