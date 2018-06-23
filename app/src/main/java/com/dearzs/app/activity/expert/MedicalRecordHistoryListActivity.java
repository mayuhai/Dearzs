package com.dearzs.app.activity.expert;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import com.dearzs.app.R;
import com.dearzs.app.adapter.LvMedicalRecordHistoryListAdapter;
import com.dearzs.app.base.BaseActivity;
import com.dearzs.app.entity.EntityMedicalRecordHistoryInfo;
import com.dearzs.app.entity.EntityPatientInfo;
import com.dearzs.app.entity.resp.RespGetMedicalRecordHistoryList;
import com.dearzs.app.util.Constant;
import com.dearzs.app.util.ReqManager;
import com.dearzs.app.util.Utils;
import com.dearzs.app.widget.TitleBarView;
import com.dearzs.commonlib.okhttp.callback.Callback;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Request;

/**
 * Created by lx on 2016/6/28.
 * 病例更新历史
 */
public class MedicalRecordHistoryListActivity extends BaseActivity {
    private XRecyclerView mHistoryListView;
    private LvMedicalRecordHistoryListAdapter mListAdapter;
    private List<EntityMedicalRecordHistoryInfo> mDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_medical_record_history_list);
        addLeftBtn(TitleBarView.TITLE_TYPE_LEFT_IV_BACK, null);
        addCenter(TitleBarView.TITLE_TYPE_CENTER_TXT, "病例更新历史");
    }

    @Override
    public void initView() {
        super.initView();
        mHistoryListView = getView(R.id.medical_record_history_list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(MedicalRecordHistoryListActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mHistoryListView.setLayoutManager(layoutManager);

        mHistoryListView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mHistoryListView.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
        mHistoryListView.setArrowImageView(R.mipmap.ic_down_arrow);

        mHistoryListView.setLoadingMoreEnabled(false);
        mHistoryListView.setPullRefreshEnabled(false);
    }

    @Override
    public void initData() {
        super.initData();
        String patientId = "-1";
        EntityPatientInfo patientInfo = null;
        if(getIntent() != null && getIntent().getExtras() != null){
            patientId = String.valueOf(getIntent().getExtras().getLong(Constant.KEY_PATIENT_ID));
            patientInfo = (EntityPatientInfo) getIntent().getExtras().getSerializable(Constant.KEY_PATIENT_INFO);
        }
        mListAdapter = new LvMedicalRecordHistoryListAdapter(MedicalRecordHistoryListActivity.this, mDataList = new ArrayList<EntityMedicalRecordHistoryInfo>(), patientInfo);
        mHistoryListView.setAdapter(mListAdapter);
        ReqManager.getInstance().reqGetMedicalRecordHistoryList(reqMedicalRecordHistoryListCall, Utils.getUserToken(MedicalRecordHistoryListActivity.this), patientId);
    }

    //专家列表接口回调
    Callback<RespGetMedicalRecordHistoryList> reqMedicalRecordHistoryListCall = new Callback<RespGetMedicalRecordHistoryList>() {
        @Override
        public void onError(Call call, Exception e) {
            onFailure(e.toString());
        }

        @Override
        public void onResponse(RespGetMedicalRecordHistoryList response) {
            if (onSuccess(response)) {
                if(response.getResult() != null && response.getResult().getList() != null){
                    mDataList = response.getResult().getList();
                    mListAdapter.notifyData(mDataList, true);
                }
            }
        }

        @Override
        public void onBefore(Request request) {
            super.onBefore(request);
            showProgressDialog();
        }
    };
}
