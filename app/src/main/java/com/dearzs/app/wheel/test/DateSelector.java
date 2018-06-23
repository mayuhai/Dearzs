package com.dearzs.app.wheel.test;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.dearzs.app.R;
import com.dearzs.app.chat.utils.TimeUtil;
import com.dearzs.app.wheel.test.adapter.AbstractWheelTextAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class DateSelector {

    private View mTimeView;
    private WheelView mStartYearView, mStartMonthView, mStartDayView;
    private Button mBtDone;
    private String[] mMonths, mDays;
    private TextView mSelectedData;
    private String mStartYear = "", mStartMonth = "", mStartDay = "";
    private Dialog startDialog;
    private List<String> startYearList, startMonthList, startDayList;
    private YearAdapter startYearAdapter;
    private MonthAdapter startMonthAdapter;
    private DayAdapter startDayAdapter;
    private int mMinYear = 1900;
    private int mMaxYear = 2018;
    private SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
    private SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
    private SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
    private int startYearIndex, startMonthIndex, startDayIndex;
    private Resources res;
    private Context mContext;
    private Date mDate;

    /**
     * @param isFrount 是要选择之前的时间还是之后的时间，之前传true 之后传false
     */
    public void init(Context context, final Handler hander, final int handerTag, final boolean isFrount) {
        mContext = context;
        mTimeView = LayoutInflater.from(mContext).inflate(R.layout.wheel_view, null);
        mStartYearView = (WheelView) mTimeView.findViewById(R.id.year);
        mStartMonthView = (WheelView) mTimeView.findViewById(R.id.month);
        mStartDayView = (WheelView) mTimeView.findViewById(R.id.day);
        mBtDone = (Button) mTimeView.findViewById(R.id.done);
        mSelectedData = (TextView) mTimeView.findViewById(R.id.whell_view_textview);

        res = mContext.getResources();
        mMonths = res.getStringArray(R.array.months);
        mDays = res.getStringArray(R.array.days_31);
        mDate = new Date();
        String year = yearFormat.format(mDate);
        String month = monthFormat.format(mDate);

        mSelectedData.setText(TimeUtil.getYMDWeekStr(mDate));

        if (!isFrount) {
            //选取现在之前的时间的处理
            mMinYear = Integer.parseInt(year);
            if (Integer.parseInt(month) > 10) {
                // 11月份和12月份的时候 可以预约下一年的
                mMaxYear = mMinYear + 1;
            } else {
                mMaxYear = mMinYear;
            }
        } else {
            //选取现在之后的时间的处理
            mMinYear = 1900;
            mMaxYear = Integer.parseInt(year);
        }
        startYearList = new ArrayList<String>();
        for (int i = mMinYear; i <= mMaxYear; i++) {
            startYearList.add(i + "");
        }
        startMonthList = Arrays.asList(mMonths);
        startDayList = Arrays.asList(mDays);

        startYearAdapter = new YearAdapter(mContext, startYearList);
        startMonthAdapter = new MonthAdapter(mContext, startMonthList);
        startDayAdapter = new DayAdapter(mContext, startDayList);
        mStartYearView.setViewAdapter(startYearAdapter);
        mStartMonthView.setViewAdapter(startMonthAdapter);
        mStartDayView.setViewAdapter(startDayAdapter);

        startDialog = new Dialog(mContext);
        startDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        startDialog.setContentView(mTimeView);
        Window startWindow = startDialog.getWindow();
        startWindow.setGravity(Gravity.BOTTOM);
        startWindow.setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        startWindow.setWindowAnimations(R.style.view_animation);

        mStartYearView.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {
            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                startYearIndex = wheel.getCurrentItem();
                String year = (String) startYearAdapter.getItemText(startYearIndex);
                String month = (String) startMonthAdapter.getItemText(startMonthIndex);
                String day = "";

                if (Integer.parseInt(month) == 2) {
                    if (isLeapYear(year)) {
                        //29 闰年2月29天
                        if (startDayAdapter.list.size() != 29) {
                            startDayList = Arrays.asList(res.getStringArray(R.array.days_29));
                            startDayAdapter = new DayAdapter(mContext, startDayList);
                            mStartDayView.setViewAdapter(startDayAdapter);
                            if (startDayIndex > 28) {
                                mStartDayView.setCurrentItem(0);
                                startDayIndex = 0;
                            } else {
                                mStartDayView.setCurrentItem(startDayIndex);
                            }
                        }
                    } else {
                        //28 非闰年2月28天
                        if (startDayAdapter.list.size() != 28) {
                            startDayList = Arrays.asList(res.getStringArray(R.array.days_28));
                            startDayAdapter = new DayAdapter(mContext, startDayList);
                            mStartDayView.setViewAdapter(startDayAdapter);
                            if (startDayIndex > 27) {
                                mStartDayView.setCurrentItem(0);
                                startDayIndex = 0;
                            } else {
                                mStartDayView.setCurrentItem(startDayIndex);
                            }
                        }
                    }

                    year = (String) startYearAdapter.getItemText(startYearIndex);
                    month = (String) startMonthAdapter.getItemText(startMonthIndex);
                    day = (String) startDayAdapter.getItemText(startDayIndex);

                    mSelectedData.setText(TimeUtil.getYMDStr(year, month, day) + "  " + TimeUtil.getWeekStr(year, month, day));
                }
            }
        });
        mStartMonthView.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {
            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                startMonthIndex = wheel.getCurrentItem();
                String year = (String) startYearAdapter.getItemText(startYearIndex);
                String month = (String) startMonthAdapter.getItemText(startMonthIndex);
                String day = "";

                int i = Integer.parseInt(month);
                if (i == 1 || i == 3 || i == 5 || i == 7 || i == 8 || i == 10 || i == 12) {
                    //31
                    if (startDayAdapter.list.size() != 31) {
                        startDayList = Arrays.asList(res.getStringArray(R.array.days_31));
                        startDayAdapter = new DayAdapter(mContext, startDayList);
                        mStartDayView.setViewAdapter(startDayAdapter);
                        mStartDayView.setCurrentItem(startDayIndex);
                    }
                } else if (i == 2) {
                    if (isLeapYear(year)) {
                        //29
                        if (startDayAdapter.list.size() != 29) {
                            startDayList = Arrays.asList(res.getStringArray(R.array.days_29));
                            startDayAdapter = new DayAdapter(mContext, startDayList);
                            mStartDayView.setViewAdapter(startDayAdapter);
                            if (startDayIndex > 28) {
                                mStartDayView.setCurrentItem(0);
                                startDayIndex = 0;
                            } else {
                                mStartDayView.setCurrentItem(startDayIndex);
                            }
                        }
                    } else {
                        //28
                        if (startDayAdapter.list.size() != 28) {
                            startDayList = Arrays.asList(res.getStringArray(R.array.days_28));
                            startDayAdapter = new DayAdapter(mContext, startDayList);
                            mStartDayView.setViewAdapter(startDayAdapter);
                            if (startDayIndex > 27) {
                                mStartDayView.setCurrentItem(0);
                                startDayIndex = 0;
                            } else {
                                mStartDayView.setCurrentItem(startDayIndex);
                            }
                        }
                    }
                } else {
                    //30
                    if (startDayAdapter.list.size() != 30) {
                        startDayList = Arrays.asList(res.getStringArray(R.array.days_30));
                        startDayAdapter = new DayAdapter(mContext, startDayList);
                        mStartDayView.setViewAdapter(startDayAdapter);
                        if (startDayIndex > 29) {
                            mStartDayView.setCurrentItem(0);
                            startDayIndex = 0;
                        } else {
                            mStartDayView.setCurrentItem(startDayIndex);
                        }
                    }
                }
                year = (String) startYearAdapter.getItemText(startYearIndex);
                month = (String) startMonthAdapter.getItemText(startMonthIndex);
                day = (String) startDayAdapter.getItemText(startDayIndex);

                mSelectedData.setText(TimeUtil.getYMDStr(year, month, day) + "  " + TimeUtil.getWeekStr(year, month, day));
            }
        });
        mStartDayView.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {
            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                startDayIndex = wheel.getCurrentItem();
                String year = (String) startYearAdapter.getItemText(startYearIndex);
                String month = (String) startMonthAdapter.getItemText(startMonthIndex);
                String day = (String) startDayAdapter.getItemText(startDayIndex);

                mSelectedData.setText(TimeUtil.getYMDStr(year, month, day) + "  " + TimeUtil.getWeekStr(year, month, day));
            }
        });

        mBtDone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                startDialog.dismiss();
                mStartYear = (String) startYearAdapter.getItemText(startYearIndex);
                mStartMonth = (String) startMonthAdapter.getItemText(startMonthIndex);
                mStartDay = (String) startDayAdapter.getItemText(startDayIndex);
//                Toast.makeText(mContext, mStartYear + "/" + mStartMonth + "/" + mStartDay, Toast.LENGTH_LONG).show();

                Message msg = new Message();
                msg.what = handerTag;
                msg.obj = mStartYear + "-" + mStartMonth + "-" + mStartDay;
                hander.sendMessage(msg);
            }
        });
    }

    public void showDaySelectorDialog() {
        if ("".equals(mStartYear)) {
            mStartYear = mMaxYear + "";
            mStartMonth = monthFormat.format(mDate);
            mStartDay = dayFormat.format(mDate);
        }
        startYearIndex = startYearList.indexOf(mStartYear);
        startMonthIndex = startMonthList.indexOf(mStartMonth);
        startDayIndex = startDayList.indexOf(mStartDay);
        if (startYearIndex == -1) {
            startYearIndex = 0;
        }
        if (startMonthIndex == -1) {
            startMonthIndex = 0;
        }
        if (startDayIndex == -1) {
            startDayIndex = 0;
        }
        mStartYearView.setCurrentItem(startYearIndex);
        mStartMonthView.setCurrentItem(startMonthIndex);
        mStartDayView.setCurrentItem(startDayIndex);
        startDialog.show();
    }

    public void showDaySelectorDialog(String startYear, String startMonth, String startDay) {
        if ("".equals(startYear)) {
            startYear = mMaxYear + "";
            startMonth = monthFormat.format(mDate);
            startDay = dayFormat.format(mDate);
        }
        startYearIndex = startYearList.indexOf(startYear);
        startMonthIndex = startMonthList.indexOf(startMonth);
        startDayIndex = startDayList.indexOf(startDay);
        if (startYearIndex == -1) {
            startYearIndex = 0;
        }
        if (startMonthIndex == -1) {
            startMonthIndex = 0;
        }
        if (startDayIndex == -1) {
            startDayIndex = 0;
        }
        mStartYearView.setCurrentItem(startYearIndex);
        mStartMonthView.setCurrentItem(startMonthIndex);
        mStartDayView.setCurrentItem(startDayIndex);
        startDialog.show();
    }

    /**
     * 判断是否是闰年
     */
    public static boolean isLeapYear(String str) {
        int year = Integer.parseInt(str);
        return (year % 4 == 0 && year % 100 != 0) || year % 400 == 0;
    }


    private class YearAdapter extends AbstractWheelTextAdapter {
        /**
         * Constructor
         */
        public List<String> list;

        protected YearAdapter(Context context, List<String> list) {
            super(context, R.layout.wheel_view_layout, NO_RESOURCE);
            this.list = list;
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            View view = super.getItem(index, cachedView, parent);

            TextView textCity = (TextView) view.findViewById(R.id.textView);
            textCity.setText(list.get(index));
            return view;
        }

        public int getItemsCount() {
            return list.size();
        }

        @Override
        protected CharSequence getItemText(int index) {
            return list.get(index);
        }
    }

    private class MonthAdapter extends AbstractWheelTextAdapter {
        /**
         * Constructor
         */
        public List<String> list;

        protected MonthAdapter(Context context, List<String> list) {
            super(context, R.layout.wheel_view_layout, NO_RESOURCE);
            this.list = list;
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            View view = super.getItem(index, cachedView, parent);

            TextView textCity = (TextView) view.findViewById(R.id.textView);
            textCity.setText(list.get(index));
            return view;
        }

        public int getItemsCount() {
            return list.size();
        }

        @Override
        protected CharSequence getItemText(int index) {
            return list.get(index);
        }
    }

    private class DayAdapter extends AbstractWheelTextAdapter {
        /**
         * Constructor
         */
        public List<String> list;

        protected DayAdapter(Context context, List<String> list) {
            super(context, R.layout.wheel_view_layout, NO_RESOURCE);
            this.list = list;
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            View view = super.getItem(index, cachedView, parent);

            TextView textCity = (TextView) view.findViewById(R.id.textView);
            textCity.setText(list.get(index));
            return view;
        }

        public int getItemsCount() {
            return list.size();
        }

        @Override
        protected CharSequence getItemText(int index) {
            return list.get(index);
        }
    }
}



















