package com.dearzs.app.activity.mine;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.dearzs.app.R;
import com.dearzs.app.adapter.CustomSpinerAdapter;
import com.dearzs.app.base.BaseActivity;
import com.dearzs.app.entity.EntityPatientInfo;
import com.dearzs.app.util.Constant;
import com.dearzs.app.util.ToastUtil;
import com.dearzs.app.util.Utils;
import com.dearzs.app.widget.TitleBarView;

import de.greenrobot.event.Subscribe;

/**
 * Created by mayuhai on 2016/6/15.
 */
public class PatientSearchActivity extends BaseActivity implements View.OnClickListener {
    private TextView mTView;
    private EditText mEdtCardNO;
    private CustomSpinerAdapter mAdapter;
    public static Activity mActivity;
    private int mPatientType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentLayout(R.layout.activity_patient_search);
        addCenter(TitleBarView.TITLE_TYPE_CENTER_TXT, "查询患者");
        addLeftBtn(TitleBarView.TITLE_TYPE_LEFT_IV_BACK, null);
        mActivity = this;
    }

    @Override
    public void initView() {
        super.initView();
//        mTView = (TextView) findViewById(R.id.tv_value);
        mEdtCardNO = (EditText) findViewById(R.id.edt_cardNO);
        findViewById(R.id.btn_query).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_query:
                String cardNO = mEdtCardNO.getText().toString();
                if (!TextUtils.isEmpty(cardNO) && Utils.isIdentityCard(cardNO)) {
                    PatientAddActivity.startIntent(PatientSearchActivity.this, mPatientType, cardNO);
                }else {
                    ToastUtil.showLongToast("请输入正确的身份证号！");
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == Constant.REQUEST_CODE_ADD_PATIENT_ACTIVITY){
                setResult(RESULT_OK, data);
                finish();
            }
        }
    }

    //添加患者
    @Subscribe
    public void handleEventBus(EntityPatientInfo patientInfo) {
        if (patientInfo != null) {
            //如果是添加患者，则将搜索患者页面一并关闭，直接返回上层页面
            finish();
        }
    }

    @Override
    public void initData() {
        super.initData();
        mPatientType = getIntent().getIntExtra(Constant.KEY_PATIENT_TYPE, 0);
    }

    public static void startIntent(Context context, int type){
        Intent intent = new Intent(context, PatientSearchActivity.class);
        intent.putExtra(Constant.KEY_PATIENT_TYPE, type);
        context.startActivity(intent);
    }

}
