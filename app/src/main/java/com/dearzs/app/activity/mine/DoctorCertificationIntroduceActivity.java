package com.dearzs.app.activity.mine;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.dearzs.app.R;
import com.dearzs.app.base.BaseActivity;
import com.dearzs.app.util.Constant;
import com.dearzs.app.util.Utils;
import com.dearzs.app.widget.TitleBarView;

/**
 * Created by Lyl
 * 医生认证介绍界面
 */
public class DoctorCertificationIntroduceActivity extends BaseActivity {
    private Button mToCertification;
    public static Activity mDoctorCertificationIntroduceActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDoctorCertificationIntroduceActivity = this;

        setContentLayout(R.layout.activity_doctor_certification);
        addCenter(TitleBarView.TITLE_TYPE_CENTER_TXT, "医生认证");
        addLeftBtn(TitleBarView.TITLE_TYPE_LEFT_IV_BACK, null);
    }

    @Override
    public void initView() {
        super.initView();
        mToCertification = getView(R.id.bt_to_certification);

        mToCertification.setOnClickListener(this);
    }

    @Override
    public void initData() {
        super.initData();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.bt_to_certification:
                Bundle bundle = new Bundle();
                bundle.putBoolean(Constant.KEY_CERTIFICATION, true);
                Utils.startIntent(DoctorCertificationIntroduceActivity.this, PersionalDataActivity.class, bundle);
//                PersionalDataActivity.startIntent(DoctorCertificationIntroduceActivity.this);
//                DoctorCertificationActivity.startIntent(DoctorCertificationIntroduceActivity.this);
                break;

        }
    }

    /**
     * Activity跳转
     *
     * @param ctx
     */
    public static void startIntent(Context ctx) {
        Intent intent = new Intent();
        intent.setClass(ctx, DoctorCertificationIntroduceActivity.class);
        ctx.startActivity(intent);
    }
}
