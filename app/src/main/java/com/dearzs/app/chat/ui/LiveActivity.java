package com.dearzs.app.chat.ui;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dearzs.app.BaseApplication;
import com.dearzs.app.R;
import com.dearzs.app.adapter.LvLiveMemberHeadListAdapter;
import com.dearzs.app.base.BaseActivity;
import com.dearzs.app.chat.adapter.ChatMsgListAdapter;
import com.dearzs.app.chat.avcontrollers.QavsdkControl;
import com.dearzs.app.chat.model.ChatEntity;
import com.dearzs.app.chat.model.CurLiveInfo;
import com.dearzs.app.chat.model.LiveInfoJson;
import com.dearzs.app.chat.model.MySelfInfo;
import com.dearzs.app.chat.presenters.EnterLiveHelper;
import com.dearzs.app.chat.presenters.LiveHelper;
import com.dearzs.app.chat.presenters.MembersDialog;
import com.dearzs.app.chat.presenters.ProfileInfoHelper;
import com.dearzs.app.chat.presenters.iml.EnterQuiteRoomView;
import com.dearzs.app.chat.presenters.iml.LiveView;
import com.dearzs.app.chat.presenters.iml.ProfileView;
import com.dearzs.app.chat.ui.widget.CircleImageView;
import com.dearzs.app.chat.utils.MemberInfo;
import com.dearzs.app.chat.utils.UIUtils;
import com.dearzs.app.entity.resp.RespStartOrEndConsult;
import com.dearzs.app.util.ImageLoaderManager;
import com.dearzs.app.util.ReqManager;
import com.dearzs.app.util.ToastUtil;
import com.dearzs.app.util.Utils;
import com.dearzs.app.widget.HorizontalListView;
import com.dearzs.commonlib.okhttp.callback.Callback;
import com.dearzs.commonlib.utils.log.LogUtil;
import com.dearzs.tim.sdk.IMConstant;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMGroupManager;
import com.tencent.TIMGroupMemberInfo;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;
import com.tencent.av.TIMAvManager;
import com.tencent.av.sdk.AVView;
import com.tencent.av.utils.PhoneStatusTools;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Request;


/**
 * Live直播类
 */
public class LiveActivity extends BaseActivity implements EnterQuiteRoomView, LiveView, View.OnClickListener, ProfileView {
    private static final String TAG = LiveActivity.class.getSimpleName();
    private static final int GETPROFILE_JOIN = 0x200;

    private EnterLiveHelper mEnterRoomHelper;
    private ProfileInfoHelper mUserInfoHelper;
    private LiveHelper mLiveHelper;

    private ArrayList<ChatEntity> mArrayListChatEntity;
    private ChatMsgListAdapter mChatMsgListAdapter;
    private static final int MINFRESHINTERVAL = 500;
    private static final int UPDAT_WALL_TIME_TIMER_TASK = 1;
    private static final int TIMEOUT_INVITE = 2;
    private boolean mBoolRefreshLock = false;
    private boolean mBoolNeedRefresh = false;
    private final Timer mTimer = new Timer();
    private ArrayList<ChatEntity> mTmpChatList = new ArrayList<ChatEntity>();//缓冲队列
    private TimerTask mTimerTask = null;
    private static final int REFRESH_LISTVIEW = 5;
    private HeartLayout mHeartLayout;
    //    private TextView mLikeTv;
    private HeartBeatTask mHeartBeatTask;//心跳
    private CircleImageView mHeadIcon;
    private TextView mHostNameTv;
    private TextView mCloseLiveTips;
    private LinearLayout mHostLayout, mHostLeaveLayout;
    private final int REQUEST_PHONE_PERMISSIONS = 0;
    private long mSecond = 0;
    private String formatTime;
    private Timer mHearBeatTimer, mVideoTimer;
    private VideoTimerTask mVideoTimerTask;//计时器
    private TextView mVideoTime;
    private ObjectAnimator mObjAnim;
    private ImageView mRecordBall;
    private TextView mTvToggleCamera;
    private Dialog inviteDg;


    private HorizontalListView mMemberHeadListView;
    private LvLiveMemberHeadListAdapter mMemberHeadLiseAdapter;

    private int thumbUp = 0;
    private long admireTime = 0;
    private int watchCount = 0;
    private static boolean mBeatuy = false;
    private static boolean mWhite = true;
    private boolean bCleanMode = false;
    private boolean mProfile;
    private boolean bFirstRender = true;

    private String backGroundId;

//    private TextView tvMembers;
    private TextView tvAdmires;

    private Dialog mDetailDialog;
    private int mClickPosition;

    private ArrayList<String> mRenderUserList = new ArrayList<>();

    //专家直播状态
    private LiveState mLiveState = LiveState.NO_BEGIN;

    private String mUserPhone;

    public enum LiveState {
        //NO_BEGIN 表示还没开始直播；OVER表示直播已结束,OWN_EXIT表示主动退出,FOCUS_EXIT被动退出
        NO_BEGIN, OVER, OWN_EXIT, FOCUS_EXIT;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_live);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   // 不锁屏
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        checkPermission();
        mUserPhone = BaseApplication.getInstance().getUserInfo() != null ? BaseApplication.getInstance().getUserInfo().getPhone() : "";
        initView();
        //进出房间的协助类
        mEnterRoomHelper = new EnterLiveHelper(this, this);
        //房间内的交互协助类
        mLiveHelper = new LiveHelper(this, this, mMemberHeadLiseAdapter);
        // 用户资料类
        mUserInfoHelper = new ProfileInfoHelper(this);
        registerReceiver();
        backGroundId = CurLiveInfo.getHostID();
        //进入房间流程
        mEnterRoomHelper.startEnterRoom();

        //QavsdkControl.getInstance().setCameraPreviewChangeCallback();
        mLiveHelper.setCameraPreviewChangeCallback();
        registerOrientationListener();
    }


    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case UPDAT_WALL_TIME_TIMER_TASK:
                    updateWallTime();
                    break;
                case REFRESH_LISTVIEW:
                    doRefreshListView();
                    break;
                case TIMEOUT_INVITE:
                    String id = "" + msg.obj;
                    cancelInviteView(id);
                    mLiveHelper.sendGroupMessage(IMConstant.AVIMCMD_MULTI_HOST_CANCELINVITE, id);
                    break;
            }
            return false;
        }
    });

    /**
     * 时间格式化
     */
    private void updateWallTime() {
        String hs, ms, ss;

        long h, m, s;
        h = mSecond / 3600;
        m = (mSecond % 3600) / 60;
        s = (mSecond % 3600) % 60;
        if (h < 10) {
            hs = "0" + h;
        } else {
            hs = "" + h;
        }

        if (m < 10) {
            ms = "0" + m;
        } else {
            ms = "" + m;
        }

        if (s < 10) {
            ss = "0" + s;
        } else {
            ss = "" + s;
        }
        if (hs.equals("00")) {
            formatTime = ms + ":" + ss;
        } else {
            formatTime = hs + ":" + ms + ":" + ss;
        }

        if (IMConstant.HOST == MySelfInfo.getInstance().getIdStatus() && null != mVideoTime) {
            LogUtil.LogD(TAG, " refresh time ");
            mVideoTime.setText(formatTime);
        }
    }


    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //AvSurfaceView 初始化成功
            if (action.equals(IMConstant.ACTION_SURFACE_CREATED)) {
                //打开摄像头
                if (MySelfInfo.getInstance().getIdStatus() == IMConstant.HOST) {
                    mLiveHelper.openCameraAndMic();
                }
            }

            if (action.equals(IMConstant.ACTION_CAMERA_OPEN_IN_LIVE)) {//有人打开摄像头
                ArrayList<String> ids = intent.getStringArrayListExtra("ids");
                //如果是自己本地直接渲染
                for (String id : ids) {
                    if (id.equals(backGroundId)) {
                        mHostLeaveLayout.setVisibility(View.GONE);
                    }
                    if (!mRenderUserList.contains(id)) {
                        mRenderUserList.add(id);
                    }

                    if (id.equals(MySelfInfo.getInstance().getId())) {
                        showVideoView(true, id);
                        return;
//                        ids.remove(id);
                    }
                }
                //其他人一并获取
                int requestCount = CurLiveInfo.getCurrentRequestCount();
                mLiveHelper.requestViewList(ids);
                requestCount = requestCount + ids.size();
                CurLiveInfo.setCurrentRequestCount(requestCount);
//                }
            }

            if (action.equals(IMConstant.ACTION_CAMERA_CLOSE_IN_LIVE)) {//有人关闭摄像头
                ArrayList<String> ids = intent.getStringArrayListExtra("ids");
                //如果是自己本地直接渲染
                for (String id : ids) {
                    mRenderUserList.remove(id);
                    if (id.equals(backGroundId)) {
                        mHostLeaveLayout.setVisibility(View.VISIBLE);
                        return;
                    }
                    //区分是谁关闭了摄像头
//                    if(id.equals(String.valueOf(CurLiveInfo.getPatientID()))){
//                        if (MySelfInfo.getInstance().getIdStatus() == IMConstant.HOST) {//自己是主播
//                            if (backGroundId.equals(MySelfInfo.getInstance().getId())) {//背景是自己
//                                mCloseLiveTips.setText("患者暂时离开了视频");
//                            }
//                    } else if(id.equals(String.valueOf(CurLiveInfo.getDoctorID()))){
//                        mCloseLiveTips.setText("医生暂时离开了视频");
//                    } else {
//                        mCloseLiveTips.setText("专家暂时离开了视频");
//                    }
                }
            }

            if (action.equals(IMConstant.ACTION_SWITCH_VIDEO)) {//点击成员回调
                backGroundId = intent.getStringExtra(IMConstant.EXTRA_IDENTIFIER);
                LogUtil.LogD(TAG, "switch video enter with id:" + backGroundId);

                if (mRenderUserList.contains(backGroundId)) {
                    mHostLeaveLayout.setVisibility(View.GONE);
                } else {
                    mHostLeaveLayout.setVisibility(View.VISIBLE);
                }

                if (MySelfInfo.getInstance().getIdStatus() == IMConstant.HOST) {//自己是主播
                    if (backGroundId.equals(MySelfInfo.getInstance().getId())) {//背景是自己
                        mHostCtrView.setVisibility(View.VISIBLE);
                        mVideoMemberCtrlView.setVisibility(View.INVISIBLE);
                    } else {//背景是其他成员
                        mHostCtrView.setVisibility(View.INVISIBLE);
                        mVideoMemberCtrlView.setVisibility(View.VISIBLE);
                    }
                } else {//自己成员方式
                    if (backGroundId.equals(MySelfInfo.getInstance().getId())) {//背景是自己
                        mVideoMemberCtrlView.setVisibility(View.VISIBLE);
                        mNomalMemberCtrView.setVisibility(View.INVISIBLE);
                    } else if (backGroundId.equals(CurLiveInfo.getHostID())) {//主播自己
                        mVideoMemberCtrlView.setVisibility(View.INVISIBLE);
                        mNomalMemberCtrView.setVisibility(View.VISIBLE);
                    } else {
                        mVideoMemberCtrlView.setVisibility(View.INVISIBLE);
                        mNomalMemberCtrView.setVisibility(View.INVISIBLE);
                    }

                }

            }
            if (action.equals(IMConstant.ACTION_HOST_LEAVE)) {//主播结束
                quiteLivePassively();
            }


        }
    };

    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(IMConstant.ACTION_SURFACE_CREATED);
        intentFilter.addAction(IMConstant.ACTION_HOST_ENTER);
        intentFilter.addAction(IMConstant.ACTION_CAMERA_OPEN_IN_LIVE);
        intentFilter.addAction(IMConstant.ACTION_CAMERA_CLOSE_IN_LIVE);
        intentFilter.addAction(IMConstant.ACTION_SWITCH_VIDEO);
        intentFilter.addAction(IMConstant.ACTION_HOST_LEAVE);
        registerReceiver(mBroadcastReceiver, intentFilter);

    }

    private void unregisterReceiver() {
        unregisterReceiver(mBroadcastReceiver);
    }

    /**
     * 初始化UI
     */
    private View avView;
    private TextView BtnBack, BtnInput, Btnflash, BtnSwitch, BtnBeauty, BtnWhite, BtnMic, BtnScreen, BtnHeart, BtnNormal, mVideoChat, BtnCtrlVideo, BtnCtrlMic, BtnHungup, mBeautyConfirm;
    private TextView inviteView1, inviteView2, inviteView3;
    private ListView mListViewMsgItems;
    private LinearLayout mHostCtrView, mNomalMemberCtrView, mVideoMemberCtrlView, mBeautySettings;
    private FrameLayout mFullControllerUi, mBackgound;
    private SeekBar mBeautyBar;
    private int mBeautyRate, mWhiteRate;
    private TextView pushBtn, recordBtn;

    private void showHeadIcon(CircleImageView view, String avatar) {
        if (TextUtils.isEmpty(avatar)) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.un_icon);
            Bitmap cirBitMap = UIUtils.createCircleImage(bitmap, 0);
            view.setImageBitmap(cirBitMap);
        } else {
            LogUtil.LogD(TAG, "load icon: " + avatar);
            ImageLoaderManager.getInstance().displayImage(avatar, view);
        }
    }

    /**
     * 初始化界面
     */
    public void initView() {
        mHostCtrView = (LinearLayout) findViewById(R.id.host_bottom_layout);
        mNomalMemberCtrView = (LinearLayout) findViewById(R.id.member_bottom_layout);
        mVideoMemberCtrlView = (LinearLayout) findViewById(R.id.video_member_bottom_layout);
        mHostLeaveLayout = (LinearLayout) findViewById(R.id.ll_host_leave);
        mVideoChat = (TextView) findViewById(R.id.video_interact);
        mCloseLiveTips = (TextView) findViewById(R.id.tv_live_close_live_tips);
        mHeartLayout = (HeartLayout) findViewById(R.id.heart_layout);
        mVideoTime = (TextView) findViewById(R.id.broadcasting_time);
        mHeadIcon = (CircleImageView) findViewById(R.id.head_icon);
        mVideoMemberCtrlView.setVisibility(View.INVISIBLE);
        mHostNameTv = (TextView) findViewById(R.id.host_name);
//        tvMembers = (TextView) findViewById(R.id.member_counts);
        tvAdmires = (TextView) findViewById(R.id.heart_counts);

        BtnCtrlVideo = (TextView) findViewById(R.id.camera_controll);
        BtnCtrlMic = (TextView) findViewById(R.id.mic_controll);
        BtnHungup = (TextView) findViewById(R.id.close_member_video);
        mTvToggleCamera = (TextView) findViewById(R.id.im_tv_toggle_camera);
        mMemberHeadListView = getView(R.id.member_headphpto_list);
        boolean isHost = false;
        if(CurLiveInfo.getHostID() != null && mUserPhone != null ){
            isHost = mUserPhone.equals(CurLiveInfo.getHostID());
        }

        mMemberHeadLiseAdapter = new LvLiveMemberHeadListAdapter(LiveActivity.this, R.layout.item_lv_live_memberhead_list, null, isHost);
        mMemberHeadListView.setAdapter(mMemberHeadLiseAdapter);

        //只有主播才能够邀请
        if(isHost){
            mMemberHeadListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(final AdapterView<?> parent, final View view, final int position, long id) {
                    final MemberInfo memberInfo = (MemberInfo) parent.getAdapter().getItem(position);
                    String dialogContent = memberInfo.getIsOnVideoChat() ? "确认要取消该用户互动直播吗？" : "确认要邀请该用户进行互动直播吗？";
                    showConfirmDialog(LiveActivity.this, dialogContent, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            inviteMemberLive(memberInfo, view, position);
                        }
                    }, null);
                }
            });
        }

        BtnCtrlVideo.setOnClickListener(this);
        BtnCtrlMic.setOnClickListener(this);
        BtnHungup.setOnClickListener(this);
        mHeadIcon.setOnClickListener(this);
        mTvToggleCamera.setOnClickListener(this);
        TextView roomId = (TextView) findViewById(R.id.room_id);
        roomId.setText("房间号：" + CurLiveInfo.getChatRoomId());

        //for 测试用
        TextView paramVideo = (TextView) findViewById(R.id.param_video);
        paramVideo.setOnClickListener(this);
        tvTipsMsg = (TextView) findViewById(R.id.qav_tips_msg);
        tvTipsMsg.setTextColor(Color.GREEN);
        //paramTimer.schedule(task, 1000, 1000);

        if (MySelfInfo.getInstance().getIdStatus() == IMConstant.HOST) {
//            mMemberDg = new MembersDialog(this, R.style.floag_dialog, this);
            //主播 显示“打开摄像头”文字
//            mTvToggleCamera.setVisibility(View.VISIBLE);
            mHostCtrView.setVisibility(View.VISIBLE);
            mNomalMemberCtrView.setVisibility(View.GONE);
            mRecordBall = (ImageView) findViewById(R.id.record_ball);
            Btnflash = (TextView) findViewById(R.id.flash_btn);
            BtnSwitch = (TextView) findViewById(R.id.switch_cam);
//            BtnBeauty = (TextView) findViewById(R.id.beauty_btn);
//            BtnWhite = (TextView) findViewById(R.id.white_btn);
            BtnMic = (TextView) findViewById(R.id.mic_btn);
            BtnScreen = (TextView) findViewById(R.id.fullscreen_btn);
//            mVideoChat.setVisibility(View.VISIBLE);
            Btnflash.setOnClickListener(this);
            BtnSwitch.setOnClickListener(this);
//            BtnBeauty.setOnClickListener(this);
//            BtnWhite.setOnClickListener(this);
            BtnMic.setOnClickListener(this);
            BtnScreen.setOnClickListener(this);
            mVideoChat.setOnClickListener(this);
            inviteView1 = (TextView) findViewById(R.id.invite_view1);
            inviteView2 = (TextView) findViewById(R.id.invite_view2);
            inviteView3 = (TextView) findViewById(R.id.invite_view3);
            inviteView1.setOnClickListener(this);
            inviteView2.setOnClickListener(this);
            inviteView3.setOnClickListener(this);

            pushBtn = (TextView) findViewById(R.id.push_btn);
            pushBtn.setVisibility(View.GONE);
            pushBtn.setOnClickListener(this);

            recordBtn = (TextView) findViewById(R.id.record_btn);
            recordBtn.setVisibility(View.GONE);
            recordBtn.setOnClickListener(this);

            initBackDialog();
            initDetailDailog();
            initPushDialog();
            initRecordDialog();

            startRecordAnimation();
            showHeadIcon(mHeadIcon, MySelfInfo.getInstance().getAvatar());
            mBeautySettings = (LinearLayout) findViewById(R.id.qav_beauty_setting);
            mBeautyConfirm = (TextView) findViewById(R.id.qav_beauty_setting_finish);
            mBeautyConfirm.setOnClickListener(this);
            mBeautyBar = (SeekBar) (findViewById(R.id.qav_beauty_progress));
            mBeautyBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    LogUtil.LogD("SeekBar", "onStopTrackingTouch");
                    if (mProfile == mBeatuy) {
                        Toast.makeText(LiveActivity.this, "beauty " + mBeautyRate + "%", Toast.LENGTH_SHORT).show();//美颜度
                    } else {
                        Toast.makeText(LiveActivity.this, "white " + mWhiteRate + "%", Toast.LENGTH_SHORT).show();//美白度
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    LogUtil.LogD("SeekBar", "onStartTrackingTouch");
                }

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress,
                                              boolean fromUser) {
                    Log.i(TAG, "onProgressChanged " + progress);
                    if (mProfile == mBeatuy) {
                        mBeautyRate = progress;
                        QavsdkControl.getInstance().getAVContext().getVideoCtrl().inputBeautyParam(getBeautyProgress(progress));//美颜
                    } else {
                        mWhiteRate = progress;
                        QavsdkControl.getInstance().getAVContext().getVideoCtrl().inputWhiteningParam(getBeautyProgress(progress));//美白
                    }
                }
            });
        } else {
            mTvToggleCamera.setVisibility(View.GONE);
            LinearLayout llRecordTip = (LinearLayout) findViewById(R.id.record_tip);
            llRecordTip.setVisibility(View.GONE);
            mHostNameTv.setVisibility(View.VISIBLE);
            initInviteDialog();
            mNomalMemberCtrView.setVisibility(View.VISIBLE);
            mHostCtrView.setVisibility(View.GONE);
            BtnInput = (TextView) findViewById(R.id.message_input);
            BtnInput.setOnClickListener(this);
//            mLikeTv = (TextView) findViewById(R.id.member_send_good);
//            mLikeTv.setOnClickListener(this);
            mVideoChat.setVisibility(View.GONE);
            BtnScreen = (TextView) findViewById(R.id.clean_screen);

            List<String> ids = new ArrayList<>();
            ids.add(CurLiveInfo.getHostID());
            showHeadIcon(mHeadIcon, CurLiveInfo.getHostAvator());
            mHostNameTv.setText(UIUtils.getLimitString(CurLiveInfo.getHostName(), 10));

            mHostLayout = (LinearLayout) findViewById(R.id.head_up_layout);
            mHostLayout.setOnClickListener(this);
            BtnScreen.setOnClickListener(this);
        }
        BtnNormal = (TextView) findViewById(R.id.normal_btn);
        BtnNormal.setOnClickListener(this);
        mFullControllerUi = (FrameLayout) findViewById(R.id.controll_ui);
        avView = findViewById(R.id.av_video_layer_ui);//surfaceView;
        BtnBack = (TextView) findViewById(R.id.btn_back);
        BtnBack.setOnClickListener(this);

        mListViewMsgItems = (ListView) findViewById(R.id.im_msg_listview);
        mArrayListChatEntity = new ArrayList<ChatEntity>();
        mChatMsgListAdapter = new ChatMsgListAdapter(this, mListViewMsgItems, mArrayListChatEntity);
        mListViewMsgItems.setAdapter(mChatMsgListAdapter);

//        tvMembers.setText("" + CurLiveInfo.getMembers());
        tvAdmires.setText("" + CurLiveInfo.getAdmires());
    }

    public void initMemberData() {
        TIMGroupManager.getInstance().getGroupMembers("" + MySelfInfo.getInstance().getMyRoomNum(), new TIMValueCallBack<List<TIMGroupMemberInfo>>() {
            @Override
            public void onError(int i, String s) {
                LogUtil.LogE("get MemberList onError---------------　ｉ　＝　" + i  + s );
            }

            @Override
            public void onSuccess(List<TIMGroupMemberInfo> timGroupMemberInfos) {
                getMemberInfo(timGroupMemberInfos);
                watchCount = timGroupMemberInfos != null ? timGroupMemberInfos.size() : 0;
            }
        });
    }

    //邀请互动直播
    private void inviteMemberLive(final MemberInfo memberInfo, View view, int position){
        mClickPosition = position;
        ImageView headTag = getView(view, R.id.iv_item_live_member_head_tag);
        if (!memberInfo.getIsOnVideoChat()) {//不在房间中，发起邀请
            if (showInviteView(memberInfo.getUserId())) {
                memberInfo.setIsOnVideoChat(true);
                headTag.setImageResource(R.mipmap.consultation_video_invitation);
            }
        } else {
            cancelInviteView(memberInfo.getUserId());
            memberInfo.setIsOnVideoChat(false);
            headTag.setImageResource(R.mipmap.consultation_video_being);
            cancelMemberView(memberInfo.getUserId());
        }
        mMemberHeadLiseAdapter.notifyDataSetChanged();
        LogUtil.LogE("asdasasdasdsadas");
    }

    private void getMemberInfo(final List<TIMGroupMemberInfo> timGroupMemberInfos) {
        final List<MemberInfo> mMembers = new ArrayList<MemberInfo>();
        if (timGroupMemberInfos == null) return ;
        List<String> memberIdArr = new ArrayList<String>();
        for (TIMGroupMemberInfo item : timGroupMemberInfos) {
//            LogUtil.LogE("getMemberInfo size() = " + memberIdArr.size());
            if (item.getUser().equals(CurLiveInfo.getHostID())) {
                continue;
            }
            memberIdArr.add(item.getUser());
        }
        //如果房间中只有主播，则直接回调，否则获取房间成员信息
        if (memberIdArr != null && memberIdArr.size() == 0) {
            mMemberHeadLiseAdapter.replaceAll(mMembers);
            LogUtil.LogE("getMemberInfo === memberIdArr.size = 0?");
        } else {
            TIMFriendshipManager.getInstance().getUsersProfile(memberIdArr, new TIMValueCallBack<List<TIMUserProfile>>() {
                @Override
                public void onError(int i, String s) {
                    LogUtil.LogE("getMemberInfo ===" + s);
                }

                @Override
                public void onSuccess(List<TIMUserProfile> timUserProfiles) {
                    if (timGroupMemberInfos == null) return;
                    for (TIMUserProfile profile : timUserProfiles) {
                        MemberInfo member = new MemberInfo();
                        member.setUserId(profile.getIdentifier());
                        member.setAvatar(profile.getFaceUrl());
                        member.setUserName(profile.getNickName());
                        if (QavsdkControl.getInstance().containIdView(profile.getIdentifier())) {
                            member.setIsOnVideoChat(true);
                        }
                        mMembers.add(member);
                    }
                    LogUtil.LogE("get mMembers size() = " + mMembers.size());
                    mMemberHeadLiseAdapter.replaceAll(mMembers);
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLiveHelper.resume();
        QavsdkControl.getInstance().onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLiveHelper.pause();
        QavsdkControl.getInstance().onPause();
    }


    /**
     * 直播心跳
     */
    private class HeartBeatTask extends TimerTask {
        @Override
        public void run() {
            String host = CurLiveInfo.getHostID();
            LogUtil.LogD(TAG, "HeartBeatTask " + host);
        }
    }

    /**
     * 记时器
     */
    private class VideoTimerTask extends TimerTask {
        public void run() {
            LogUtil.LogD(TAG, "timeTask ");
            ++mSecond;
            if (MySelfInfo.getInstance().getIdStatus() == IMConstant.HOST)
                mHandler.sendEmptyMessage(UPDAT_WALL_TIME_TIMER_TASK);
        }
    }

    @Override
    protected void onDestroy() {

        watchCount = 0;
        super.onDestroy();
        if (null != mHearBeatTimer) {
            mHearBeatTimer.cancel();
            mHearBeatTimer = null;
        }
        if (null != mVideoTimer) {
            mVideoTimer.cancel();
            mVideoTimer = null;
        }
        if (null != paramTimer) {
            paramTimer.cancel();
            paramTimer = null;
        }

        inviteViewCount = 0;
        thumbUp = 0;
        CurLiveInfo.setMembers(0);
        CurLiveInfo.setAdmires(0);
        CurLiveInfo.setCurrentRequestCount(0);
        unregisterReceiver();
        mLiveHelper.onDestory();
        mEnterRoomHelper.onDestory();
        QavsdkControl.getInstance().clearVideoMembers();
        QavsdkControl.getInstance().onDestroy();
    }

    @Override
    public boolean handleBack() {
        quiteLiveByPurpose();
        return true;
    }

    /**
     * 点击Back键
     */
    @Override
    public void onBackPressed() {
        //quiteLiveByPurpose();
    }

    /**
     * 主动退出直播
     */
    private void quiteLiveByPurpose() {
        if (MySelfInfo.getInstance().getIdStatus() == IMConstant.HOST) {
            if (!backDialog.isShowing())
                backDialog.show();
        } else {
            mLiveHelper.perpareQuitRoom(true);
            mLiveState = LiveState.OWN_EXIT;
            //mEnterRoomHelper.quiteLive();
        }
    }


    private Dialog backDialog;

    private void initBackDialog() {
        backDialog = new Dialog(this, R.style.dialog);
        backDialog.setContentView(R.layout.dialog_end_live);
        TextView tvSure = (TextView) backDialog.findViewById(R.id.btn_sure);
        tvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog();
                //如果是直播，发消息
                if (null != mLiveHelper) {
                    mLiveHelper.perpareQuitRoom(true);
                    if (isPushed) {
                        mLiveHelper.stopPushAction();
                    }
                }
                backDialog.dismiss();
            }
        });
        TextView tvCancel = (TextView) backDialog.findViewById(R.id.btn_cancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backDialog.cancel();
            }
        });
//        backDialog.show();
    }

    /**
     * 被动退出直播
     */
    private void quiteLivePassively() {
        mLiveHelper.perpareQuitRoom(false);
        mLiveState = LiveState.FOCUS_EXIT;
//        mEnterRoomHelper.quiteLive();
    }

    @Override
    public void readyToQuit() {
        mEnterRoomHelper.quiteLive();
    }

    /**
     * 完成进出房间流程
     *
     * @param id_status
     * @param isSucc
     */
    @Override
    public void enterRoomComplete(int id_status, boolean isSucc) {
        //Toast.makeText(LiveActivity.this, "EnterRoom  " + id_status + " isSucc " + isSucc, Toast.LENGTH_SHORT).show();
        //必须得进入房间之后才能初始化UI
        mEnterRoomHelper.initAvUILayer(avView);

        //设置预览回调，修正摄像头镜像
        mLiveHelper.setCameraPreviewChangeCallback();
        if (isSucc == true) {
            //IM初始化
            mLiveHelper.initTIMListener("" + CurLiveInfo.getRoomNum());
            if (id_status == IMConstant.HOST) {//主播方式加入房间成功
                //开启摄像头渲染画面
                LogUtil.LogD(TAG, "createlive enterRoomComplete isSucc" + isSucc);
                long roomId = Long.parseLong(CurLiveInfo.getChatRoomId()) - 100000;
                //TODO 判断是否是第一次进入会诊室直播
                ReqManager.getInstance().reqConsultExpertResult(reqConsultExpertResultCall, Utils.getUserToken(LiveActivity.this), roomId);

            } else {
                //发消息通知上线
                mLiveHelper.sendGroupMessage(IMConstant.AVIMCMD_EnterLive, "");
                //watchCount = CurLiveInfo.getMembers();
                //tvMembers.setText("" + CurLiveInfo.getMembers());
            }
            ArrayList<String> groupList = new ArrayList<String>();
            groupList.add(CurLiveInfo.getRoomNum() + "");
            initMemberData();

//            TIMGroupManager.getInstance().getGroupDetailInfo(groupList, new TIMValueCallBack<List<TIMGroupDetailInfo>>() {
//                @Override
//                public void onError(int i, String s) {
//                    LogUtil.LogD(TAG, "get MemberList ");
//                }
//
//                @Override
//                public void onSuccess(List<TIMGroupDetailInfo> timGroupDetailInfos) {
//                    if (timGroupDetailInfos != null) {
//                        for (TIMGroupDetailInfo info : timGroupDetailInfos) {
//                            if (info.getGroupId().equals(CurLiveInfo.getRoomNum() + "")) {
//                                LogUtil.LogD(TAG, "====当前群组人数：" + info.getMemberNum());
//                                CurLiveInfo.setMembers((int) info.getMemberNum());
//                                watchCount = CurLiveInfo.getMembers();
////                                tvMembers.setText("" + CurLiveInfo.getMembers());
//                                initMemberData();
//                                break;
//                            }
//                        }
//                    }
//                }
//            });
        }
    }

    Callback<RespStartOrEndConsult> reqConsultExpertResultCall = new Callback<RespStartOrEndConsult>() {
        @Override
        public void onError(Call call, Exception e) {
            e.printStackTrace();
            onFailure(e.toString());
        }

        @Override
        public void onResponse(RespStartOrEndConsult response) {
            if (onSuccess(response)) {
                ToastUtil.showLongToast("进入直播室成功");
            }
        }

        @Override
        public void onBefore(Request request) {
            super.onBefore(request);
            showProgressDialog();
        }
    };


    @Override
    public void quiteRoomComplete(int id_status, boolean succ, LiveInfoJson liveinfo) {
        LogUtil.LogE("quiteRoomComplete");
        closeProgressDialog();
        if (MySelfInfo.getInstance().getIdStatus() == IMConstant.HOST) {
            if ((getBaseContext() != null) && (null != mDetailDialog) && (mDetailDialog.isShowing() == false)) {
                mDetailTime.setText(formatTime);
                mDetailAdmires.setText("" + CurLiveInfo.getAdmires());
                mDetailWatchCount.setText("" + watchCount);
                mDetailDialog.show();
            }
        } else {
            String tipMsg = null;
            switch (mLiveState) {
                case NO_BEGIN:
                    tipMsg = "专家还没开始会诊,请耐心等待";
                    break;
                case OVER:
                    break;
                case OWN_EXIT:
                    break;
                case FOCUS_EXIT:
                    tipMsg = "专家退出直播";
                    break;
            }
            if (!TextUtils.isEmpty(tipMsg)) {
                LogUtil.showToast(this, tipMsg);
            }
            finish();
        }
    }

    private TextView mDetailTime, mDetailAdmires, mDetailWatchCount;

    private void initDetailDailog() {
        mDetailDialog = new Dialog(this, R.style.dialog);
        mDetailDialog.setContentView(R.layout.dialog_live_detail);
        mDetailTime = (TextView) mDetailDialog.findViewById(R.id.tv_time);
        mDetailAdmires = (TextView) mDetailDialog.findViewById(R.id.tv_admires);
        mDetailWatchCount = (TextView) mDetailDialog.findViewById(R.id.tv_members);

        mDetailDialog.setCancelable(false);

        TextView tvCancel = (TextView) mDetailDialog.findViewById(R.id.btn_cancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDetailDialog.dismiss();
                finish();
            }
        });
//        mDetailDialog.show();
    }

    /**
     * 成员状态变更
     *
     * @param id
     * @param name
     */
    @Override
    public void memberJoin(String id, String name) {
        CurLiveInfo.setMembers(CurLiveInfo.getMembers() + 1);
        watchCount = CurLiveInfo.getMembers();
        refreshTextListView(TextUtils.isEmpty(name) ? id : name, "进入会诊室", IMConstant.MEMBER_ENTER);
//        tvMembers.setText("" + CurLiveInfo.getMembers());
        initMemberData();
    }

    @Override
    public void memberQuit(String id, String name) {
        List<MemberInfo> data = mMemberHeadLiseAdapter.getData();
        if(data != null && data.size() > 0){
            int position = -1;
            for (int i = 0; i < data.size(); i++) {
                if(id != null && id.equals(data.get(i).getUserId())){
                    position = i; break;
                }
            }
            if(position >= 0 && position < data.size()) {
                mMemberHeadLiseAdapter.remove(position);
            }
        }

        refreshTextListView(TextUtils.isEmpty(name) ? id : name, "离开会诊室", IMConstant.MEMBER_EXIT);

        if (CurLiveInfo.getMembers() > 0) {
            CurLiveInfo.setMembers(CurLiveInfo.getMembers() - 1);
            watchCount = CurLiveInfo.getMembers();
//            tvMembers.setText("" + CurLiveInfo.getMembers());
        }
        //如果存在视频互动，取消
        QavsdkControl.getInstance().closeMemberView(id);
    }

    @Override
    public void hostLeave(String id, String name) {
        refreshTextListView(TextUtils.isEmpty(name) ? id : name, "离开了一会", IMConstant.HOST_LEAVE);
    }

    @Override
    public void hostBack(String id, String name) {
        refreshTextListView(TextUtils.isEmpty(name) ? id : name, "回来了", IMConstant.HOST_BACK);
    }

    /**
     * 有成员退群
     *
     * @param list 成员ID 列表
     */
    @Override
    public void memberQuiteLive(String[] list) {
        if (list == null) return;
        for (String id : list) {
            LogUtil.LogD(TAG, "memberQuiteLive id " + id);
            if (CurLiveInfo.getHostID().equals(id)) {
                if (MySelfInfo.getInstance().getIdStatus() == IMConstant.MEMBER)
                    quiteLivePassively();
            }
        }
    }

    /**
     * 有成员入群
     *
     * @param list 成员ID 列表
     */
    @Override
    public void memberJoinLive(final String[] list) {
    }

    @Override
    public void alreadyInLive(String[] list) {
        for (String id : list) {
            if (id.equals(MySelfInfo.getInstance().getId())) {
                QavsdkControl.getInstance().setSelfId(MySelfInfo.getInstance().getId());
                QavsdkControl.getInstance().setLocalHasVideo(true, MySelfInfo.getInstance().getId());
            } else {
                QavsdkControl.getInstance().setRemoteHasVideo(true, id, AVView.VIDEO_SRC_TYPE_CAMERA);
            }
        }
    }

    /**
     * 红点动画
     */
    private void startRecordAnimation() {
        mObjAnim = ObjectAnimator.ofFloat(mRecordBall, "alpha", 1f, 0f, 1f);
        mObjAnim.setDuration(1000);
        mObjAnim.setRepeatCount(-1);
        mObjAnim.start();
    }

    private static int index = 0;

    /**
     * 加载视频数据
     *
     * @param isLocal 是否是本地数据
     * @param id      身份
     */
    @Override
    public void showVideoView(boolean isLocal, String id) {
        LogUtil.LogD(TAG, "showVideoView " + id);

        //渲染本地Camera
        if (isLocal == true) {
            LogUtil.LogD(TAG, "showVideoView host :" + MySelfInfo.getInstance().getId());
            QavsdkControl.getInstance().setSelfId(MySelfInfo.getInstance().getId());
            QavsdkControl.getInstance().setLocalHasVideo(true, MySelfInfo.getInstance().getId());
            //主播通知用户服务器
            if (MySelfInfo.getInstance().getIdStatus() == IMConstant.HOST) {
                if (bFirstRender) {
                    mEnterRoomHelper.notifyServerCreateRoom();

                    //主播心跳
                    //mHearBeatTimer = new Timer(true);
                    //mHeartBeatTask = new HeartBeatTask();
                    //mHearBeatTimer.schedule(mHeartBeatTask, 1000, 3 * 1000);
                    //直播时间
                    mVideoTimer = new Timer(true);
                    mVideoTimerTask = new VideoTimerTask();
                    mVideoTimer.schedule(mVideoTimerTask, 1000, 1000);
                    bFirstRender = false;
                }
            }
        } else {
//            QavsdkControl.getInstance().addRemoteVideoMembers(id);
            QavsdkControl.getInstance().setRemoteHasVideo(true, id, AVView.VIDEO_SRC_TYPE_CAMERA);
        }
    }

    private float getBeautyProgress(int progress) {
        LogUtil.LogD("shixu", "progress: " + progress);
        return (9.0f * progress / 100.0f);
    }

    @Override
    public void showInviteDialog() {
        if ((inviteDg != null) && (getBaseContext() != null) && (inviteDg.isShowing() != true)) {
            inviteDg.show();
        }
    }

    @Override
    public void hideInviteDialog() {
        if ((inviteDg != null) && (inviteDg.isShowing() == true)) {
            inviteDg.dismiss();
        }
    }


    @Override
    public void refreshText(String text, String name) {
        if (text != null) {
            refreshTextListView(name, text, IMConstant.TEXT_TYPE);
        }
    }

    @Override
    public void refreshThumbUp() {
        CurLiveInfo.setAdmires(CurLiveInfo.getAdmires() + 1);
        if (!bCleanMode) {      // 纯净模式下不播放飘星动画
            mHeartLayout.addFavor();
        }
        tvAdmires.setText("" + CurLiveInfo.getAdmires());
    }

    @Override
    public void refreshUI(String id) {
        //当主播选中这个人，而他主动退出时需要恢复到正常状态
        if (MySelfInfo.getInstance().getIdStatus() == IMConstant.HOST)
            if (!backGroundId.equals(CurLiveInfo.getHostID()) && backGroundId.equals(id)) {
                backToNormalCtrlView();
            }
    }


    private int inviteViewCount = 0;

    @Override
    public boolean showInviteView(String id) {
        int index = QavsdkControl.getInstance().getAvailableViewIndex(1);
        if (index == -1) {
            Toast.makeText(LiveActivity.this, "the invitation's upper limit is 3", Toast.LENGTH_SHORT).show();
            return false;
        }
        int requetCount = index + inviteViewCount;
        if (requetCount > 3) {
            Toast.makeText(LiveActivity.this, "the invitation's upper limit is 3", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (hasInvited(id)) {
            Toast.makeText(LiveActivity.this, "it has already invited", Toast.LENGTH_SHORT).show();
            return false;
        }
        switch (requetCount) {
            case 1:
                inviteView1.setText(id);
                inviteView1.setVisibility(View.VISIBLE);
                inviteView1.setTag(id);

                break;
            case 2:
                inviteView2.setText(id);
                inviteView2.setVisibility(View.VISIBLE);
                inviteView2.setTag(id);
                break;
            case 3:
                inviteView3.setText(id);
                inviteView3.setVisibility(View.VISIBLE);
                inviteView3.setTag(id);
                break;
        }
        mLiveHelper.sendC2CMessage(IMConstant.AVIMCMD_MUlTI_HOST_INVITE, "", id);
        inviteViewCount++;
        //30s超时取消
        Message msg = new Message();
        msg.what = TIMEOUT_INVITE;
        msg.obj = id;
        mHandler.sendMessageDelayed(msg, 30 * 1000);
        return true;
    }


    /**
     * 判断是否邀请过同一个人
     *
     * @param id
     * @return
     */
    private boolean hasInvited(String id) {
        if (id.equals(inviteView1.getTag())) {
            return true;
        }
        if (id.equals(inviteView2.getTag())) {
            return true;
        }
        if (id.equals(inviteView3.getTag())) {
            return true;
        }
        return false;
    }

    @Override
    public void cancelInviteView(String id) {
        if ((inviteView1 != null) && (inviteView1.getTag() != null)) {
            if (inviteView1.getTag().equals(id)) {
            }
            if (inviteView1.getVisibility() == View.VISIBLE) {
                inviteView1.setVisibility(View.INVISIBLE);
                inviteView1.setTag("");
                inviteViewCount--;
            }
        }

        if (inviteView2 != null && inviteView2.getTag() != null) {
            if (inviteView2.getTag().equals(id)) {
                if (inviteView2.getVisibility() == View.VISIBLE) {
                    inviteView2.setVisibility(View.INVISIBLE);
                    inviteView2.setTag("");
                    inviteViewCount--;
                }
            } else {
                Log.i(TAG, "cancelInviteView inviteView2 is null");
            }
        } else {
            Log.i(TAG, "cancelInviteView inviteView2 is null");
        }

        if (inviteView3 != null && inviteView3.getTag() != null) {
            if (inviteView3.getTag().equals(id)) {
                if (inviteView3.getVisibility() == View.VISIBLE) {
                    inviteView3.setVisibility(View.INVISIBLE);
                    inviteView3.setTag("");
                    inviteViewCount--;
                }
            } else {
                Log.i(TAG, "cancelInviteView inviteView3 is null");
            }
        } else {
            Log.i(TAG, "cancelInviteView inviteView3 is null");
        }


    }

    @Override
    public void cancelMemberView(String id) {
        //患者和县级医生，点击同意加入互动直播，需要更新患者和县级医生自身的头像的状态
        if(mMemberHeadLiseAdapter != null) {
            if(!TextUtils.isEmpty(mUserPhone)){
                mMemberHeadLiseAdapter.notifyItemData(mUserPhone, false);
            }
        }
        if (MySelfInfo.getInstance().getIdStatus() == IMConstant.HOST) {
        } else {
            //TODO 主动下麦 下麦；
            mLiveHelper.changeAuthandRole(false, IMConstant.NORMAL_MEMBER_AUTH, IMConstant.NORMAL_MEMBER_ROLE);
//            mLiveHelper.closeCameraAndMic();//是自己成员关闭
        }
        mLiveHelper.sendGroupMessage(IMConstant.AVIMCMD_MULTI_CANCEL_INTERACT, id);
        QavsdkControl.getInstance().closeMemberView(id);
        backToNormalCtrlView();
    }


    private void showReportDialog() {
        final Dialog reportDialog = new Dialog(this, R.style.report_dlg);
        reportDialog.setContentView(R.layout.dialog_live_report);

        TextView tvReportDirty = (TextView) reportDialog.findViewById(R.id.btn_dirty);
        TextView tvReportFalse = (TextView) reportDialog.findViewById(R.id.btn_false);
        TextView tvReportVirus = (TextView) reportDialog.findViewById(R.id.btn_virus);
        TextView tvReportIllegal = (TextView) reportDialog.findViewById(R.id.btn_illegal);
        TextView tvReportYellow = (TextView) reportDialog.findViewById(R.id.btn_yellow);
        TextView tvReportCancel = (TextView) reportDialog.findViewById(R.id.btn_cancel);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    default:
                        reportDialog.cancel();
                        break;
                }
            }
        };

        tvReportDirty.setOnClickListener(listener);
        tvReportFalse.setOnClickListener(listener);
        tvReportVirus.setOnClickListener(listener);
        tvReportIllegal.setOnClickListener(listener);
        tvReportYellow.setOnClickListener(listener);
        tvReportCancel.setOnClickListener(listener);

        reportDialog.setCanceledOnTouchOutside(true);
        reportDialog.show();
    }

    private void showHostDetail() {
        Dialog hostDlg = new Dialog(this, R.style.host_info_dlg);
        hostDlg.setContentView(R.layout.host_info_layout);

        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        Window dlgwin = hostDlg.getWindow();
        WindowManager.LayoutParams lp = dlgwin.getAttributes();
        dlgwin.setGravity(Gravity.TOP);
        lp.width = (int) (display.getWidth()); //设置宽度

        hostDlg.getWindow().setAttributes(lp);
        hostDlg.show();

        TextView tvHost = (TextView) hostDlg.findViewById(R.id.tv_host_name);
        tvHost.setText(CurLiveInfo.getHostName());
        CircleImageView ivHostIcon = (CircleImageView) hostDlg.findViewById(R.id.iv_host_icon);
        showHeadIcon(ivHostIcon, CurLiveInfo.getHostAvator());
        TextView tvLbs = (TextView) hostDlg.findViewById(R.id.tv_host_lbs);
        tvLbs.setText(UIUtils.getLimitString(CurLiveInfo.getAddress(), 6));
        ImageView ivReport = (ImageView) hostDlg.findViewById(R.id.iv_report);
        ivReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReportDialog();
            }
        });
    }

    private boolean checkInterval() {
        if (0 == admireTime) {
            admireTime = System.currentTimeMillis();
            return true;
        }
        long newTime = System.currentTimeMillis();
        if (newTime >= admireTime + 1000) {
            admireTime = newTime;
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                quiteLiveByPurpose();
                break;
            case R.id.message_input:
                inputMsgDialog();
                break;
//            case R.id.member_send_good:
//                // 添加飘星动画
//                mHeartLayout.addFavor();
//                if (checkInterval()) {
//                    mLiveHelper.sendC2CMessage(IMConstant.AVIMCMD_Praise, "", CurLiveInfo.getHostID());
//                    CurLiveInfo.setAdmires(CurLiveInfo.getAdmires() + 1);
//                    tvAdmires.setText("" + CurLiveInfo.getAdmires());
//                } else {
//                    //Toast.makeText(this, getString(R.string.text_live_admire_limit), Toast.LENGTH_SHORT).show();
//                }
//                break;
            case R.id.flash_btn:
                if (mLiveHelper.isFrontCamera() == true) {
                    Toast.makeText(LiveActivity.this, "当前是前置摄像头，该功能在后置摄像头打开时可用", Toast.LENGTH_SHORT).show();
                } else {
                    mLiveHelper.toggleFlashLight();
                }
                break;
            case R.id.switch_cam:
                mLiveHelper.switchCamera();
                break;
            case R.id.mic_btn:
                if (mLiveHelper.isMicOpen()) {
                    BtnMic.setBackgroundResource(R.mipmap.icon_mic_close);
                    mLiveHelper.muteMic();
                } else {
                    BtnMic.setBackgroundResource(R.mipmap.icon_mic_open);
                    mLiveHelper.openMic();
                }
                break;
            case R.id.head_up_layout:
                //showHostDetail();
                break;
            case R.id.clean_screen:
            case R.id.fullscreen_btn:
                bCleanMode = true;
                mFullControllerUi.setVisibility(View.INVISIBLE);
                BtnNormal.setVisibility(View.VISIBLE);
                break;
            case R.id.normal_btn:
                bCleanMode = false;
                mFullControllerUi.setVisibility(View.VISIBLE);
                BtnNormal.setVisibility(View.GONE);
                break;
            case R.id.video_interact:
//                mMemberDg.setCanceledOnTouchOutside(true);
//                mMemberDg.show();
                break;
            case R.id.camera_controll:
                Toast.makeText(LiveActivity.this, "切换" + backGroundId + "camrea 状态", Toast.LENGTH_SHORT).show();
                if (backGroundId.equals(MySelfInfo.getInstance().getId())) {//自己关闭自己
                    mLiveHelper.toggleCamera();
                } else {
                    mLiveHelper.sendC2CMessage(IMConstant.AVIMCMD_MULTI_HOST_CONTROLL_CAMERA, backGroundId, backGroundId);//主播关闭自己
                }
                break;
            case R.id.mic_controll:
                Toast.makeText(LiveActivity.this, "切换" + backGroundId + "mic 状态", Toast.LENGTH_SHORT).show();
                if (backGroundId.equals(MySelfInfo.getInstance().getId())) {//自己关闭自己
                    mLiveHelper.toggleMic();
                } else {
                    mLiveHelper.sendC2CMessage(IMConstant.AVIMCMD_MULTI_HOST_CONTROLL_MIC, backGroundId, backGroundId);//主播关闭自己
                }
                break;
            case R.id.close_member_video://主动关闭成员摄像头
                cancelMemberView(backGroundId);
                break;
//            case R.id.beauty_btn:
//                Log.i(TAG, "onClick " + mBeautyRate);
//
//                mProfile = mBeatuy;
//                if (mBeautySettings != null) {
//                    if (mBeautySettings.getVisibility() == View.GONE) {
//                        mBeautySettings.setVisibility(View.VISIBLE);
//                        mFullControllerUi.setVisibility(View.INVISIBLE);
//                        mBeautyBar.setProgress(mBeautyRate);
//                    } else {
//                        mBeautySettings.setVisibility(View.GONE);
//                        mFullControllerUi.setVisibility(View.VISIBLE);
//                    }
//                } else {
//                    LogUtil.LogD(TAG, "beauty_btn mTopBar  is null ");
//                }
//                break;

            case R.id.white_btn:
                Log.i(TAG, "onClick " + mWhiteRate);
                mProfile = mWhite;
                if (mBeautySettings != null) {
                    if (mBeautySettings.getVisibility() == View.GONE) {
                        mBeautySettings.setVisibility(View.VISIBLE);
                        mFullControllerUi.setVisibility(View.INVISIBLE);
                        mBeautyBar.setProgress(mWhiteRate);
                    } else {
                        mBeautySettings.setVisibility(View.GONE);
                        mFullControllerUi.setVisibility(View.VISIBLE);
                    }
                } else {
                    LogUtil.LogD(TAG, "beauty_btn mTopBar  is null ");
                }
                break;
            case R.id.qav_beauty_setting_finish:
                mBeautySettings.setVisibility(View.GONE);
                mFullControllerUi.setVisibility(View.VISIBLE);
                break;
            case R.id.invite_view1:
                inviteView1.setVisibility(View.INVISIBLE);
                mLiveHelper.sendGroupMessage(IMConstant.AVIMCMD_MULTI_CANCEL_INTERACT, "" + inviteView1.getTag());
                break;
            case R.id.invite_view2:
                inviteView2.setVisibility(View.INVISIBLE);
                mLiveHelper.sendGroupMessage(IMConstant.AVIMCMD_MULTI_CANCEL_INTERACT, "" + inviteView2.getTag());
                break;
            case R.id.invite_view3:
                inviteView3.setVisibility(View.INVISIBLE);
                mLiveHelper.sendGroupMessage(IMConstant.AVIMCMD_MULTI_CANCEL_INTERACT, "" + inviteView3.getTag());
                break;
            case R.id.param_video:
                showTips = !showTips;
                break;
            case R.id.push_btn:
                pushStream();
                break;
            case R.id.record_btn:
                if (!mRecord) {
                    if (recordDialog != null)
                        recordDialog.show();
                } else {
                    mLiveHelper.stopRecord();
                }
                break;
            case R.id.im_tv_toggle_camera:
                if (mLiveHelper != null) {
                    mLiveHelper.toggleCamera();
                }
                break;
        }
    }

    //for 测试获取测试参数
    private boolean showTips = false;
    private TextView tvTipsMsg;
    Timer paramTimer = new Timer();
    TimerTask task = new TimerTask() {
        public void run() {
            runOnUiThread(new Runnable() {
                public void run() {
                    if (showTips) {
                        if (tvTipsMsg != null) {
                            String strTips = QavsdkControl.getInstance().getQualityTips();
                            strTips = praseString(strTips);
                            if (!TextUtils.isEmpty(strTips)) {
                                tvTipsMsg.setText(strTips);
                            }
                        }
                    } else {
                        tvTipsMsg.setText("");
                    }
                }
            });
        }
    };

    //for 测试 解析参数
    private String praseString(String video) {
        if (video.length() == 0) {
            return "";
        }
        String result = "";
        String splitItems[];
        String tokens[];
        splitItems = video.split("\\n");
        for (int i = 0; i < splitItems.length; ++i) {
            if (splitItems[i].length() < 2)
                continue;

            tokens = splitItems[i].split(":");
            if (tokens[0].length() == "mainVideoSendSmallViewQua".length()) {
                continue;
            }
            if (tokens[0].endsWith("BigViewQua")) {
                tokens[0] = "mainVideoSendViewQua";
            }
            if (tokens[0].endsWith("BigViewQos")) {
                tokens[0] = "mainVideoSendViewQos";
            }
            result += tokens[0] + ":\n" + "\t\t";
            for (int j = 1; j < tokens.length; ++j)
                result += tokens[j];
            result += "\n\n";
            //Log.d(TAG, "test:" + result);
        }
        //Log.d(TAG, "test:" + result);
        return result;
    }


    private void backToNormalCtrlView() {
        if (MySelfInfo.getInstance().getIdStatus() == IMConstant.HOST) {
            backGroundId = CurLiveInfo.getHostID();
            mHostCtrView.setVisibility(View.VISIBLE);
            mVideoMemberCtrlView.setVisibility(View.GONE);
        } else {
            backGroundId = CurLiveInfo.getHostID();
            mNomalMemberCtrView.setVisibility(View.VISIBLE);
            mVideoMemberCtrlView.setVisibility(View.GONE);
        }
    }


    /**
     * 发消息弹出框
     */
    private void inputMsgDialog() {
        InputTextMsgDialog inputMsgDialog = new InputTextMsgDialog(this, R.style.inputdialog, mLiveHelper, this);
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = inputMsgDialog.getWindow().getAttributes();

        lp.width = (int) (display.getWidth()); //设置宽度
        inputMsgDialog.getWindow().setAttributes(lp);
        inputMsgDialog.setCancelable(true);
        inputMsgDialog.show();
    }

    /**
     * 主播邀请应答框
     */
    private void initInviteDialog() {
        inviteDg = new Dialog(this, R.style.alert_dialog);
        inviteDg.setContentView(R.layout.alert_dialog);
        Button agreeBtn = (Button) inviteDg.findViewById(R.id.confirm_button);
        Button refusebtn = (Button) inviteDg.findViewById(R.id.cancel_button);
        View line = (View) inviteDg.findViewById(R.id.dialog_line);
        TextView hostId = (TextView) inviteDg.findViewById(R.id.title_text);
        TextView content = (TextView) inviteDg.findViewById(R.id.content_text);
        hostId.setText("提示");
        content.setText("专家邀请您与他进行会诊");
        content.setVisibility(View.VISIBLE);
        hostId.setVisibility(View.VISIBLE);
        agreeBtn.setVisibility(View.VISIBLE);
        refusebtn.setVisibility(View.VISIBLE);
        line.setVisibility(View.VISIBLE);
        inviteDg.setCanceledOnTouchOutside(true);

        agreeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                mVideoMemberCtrlView.setVisibility(View.VISIBLE);
//                mNomalMemberCtrView.setVisibility(View.INVISIBLE);
                //患者和县级医生，点击同意加入互动直播，需要更新患者和县级医生自身的头像的状态
                if(mMemberHeadLiseAdapter != null) {
                    if(!TextUtils.isEmpty(mUserPhone)){
                        mMemberHeadLiseAdapter.notifyItemData(mUserPhone, true);
                    }
                }
                //上麦 ；TODO 上麦 上麦 上麦 ！！！！！；
                mLiveHelper.changeAuthandRole(true, IMConstant.VIDEO_MEMBER_AUTH, IMConstant.VIDEO_MEMBER_ROLE);
                inviteDg.dismiss();
            }
        });

        refusebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //患者和县级医生，点击同意加入互动直播，需要更新患者和县级医生自身的头像的状态
                if(mMemberHeadLiseAdapter != null) {
                    if(!TextUtils.isEmpty(mUserPhone)){
                        mMemberHeadLiseAdapter.notifyItemData(mUserPhone, false);
                    }
                }
                mLiveHelper.sendC2CMessage(IMConstant.AVIMCMD_MUlTI_REFUSE, "", CurLiveInfo.getHostID());
                inviteDg.dismiss();
            }
        });

        Window dialogWindow = inviteDg.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setAttributes(lp);
    }


    /**
     * 消息刷新显示
     *
     * @param name    发送者
     * @param context 内容
     * @param type    类型 （上线线消息和 聊天消息）
     */
    public void refreshTextListView(String name, String context, int type) {
        ChatEntity entity = new ChatEntity();
        entity.setSenderName(name);
        entity.setContext(context);
        entity.setType(type);
        //mArrayListChatEntity.add(entity);
        notifyRefreshListView(entity);
        //mChatMsgListAdapter.notifyDataSetChanged();

        mListViewMsgItems.setVisibility(View.VISIBLE);
        LogUtil.LogD(TAG, "refreshTextListView height " + mListViewMsgItems.getHeight());

        if (mListViewMsgItems.getCount() > 1) {
            if (true)
                mListViewMsgItems.setSelection(0);
            else
                mListViewMsgItems.setSelection(mListViewMsgItems.getCount() - 1);
        }
    }


    /**
     * 通知刷新消息ListView
     */
    private void notifyRefreshListView(ChatEntity entity) {
        mBoolNeedRefresh = true;
        mTmpChatList.add(entity);
        if (mBoolRefreshLock) {
            return;
        } else {
            doRefreshListView();
        }
    }


    /**
     * 刷新ListView并重置状态
     */
    private void doRefreshListView() {
        if (mBoolNeedRefresh) {
            mBoolRefreshLock = true;
            mBoolNeedRefresh = false;
            mArrayListChatEntity.addAll(mTmpChatList);
            mTmpChatList.clear();
            mChatMsgListAdapter.notifyDataSetChanged();

            if (null != mTimerTask) {
                mTimerTask.cancel();
            }
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    LogUtil.LogD(TAG, "doRefreshListView->task enter with need:" + mBoolNeedRefresh);
                    mHandler.sendEmptyMessage(REFRESH_LISTVIEW);
                }
            };
            //mTimer.cancel();
            mTimer.schedule(mTimerTask, MINFRESHINTERVAL);
        } else {
            mBoolRefreshLock = false;
        }
    }

    @Override
    public void updateProfileInfo(TIMUserProfile profile) {

    }

    @Override
    public void updateUserInfo(int requestCode, List<TIMUserProfile> profiles) {
        if (null != profiles) {
            switch (requestCode) {
                case GETPROFILE_JOIN:
                    for (TIMUserProfile user : profiles) {
//                        tvMembers.setText("" + CurLiveInfo.getMembers());
                        LogUtil.LogD(TAG, "get nick name:" + user.getNickName());
                        LogUtil.LogD(TAG, "get remark name:" + user.getRemark());
                        LogUtil.LogD(TAG, "get avatar:" + user.getFaceUrl());
                        if (!TextUtils.isEmpty(user.getNickName())) {
                            refreshTextListView(user.getNickName(), "join live", IMConstant.MEMBER_ENTER);
                        } else {
                            refreshTextListView(user.getIdentifier(), "join live", IMConstant.MEMBER_ENTER);
                        }
//                        initMemberData();
                    }
                    break;
            }

        }
    }

    //旁路直播
    private static boolean isPushed = false;

    /**
     * 旁路直播 退出房间时必须退出推流。否则会占用后台channel。
     */
    public void pushStream() {
        if (!isPushed) {
            if (mPushDialog != null)
                mPushDialog.show();
        } else {
            mLiveHelper.stopPushAction();
        }
    }

    private Dialog mPushDialog;

    private void initPushDialog() {
        mPushDialog = new Dialog(this, R.style.dialog);
        mPushDialog.setContentView(R.layout.push_dialog_layout);
        final TIMAvManager.StreamParam mStreamParam = TIMAvManager.getInstance().new StreamParam();
        final EditText pushfileNameInput = (EditText) mPushDialog.findViewById(R.id.push_filename);
        final RadioGroup radgroup = (RadioGroup) mPushDialog.findViewById(R.id.push_type);


        Button recordOk = (Button) mPushDialog.findViewById(R.id.btn_record_ok);
        recordOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pushfileNameInput.getText().toString().equals("")) {
                    Toast.makeText(LiveActivity.this, "name can't be empty", Toast.LENGTH_SHORT);
                    return;
                } else {
                    mStreamParam.setChannelName(pushfileNameInput.getText().toString());
                }

                if (radgroup.getCheckedRadioButtonId() == R.id.hls) {
                    mStreamParam.setEncode(TIMAvManager.StreamEncode.HLS);
                } else {
                    mStreamParam.setEncode(TIMAvManager.StreamEncode.RTMP);
                }
//                mStreamParam.setEncode(TIMAvManager.StreamEncode.HLS);
                mLiveHelper.pushAction(mStreamParam);
                mPushDialog.dismiss();
            }
        });


        Button recordCancel = (Button) mPushDialog.findViewById(R.id.btn_record_cancel);
        recordCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPushDialog.dismiss();
            }
        });

        Window dialogWindow = mPushDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setAttributes(lp);
        mPushDialog.setCanceledOnTouchOutside(false);
    }


    /**
     * 推流成功
     *
     * @param streamRes
     */
    @Override
    public void pushStreamSucc(TIMAvManager.StreamRes streamRes) {
        List<TIMAvManager.LiveUrl> liveUrls = streamRes.getUrls();
        isPushed = true;
        pushBtn.setBackgroundResource(R.mipmap.icon_stop_push);
        int length = liveUrls.size();
        String url = null;
        String url2 = null;
        if (length == 1) {
            TIMAvManager.LiveUrl avUrl = liveUrls.get(0);
            url = avUrl.getUrl();
        } else if (length == 2) {
            TIMAvManager.LiveUrl avUrl = liveUrls.get(0);
            url = avUrl.getUrl();
            TIMAvManager.LiveUrl avUrl2 = liveUrls.get(1);
            url2 = avUrl2.getUrl();
        }
        ClipToBoard(url, url2);
    }

    /**
     * 将地址黏贴到黏贴版
     *
     * @param url
     * @param url2
     */
    private void ClipToBoard(final String url, final String url2) {
        LogUtil.LogD(TAG, "ClipToBoard url " + url);
        LogUtil.LogD(TAG, "ClipToBoard url2 " + url2);
        if (url == null) return;
        final Dialog dialog = new Dialog(this, R.style.dialog);
        dialog.setContentView(R.layout.clip_dialog);
        TextView urlText = ((TextView) dialog.findViewById(R.id.url1));
        TextView urlText2 = ((TextView) dialog.findViewById(R.id.url2));
        Button btnClose = ((Button) dialog.findViewById(R.id.close_dialog));
        urlText.setText(url);
        urlText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clip = (ClipboardManager) getApplicationContext().getSystemService(getApplicationContext().CLIPBOARD_SERVICE);
                clip.setText(url);
                Toast.makeText(LiveActivity.this, "地址复制到黏贴版上了", Toast.LENGTH_SHORT).show();
            }
        });
        if (url2 == null) {
            urlText2.setVisibility(View.GONE);
        } else {
            urlText2.setText(url2);
            urlText2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ClipboardManager clip = (ClipboardManager) getApplicationContext().getSystemService(getApplicationContext().CLIPBOARD_SERVICE);
                    clip.setText(url2);
                    Toast.makeText(LiveActivity.this, "地址复制到黏贴版上了", Toast.LENGTH_SHORT).show();
                }
            });
        }
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }


    private Dialog recordDialog;
    private TIMAvManager.RecordParam mRecordParam;
    private String filename = "";
    private String tags = "";
    private String classId = "";
    private boolean mRecord = false;
    private EditText filenameEditText, tagEditText, classEditText;
    private CheckBox trancodeCheckBox, screenshotCheckBox, watermarkCheckBox;

    private void initRecordDialog() {
        recordDialog = new Dialog(this, R.style.dialog);
        recordDialog.setContentView(R.layout.record_param);
        mRecordParam = TIMAvManager.getInstance().new RecordParam();

        filenameEditText = (EditText) recordDialog.findViewById(R.id.record_filename);
        tagEditText = (EditText) recordDialog.findViewById(R.id.record_tag);
        classEditText = (EditText) recordDialog.findViewById(R.id.record_class);
        trancodeCheckBox = (CheckBox) recordDialog.findViewById(R.id.record_tran_code);
        screenshotCheckBox = (CheckBox) recordDialog.findViewById(R.id.record_screen_shot);
        watermarkCheckBox = (CheckBox) recordDialog.findViewById(R.id.record_water_mark);

        if (filename.length() > 0) {
            filenameEditText.setText(filename);
        }
        filenameEditText.setText("" + CurLiveInfo.getRoomNum());

        if (tags.length() > 0) {
            tagEditText.setText(tags);
        }

        if (classId.length() > 0) {
            classEditText.setText(classId);
        }
        Button recordOk = (Button) recordDialog.findViewById(R.id.btn_record_ok);
        recordOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                filename = filenameEditText.getText().toString();
                mRecordParam.setFilename(filename);
                tags = tagEditText.getText().toString();
                classId = classEditText.getText().toString();
                Log.d(TAG, "onClick classId " + classId);
                if (classId.equals("")) {
                    Toast.makeText(getApplicationContext(), "classID can not be empty", Toast.LENGTH_LONG).show();
                    return;
                }
                mRecordParam.setClassId(Integer.parseInt(classId));
                mRecordParam.setTransCode(trancodeCheckBox.isChecked());
                mRecordParam.setSreenShot(screenshotCheckBox.isChecked());
                mRecordParam.setWaterMark(watermarkCheckBox.isChecked());
                mLiveHelper.startRecord(mRecordParam);
                startOrientationListener();
                recordDialog.dismiss();
            }
        });
        Button recordCancel = (Button) recordDialog.findViewById(R.id.btn_record_cancel);
        recordCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startOrientationListener();
                recordDialog.dismiss();
            }
        });
        stopOrientationListener();
        Window dialogWindow = recordDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setAttributes(lp);
        recordDialog.setCanceledOnTouchOutside(false);
    }

    /**
     * 停止推流成功
     */
    @Override
    public void stopStreamSucc() {
        isPushed = false;
        pushBtn.setBackgroundResource(R.mipmap.icon_push_stream);
    }

    @Override
    public void startRecordCallback(boolean isSucc) {
        mRecord = true;
        recordBtn.setBackgroundResource(R.mipmap.icon_stoprecord);

    }

    @Override
    public void stopRecordCallback(boolean isSucc, List<String> files) {
        if (isSucc == true) {
            mRecord = false;
            recordBtn.setBackgroundResource(R.mipmap.icon_record);
        }
    }

    private VideoOrientationEventListener mOrientationEventListener;

    void registerOrientationListener() {
        if (mOrientationEventListener == null) {
            mOrientationEventListener = new VideoOrientationEventListener(super.getApplicationContext(), SensorManager.SENSOR_DELAY_UI);
        }
    }

    void startOrientationListener() {
        if (mOrientationEventListener != null) {
            mOrientationEventListener.enable();
        }
    }

    void stopOrientationListener() {
        if (mOrientationEventListener != null) {
            mOrientationEventListener.disable();
        }
    }


    class VideoOrientationEventListener extends OrientationEventListener {
        boolean mbIsTablet = false;
        int mRotationAngle = 0;

        public VideoOrientationEventListener(Context context, int rate) {
            super(context, rate);
            mbIsTablet = PhoneStatusTools.isTablet(context);
        }

        int mLastOrientation = -25;

        @Override
        public void onOrientationChanged(int orientation) {
            if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
                mLastOrientation = orientation;
                return;
            }

            if (mLastOrientation < 0) {
                mLastOrientation = 0;
            }

            if (((orientation - mLastOrientation) < 20)
                    && ((orientation - mLastOrientation) > -20)) {
                return;
            }

            if (mbIsTablet) {
                orientation -= 90;
                if (orientation < 0) {
                    orientation += 360;
                }
            }
            mLastOrientation = orientation;

            if (orientation > 314 || orientation < 45) {
                if (QavsdkControl.getInstance() != null) {
                    QavsdkControl.getInstance().setRotation(0);
                }
                mRotationAngle = 0;
            } else if (orientation > 44 && orientation < 135) {
                if (QavsdkControl.getInstance() != null) {
                    QavsdkControl.getInstance().setRotation(90);
                }
                mRotationAngle = 90;
            } else if (orientation > 134 && orientation < 225) {
                if (QavsdkControl.getInstance() != null) {
                    QavsdkControl.getInstance().setRotation(180);
                }
                mRotationAngle = 180;
            } else {
                if (QavsdkControl.getInstance() != null) {
                    QavsdkControl.getInstance().setRotation(270);
                }
                mRotationAngle = 270;
            }
        }
    }


    void checkPermission() {
        final List<String> permissionsList = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED))
                permissionsList.add(Manifest.permission.CAMERA);
            if ((checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED))
                permissionsList.add(Manifest.permission.RECORD_AUDIO);
            if ((checkSelfPermission(Manifest.permission.WAKE_LOCK) != PackageManager.PERMISSION_GRANTED))
                permissionsList.add(Manifest.permission.WAKE_LOCK);
            if ((checkSelfPermission(Manifest.permission.MODIFY_AUDIO_SETTINGS) != PackageManager.PERMISSION_GRANTED))
                permissionsList.add(Manifest.permission.MODIFY_AUDIO_SETTINGS);
            if (permissionsList.size() != 0) {
                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_PHONE_PERMISSIONS);
            }
        }
    }
}
