package com.dearzs.app.activity.order;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dearzs.app.R;
import com.dearzs.app.base.BaseActivity;
import com.dearzs.app.entity.EntityExpertInfo;
import com.dearzs.app.entity.EntityHospitalDepartmentInfo;
import com.dearzs.app.entity.EntityHospitalInfo;
import com.dearzs.app.util.Constant;
import com.dearzs.app.util.ImageLoaderManager;
import com.dearzs.app.widget.TitleBarView;

/**
 * Created by luyanlong on 2016/6/18.
 * 预约成功界面
 */
public class OrderCompleteActivity extends BaseActivity {
    private TextView mDoctorName, mDoctorJob, mDoctorHospital, mDoctorDepartment, mVisitTime, mOrderMoney;
    private ImageView mDoctorPhoto;
    private Button mConfirmBt;

    private EntityExpertInfo mExpertInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_order_complete);

        addCenter(TitleBarView.TITLE_TYPE_CENTER_TXT, "预约成功");
        addLeftBtn(TitleBarView.TITLE_TYPE_LEFT_IV_BACK, null);
        addRightBtn(TitleBarView.TITLE_TYPE_RIGHT_TXT, "取消订单");
    }

    @Override
    public void initView() {
        super.initView();
        mDoctorName = getView(R.id.order_complete_expert_name);
        mDoctorJob = getView(R.id.order_complete_expert_job);
        mDoctorHospital = getView(R.id.order_complete_expert_hospital);
        mDoctorDepartment = getView(R.id.order_complete_expert_department);
        mVisitTime = getView(R.id.order_complete_expert_time);
        mOrderMoney = getView(R.id.order_complete_order_money);
        mDoctorPhoto = getView(R.id.order_complete_expert_photo);
        mConfirmBt = getView(R.id.order_complete_bt);
    }

    @Override
    public void onRightBtnClick() {
        super.onRightBtnClick();
        finish();
    }

    @Override
    public void initData() {
        super.initData();
        if(getIntent() != null){
            mExpertInfo = (EntityExpertInfo) getIntent().getSerializableExtra(Constant.KEY_EXPERT_INFO);
            if(mExpertInfo != null){
                ImageLoaderManager.getInstance().displayImage(mExpertInfo.getAvatar(), mDoctorPhoto);
                EntityHospitalInfo hospitalInfo = mExpertInfo.getHospital();
                EntityHospitalDepartmentInfo departmentInfo = mExpertInfo.getDepartment();
                mDoctorName.setText(mExpertInfo.getName());
                if(hospitalInfo != null){
                    mDoctorDepartment.setText(hospitalInfo.getName());
                }
                if(departmentInfo != null){
                    mDoctorDepartment.setText(departmentInfo.getName());
                }
                mDoctorJob.setText(mExpertInfo.getJob());
                mOrderMoney.setText(mExpertInfo.getReferralMoney() + "");
//                mVisitTime.setText(expertInfo.());
            }
        }
    }
}
