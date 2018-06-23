package com.dearzs.app.activity.expert;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.dearzs.app.R;
import com.dearzs.app.activity.mine.DoctorCertificationIntroduceActivity;
import com.dearzs.app.activity.order.OrderConfirmActivity;
import com.dearzs.app.adapter.GvVisitTimeAdapter;
import com.dearzs.app.adapter.LvPatientEvaluationListAdapter;
import com.dearzs.app.base.BaseActivity;
import com.dearzs.app.chat.ui.ChatActivity;
import com.dearzs.app.entity.EntityBase;
import com.dearzs.app.entity.EntityComment;
import com.dearzs.app.entity.EntityExpertDetails;
import com.dearzs.app.entity.EntityExpertInfo;
import com.dearzs.app.entity.EntityHospitalDepartmentInfo;
import com.dearzs.app.entity.EntityHospitalInfo;
import com.dearzs.app.entity.EntityUserVisits;
import com.dearzs.app.entity.resp.RespGetExpertCommentList;
import com.dearzs.app.entity.resp.RespGetExpertDetails;
import com.dearzs.app.util.Constant;
import com.dearzs.app.util.DimenUtils;
import com.dearzs.app.util.ImageLoaderManager;
import com.dearzs.app.util.ReqManager;
import com.dearzs.app.util.ToastUtil;
import com.dearzs.app.util.Utils;
import com.dearzs.app.widget.CircleImageView;
import com.dearzs.app.widget.FixGridLayout;
import com.dearzs.app.widget.TitleBarView;
import com.dearzs.commonlib.okhttp.callback.Callback;
import com.dearzs.commonlib.utils.GetViewUtil;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.tencent.TIMConversationType;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import okhttp3.Call;
import okhttp3.Request;

/**
 * 专家详情页
 */
public class ExpertDetailsActivity extends BaseActivity implements ViewTreeObserver.OnGlobalLayoutListener {
    private int mHeaderViewHeight;
    private LvPatientEvaluationListAdapter mListAdapter;
    private GvVisitTimeAdapter mTimeAdapter;

    private List<EntityUserVisits> mGvTimeDataList;
    private LinearLayout mHeaderLayout;
    private LinearLayout mFloatView;
    private LinearLayout mButtonLayout;
    private LinearLayout mIntroLayout;
    private CircleImageView mUserPhoto;
    private ImageView mZhuanZhenTag;
    private TextView mUserName;
    //    private TextView mAppointmentCount;
    private TextView mUserScore;
    private TextView mGoodArea;
    private TextView mUserJob;
    private TextView mUserHospital;
    private TextView mUserDepartment;
    private TextView mComments;
    //    private LinearLayout mLinEvaluate;
    private XRecyclerView mRecyclerView;
    private RatingBar mRatingBar;
    private FixGridLayout mFixGridLayout;
    private GridView mGvVisitTime;
    private String mUid;
    private int mPageIndex = 1;
    private boolean mIsCollected = false;
    private Button mToAppointment, mConsultation, mToAppointmentFlow, mConsultationFlow;
    private EntityExpertInfo mExpertInfo;
    private View mHeadView;
    private int mConsultType = 1;
    private boolean mIsSelectDoc = false;
    private Button mSelectBtn;
    private LinearLayout mSelectLin;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_expert_details);
        addLeftBtn(TitleBarView.TITLE_TYPE_LEFT_IV_BACK, null);
        addRightBtn(TitleBarView.TITLE_TYPE_RIGHT_IV_COLLECTION, null);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(ExpertDetailsActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
        mRecyclerView.setArrowImageView(R.mipmap.ic_down_arrow);

        mRecyclerView.addHeaderView(mHeadView);
        mRecyclerView.setPullRefreshEnabled(false);

        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
            }

            @Override
            public void onLoadMore() {
                reqData(++mPageIndex);
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.e("test", "dx = " + dx);
                Log.e("test", "dy = " + dy);
            }
        });

//        mRecyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
//        @Override
//        public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//            Log.e("test","scrollY = " + scrollX);
//            Log.e("test","mHeaderViewHeight = " + mHeaderViewHeight);
//            Log.e("test","oldScrollY = " + oldScrollX);
//            if (mHeaderViewHeight > 0 && scrollY >= mHeaderViewHeight) {
//                mFloatView.setVisibility(View.VISIBLE);
//            } else {
//                mFloatView.setVisibility(View.GONE);
//            }
//        }
//    });
    }

    private void reqData(int pageIndex) {
        ReqManager.getInstance().reqExpertCommentList(reqCommentListCallback, Utils.getUserToken(ExpertDetailsActivity.this), mUid, pageIndex);
    }

    @Override
    public void onRightBtnClick() {
        super.onRightBtnClick();
        int optsType = mIsCollected ? 0 : 1;
        long uid = 0;
        try {
            uid = Long.valueOf(mUid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ReqManager.getInstance().reqDealCollection(reqDealCollectionCallback, Utils.getUserToken(ExpertDetailsActivity.this), uid, Constant.COLLECTION_EXPERT, optsType);
    }

    @Override
    public void initView() {
        super.initView();
        //浮动按钮的layout
        mSelectBtn = getView(R.id.bt_select_doctor);
        mSelectLin = getView(R.id.lin_select_doctor);
        mFloatView = getView(R.id.expert_details_float_view);
        mToAppointmentFlow = getView(R.id.expert_details_consultation_bt_float);
        mConsultationFlow = getView(R.id.expert_details_appointment_bt_float);
        mRecyclerView = getView(R.id.expert_details_patient_evaluate_listview);

        mHeadView = LayoutInflater.from(ExpertDetailsActivity.this).inflate(R.layout.view_expert_details_list_header_layout, null);
        mUserPhoto = getView(mHeadView, R.id.expert_details_user_photo);
        mHeaderLayout = getView(mHeadView, R.id.expert_details_header_layout);
        mIntroLayout = getView(mHeadView, R.id.expert_details_intro_layout);
        mUserName = getView(mHeadView, R.id.expert_details_name);
        mUserScore = getView(mHeadView, R.id.expert_details_user_score);
        mZhuanZhenTag = getView(mHeadView, R.id.expert_details_can_zhuan);
        mGoodArea = getView(mHeadView, R.id.expert_details_good);
        mUserJob = getView(mHeadView, R.id.expert_details_user_job);
        mRatingBar = getView(mHeadView, R.id.expert_details_user_rating);
        mUserHospital = getView(mHeadView, R.id.expert_details_user_hospital);
        mUserDepartment = getView(mHeadView, R.id.expert_details_user_department);
        mComments = getView(mHeadView, R.id.expert_details_comments);
        mFixGridLayout = getView(mHeadView, R.id.expert_details_good_layout);
        mGvVisitTime = getView(mHeadView, R.id.gv_expert_details_visit_time);
        mToAppointment = getView(mHeadView, R.id.expert_details_appointment_bt);
        mConsultation = getView(mHeadView, R.id.expert_details_consultation_bt);
        mButtonLayout = getView(mHeadView, R.id.lin_button_layout);

        mToAppointment.setOnClickListener(this);
        mConsultation.setOnClickListener(this);
        mToAppointmentFlow.setOnClickListener(this);
        mConsultationFlow.setOnClickListener(this);
        mSelectBtn.setOnClickListener(this);

        mGvVisitTime.setNumColumns(2);
        mGvVisitTime.setFocusable(false);

        mHeaderLayout.getViewTreeObserver().addOnGlobalLayoutListener(this);
        initRecyclerView();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.expert_details_appointment_bt:
            case R.id.expert_details_consultation_bt_float:
                boolean isDoctoruser = Utils.isDoctoruser();
                if (Utils.getUserId() != mExpertInfo.getId()) {
                    OrderConfirmActivity.startIntent(ExpertDetailsActivity.this, mExpertInfo, mConsultType, isDoctoruser);
                } else {
                    ToastUtil.showShortToast("自己不能预约自己");
                }
                break;
            case R.id.expert_details_consultation_bt:
            case R.id.expert_details_appointment_bt_float:
                if (mExpertInfo != null) {
                    if (Utils.getUserId() != mExpertInfo.getId()) {
                        ChatActivity.startIntent(this, mExpertInfo.getPhone(), false, TIMConversationType.C2C);
                    } else {
                        ToastUtil.showShortToast("自己不能和自己聊天");
                    }
                }
                break;
            case R.id.bt_select_doctor:
                if(mExpertInfo != null){
                    EventBus.getDefault().post(mExpertInfo);
                    finish();
                }
                break;
        }
    }

    private void bindViewData(RespGetExpertDetails respGetExpertDetails) {
        EntityExpertDetails entityExpertDetails = respGetExpertDetails.getResult();
        if (entityExpertDetails != null) {
            int isFav = entityExpertDetails.getIsFav();
            mIsCollected = isFav == 1;
            setRightIvResource(mIsCollected ? R.mipmap.ic_collection_selected : R.mipmap.ic_collection_normal);
            EntityExpertInfo expertInfo = entityExpertDetails.getUser();
            mExpertInfo = expertInfo;
            if (expertInfo != null) {
                ImageLoaderManager.getInstance().displayImage(expertInfo.getAvatar(), mUserPhoto);
                EntityHospitalInfo hospitalInfo = expertInfo.getHospital();
                if (hospitalInfo != null) {
                    mUserHospital.setText(hospitalInfo.getName());
                }
                EntityHospitalDepartmentInfo departmentInfo = expertInfo.getDepartment();
                if (hospitalInfo != null) {
                    mUserDepartment.setText(departmentInfo.getName());
                }
                mUserName.setText(expertInfo.getName());
                mUserScore.setText("评分：" + expertInfo.getStar() + "");
                mUserName.setText(expertInfo.getName());
                mUserJob.setText(expertInfo.getJob());
                mComments.setText("(共" + expertInfo.getComments() + "条)");
                mGoodArea.setText(expertInfo.getIntro());
                mGoodArea.setVisibility(TextUtils.isEmpty(expertInfo.getIntro()) ? View.GONE : View.VISIBLE);
                mRatingBar.setRating((float) expertInfo.getStar());
                mZhuanZhenTag.setVisibility(expertInfo.getReferralState() == 1 ? View.VISIBLE : View.GONE);
                if (!TextUtils.isEmpty(expertInfo.getLabel())) {
                    mIntroLayout.setVisibility(View.VISIBLE);
                    if (expertInfo.getLabel().contains(",")) {
                        String[] labels = expertInfo.getLabel().split(",");
                        for (int i = 0; i < labels.length; i++) {
                            mFixGridLayout.addView(getTextView(labels[i]));
                        }
                    } else {
                        mFixGridLayout.addView(getTextView(expertInfo.getLabel()));
                    }
                } else {
                    mIntroLayout.setVisibility(View.GONE);
                }
                mTimeAdapter.replaceAll(expertInfo.getUserVisits());
                mGvVisitTime.getLayoutParams().height = Utils.getGridViewHeight(mGvVisitTime);
            }
        }
    }

    private View getTextView(String text) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        View view = LayoutInflater.from(ExpertDetailsActivity.this).inflate(R.layout.item_lable_layout, null);
        CheckedTextView textView = GetViewUtil.getView(view, R.id.item_title_text);
        textView.setText(text);
        view.setLayoutParams(params);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        mTimeAdapter = new GvVisitTimeAdapter(ExpertDetailsActivity.this, R.layout.item_expert_details_visit_time_layout, mGvTimeDataList = new ArrayList<EntityUserVisits>());
        mGvVisitTime.setAdapter(mTimeAdapter);
        mListAdapter = new LvPatientEvaluationListAdapter(ExpertDetailsActivity.this, new ArrayList<EntityComment>());
        mRecyclerView.setAdapter(mListAdapter);

        Intent intent = getIntent();
        if (intent != null) {
            mConsultType = intent.getIntExtra(Constant.KEY_CONSULT_TYPE, Constant.KEY_CONSULT_NORMAL);
            mUid = intent.getStringExtra(Constant.KEY_EXPERT_ID);
            mIsSelectDoc = intent.getBooleanExtra(Constant.KEY_IS_SELECT_DOCTOR, false);
            mButtonLayout.setVisibility(mIsSelectDoc ? View.GONE : View.VISIBLE);
            mSelectLin.setVisibility(mIsSelectDoc ? View.VISIBLE : View.GONE);
        }
        mToAppointment.setText(Constant.KEY_CONSULT_NORMAL == mConsultType ? "预约会诊" : "预约转诊");
        mToAppointmentFlow.setText(Constant.KEY_CONSULT_NORMAL == mConsultType ? "预约会诊" : "预约转诊");
        ReqManager.getInstance().reqExpertDetail(reqExpertDetailsCallback, Utils.getUserToken(ExpertDetailsActivity.this), mUid);
        mPageIndex = 1;
        reqData(mPageIndex);
    }


    //专家详情接口回调
    Callback<RespGetExpertDetails> reqExpertDetailsCallback = new Callback<RespGetExpertDetails>() {
        @Override
        public void onError(Call call, Exception e) {
            closeProgressDialog();
            showErrorLayout(getString(R.string.error_message), R.mipmap.ic_empty_img);
            onFailure(e.toString());
        }

        @Override
        public void onResponse(RespGetExpertDetails response) {
            closeProgressDialog();
            hideErrorLayout();
            if (onSuccess(response)) {
                bindViewData(response);
            } else {
                showEmptyLayout(getString(R.string.error_message), R.mipmap.ic_empty_img);
            }
        }

        @Override
        public void onBefore(Request request) {
            showProgressDialog();
            super.onBefore(request);
        }
    };

    //专家评论接口回调
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
            //请求成功，设置加载和刷新完毕
            if (onSuccess(response)) {
                hideErrorLayout();
                final List<EntityComment> list = response.getResult().getList();
                if (list != null) {
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

    //收藏处理接口回调
    Callback<EntityBase> reqDealCollectionCallback = new Callback<EntityBase>() {
        @Override
        public void onError(Call call, Exception e) {
            closeProgressDialog();
            onFailure(e.toString());
        }

        @Override
        public void onResponse(EntityBase response) {
            closeProgressDialog();
            //请求成功，设置加载和刷新完毕
            if (response.getCode().equals("000000")) {
                mIsCollected = !mIsCollected;
                setRightIvResource(mIsCollected ? R.mipmap.ic_collection_selected : R.mipmap.ic_collection_normal);
                String toast = mIsCollected ? "收藏成功" : "取消收藏成功";
                ToastUtil.showLongToast(toast);
            } else {
                ToastUtil.showLongToast(response.getMsg());
            }
        }

        @Override
        public void onBefore(Request request) {
            showProgressDialog();
            super.onBefore(request);
        }
    };


    @Override
    public void onErrorClick() {
        super.onErrorClick();
        mPageIndex = 1;
        ReqManager.getInstance().reqExpertDetail(reqExpertDetailsCallback, Utils.getUserToken(ExpertDetailsActivity.this), mUid);
        reqData(mPageIndex);
    }


    /**
     * Activity跳转
     */
    public static void startIntent(Context ctx, String expertId, int consultType) {
        Intent intent = new Intent();
        intent.setClass(ctx, ExpertDetailsActivity.class);
        intent.putExtra(Constant.KEY_EXPERT_ID, String.valueOf(expertId));
        intent.putExtra(Constant.KEY_CONSULT_TYPE, consultType);
        ctx.startActivity(intent);
    }

    /**
     * Activity跳转
     */
    public static void startIntent(Context ctx, String expertId, int consultType, boolean isSelectDoc) {
        Intent intent = new Intent();
        intent.setClass(ctx, ExpertDetailsActivity.class);
        intent.putExtra(Constant.KEY_EXPERT_ID, String.valueOf(expertId));
        intent.putExtra(Constant.KEY_CONSULT_TYPE, consultType);
        intent.putExtra(Constant.KEY_IS_SELECT_DOCTOR, isSelectDoc);
        ctx.startActivity(intent);
    }

    @Override
    public void onGlobalLayout() {
        mHeaderViewHeight = mHeaderLayout.getHeight();
//        mHeaderLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

//    @Override
//    public void onScrollChanged(MScrollView scrollView, int x, int y, int oldx, int oldy) {
//        if (mHeaderViewHeight > 0 && y >= mHeaderViewHeight) {
//            mFloatView.setVisibility(View.VISIBLE);
//        } else {
//            mFloatView.setVisibility(View.GONE);
//        }
//    }
}
