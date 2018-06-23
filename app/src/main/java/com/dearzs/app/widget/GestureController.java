package com.dearzs.app.widget;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.dearzs.commonlib.utils.DisplayUtil;
import com.dearzs.commonlib.utils.log.LogUtil;

/**
 * 播放器音量，亮度，进度手势控制器
 */
public class GestureController implements GestureDetector.OnGestureListener, View.OnTouchListener {
    private static final float STEP_PROGRESS = 2f;// 设定进度滑动时的步长，避免每次滑动都改变，导致改变过快
    private static final float STEP_VOLUME = 2f;// 协调音量滑动时的步长，避免每次滑动都改变，导致改变过快
    private static final int GESTURE_MODIFY_PROGRESS = 1;
    private static final int GESTURE_MODIFY_VOLUME = 2;
    private static final int GESTURE_MODIFY_BRIGHT = 3;

    private Context context;
    private GestureDetector mGestureDetector;
    private AudioManager mAudioManager;

    /**
     * 视频窗口的宽和高
     */
    private float mPlayerWidth, mPlayerHeight;
    /**
     * 视频播放时间,视频播放的总时长(单位是毫秒)
     */
    private int mVideoPlayingTime, mVideoTotalTime;
    /**
     * 视频播放最大音量,视频播放的当前音量
     */
    private int maxVolume, mCurrentVolume;

    private float mBrightness = -1f; // 亮度
    private boolean mFirstScroll = false;// 每次触摸屏幕后，第一次scroll的标志
    private int mGestureFlag = 0;// 1,调节进度，2，调节音量,3.调节亮度

    private boolean mEnable;
    private OnGestureControllerListener listener;

    public GestureController(Context context) {
        this.context = context;
        initData();
    }

    public void setData(int playingTime, int videoTotalTime) {
        this.mPlayerWidth = DisplayUtil.getScreenWidth(context);
        this.mPlayerHeight = DisplayUtil.getScreenHeight(context);
        LogUtil.LogD("Gesture", "===" + mPlayerWidth + "," + mPlayerHeight);
        this.mVideoPlayingTime = playingTime;
        this.mVideoTotalTime = videoTotalTime;
    }

    private void initData() {
        mGestureDetector = new GestureDetector(context, this);
        mGestureDetector.setIsLongpressEnabled(true);
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC); // 获取系统最大音量
        mCurrentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC); // 获取当前值
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // 手势里除了singleTapUp，没有其他检测up的方法
        if (!mEnable) {
            return false;
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            mGestureFlag = 0;// 手指离开屏幕后，重置调节音量或进度的标志
            if (listener != null) {
                listener.onControllerFinish();
            }
        }
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        mFirstScroll = true;// 设定是触摸屏幕后第一次scroll的标志
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if(e1 == null || e2 == null){ return false;}
        float mOldX = e1.getX(), mOldY = e1.getY();
        //LogUtil.LogD("Gesture", "===x:" + mOldX);
        int y = (int) e2.getRawY();
        if (mFirstScroll) {// 以触摸屏幕后第一次滑动为标准，避免在屏幕上操作切换混乱
            // 横向的距离变化大则调整进度，纵向的变化大则调整音量
            if (Math.abs(distanceX) >= Math.abs(distanceY)) {
                if (listener != null) {
                    listener.onStartProgress(true);
                }
                mGestureFlag = GESTURE_MODIFY_PROGRESS;
            } else {
                if (mOldX > mPlayerWidth * 4.0 / 5) {// 音量
                    if (listener != null) {
                        listener.onStartProgress(false);
                    }
                    mGestureFlag = GESTURE_MODIFY_VOLUME;
                } else if (mOldX < mPlayerWidth * 1.0 / 5) {// 亮度
                    if (listener != null) {
                        listener.onStartProgress(false);
                    }
                    mGestureFlag = GESTURE_MODIFY_BRIGHT;
                }
            }
        }
        // 如果每次触摸屏幕后第一次scroll是调节进度，那之后的scroll事件都处理音量进度，直到离开屏幕执行下一次操作
        if (mGestureFlag == GESTURE_MODIFY_PROGRESS) {
            // distanceX=lastScrollPositionX-currentScrollPositionX，因此为正时是快进
            if (Math.abs(distanceX) > Math.abs(distanceY)) {// 横向移动大于纵向移动
                if (distanceX >= DisplayUtil.dip2px(context, STEP_PROGRESS)) {// 快退，用步长控制改变速度，可微调
                    if (listener != null) {
                        listener.onProgressBackward();
                    }
                    if (mVideoPlayingTime > 3 * 1000) {// 避免为负
                        mVideoPlayingTime -= 3 * 1000;// scroll方法执行一次快退3秒
                    } else {
                        mVideoPlayingTime = 0;
                    }
                } else if (distanceX <= -DisplayUtil.dip2px(context, STEP_PROGRESS)) {// 快进
                    if (listener != null) {
                        listener.onProgressForward();
                    }
                    if (mVideoPlayingTime < mVideoTotalTime - 16 * 1000) {// 避免超过总时长
                        mVideoPlayingTime += 3 * 1000;// scroll执行一次快进3秒
                    } else {
                        mVideoPlayingTime = mVideoTotalTime - 10 * 1000;
                    }
                }
                if (mVideoPlayingTime < 0) {
                    mVideoPlayingTime = 0;
                }
                if (listener != null) {
                    listener.onProgressTo(mVideoPlayingTime);
                    listener.onProgressTime(mVideoPlayingTime, mVideoTotalTime);
                }
            }
        }
        // 如果每次触摸屏幕后第一次scroll是调节音量，那之后的scroll事件都处理音量调节，直到离开屏幕执行下一次操作
        else if (mGestureFlag == GESTURE_MODIFY_VOLUME) {
            mCurrentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC); // 获取当前值
            if (Math.abs(distanceY) > Math.abs(distanceX)) {// 纵向移动大于横向移动
                if (distanceY >= DisplayUtil.dip2px(context, STEP_VOLUME)) {// 音量调大,注意横屏时的坐标体系,尽管左上角是原点，但横向向上滑动时distanceY为正
                    if (mCurrentVolume < maxVolume) {// 为避免调节过快，distanceY应大于一个设定值
                        mCurrentVolume++;
                    }
                } else if (distanceY <= -DisplayUtil.dip2px(context, STEP_VOLUME)) {// 音量调小
                    if (mCurrentVolume > 0) {
                        mCurrentVolume--;
                        if (mCurrentVolume == 0) {// 静音，设定静音独有的图片
                        }
                    }
                }
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mCurrentVolume, 0);
            }
        }
        // 如果每次触摸屏幕后第一次scroll是调节亮度，那之后的scroll事件都处理亮度调节，直到离开屏幕执行下一次操作
        else if (mGestureFlag == GESTURE_MODIFY_BRIGHT) {
            Window window = ((Activity) context).getWindow();
            if (mBrightness < 0) {
                mBrightness = window.getAttributes().screenBrightness;
                if (mBrightness <= 0.00f)
                    mBrightness = 0.50f;
                if (mBrightness < 0.01f)
                    mBrightness = 0.01f;
            }
            WindowManager.LayoutParams lpa = window.getAttributes();
            lpa.screenBrightness = mBrightness + (mOldY - y) / mPlayerHeight;
            if (lpa.screenBrightness > 1.0f)
                lpa.screenBrightness = 1.0f;
            else if (lpa.screenBrightness < 0.01f)
                lpa.screenBrightness = 0.01f;
            window.setAttributes(lpa);
        }

        mFirstScroll = false;// 第一次scroll执行完成，修改标志
        return false;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    public interface OnGestureControllerListener {
        public void onControllerFinish();

        public void onStartProgress(boolean isStart);

        public void onProgressBackward();

        public void onProgressForward();

        public void onProgressTo(int progress);

        public void onProgressTime(long playTime, long totalTime);
    }

    public void setListener(OnGestureControllerListener listener) {
        this.listener = listener;
    }

    public void setEnable(boolean enable) {
        this.mEnable = enable;
    }
}
