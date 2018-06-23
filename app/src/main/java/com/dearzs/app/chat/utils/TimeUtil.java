package com.dearzs.app.chat.utils;

import android.text.TextUtils;

import com.dearzs.app.BaseApplication;
import com.dearzs.app.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * 时间转换工具
 */
public class TimeUtil {


    private TimeUtil() {
    }

    /**
     * 时间转化为显示字符串
     *
     * @param timeStamp 单位为秒
     */
    public static String getYmd(long timeStamp) {
        if (timeStamp == 0) return "";
        Calendar inputTime = Calendar.getInstance();
        inputTime.setTimeInMillis(timeStamp * 1000);
        Date currenTimeZone = inputTime.getTime();
        //当前时间在输入时间之前
        SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
        return sdf.format(currenTimeZone);
    }

    /**
     * 时间转化为显示字符串
     *
     * @param timeStamp 单位为秒
     */
    public static String getTimeStr(long timeStamp) {
        if (timeStamp == 0) return "";
        Calendar inputTime = Calendar.getInstance();
        inputTime.setTimeInMillis(timeStamp * 1000);
        Date currenTimeZone = inputTime.getTime();
        Calendar calendar = Calendar.getInstance();
        if (calendar.before(inputTime)) {
            //当前时间在输入时间之前
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy" + BaseApplication.getInstance().getResources().getString(R.string.time_year) + "MM" + BaseApplication.getInstance().getResources().getString(R.string.time_month) + "dd" + BaseApplication.getInstance().getResources().getString(R.string.time_day));
            return sdf.format(currenTimeZone);
        }
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if (calendar.before(inputTime)) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            return sdf.format(currenTimeZone);
        }
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        if (calendar.before(inputTime)) {
            return BaseApplication.getInstance().getResources().getString(R.string.time_yesterday);
        } else {
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.MONTH, Calendar.JANUARY);
            if (calendar.before(inputTime)) {
                SimpleDateFormat sdf = new SimpleDateFormat("M" + BaseApplication.getInstance().getResources().getString(R.string.time_month) + "d" + BaseApplication.getInstance().getResources().getString(R.string.time_day));
                return sdf.format(currenTimeZone);
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy" + BaseApplication.getInstance().getResources().getString(R.string.time_year) + "MM" + BaseApplication.getInstance().getResources().getString(R.string.time_month) + "dd" + BaseApplication.getInstance().getResources().getString(R.string.time_day));
                return sdf.format(currenTimeZone);

            }

        }

    }

    /**
     * 时间转化为聊天界面显示字符串
     *
     * @param timeStamp 单位为秒
     */
    public static String getChatTimeStr(long timeStamp) {
        if (timeStamp == 0) return "";
        Calendar inputTime = Calendar.getInstance();
        inputTime.setTimeInMillis(timeStamp * 1000);
        Date currenTimeZone = inputTime.getTime();
        Calendar calendar = Calendar.getInstance();
        if (calendar.before(inputTime)) {
            //当前时间在输入时间之前
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy" + BaseApplication.getInstance().getResources().getString(R.string.time_year) + "MM" + BaseApplication.getInstance().getResources().getString(R.string.time_month) + "dd" + BaseApplication.getInstance().getResources().getString(R.string.time_day));
            return sdf.format(currenTimeZone);
        }
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if (calendar.before(inputTime)) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            return sdf.format(currenTimeZone);
        }
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        if (calendar.before(inputTime)) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            return BaseApplication.getInstance().getResources().getString(R.string.time_yesterday) + " " + sdf.format(currenTimeZone);
        } else {
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.MONTH, Calendar.JANUARY);
            if (calendar.before(inputTime)) {
                SimpleDateFormat sdf = new SimpleDateFormat("M" + BaseApplication.getInstance().getResources().getString(R.string.time_month) + "d" + BaseApplication.getInstance().getResources().getString(R.string.time_day) + " HH:mm");
                return sdf.format(currenTimeZone);
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy" + BaseApplication.getInstance().getResources().getString(R.string.time_year) + "MM" + BaseApplication.getInstance().getResources().getString(R.string.time_month) + "dd" + BaseApplication.getInstance().getResources().getString(R.string.time_day) + " HH:mm");
                return sdf.format(currenTimeZone);
            }

        }

    }

    /*
	 * 将时间戳转为字符串 ，格式：yyyy-MM-dd HH:mm
	 */
    public static String getStrTime_ymd_hm(String cc_time) {
        String re_StrTime = "";
        if (TextUtils.isEmpty(cc_time) || "null".equals(cc_time)) {
            return re_StrTime;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        // 例如：cc_time=1291778220
        long lcc_time = Long.valueOf(cc_time);
        re_StrTime = sdf.format(new Date(lcc_time * 1000L));
        return re_StrTime;

    }

    /*
     * 将时间戳转为字符串 ，格式：yyyy-MM-dd HH:mm:ss
     */
    public static String getStrTime_ymd_hms(String cc_time) {
        String re_StrTime = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 例如：cc_time=1291778220
        long lcc_time = Long.valueOf(cc_time);
        re_StrTime = sdf.format(new Date(lcc_time * 1000L));
        return re_StrTime;

    }

    /*
     * 将时间戳转为字符串 ，格式：yyyy.MM.dd
     */
    public static String getStrTime_ymd(String cc_time) {
        String re_StrTime = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        // 例如：cc_time=1291778220
        long lcc_time = Long.valueOf(cc_time);
        re_StrTime = sdf.format(new Date(lcc_time * 1000L));
        return re_StrTime;
    }

    /*
     * 将时间戳转为字符串 ，格式：yyyy
     */
    public static String getStrTime_y(String cc_time) {
        String re_StrTime = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        // 例如：cc_time=1291778220
        long lcc_time = Long.valueOf(cc_time);
        re_StrTime = sdf.format(new Date(lcc_time * 1000L));
        return re_StrTime;
    }

    /*
     * 将时间戳转为字符串 ，格式：MM-dd
     */
    public static String getStrTime_md(String cc_time) {
        String re_StrTime = null;
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
        // 例如：cc_time=1291778220
        long lcc_time = Long.valueOf(cc_time);
        re_StrTime = sdf.format(new Date(lcc_time * 1000L));
        return re_StrTime;
    }

    /*
     * 将时间戳转为字符串 ，格式：HH:mm
     */
    public static String getStrTime_hm(String cc_time) {
        String re_StrTime = null;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        // 例如：cc_time=1291778220
        long lcc_time = Long.valueOf(cc_time);
        re_StrTime = sdf.format(new Date(lcc_time * 1000L));
        return re_StrTime;
    }

    /*
     * 将时间戳转为字符串 ，格式：HH:mm:ss
     */
    public static String getStrTime_hms(String cc_time) {
        String re_StrTime = null;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        // 例如：cc_time=1291778220
        long lcc_time = Long.valueOf(cc_time);
        re_StrTime = sdf.format(new Date(lcc_time * 1000L));
        return re_StrTime;
    }

    /*
     * 将时间戳转为字符串 ，格式：MM-dd HH:mm:ss
     */
    public static String getNewsDetailsDate(String cc_time) {
        String re_StrTime = null;
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss");
        // 例如：cc_time=1291778220
        long lcc_time = Long.valueOf(cc_time);
        re_StrTime = sdf.format(new Date(lcc_time * 1000L));
        return re_StrTime;
    }

    /*
     * 将字符串转为时间戳
     */
    public static String getTime() {
        String re_time = null;
        long currentTime = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        Date d;
        d = new Date(currentTime);
        long l = d.getTime();
        String str = String.valueOf(l);
        re_time = str.substring(0, 10);
        return re_time;
    }

    /*
     * 将字符串转为日期
     */
    public static String getTimeToday(long times) {
        String re_time = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        Date d;
        d = new Date(times);
        String str = sdf.format(d);
        re_time = str.substring(5, 10);
        return re_time;
    }

    /*
     * 将时间戳转为字符串 ，格式：yyyy.MM.dd 星期几
     */
    public static String getSection(String cc_time) {
        String re_StrTime = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd  EEEE");
        // 对于创建SimpleDateFormat传入的参数：EEEE代表星期，如“星期四”；MMMM代表中文月份，如“十一月”；MM代表月份，如“11”；
        // yyyy代表年份，如“2010”；dd代表天，如“25”
        // 例如：cc_time=1291778220
        long lcc_time = Long.valueOf(cc_time);
        re_StrTime = sdf.format(new Date(lcc_time * 1000L));
        return re_StrTime;
    }

    /** 将时间String转换成long 例如2015-12-12 12:15 */
    public static long getLongTim(String strTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            return sdf.parse(strTime).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /** 将时间String转换成week(周几)*/
    public static String getWeekStr(String year, String month, String day) {
        SimpleDateFormat sdf = new SimpleDateFormat("E");
        Date date = new Date(Integer.parseInt(year) - 1900, Integer.parseInt(month) - 1, Integer.parseInt(day));
        return sdf.format(date).replace("星期", "周");
    }

    /** 将时间String转换成日期*/
    public static String getYMDStr(String year, String month, String day) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(Integer.parseInt(year) - 1900, Integer.parseInt(month) - 1, Integer.parseInt(day));
        return sdf.format(date);
    }

    /** 将时间String转换成日期+周几*/
    public static String getYMDWeekStr(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("E");
        return sdf.format(date) + "  " + sdf2.format(date).replace("星期", "周");
    }

    /**
     * 计算两个##:##格式的时间 段(视频总播放时间--格式##:##String)
     *
     * @param start
     *            开始的时间
     * @param end
     *            结束的时间
     * @return
     */
    public static String getTimePeriod(String start, String end) {
        String timeStr = null;
        int startInt = Integer.parseInt(start.split(":")[0]) * 60
                + Integer.parseInt(start.split(":")[1]);
        int endInt = Integer.parseInt(end.split(":")[0]) * 60
                + Integer.parseInt(end.split(":")[1]);
        int timeInt = endInt - startInt;
        if (timeInt >= 60) {
            if (timeInt % 60 >= 10) {
                timeStr = timeInt / 60 + ":" + timeInt % 60 + ":00";
            } else {
                timeStr = timeInt / 60 + ":0" + timeInt % 60 + ":00";
            }
        } else {
            timeStr = timeInt + ":00";
        }
        return timeStr;
    }

    /**
     * 计算两个##:##格式的时间 段(总时间int)---视频进度条使用
     *
     * @param start
     *            开始的时间
     * @param end
     *            结束的时间
     * @return
     */
    public static int getTimePeriodInt(String start, String end) {
        int timeInt = 0;
        int startInt = Integer.parseInt(start.split(":")[0]) * 60
                + Integer.parseInt(start.split(":")[1]);
        int endInt = Integer.parseInt(end.split(":")[0]) * 60
                + Integer.parseInt(end.split(":")[1]);
        timeInt = (endInt - startInt) * 60;

        return timeInt;
    }

    /**
     * 将int型的时间转成##:##格式的时间
     */
//    public static String getTimeStr(long timeInt) {
//        int mi = 1 * 60;
//        int hh = mi * 60;
//
//        long hour = (timeInt) / hh;
//        long minute = (timeInt - hour * hh) / mi;
//        long second = timeInt - hour * hh - minute * mi;
//
//        String strHour = hour < 10 ? "0" + hour : "" + hour;
//        String strMinute = minute < 10 ? "0" + minute : "" + minute;
//        String strSecond = second < 10 ? "0" + second : "" + second;
//        if (hour > 0) {
//            return strHour + ":" + strMinute + ":" + strSecond;
//        } else {
//            return strMinute + ":" + strSecond;
//        }
//    }
}
