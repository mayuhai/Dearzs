package com.dearzs.app.activity.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dearzs.app.R;
import com.dearzs.app.activity.expert.ExpertDetailsActivity;
import com.dearzs.app.base.BaseActivity;
import com.dearzs.app.chat.ui.ChatActivity;
import com.dearzs.app.entity.EntityConsultInfo;
import com.dearzs.app.entity.EntityPatientInfo;
import com.dearzs.app.entity.resp.RespStartOrEndConsult;
import com.dearzs.app.util.Constant;
import com.dearzs.app.util.ImageLoaderManager;
import com.dearzs.app.util.ReqManager;
import com.dearzs.app.util.ToastUtil;
import com.dearzs.app.util.Utils;
import com.dearzs.app.widget.CircleImageView;
import com.dearzs.app.widget.FixGridLayout;
import com.dearzs.app.widget.TitleBarView;
import com.dearzs.commonlib.okhttp.callback.Callback;

import okhttp3.Call;
import okhttp3.Request;

/**
 * Created by luyanlong on 2016/6/24.
 * 编辑会诊结果界面
 */
public class EditConsultResultActivity extends BaseActivity {
    public static final String CONSULT_STATE_KEY = "consult_state";
    public static final String CONSULT_ROOMID_KEY = "consult_room_id";
    public static final String IS_EXPERT_CAN_ZHUAN_KEY = "is_expert_can_zhuan";
    public static final String IS_ADDED_RESULT = "is_added_result";

    public static final int mCurrentType = 100;
    public static final int mPastType = mCurrentType + 1;
    public static final int mSpecializedType = mPastType + 1;
    public static final int mAuxiliaryType = mSpecializedType + 1;
    public static final int mTestType = mAuxiliaryType + 1;
    public static final int mDiagnosticType = mTestType + 1;

    private String mCaseAnalysis, mDiseaseDiagnosis, mTreatmentPrograms;

    private int mState = -1;
    private int mIdentity = 0;
    private long mRoomId;
    private boolean mIsCanZhuan;
    private boolean mIsAddedResult;

    private TextView mPatientName, mPatientAge;
    private CircleImageView mPatientPhoto;
    private ImageView mPatientSex;
    private EditText mEtCaseAnalysis, mEtDiseaseDiagnosis, mEtTreatmentPrograms;

    private LinearLayout mBottomLayout;
    private Button mBtZhuan;

    private EntityConsultInfo mConsultionInfo;
    private long mExpertId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_edit_consult_record);

        addLeftBtn(TitleBarView.TITLE_TYPE_LEFT_IV_BACK, null);
        addCenter(TitleBarView.TITLE_TYPE_CENTER_TXT, "会诊结果");
    }

    @Override
    public void onRightBtnClick() {
        super.onRightBtnClick();
//        if(mState != 2){
        mCaseAnalysis = mEtCaseAnalysis.getText().toString();
        mDiseaseDiagnosis = mEtDiseaseDiagnosis.getText().toString();
        mTreatmentPrograms = mEtTreatmentPrograms.getText().toString();
        ReqManager.getInstance().reqEndConsult(reqDelConsultationCall, Utils.getUserToken(EditConsultResultActivity.this), mRoomId, mCaseAnalysis, mDiseaseDiagnosis, mTreatmentPrograms, "");
//        }
    }

    private void delEditState(){
        if(mIsAddedResult){
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            mEtCaseAnalysis.setBackground(null);
            mEtDiseaseDiagnosis.setBackground(null);
            mEtTreatmentPrograms.setBackground(null);
            mEtCaseAnalysis.setLayoutParams(layoutParams);
            mEtDiseaseDiagnosis.setLayoutParams(layoutParams);
            mEtTreatmentPrograms.setLayoutParams(layoutParams);
            mEtCaseAnalysis.setEnabled(!mIsAddedResult);
            mEtDiseaseDiagnosis.setEnabled(!mIsAddedResult);
            mEtTreatmentPrograms.setEnabled(!mIsAddedResult);
        }
    }

    //接口回调
    Callback<RespStartOrEndConsult> reqDelConsultationCall = new Callback<RespStartOrEndConsult>() {
        @Override
        public void onError(Call call, Exception e) {
            onFailure(e.toString());
            closeProgressDialog();
        }

        @Override
        public void onResponse(RespStartOrEndConsult response) {
            closeProgressDialog();
            if (onSuccess(response)) {
                if (response.getResult() != null && response.getResult().getConsult() != null) {
                    mState = response.getResult().getConsult().getState();
                    ToastUtil.showLongToast("保存成功");
                    removeRightBtn();
                    mIsAddedResult = true;
                    delEditState();
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
    public void initView() {
        super.initView();
        mPatientAge = getView(R.id.modify_medical_record_patient_age);
        mPatientName = getView(R.id.modify_medical_record_patient_name);
        mPatientPhoto = getView(R.id.modify_medical_record_patient_photo);
        mPatientSex = getView(R.id.modify_medical_record_patient_sex);
        mBtZhuan = getView(R.id.bt_zhuanzhen);
        mBottomLayout = getView(R.id.consult_result_bottom_layout);

        mEtCaseAnalysis = getView(R.id.et_modify_medical_record_current_history);
        mEtDiseaseDiagnosis = getView(R.id.et_modify_medical_record_past_history);
        mEtTreatmentPrograms = getView(R.id.et_modify_medical_record_specialized);

        mBtZhuan.setOnClickListener(this);
    }

    @Override
    public void initData() {
        super.initData();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mExpertId = bundle.getLong(Constant.KEY_EXPERT_ID);
            mConsultionInfo = (EntityConsultInfo) bundle.getSerializable(Constant.KEY_CONSULTION_INFO);
            mState = bundle.getInt(CONSULT_STATE_KEY, -1);
            mRoomId = bundle.getLong(CONSULT_ROOMID_KEY);
            mIdentity = bundle.getInt(ChatActivity.IDENTITY_KEY);
            mIsCanZhuan = bundle.getBoolean(IS_EXPERT_CAN_ZHUAN_KEY);
            mIsAddedResult = bundle.getBoolean(IS_ADDED_RESULT);
            mBottomLayout.setVisibility(mIdentity == ChatActivity.IDENTITY_DOCTIR_KEY && mIsCanZhuan ? View.VISIBLE : View.GONE);

            if (mConsultionInfo != null) {
                bindViewData(mConsultionInfo);
            }

            delEditState();
            if (!mIsAddedResult) addRightBtn(TitleBarView.TITLE_TYPE_RIGHT_TXT, "保存");
        }
    }

    private void bindViewData(EntityConsultInfo consultInfo) {
        if (consultInfo != null) {
            EntityPatientInfo patientInfo = consultInfo.getPatient();
            if(patientInfo != null){
                mPatientAge.setText(String.valueOf(patientInfo.getAge()));
                mPatientName.setText(patientInfo.getName());
                ImageLoaderManager.getInstance().displayImage(patientInfo.getAvatar(), mPatientPhoto);
                mPatientSex.setImageResource(patientInfo.getGender() == 1 ? R.mipmap.ic_male : R.mipmap.ic_female);
            }
            if(mIsAddedResult){
                mEtCaseAnalysis.setText(mConsultionInfo.getCaseAnalysis());
                mEtDiseaseDiagnosis.setText(mConsultionInfo.getDiseaseDiagnosis());
                mEtTreatmentPrograms.setText(mConsultionInfo.getTreatmentPrograms());
            }
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.bt_zhuanzhen) {
            ExpertDetailsActivity.startIntent(EditConsultResultActivity.this, String.valueOf(mExpertId), Constant.KEY_CONSULT_ZHUAN);
        }
    }

    /**
     * Activity跳转
     *
     * @param consultInfo
     */
    public static void startIntent(Context ctx, boolean isAddResult, long expertId, EntityConsultInfo consultInfo, int state, long roomId, int identity, boolean isCanZhuan) {
        Intent intent = new Intent();
        intent.setClass(ctx, EditConsultResultActivity.class);
        Bundle b = new Bundle();
        b.putSerializable(Constant.KEY_CONSULTION_INFO, consultInfo);
        b.putInt(CONSULT_STATE_KEY, state);
        b.putInt(ChatActivity.IDENTITY_KEY, identity);
        b.putBoolean(IS_EXPERT_CAN_ZHUAN_KEY, isCanZhuan);
        b.putBoolean(IS_ADDED_RESULT, isAddResult);
        b.putLong(CONSULT_ROOMID_KEY, roomId);
        b.putLong(Constant.KEY_EXPERT_ID, expertId);
        intent.putExtras(b);
        ctx.startActivity(intent);
    }

    @Override
    public void onLeftBtnClick() {
        super.onLeftBtnClick();
    }

    @Override
    public boolean handleBack() {
        return super.handleBack();
    }

    @Override
    protected void onActivityResult(int mPicType, int resultCode, Intent data) {
        super.onActivityResult(mPicType, resultCode, data);
        if (data == null) {
            return;
        }
    }
}
