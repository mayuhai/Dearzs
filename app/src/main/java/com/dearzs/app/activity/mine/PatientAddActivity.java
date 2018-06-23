package com.dearzs.app.activity.mine;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.dearzs.app.BaseApplication;
import com.dearzs.app.R;
import com.dearzs.app.base.BaseActivity;
import com.dearzs.app.entity.EntityBase;
import com.dearzs.app.entity.EntityPatientInfo;
import com.dearzs.app.entity.EntityUserInfo;
import com.dearzs.app.entity.resp.RespPatientDetails;
import com.dearzs.app.util.Constant;
import com.dearzs.app.util.ReqManager;
import com.dearzs.app.util.ToastUtil;
import com.dearzs.app.util.Utils;
import com.dearzs.app.wheel.test.DateSelector;
import com.dearzs.app.widget.CustomCellView;
import com.dearzs.app.widget.TitleBarView;
import com.dearzs.commonlib.okhttp.callback.Callback;

import de.greenrobot.event.EventBus;
import okhttp3.Call;
import okhttp3.Request;

/**
 * Created by mayuhai on 2016/6/15.
 */
public class PatientAddActivity extends BaseActivity implements View.OnClickListener {

    private EditText mEdtName;
    private EditText mEdtPhone;
    private EditText mEdtCardNO;
    private EditText mEdtAddress;
    private Button mAddBtn;
    private EntityPatientInfo mPatientSearchUsrInfo;
    private EntityPatientInfo mPatientUsrInfo;
    private boolean isNewPatient = true;

    private CustomCellView mGender;
    private CustomCellView mAge;

    private final static int DIALOG = 1;
    private static final int PERSIONAL_AGE = 0;

    private String mIdentityNum;
    private int mPatientType;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String dateStr = (String) msg.obj;
            switch (msg.what) {
                case PERSIONAL_AGE:
                    int age = Utils.getTime(dateStr, "yyyy-MM-dd");
                    mAge.setDesc(age + "岁");
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentLayout(R.layout.activity_patient_add);
        addCenter(TitleBarView.TITLE_TYPE_CENTER_TXT, "添加患者");
        addLeftBtn(TitleBarView.TITLE_TYPE_LEFT_IV_BACK, null);
    }

    Callback<RespPatientDetails> reqPatientSearchCall = new Callback<RespPatientDetails>() {
        @Override
        public void onError(Call call, Exception e) {
            e.printStackTrace();
            onFailure(e.toString());
        }

        @Override
        public void onResponse(RespPatientDetails response) {
            if (onSuccess(response)) {
                if(response.getResult() == null){
                    return;
                }
                if (!TextUtils.isEmpty(mIdentityNum)) {
                    mEdtCardNO.setText(mIdentityNum);
                }
                EntityPatientInfo patientInfo =  response.getResult().getUser();
                if (patientInfo != null) {
                    mPatientSearchUsrInfo = patientInfo;
                    if (mPatientSearchUsrInfo != null) {
                        ToastUtil.showLongToast("查到该患者");
                        String name = mPatientSearchUsrInfo.getName();
                        int age = mPatientSearchUsrInfo.getAge();
                        int gender = mPatientSearchUsrInfo.getGender();
                        String phone = mPatientSearchUsrInfo.getPhone();
                        String cardNO = mPatientSearchUsrInfo.getCardNo();
                        String address = mPatientSearchUsrInfo.getAddress();

                        mEdtName.setText(name);
                        mAge.setDesc(age + "岁");

                        if (gender == Constant.MALE) {
                            mGender.setDesc("男");
                        }else {
                            mGender.setDesc("女");
                        }
                        mEdtPhone.setText(phone);
                        mEdtAddress.setText(address);

                        isNewPatient = false;
                        mAddBtn.setText(R.string.ok);

                        mEdtName.setEnabled(false);
                        mEdtPhone.setEnabled(false);
                        mEdtCardNO.setEnabled(false);
                        mEdtAddress.setEnabled(false);
                        mGender.setClickable(false);
                        mAge.setClickable(false);
                    }else {
                        ToastUtil.showLongToast("没有查到该患者，请填写信息");
                    }
                }else {
                    ToastUtil.showLongToast("没有查到该患者，请填写信息");
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
        mEdtName = (EditText) findViewById(R.id.edtName);
//        mEdtGender = (EditText) findViewById(R.id.edtGender);
//        mEdtAge = (EditText) findViewById(R.id.edtAge);
        mEdtPhone = (EditText) findViewById(R.id.edtPhone);
        mEdtCardNO = (EditText) findViewById(R.id.edtCardNO);
        mEdtAddress = (EditText) findViewById(R.id.edtAddress);

        mGender = getView(R.id.cell_persional_sex);
        mAge = getView(R.id.cell_persional_age);

        mGender.setClickable(true);
        mAge.setClickable(true);

        mGender.setOnClickListener(this);
        mAge.setOnClickListener(this);

        mAddBtn = (Button) findViewById(R.id.btnAdd);
        mAddBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAdd:
                if (!isNewPatient) {
                    String name = mPatientSearchUsrInfo.getName();
                    String address = mPatientSearchUsrInfo.getAddress();
                    String ageStr = mPatientSearchUsrInfo.getAge() + "";
                    if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(address) && !TextUtils.isEmpty(mGender.getDesc()) && !TextUtils.isEmpty(ageStr)) {
//                        EventBus.getDefault().post(mPatientSearchUsrInfo);
//                        finish();
                        ReqManager.getInstance().reqPatientAdd(reqPatientAddCallBack, Utils.getUserToken(PatientAddActivity.this), null, mPatientSearchUsrInfo.getId() + "", null, null, null, null, null, mPatientType);
                    }else {
                        ToastUtil.showShortToast("请补全所有信息！！！");
                    }
                }else {
                    String name = mEdtName.getText().toString().trim();
                    String phone = mEdtPhone.getText().toString().trim();
                    String cardNO = mEdtCardNO.getText().toString().trim();
                    String address = mEdtAddress.getText().toString().trim();

                    String ageStr = mAge.getDesc();
                    String age = "0";
                    if (!TextUtils.isEmpty(ageStr)) {
                        age = ageStr.substring(0, mAge.getDesc().length() - 1);
                    }

                    int gender = Constant.MALE;
                    if (mGender.getDesc().equalsIgnoreCase("女")){
                        gender = Constant.FEMALE;
                    }

                    mPatientUsrInfo = new EntityPatientInfo();
                    mPatientUsrInfo.setName(name);
                    mPatientUsrInfo.setPhone(phone);
                    mPatientUsrInfo.setCardNo(cardNO);
                    mPatientUsrInfo.setAge(Integer.valueOf(age));
                    mPatientUsrInfo.setAddress(address);
                    mPatientUsrInfo.setGender(gender);

                    if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(address) && !TextUtils.isEmpty(mGender.getDesc()) && !TextUtils.isEmpty(ageStr)) {
                        if (!TextUtils.isEmpty(phone) && Utils.isPhoneNumberValid(phone)){
                            if(!TextUtils.isEmpty(cardNO) && Utils.isIdentityCard(cardNO)) {
                                ReqManager.getInstance().reqPatientAdd(reqPatientAddCallBack, Utils.getUserToken(PatientAddActivity.this), cardNO, null, name, gender, Integer.valueOf(age), phone, address, mPatientType);
                            }else {
                                ToastUtil.showShortToast("身份证信息输入错误，请重新输入！！！");
                            }
                        }else{
                            ToastUtil.showShortToast("手机号码输入错误，请重新输入！！！");
                        }
                    }else {
                        ToastUtil.showShortToast("请补全所有信息！！！");
                    }
                }
                break;
            case R.id.cell_persional_age:
                //年龄
                DateSelector ageDateSelector = new DateSelector();
                ageDateSelector.init(PatientAddActivity.this, mHandler, PERSIONAL_AGE, true);
                ageDateSelector.showDaySelectorDialog();
                break;
            case R.id.cell_persional_sex:
                //性别
                showDialog(DIALOG);
                break;
        }
    }

    Callback<EntityBase> reqPatientAddCallBack = new Callback<EntityBase>() {
        @Override
        public void onError(Call call, Exception e) {
            closeProgressDialog();
            onFailure(e.toString());
        }

        @Override
        public void onResponse(EntityBase response) {
            if (onSuccess(response)) {
                ToastUtil.showLongToast("添加成功 ！");
                if (mPatientUsrInfo != null) {
                    EventBus.getDefault().post(mPatientUsrInfo);
                }

                if (mPatientSearchUsrInfo != null) {
                    EventBus.getDefault().post(mPatientSearchUsrInfo);
                }

                if (PatientSearchActivity.mActivity != null) {
                    PatientSearchActivity.mActivity.finish();
                }
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
        mIdentityNum = getIntent().getStringExtra(Constant.KEY_IDENTITY_NUM);
        mPatientType = getIntent().getIntExtra(Constant.KEY_PATIENT_TYPE, 0);

        if (!TextUtils.isEmpty(mIdentityNum)) {
            ReqManager.getInstance().reqPatientSearch(reqPatientSearchCall, Utils.getUserToken(PatientAddActivity.this), mIdentityNum);
        }
    }

    /**
     * 创建单选按钮对话框
     */
    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog=null;
        switch (id) {
            case DIALOG:
                android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(this);
                //0: 默认第一个单选按钮被选中
                int sexIndex = 0;

                EntityUserInfo userInfo = BaseApplication.getInstance().getUserInfo();
                if (userInfo.getGender() == Constant.MALE) {
                    sexIndex = 0;
                }else {
                    sexIndex = 1;
                }
                builder.setSingleChoiceItems(R.array.sex, sexIndex, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        String sexStr = getResources().getStringArray(R.array.sex)[which];
                        mGender.setDesc(sexStr);
                        dialog.dismiss();
                    }
                });
                //创建一个单选按钮对话框
                dialog=builder.create();
                break;
        }
        return dialog;
    }

    public static void startIntent(Context context, int type, String identityNum){
        Intent intent = new Intent(context, PatientAddActivity.class);
        intent.putExtra(Constant.KEY_PATIENT_TYPE, type);
        intent.putExtra(Constant.KEY_IDENTITY_NUM, identityNum);
        context.startActivity(intent);
    }

}
