package com.dearzs.app.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dearzs.app.R;
import com.dearzs.app.activity.expert.MedicalRecordActivity;
import com.dearzs.app.base.BaseActivity;
import com.dearzs.app.chat.ui.ChatActivity;
import com.dearzs.app.entity.EntityBase;
import com.dearzs.app.entity.EntityPatientInfo;
import com.dearzs.app.entity.resp.RespPatientDetails;
import com.dearzs.app.util.Constant;
import com.dearzs.app.util.ImageLoaderManager;
import com.dearzs.app.util.ReqManager;
import com.dearzs.app.util.ToastUtil;
import com.dearzs.app.util.Utils;
import com.dearzs.app.widget.CircleImageView;
import com.dearzs.app.widget.CustomCellViewWithImage;
import com.dearzs.app.widget.TitleBarView;
import com.dearzs.commonlib.okhttp.callback.Callback;
import com.tencent.TIMConversationType;

import okhttp3.Call;
import okhttp3.Request;

/**
 * Created by mayuhai on 2016/6/15.
 */
public class PatientDetailActivity extends BaseActivity implements View.OnClickListener {

    private CustomCellViewWithImage mCcvCardNo;
    private CustomCellViewWithImage mCcvPhone;
    private CustomCellViewWithImage mCcvMedicalRecord;
    private CircleImageView mUserPhoto;
    private TextView mTvPatinetName;
    private ImageView mImPantentGander;
    private TextView mTvPatientAge;
    private Button mDeleteBtn;
    private Button mMessageBtn;
    private String mUid;
    private String mPhone;
    private EntityPatientInfo patientInfo;

    private boolean isNewPatient = true;
    private int mPatientType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentLayout(R.layout.activity_patient_detail);
        addCenter(TitleBarView.TITLE_TYPE_CENTER_TXT, "患者详情");
        addLeftBtn(TitleBarView.TITLE_TYPE_LEFT_IV_BACK, null);

        Intent intent = getIntent();
        if (intent != null) {
            mUid = intent.getStringExtra(Constant.KEY_PATIENT_ID);
            mPatientType = intent.getIntExtra(Constant.KEY_PATIENT_TYPE, 0);
            if (!TextUtils.isEmpty(mUid)) {
                ReqManager.getInstance().reqPatientDetail(reqPatientDetailCall, Utils.getUserToken(PatientDetailActivity.this), mUid);
            }
        }

    }


    Callback<RespPatientDetails> reqPatientDetailCall = new Callback<RespPatientDetails>() {
        @Override
        public void onError(Call call, Exception e) {
            e.printStackTrace();
            onFailure(e.toString());
        }

        @Override
        public void onResponse(RespPatientDetails response) {
            if (onSuccess(response)) {
                if (response.getResult() == null) {
                    return;
                }
                patientInfo = response.getResult().getUser();
                if (patientInfo != null) {
                    String name = patientInfo.getName();
                    int age = patientInfo.getAge();
                    int gender = patientInfo.getGender();
                    String cardNO = patientInfo.getCardNo();
                    mPhone = patientInfo.getPhone();

                    mTvPatinetName.setText(name);
                    if (gender == Constant.FEMALE) {
                        mImPantentGander.setImageResource(R.mipmap.ic_female);
                    }else {
                        mImPantentGander.setImageResource(R.mipmap.ic_male);
                    }

                    mTvPatientAge.setText("年龄:" + age);
                    mCcvCardNo.setDesc(cardNO);
                    mCcvPhone.setDesc(mPhone);

                    ImageLoaderManager.getInstance().displayImageNoCache(patientInfo.getAvatar(), mUserPhoto);

                    addCenter(TitleBarView.TITLE_TYPE_CENTER_TXT, name);
                } else {
                    ToastUtil.showLongToast("没有查到该患者");
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
        mCcvCardNo = getView(R.id.iv_cardNO);
        mCcvPhone = getView(R.id.iv_phone);
        mCcvMedicalRecord = getView(R.id.iv_medical_record);
        mUserPhoto = getView(R.id.iv_user_photo);

        mTvPatinetName = getView(R.id.tv_name);
        mImPantentGander = getView(R.id.iv_gander);
        mTvPatientAge = getView(R.id.tv_age);

        mCcvPhone.setOnClickListener(this);
        mCcvMedicalRecord.setOnClickListener(this);

        mDeleteBtn = (Button) findViewById(R.id.btn_delete);
        mDeleteBtn.setOnClickListener(this);

        mDeleteBtn = (Button) findViewById(R.id.btn_message);
        mDeleteBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_delete:
                showConfirmDialog(PatientDetailActivity.this, "确定要删除该患者吗？", new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        ReqManager.getInstance().reqPatientDelete(reqPatientDeleteCallBack, Utils.getUserToken(PatientDetailActivity.this), mUid, mPatientType);
                    }
                }, null);

                break;
            case R.id.btn_message:
                if(patientInfo == null) return;
                ChatActivity.startIntent(this, patientInfo.getPhone(), false, TIMConversationType.C2C);
                break;
            case R.id.iv_phone:
                //用intent启动拨打电话
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mPhone));
                startActivity(intent);
                break;
            case R.id.iv_medical_record:
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constant.KEY_PATIENT_INFO, patientInfo);
                bundle.putString(Constant.KEY_FROM, Constant.KEY_FROM_PATIENT_DETAILS);
                Utils.startIntent(PatientDetailActivity.this, MedicalRecordActivity.class, bundle);
                break;
        }
    }

    Callback<EntityBase> reqPatientDeleteCallBack = new Callback<EntityBase>() {
        @Override
        public void onError(Call call, Exception e) {
            closeProgressDialog();
            onFailure(e.toString());
        }

        @Override
        public void onResponse(EntityBase response) {
            if (onSuccess(response)) {
                ToastUtil.showLongToast("删除成功 ！");
                finish();
            }
        }

        @Override
        public void onBefore(Request request) {
            super.onBefore(request);
            showProgressDialog();
        }
    };

    @Override
    public void initData() {
        super.initData();
    }

    public static void startIntent(Context context, String patientId, int type){
        Intent intent =new Intent(context, PatientDetailActivity.class);
        intent.putExtra(Constant.KEY_PATIENT_ID, patientId);
        intent.putExtra(Constant.KEY_PATIENT_TYPE, type);
        context.startActivity(intent);
    }
}
