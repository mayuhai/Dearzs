package com.dearzs.app.activity.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.dearzs.app.R;
import com.dearzs.app.adapter.LvExpertListAdapter;
import com.dearzs.app.base.BaseActivity;
import com.dearzs.app.entity.EntityExpertInfo;
import com.dearzs.app.entity.EntityHospitalDepartmentInfo;
import com.dearzs.app.entity.EntityHospitalInfo;
import com.dearzs.app.entity.resp.RespGetExpertList;
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
import okhttp3.Call;
import okhttp3.Request;

/**
 * Created by lyl on 2016/5/30.
 * 搜索界面
 */
public class SearchActivity extends BaseActivity implements View.OnClickListener {
    private XRecyclerView mRecyclerView;
    private LvExpertListAdapter mListAdapter;
    private List<EntityExpertInfo> mDataList;
    private EditText mEtSearch;
    private String mSearchCode;
    private View mFootView;
    private boolean mIsSelectDoctor = false;
    private long mSelectedExpertId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_search);
        addLeftBtn(TitleBarView.TITLE_TYPE_LEFT_ET_SEARCH, null);
        addRightBtn(TitleBarView.TITLE_TYPE_RIGHT_TXT, "取消");
    }

    @Override
    public void onRightBtnClick() {
        super.onRightBtnClick();
        mSoftInputHandler.sendEmptyMessage(1);
        finish();
    }

    private final Handler mSoftInputHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                showInputMethod(mEtSearch);
            } else {
                hideInputMethod(mEtSearch);
            }
        }
    };

    @Override
    public void initView() {
        super.initView();
        mFootView = LayoutInflater.from(SearchActivity.this).inflate(R.layout.listview_footer_view_more, null);
        mFootView.setVisibility(View.GONE);
        mEtSearch = getTitleBar().getEtSearch();
        mFootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppointmentConsultationActivity.startIntent(SearchActivity.this, mSearchCode);
            }
        });
        mEtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mSearchCode = s.toString();
                if(s != null && s.toString().length() > 0){
                    reqData(1, mSearchCode);
                } else {
                    reqData(1, "-%-");
                }
            }
        });
        mRecyclerView = getView(R.id.mine_customer_list);
        initRecylerView();
    }

    private void initRecylerView(){

        LinearLayoutManager layoutManager = new LinearLayoutManager(SearchActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
        mRecyclerView.setArrowImageView(R.mipmap.ic_down_arrow);

        mRecyclerView.setLoadingMoreEnabled(false);
        mRecyclerView.setPullRefreshEnabled(false);
    }

    private void reqData(int pageIndex, String searchCode){
        ReqManager.getInstance().reqExpertList(reqExpertListCall, pageIndex, Utils.getUserToken(SearchActivity.this)
                , -1, -1, "", "" , -1, "", "", searchCode, 3, false);
    }

    @Override
    public void initData() {
        super.initData();
        Intent intent = getIntent();
        if(intent != null){
            mIsSelectDoctor = getIntent().getBooleanExtra(Constant.KEY_IS_SELECT_DOCTOR, false);
            mSelectedExpertId = getIntent().getLongExtra(Constant.KEY_EXPERT_ID, 0);
        }
        mRecyclerView.addFootView(mFootView);
        mListAdapter = new LvExpertListAdapter(SearchActivity.this, mDataList = new ArrayList<EntityExpertInfo>(), mIsSelectDoctor, mSelectedExpertId);
        mRecyclerView.setAdapter(mListAdapter);
    }

    //专家列表接口回调
    Callback<RespGetExpertList> reqExpertListCall = new Callback<RespGetExpertList>() {
        @Override
        public void onError(Call call, Exception e) {
            mRecyclerView.refreshComplete();
            mRecyclerView.loadMoreComplete();
            onFailure(e.toString());
        }

        @Override
        public void onResponse(RespGetExpertList response) {
            mRecyclerView.refreshComplete();
            mRecyclerView.loadMoreComplete();
            if (onSuccess(response)) {
                if(response.getResult() != null && response.getResult().getList() != null){
                    mDataList = response.getResult().getList();
                    if(response.getResult().getTotal() > 3){
                        mFootView.setVisibility(View.VISIBLE);
                    } else {
                        mFootView.setVisibility(View.GONE);
                    }
                    mListAdapter.notifyData(mDataList, true);
                }
            }
        }

        @Override
        public void onBefore(Request request) {
            super.onBefore(request);
        }
    };

    @Subscribe
    public void handlSelectDoc(EntityExpertInfo doctorInfo){
        //处理选择主治医生时，搜索到医生，直接选择，需要关闭此页面直接返回订单确认页面
        if(mIsSelectDoctor && doctorInfo != null){
            finish();
        }
    }

    /**
     * Activity跳转
     */
    public static void startIntent(Context ctx, boolean isSelectDoctor, long selectedExoertId) {
        Intent intent = new Intent();
        intent.setClass(ctx, SearchActivity.class);
        intent.putExtra(Constant.KEY_IS_SELECT_DOCTOR, isSelectDoctor);
        intent.putExtra(Constant.KEY_EXPERT_ID, selectedExoertId);
        ctx.startActivity(intent);
    }

    /**
     * Activity跳转
     */
    public static void startIntent(Context ctx) {
        Intent intent = new Intent();
        intent.setClass(ctx, SearchActivity.class);
        ctx.startActivity(intent);
    }
}
