package com.dearzs.app.activity.expert;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dearzs.app.BaseApplication;
import com.dearzs.app.R;
import com.dearzs.app.adapter.GvImageAdapter;
import com.dearzs.app.base.BaseActivity;
import com.dearzs.app.entity.EntityMedicalRecordHistoryInfo;
import com.dearzs.app.entity.EntityMedicalRecordImgs;
import com.dearzs.app.entity.EntityPatientInfo;
import com.dearzs.app.entity.EntityPatientMedicalRecord;
import com.dearzs.app.entity.EntityUserInfo;
import com.dearzs.app.entity.resp.RespGetPatientMedicalRecord;
import com.dearzs.app.util.Constant;
import com.dearzs.app.util.DimenUtils;
import com.dearzs.app.util.ImageLoaderManager;
import com.dearzs.app.util.ReqManager;
import com.dearzs.app.util.Utils;
import com.dearzs.app.widget.CircleImageView;
import com.dearzs.app.widget.FixGridLayout;
import com.dearzs.app.widget.TitleBarView;
import com.dearzs.commonlib.okhttp.callback.Callback;
import com.dearzs.commonlib.utils.DisplayUtil;
import com.dearzs.commonlib.utils.GetViewUtil;
import com.dearzs.commonlib.utils.LayoutUtil;

import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Request;

/**
 * Created by luyanlong on 2016/6/24.
 * 病历界面
 */
public class MedicalRecordActivity extends BaseActivity {
    private TextView mPatientName, mPatientAge, mDiagnosisData;
    private CircleImageView mPatientPhoto;
    private ImageView mPatientSex;
    private TextView mTvCurrentHistory, mTvPastHistory, mTvSpecialized, mTvAuxiliary, mTvTestResult, mTvDiagnosticResult;
    private GridView mGvCurrentHistory, mGvPastHistory, mGvSpecialized, mGvAuxiliary, mGvTestResult, mGvDiagnosticResult;
    private FixGridLayout mLableLayout;
    private GvImageAdapter mCurrentImageAdapter;
    private GvImageAdapter mPastImageAdapter;
    private GvImageAdapter mSpecializedImageAdapter;
    private GvImageAdapter mAuxiliaryImageAdapter;
    private GvImageAdapter mTestResultImageAdapter;
    private GvImageAdapter mDiagnosticResultImageAdapter;

    private EntityPatientMedicalRecord mMedicalRecord;
    private EntityUserInfo mUserInfo;
    private EntityPatientInfo mPatientInfo;

    private long mPatientId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_medical_record);

        addLeftBtn(TitleBarView.TITLE_TYPE_LEFT_IV_BACK, null);
        addCenter(TitleBarView.TITLE_TYPE_CENTER_TXT, "病历");
    }

    @Override
    public void onRightBtnClick() {
        super.onRightBtnClick();
        if (mPatientId == mUserInfo.getId() || mUserInfo.getType() == EntityUserInfo.NORMALUSER) {
            Bundle bundle = new Bundle();
            bundle.putLong(Constant.KEY_PATIENT_ID, mPatientId);
            Utils.startIntent(MedicalRecordActivity.this, MedicalRecordHistoryListActivity.class, bundle);
        } else {
            Intent intent = new Intent(MedicalRecordActivity.this, MedicalRecordModifyActivity.class);
            intent.putExtra(Constant.KEY_MEDICAL_RECORD_INFO, mMedicalRecord);
            intent.putExtra(Constant.KEY_PATIENT_INFO, mPatientInfo);
            startActivityForResult(intent, Constant.REQUEST_CODE_PATIENT_MEDICAL_RECORD);
        }
    }

    @Override
    public void onRightBtn2Click() {
        super.onRightBtn2Click();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.KEY_PATIENT_INFO, mPatientInfo);
        bundle.putLong(Constant.KEY_PATIENT_ID, mPatientId);
        Utils.startIntent(MedicalRecordActivity.this, MedicalRecordHistoryListActivity.class, bundle);
    }

    @Override
    public void initView() {
        super.initView();
        mPatientAge = getView(R.id.medical_record_patient_age);
        mPatientName = getView(R.id.medical_record_patient_name);
        mPatientPhoto = getView(R.id.medical_record_patient_photo);
        mPatientSex = getView(R.id.medical_record_patient_sex);
        mDiagnosisData = getView(R.id.medical_record_data);

        mLableLayout = getView(R.id.medical_record_auxiliary_lable_layout);

        mTvCurrentHistory = getView(R.id.tv_medical_record_current_history);
        mTvPastHistory = getView(R.id.tv_medical_record_past_history);
        mTvSpecialized = getView(R.id.tv_medical_record_specialized);
        mTvAuxiliary = getView(R.id.tv_medical_record_auxiliary);
        mTvTestResult = getView(R.id.tv_medical_record_test_result);
        mTvDiagnosticResult = getView(R.id.tv_medical_record_diagnostic_result);
        mGvCurrentHistory = getView(R.id.gv_medical_record_current_history);
        mGvPastHistory = getView(R.id.gv_medical_record_past_history);
        mGvSpecialized = getView(R.id.gv_medical_record_specialized);
        mGvAuxiliary = getView(R.id.gv_medical_record_auxiliary);
        mGvTestResult = getView(R.id.gv_medical_record_test_result);
        mGvDiagnosticResult = getView(R.id.gv_medical_record_diagnostic_result);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Constant.REQUEST_CODE_PATIENT_MEDICAL_RECORD) {
                ReqManager.getInstance().reqPatientMedicalRecord(reqMedicalRecordallback, Utils.getUserToken(MedicalRecordActivity.this), String.valueOf(mPatientId));
            }
        }
    }

    @Override
    public void initData() {
        super.initData();
        Bundle bundle = getIntent().getExtras();
        mUserInfo = BaseApplication.getInstance().getUserInfo();
        if (bundle != null) {
            String from = bundle.getString(Constant.KEY_FROM);
            if (Constant.KEY_FROM_PATIENT_DETAILS.equals(from) || Constant.KEY_FROM_CONSULT_RESULT.equals(from)) {     //患者详情
                EntityPatientInfo patientInfo = (EntityPatientInfo) bundle.getSerializable(Constant.KEY_PATIENT_INFO);
                String uid = "";
                if (patientInfo != null) {
                    uid = String.valueOf(patientInfo.getId());
                    mPatientId = patientInfo.getId();
                    ImageLoaderManager.getInstance().displayImage(patientInfo.getAvatar(), mPatientPhoto);
                    mPatientAge.setText(patientInfo.getAge() + "");
                    mPatientSex.setImageResource(patientInfo.getGender() == 1 ? R.mipmap.ic_male : R.mipmap.ic_female);
                    mPatientName.setText(patientInfo.getName());
                }
                ReqManager.getInstance().reqPatientMedicalRecord(reqMedicalRecordallback, Utils.getUserToken(MedicalRecordActivity.this), uid);
            } else if (Constant.KEY_FROM_MEDICAL_RECORD_HISTORY.equals(from)) {
                EntityMedicalRecordHistoryInfo historyInfo = (EntityMedicalRecordHistoryInfo) bundle.getSerializable(Constant.KEY_MEDICAL_RECORD_HISTORY_INFO);
                String historyId = "";
                mPatientInfo = (EntityPatientInfo) bundle.getSerializable(Constant.KEY_PATIENT_INFO);
                if (historyInfo == null) return;
                historyId = String.valueOf(historyInfo.getId());
                ReqManager.getInstance().reqMedicalRecordDetails(reqMedicalRecordallback, Utils.getUserToken(MedicalRecordActivity.this), historyId);
            }

            if (mPatientId == mUserInfo.getId() || mUserInfo.getType() == EntityUserInfo.NORMALUSER) {
                addRightBtn(TitleBarView.TITLE_TYPE_RIGHT_IV_MESSAGE, null);
                setRightIvResource(R.mipmap.ic_title_history);
            } else {
                addRightBtn(TitleBarView.TITLE_TYPE_RIGHT_IV_EDIT_AND_HISTORY, null);
            }
        }

        mCurrentImageAdapter = new GvImageAdapter(MedicalRecordActivity.this, R.layout.item_gv_dynamic_image, new ArrayList<EntityMedicalRecordImgs>());
        mGvCurrentHistory.setAdapter(mCurrentImageAdapter);
        mPastImageAdapter = new GvImageAdapter(MedicalRecordActivity.this, R.layout.item_gv_dynamic_image, new ArrayList<EntityMedicalRecordImgs>());
        mGvPastHistory.setAdapter(mPastImageAdapter);
        mAuxiliaryImageAdapter = new GvImageAdapter(MedicalRecordActivity.this, R.layout.item_gv_dynamic_image, new ArrayList<EntityMedicalRecordImgs>());
        mGvAuxiliary.setAdapter(mAuxiliaryImageAdapter);
        mSpecializedImageAdapter = new GvImageAdapter(MedicalRecordActivity.this, R.layout.item_gv_dynamic_image, new ArrayList<EntityMedicalRecordImgs>());
        mGvSpecialized.setAdapter(mSpecializedImageAdapter);
        mTestResultImageAdapter = new GvImageAdapter(MedicalRecordActivity.this, R.layout.item_gv_dynamic_image, new ArrayList<EntityMedicalRecordImgs>());
        mGvTestResult.setAdapter(mTestResultImageAdapter);
        mDiagnosticResultImageAdapter = new GvImageAdapter(MedicalRecordActivity.this, R.layout.item_gv_dynamic_image, new ArrayList<EntityMedicalRecordImgs>());
        mGvDiagnosticResult.setAdapter(mDiagnosticResultImageAdapter);
    }

    //获取患者病历接口回调
    Callback<RespGetPatientMedicalRecord> reqMedicalRecordallback = new Callback<RespGetPatientMedicalRecord>() {
        @Override
        public void onError(Call call, Exception e) {
            onFailure(e.toString());
            showErrorLayout(getString(R.string.error_message), R.mipmap.ic_empty_img);
        }

        @Override
        public void onResponse(RespGetPatientMedicalRecord response) {
            hideErrorLayout();
            if (onSuccess(response)) {
                if (response != null) {
                    bindViewData(response);
                }
            }
        }

        @Override
        public void onBefore(Request request) {
            showProgressDialog();
            super.onBefore(request);
        }
    };

    private void bindViewData(RespGetPatientMedicalRecord response) {
        if (response == null || response.getResult() == null) {
            return;
        }
        int height = (DisplayUtil.getScreenWidth(MedicalRecordActivity.this) - DimenUtils.dip2px(MedicalRecordActivity.this, 48)) / 4;
        EntityPatientMedicalRecord medicalRecord = response.getResult().getHistory();
        if (response.getResult().getUser() != null) {
            mPatientInfo = response.getResult().getUser();
        }
        if (mPatientInfo != null) {
            mPatientId = mPatientInfo.getId();
            ImageLoaderManager.getInstance().displayImage(mPatientInfo.getAvatar(), mPatientPhoto);
            mPatientAge.setText(mPatientInfo.getAge() + "");
            mPatientSex.setImageResource(mPatientInfo.getGender() == 1 ? R.mipmap.ic_male : R.mipmap.ic_female);
            mPatientName.setText(mPatientInfo.getName());
//                    if(mPatientId == mUserInfo.getId() || mUserInfo.getType() == EntityUserInfo.NORMALUSER){
//                        addRightBtn(TitleBarView.TITLE_TYPE_RIGHT_IV_MESSAGE, null);
//                        setRightIvResource(R.mipmap.ic_title_history);
//                    } else {
//                        addRightBtn(TitleBarView.TITLE_TYPE_RIGHT_IV_EDIT_AND_HISTORY, null);
//                    }
        }
        if (medicalRecord != null) {
            mMedicalRecord = medicalRecord;
            hideErrorLayout();
            mDiagnosisData.setText(Utils.getTimeStamp(medicalRecord.getCreateTime()));
            mTvCurrentHistory.setText(medicalRecord.getPresent());
            mTvPastHistory.setText(medicalRecord.getPast());
            mTvAuxiliary.setText(medicalRecord.getAidText());
            mTvSpecialized.setText(medicalRecord.getSpecial());
            mTvTestResult.setText(medicalRecord.getLabResult());
            mTvDiagnosticResult.setText(medicalRecord.getDiagResult());
            mGvCurrentHistory.setVisibility(medicalRecord.getPresentImgs() == null ? View.GONE : View.VISIBLE);
            mGvPastHistory.setVisibility(medicalRecord.getPastImgs() == null ? View.GONE : View.VISIBLE);
            mGvAuxiliary.setVisibility(medicalRecord.getAidImgs() == null ? View.GONE : View.VISIBLE);
            mGvSpecialized.setVisibility(medicalRecord.getSpecialImgs() == null ? View.GONE : View.VISIBLE);
            mTvDiagnosticResult.setVisibility(medicalRecord.getDiagImgs() == null ? View.GONE : View.VISIBLE);
            if (medicalRecord.getPresentImgs() != null) {
                mCurrentImageAdapter.replaceAll(medicalRecord.getPresentImgs());
            }
            if (medicalRecord.getPastImgs() != null) {
                mPastImageAdapter.replaceAll(medicalRecord.getPastImgs());
            }
            if (medicalRecord.getSpecialImgs() != null) {
                mSpecializedImageAdapter.replaceAll(medicalRecord.getSpecialImgs());
            }
            if (medicalRecord.getAidImgs() != null) {
                mAuxiliaryImageAdapter.replaceAll(medicalRecord.getAidImgs());
                int auxiLine = ((int) (mAuxiliaryImageAdapter.getCount() / 4)) + (mAuxiliaryImageAdapter.getCount() % 4 == 0 ? 0 : 1);
                int marginHeight = auxiLine > 1 ? (auxiLine - 1) * DimenUtils.dip2px(MedicalRecordActivity.this, 10) : 0;
                LayoutUtil.reMeasureHeight(mGvAuxiliary, height * auxiLine + marginHeight);
            }
            if (medicalRecord.getLabImgs() != null) {
                mTestResultImageAdapter.replaceAll(medicalRecord.getLabImgs());
                int testLine = ((int) (mTestResultImageAdapter.getCount() / 4)) + (mTestResultImageAdapter.getCount() % 4 == 0 ? 0 : 1);
                int marginHeight = testLine > 1 ? (testLine - 1) * DimenUtils.dip2px(MedicalRecordActivity.this, 10) : 0;
                LayoutUtil.reMeasureHeight(mGvTestResult, testLine * height + marginHeight);
            }
            if (medicalRecord.getDiagImgs() != null) {
                mDiagnosticResultImageAdapter.replaceAll(medicalRecord.getDiagImgs());
            }
        } else {
            showEmptyLayout("暂无病例", R.mipmap.ic_empty_img);
        }

        if (mLableLayout.getChildCount() > 0) {
            mLableLayout.removeAllViews();
        }
        //TODO  辅助检查的标签-------------------------------------
//        mLableLayout.setPadding(DimenUtils.dip2px(MedicalRecordActivity.this, 7), 0, DimenUtils.dip2px(MedicalRecordActivity.this, 7), 0);
//        for (int i = 0; i < 5; i++) {
//            String text = "第" + 10 * i + "个 child ！！";
//            View view = getTextView(MedicalRecordActivity.this, text);
//            mLableLayout.addView(view);
//        }
    }

    private View getTextView(Context context, String text) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_lable_layout, null);
        TextView textView = GetViewUtil.getView(view, R.id.item_title_text);
        textView.setText(text);
        return view;
    }
}
