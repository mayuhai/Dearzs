package com.dearzs.app.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.dearzs.app.R;
import com.dearzs.app.base.BaseActivity;
import com.dearzs.app.entity.EntityUserInfo;
import com.dearzs.app.entity.EntityUserVisits;
import com.dearzs.app.util.ToastUtil;
import com.dearzs.app.widget.TitleBarView;

import java.util.List;

/**
 * 出诊设置页面
 * Created by mayuhai on 2016/7/31.
 */
public class VisitCostTimeSettingActivity extends BaseActivity {

    private EntityUserInfo mUserInfo;
    private int mWeekDay;
    private CheckBox mAm;
    private CheckBox mPm;
    private CheckBox mNight;
    private CheckBox mAllDay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_visit_cost_time_setting);
        addLeftBtn(TitleBarView.TITLE_TYPE_LEFT_IV_BACK, null);
        addRightBtn(TitleBarView.TITLE_TYPE_RIGHT_TXT, "确定");
    }

    @Override
    public void initView() {
        super.initView();

        mAm = getView(R.id.cb_am);
        mPm = getView(R.id.cb_pm);
        mNight = getView(R.id.cb_night);
        mAllDay = getView(R.id.cb_all_day);

        mAm.setOnClickListener(this);
        mPm.setOnClickListener(this);
        mNight.setOnClickListener(this);
        mAllDay.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cb_all_day:
                mAllDay.setChecked(mAllDay.isChecked());
                mAm.setChecked(mAllDay.isChecked());
                mPm.setChecked(mAllDay.isChecked());
                mNight.setChecked(mAllDay.isChecked());
                break;
            case R.id.cb_am:
                mAm.setChecked(mAm.isChecked());
                mAllDay.setChecked(mAm.isChecked() && mPm.isChecked() && mNight.isChecked());
                break;
            case R.id.cb_pm:
                mPm.setChecked(mPm.isChecked());
                mAllDay.setChecked(mAm.isChecked() && mPm.isChecked() && mNight.isChecked());
                break;
            case R.id.cb_night:
                mNight.setChecked(mNight.isChecked());
                mAllDay.setChecked(mAm.isChecked() && mPm.isChecked() && mNight.isChecked());
                break;
        }
    }

    @Override
    public void onRightBtnClick() {
        super.onRightBtnClick();

//        1上午2下午3夜间4全天,多个以英文逗号隔开

        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putInt("weekday", mWeekDay);

        StringBuffer stringBuffer = new StringBuffer();
        if (mAm.isChecked()){
            stringBuffer.append("1");
        }

        if (mPm.isChecked()){
            if (!TextUtils.isEmpty(stringBuffer.toString())) {
                stringBuffer.append(",2");
            }else {
                stringBuffer.append("2");
            }
        }

        if (mNight.isChecked()){
            if (!TextUtils.isEmpty(stringBuffer.toString())) {
                stringBuffer.append(",3");
            }else {
                stringBuffer.append("3");
            }
        }

        if (mAllDay.isChecked()){
            stringBuffer = new StringBuffer();
            stringBuffer.append("4");
        }

        bundle.putString("time", stringBuffer.toString());
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void initData() {
        super.initData();

        Intent intent = getIntent();
        if (intent != null) {
            mUserInfo = (EntityUserInfo) intent.getExtras().getSerializable("userInfo");
            mWeekDay = (int) intent.getExtras().getSerializable("weekday");

            List<EntityUserVisits> visitsmUserInfo = mUserInfo.getUserVisits();

            EntityUserVisits entityUserVisit = null;
            if (visitsmUserInfo != null && visitsmUserInfo.size() > 0) {
                for (EntityUserVisits ev : visitsmUserInfo) {
                    if (ev.getWeek() == mWeekDay) {
                        entityUserVisit = ev;
                    }
                }
            }

            String timeStr = null;
            String[] timeArray = null;
            if (entityUserVisit != null) {
                timeStr = entityUserVisit.getTime();
            }

            if(!TextUtils.isEmpty(timeStr)) {
                if(timeStr.length() == 1) {
                    timeArray = new String[1];
                    timeArray[0] = timeStr;
                }else{
                    timeArray = timeStr.split(",");
                }
            }

            String title = "周一";
            switch (mWeekDay) {
                case 1:
                    title = "周日";
                    getWeekDayTimeCheckboxState(timeArray);
                    break;
                case 2:
                    title = "周一";
                    getWeekDayTimeCheckboxState(timeArray);
                    break;
                case 3:
                    title = "周二";
                    getWeekDayTimeCheckboxState(timeArray);
                    break;
                case 4:
                    title = "周三";
                    getWeekDayTimeCheckboxState(timeArray);
                    break;
                case 5:
                    title = "周四";
                    getWeekDayTimeCheckboxState(timeArray);
                    break;
                case 6:
                    title = "周五";
                    getWeekDayTimeCheckboxState(timeArray);
                    break;
                case 7:
                    title = "周六";
                    getWeekDayTimeCheckboxState(timeArray);
                    break;
            }

            addCenter(TitleBarView.TITLE_TYPE_CENTER_TXT, title);
        }
    }

    private void getWeekDayTimeCheckboxState(String[] timeArray) {
        StringBuffer weekTimeStr = new StringBuffer();
        if (timeArray != null && timeArray.length > 0) {
            for(String time : timeArray) {
                int timeInt = Integer.valueOf(time);

                switch (timeInt) {
                    case 1:
                        mAm.setChecked(true);
                        if (mPm.isChecked() && mAm.isChecked()) {
                            mAllDay.setChecked(true);
                        }else {
                            mAllDay.setChecked(false);
                        }
                        break;
                    case 2:
                        mPm.setChecked(true);
                        if (mAm.isChecked() && mNight.isChecked()) {
                            mAllDay.setChecked(true);
                        }else {
                            mAllDay.setChecked(false);
                        }
                        break;
                    case 3:
                        mNight.setChecked(true);
                        if (mPm.isChecked() && mAm.isChecked()) {
                            mAllDay.setChecked(true);
                        }else {
                            mAllDay.setChecked(false);
                        }
                        break;
                    case 4:
                        mAm.setChecked(true);
                        mPm.setChecked(true);
                        mNight.setChecked(true);
                        mAllDay.setChecked(true);
                        break;
                }
            }
        }
    }
}
