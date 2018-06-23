package com.dearzs.commonlib.utils;

import android.os.CountDownTimer;

/**
 * 自定义倒计时类
 */
public class MCountDownTimer extends CountDownTimer {

    private long millisInFuture;

    public interface CountDownCallBack {
        public void onTickCallBack(long millisUntilFinished);
        public void onFinishCallBack();
    }

    private CountDownCallBack mCountDownCallBack;

    /**
     * 构造方法
     * @param millisInFuture  表示总时间
     * @param countDownInterval 表示间隔时间，回调一次方法onTick
     * @param countDownCallBack
     */
    public MCountDownTimer(long millisInFuture, long countDownInterval, CountDownCallBack countDownCallBack) {
        super(millisInFuture, countDownInterval);
        this.millisInFuture = millisInFuture;
        setCountDownCallBack(countDownCallBack);
    }

    public MCountDownTimer(long millisInFuture, long countDownInterval) {
        this(millisInFuture, countDownInterval, null);
    }

    @Override
    public void onTick(long millisUntilFinished) {
        if (mCountDownCallBack != null) {
            mCountDownCallBack.onTickCallBack(millisUntilFinished);
        }
    }

    @Override
    public void onFinish() {
        if (mCountDownCallBack != null) {
            mCountDownCallBack.onFinishCallBack();
        }
    }

    public void setCountDownCallBack(CountDownCallBack mCountDownCallBack) {
        this.mCountDownCallBack = mCountDownCallBack;
    }

    /**
     * 启动计时器
     */
    public void startTimer() {
        this.start();
    }

    /**
     * 停止计时器
     */
    public void stopTimer() {
        this.cancel();
    }

    public long getMillisInFuture() {
        return this.millisInFuture;
    }
}
