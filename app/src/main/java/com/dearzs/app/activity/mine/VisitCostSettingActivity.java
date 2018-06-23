package com.dearzs.app.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.dearzs.app.BaseApplication;
import com.dearzs.app.R;
import com.dearzs.app.base.BaseActivity;
import com.dearzs.app.entity.EntityUserInfo;
import com.dearzs.app.entity.EntityUserVisits;
import com.dearzs.app.entity.resp.RespUserLogin;
import com.dearzs.app.util.ReqManager;
import com.dearzs.app.util.ToastUtil;
import com.dearzs.app.util.Utils;
import com.dearzs.app.widget.CustomCellViewWithImage;
import com.dearzs.app.widget.TitleBarView;
import com.dearzs.commonlib.okhttp.callback.Callback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Request;

/**
 * Created by mayuhai on 2016/4/10.
 */
public class VisitCostSettingActivity extends BaseActivity implements View.OnClickListener {

    private EditText mEditCost;
    private CheckBox mCheckBox;
    private TextView mStatus;
    private EntityUserInfo userInfo;
    private CustomCellViewWithImage mMonday;
    private CustomCellViewWithImage mTuesday;
    private CustomCellViewWithImage mWednesday;
    private CustomCellViewWithImage mThursday;
    private CustomCellViewWithImage mFriday;
    private CustomCellViewWithImage mSaturday;
    private CustomCellViewWithImage mSunday;

    public static final int RG_VISITCOTTIME_REQUEST = 1;    // 判断回调函数的值
    private String mTimeStr = null;
    private int mWeekDay = -1;

    private final Handler mSoftInputHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                showInputMethod(mEditCost);
            } else {
                hideInputMethod(mEditCost);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentLayout(R.layout.activity_visit_cost_setting);
        addCenter(TitleBarView.TITLE_TYPE_CENTER_TXT, "出诊设置");
        addLeftBtn(TitleBarView.TITLE_TYPE_LEFT_IV_BACK, null);

        findViewById(R.id.btn_ok).setOnClickListener(this);

        addRightBtn(TitleBarView.TITLE_TYPE_RIGHT_TXT, "确定");
    }

    @Override
    public void onRightBtnClick() {
        super.onRightBtnClick();
        Bundle bundle = new Bundle();
        Intent intent = null;

        boolean isOn = mCheckBox.isChecked();
        if (isOn) {
            if (mEditCost.getText() != null && !TextUtils.isEmpty(mEditCost.getText().toString())) {
                bundle.putString("cost", mEditCost.getText().toString());//给 bundle 写入数据

            }else {
                ToastUtil.showLongToast("费用不能为空");
            }
        }

        intent = new Intent();
        bundle.putBoolean("isOn", mCheckBox.isChecked());
        bundle.putInt("weekday", mWeekDay);
        bundle.putString("time", mTimeStr);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);

        mSoftInputHandler.sendEmptyMessage(1);
        finish();
    }

    @Override
    public void initView() {
        super.initView();
        mEditCost = getView(R.id.edt_cost);

        mCheckBox = getView(R.id.checkbox);
        mCheckBox.setOnClickListener(this);

        mStatus = getView(R.id.tv_status);

        mMonday = getView(R.id.iv_Monday);
        mTuesday = getView(R.id.iv_Tuesday);
        mWednesday = getView(R.id.iv_Wednesday);
        mThursday = getView(R.id.iv_Thursday);
        mFriday = getView(R.id.iv_Friday);
        mSaturday = getView(R.id.iv_Saturday);
        mSunday = getView(R.id.iv_Sunday);

        mMonday.setClickable(true);
        mTuesday.setClickable(true);
        mWednesday.setClickable(true);
        mThursday.setClickable(true);
        mFriday.setClickable(true);
        mSaturday.setClickable(true);
        mSunday.setClickable(true);

        mMonday.setCellClickListener(this);
        mTuesday.setCellClickListener(this);
        mWednesday.setCellClickListener(this);
        mThursday.setCellClickListener(this);
        mFriday.setCellClickListener(this);
        mMonday.setCellClickListener(this);
        mSaturday.setCellClickListener(this);
        mSunday.setCellClickListener(this);


        Intent intent = getIntent();
        if (intent != null) {
            userInfo = (EntityUserInfo) intent.getExtras().getSerializable("userInfo");
        }

        if (userInfo != null) {
            mEditCost.setText(userInfo.getVisitMoney() + "");
            mCheckBox.setChecked(userInfo.getVisitState() == 1 ? true : false);

            if (userInfo.getVisitState() == 1) {
                mStatus.setText("出诊中");
            }else{
                mStatus.setText("停诊中");
            }
        }
    }

    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        Intent intent = null;
        switch (v.getId()) {
            case R.id.checkbox:
                if (mCheckBox.isChecked()) {
                    mStatus.setText("出诊中");
                }else{
                    mStatus.setText("停诊中");
                }
                break;
            case R.id.iv_Monday:
                intent = new Intent(VisitCostSettingActivity.this, VisitCostTimeSettingActivity.class);
                bundle.putSerializable("weekday", 2);
                bundle.putSerializable("userInfo", userInfo);
                intent.putExtras(bundle);
                startActivityForResult(intent, VisitCostSettingActivity.RG_VISITCOTTIME_REQUEST);
                break;
            case R.id.iv_Tuesday:
                intent = new Intent(VisitCostSettingActivity.this, VisitCostTimeSettingActivity.class);
                bundle.putSerializable("weekday", 3);
                bundle.putSerializable("userInfo", userInfo);
                intent.putExtras(bundle);
                startActivityForResult(intent, VisitCostSettingActivity.RG_VISITCOTTIME_REQUEST);
                break;
            case R.id.iv_Wednesday:
                intent = new Intent(VisitCostSettingActivity.this, VisitCostTimeSettingActivity.class);
                bundle.putSerializable("weekday", 4);
                bundle.putSerializable("userInfo", userInfo);
                intent.putExtras(bundle);
                startActivityForResult(intent, VisitCostSettingActivity.RG_VISITCOTTIME_REQUEST);
                break;
            case R.id.iv_Thursday:
                intent = new Intent(VisitCostSettingActivity.this, VisitCostTimeSettingActivity.class);
                bundle.putSerializable("weekday", 5);
                bundle.putSerializable("userInfo", userInfo);
                intent.putExtras(bundle);
                startActivityForResult(intent, VisitCostSettingActivity.RG_VISITCOTTIME_REQUEST);
                break;
            case R.id.iv_Friday:
                intent = new Intent(VisitCostSettingActivity.this, VisitCostTimeSettingActivity.class);
                bundle.putSerializable("weekday", 6);
                bundle.putSerializable("userInfo", userInfo);
                intent.putExtras(bundle);
                startActivityForResult(intent, VisitCostSettingActivity.RG_VISITCOTTIME_REQUEST);
                break;
            case R.id.iv_Saturday:
                intent = new Intent(VisitCostSettingActivity.this, VisitCostTimeSettingActivity.class);
                bundle.putSerializable("weekday", 7);
                bundle.putSerializable("userInfo", userInfo);
                intent.putExtras(bundle);
                startActivityForResult(intent, VisitCostSettingActivity.RG_VISITCOTTIME_REQUEST);
                break;
            case R.id.iv_Sunday:
                intent = new Intent(VisitCostSettingActivity.this, VisitCostTimeSettingActivity.class);
                bundle.putSerializable("weekday", 1);
                bundle.putSerializable("userInfo", userInfo);
                intent.putExtras(bundle);
                startActivityForResult(intent, VisitCostSettingActivity.RG_VISITCOTTIME_REQUEST);
                break;
            case R.id.btn_ok:
                boolean isOn = mCheckBox.isChecked();
                if (isOn) {
                    if (mEditCost.getText() != null && !TextUtils.isEmpty(mEditCost.getText().toString())) {
                        bundle.putString("cost", mEditCost.getText().toString());//给 bundle 写入数据

                    }else {
                        ToastUtil.showLongToast("费用不能为空");
                    }
                }

                intent = new Intent();
                bundle.putBoolean("isOn", mCheckBox.isChecked());
                bundle.putInt("weekday", mWeekDay);
                bundle.putString("time", mTimeStr);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);

                mSoftInputHandler.sendEmptyMessage(1);
                finish();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            if (requestCode == RG_VISITCOTTIME_REQUEST) {
                if (resultCode == RESULT_CANCELED) {

                } else if (resultCode == RESULT_OK) {
                    mTimeStr = data.getStringExtra("time");
                    mWeekDay = data.getIntExtra("weekday", 1);

                    userInfo = BaseApplication.getInstance().getUserInfo();
                    if (userInfo != null) {
                        ArrayList<EntityUserVisits> mVisits = userInfo.getUserVisits();
                        if (mWeekDay > 0 || !TextUtils.isEmpty(mTimeStr)) {

                            for (EntityUserVisits visits:mVisits) {
                                if (mWeekDay == visits.getWeek()) {
                                    visits.setWeek(mWeekDay);
                                    visits.setTime(mTimeStr);
                                }
                            }
                            BaseApplication.getInstance().setUserInfo(userInfo);

                        }
                    }

                    ReqManager.getInstance().reqUpdateUserInfo(reqCheckAppUpdate, userInfo, Utils.getUserToken(VisitCostSettingActivity.this));

                }
            }
        }
    }

    /**
     * 更新用户信息接口回调
     */
    Callback<RespUserLogin> reqCheckAppUpdate = new Callback<RespUserLogin>() {
        @Override
        public void onError(Call call, Exception e) {
            onFailure(e.toString());
            ToastUtil.showLongToast("更新失败，请重试");
        }

        @Override
        public void onResponse(RespUserLogin response) {
            if (onSuccess(response)) {
                if(response != null && response.getResult() !=null){
                    BaseApplication.getInstance().setLoginInfo(response.getResult());
                    initData();
                }
                ToastUtil.showLongToast("更新成功");
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

        userInfo = BaseApplication.getInstance().getUserInfo();
        if (userInfo != null) {
            ArrayList<EntityUserVisits> mVisits = userInfo.getUserVisits();

            StringBuffer week1TimeStr = new StringBuffer();
            StringBuffer week2TimeStr = new StringBuffer();
            StringBuffer week3TimeStr = new StringBuffer();
            StringBuffer week4TimeStr = new StringBuffer();
            StringBuffer week5TimeStr = new StringBuffer();
            StringBuffer week6TimeStr = new StringBuffer();
            StringBuffer week7TimeStr = new StringBuffer();

            if (mWeekDay > 0 || !TextUtils.isEmpty(mTimeStr)) {
                if (mVisits == null || mVisits.size() <= 0) {
                    EntityUserVisits entityUserVisits = new EntityUserVisits();
                    entityUserVisits.setWeek(mWeekDay);
                    entityUserVisits.setTime(mTimeStr);

                    mVisits = new ArrayList<>();
                    mVisits.add(entityUserVisits);

                    userInfo.setUserVisits(mVisits);

                    BaseApplication.getInstance().setUserInfo(userInfo);
                }
            }

            for (int i=0; i < mVisits.size(); i++) {
                if(mVisits.get(i) != null) {

                    String timeStr = mVisits.get(i).getTime();

                    if (mWeekDay > 0 && mWeekDay == mVisits.get(i).getWeek()) {
                        timeStr = mTimeStr;
                    }

                    String[] timeArray = null;

                    if(!TextUtils.isEmpty(timeStr)) {
                        if(timeStr.length() == 1) {
                            timeArray = new String[1];
                            timeArray[0] = timeStr;
                        }else{
                            timeArray = timeStr.split(",");
                        }
                    }

                    switch (mVisits.get(i).getWeek()) {

                        case 1:
                            week7TimeStr = getWeekDayTime(timeArray);
                            break;
                        case 2:
                            week1TimeStr = getWeekDayTime(timeArray);
                            break;
                        case 3:
                            week2TimeStr = getWeekDayTime(timeArray);
                            break;
                        case 4:
                            week3TimeStr = getWeekDayTime(timeArray);
                            break;
                        case 5:
                            week4TimeStr = getWeekDayTime(timeArray);
                            break;
                        case 6:
                            week5TimeStr = getWeekDayTime(timeArray);
                            break;
                        case 7:
                            week6TimeStr = getWeekDayTime(timeArray);
                            break;
                    }
                }
            }
            mMonday.setDesc(week1TimeStr.toString());
            mTuesday.setDesc(week2TimeStr.toString());
            mWednesday.setDesc(week3TimeStr.toString());
            mThursday.setDesc(week4TimeStr.toString());
            mFriday.setDesc(week5TimeStr.toString());
            mSaturday.setDesc(week6TimeStr.toString());
            mSunday.setDesc(week7TimeStr.toString());

        }
    }

    private StringBuffer getWeekDayTime(String[] timeArray) {
        StringBuffer weekTimeStr = new StringBuffer();
        if (timeArray != null && timeArray.length > 0) {
            for(String time : timeArray) {
                int timeInt = Integer.valueOf(time);

                switch (timeInt) {
                    case 1:
                        weekTimeStr.append("上午");
                        break;
                    case 2:
                        if (!TextUtils.isEmpty(weekTimeStr.toString())) {
                            weekTimeStr.append(",下午");
                        }else {
                            weekTimeStr.append("下午");
                        }
                        break;
                    case 3:
                        if (!TextUtils.isEmpty(weekTimeStr.toString())) {
                            weekTimeStr.append(",晚上");
                        }else {
                            weekTimeStr.append("晚上");
                        }
                        break;
                    case 4:
                        weekTimeStr = new StringBuffer();
                        weekTimeStr.append("全天");
                        break;
                }
            }
        }
        return weekTimeStr;
    }
}
