package com.dearzs.app.chat.model;

import android.content.Context;

import com.dearzs.app.R;
import com.dearzs.app.chat.avcontrollers.QavsdkControl;
import com.dearzs.app.chat.utils.PushUtil;
import com.dearzs.commonlib.utils.log.LogUtil;
import com.dearzs.tim.presentation.business.InitBusiness;
import com.dearzs.tim.presentation.business.LoginBusiness;
import com.dearzs.tim.presentation.event.FriendshipEvent;
import com.dearzs.tim.presentation.event.GroupEvent;
import com.dearzs.tim.presentation.event.MessageEvent;
import com.dearzs.tim.presentation.event.RefreshEvent;
import com.dearzs.tim.sdk.IMConstant;
import com.tencent.TIMCallBack;
import com.tencent.TIMGroupReceiveMessageOpt;
import com.tencent.TIMLogLevel;
import com.tencent.TIMManager;
import com.tencent.TIMOfflinePushListener;
import com.tencent.TIMOfflinePushNotification;
import com.tencent.TIMOfflinePushSettings;
import com.tencent.TIMUserStatusListener;
import com.tencent.qalsdk.sdk.MsfSdkUtils;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * IM管理类
 */
public class TimManager {
    //强制下线
    public static final int STATE_OFFLINE = 901;
    //票据过期
    public static final int STATE_EXPIRED = STATE_OFFLINE + 1;
    //登录出错
    public static final int STATE_LOGIN_ERROR = STATE_EXPIRED + 1;
    //登录成功
    public static final int STATE_LOGIN_SUC = STATE_LOGIN_ERROR + 1;

    private Context mCtx;

    private static TimManager mInstance;

    private TimManager(Context mCtx) {
        this.mCtx = mCtx;
    }

    public static TimManager getInstance(Context mCtx) {
        if (mInstance == null) {
            mInstance = new TimManager(mCtx);
        }
        return mInstance;
    }

    public static TimManager getInstance() {
        return mInstance;
    }

    public void init() {
        QavsdkControl.initQavsdk(mCtx);

        //初始化IM
        //Foreground.init((Application) mCtx);
        if (MsfSdkUtils.isMainProcess(mCtx)) {
            TIMManager.getInstance().setOfflinePushListener(new TIMOfflinePushListener() {
                @Override
                public void handleNotification(TIMOfflinePushNotification notification) {
                    if (notification.getGroupReceiveMsgOpt() == TIMGroupReceiveMessageOpt.ReceiveAndNotify) {
                        //消息被设置为需要提醒
                        notification.doNotify(mCtx, R.mipmap.ic_launcher);
                    }
                }
            });
            //互踢下线逻辑
            TIMManager.getInstance().setUserStatusListener(new TIMUserStatusListener() {
                @Override
                public void onForceOffline() {
                    LogUtil.LogD("IM", "==receive force offline message");
//                    EventBus.getDefault().post(new EntityEvent.IMStateEvent(STATE_OFFLINE));
                }

                @Override
                public void onUserSigExpired() {
                    LogUtil.LogD("IM", "==receive userSigExpired message");
                    //票据过期，需要重新登录
//                    EventBus.getDefault().post(new EntityEvent.IMStateEvent(STATE_EXPIRED));
                }
            });
        }
        //初始化IMSDK
        InitBusiness.start(mCtx, TIMLogLevel.DEBUG);
        //设置刷新监听
        RefreshEvent.getInstance();
    }

    public void loginIM() {
        //登录之前要初始化群和好友关系链缓存
        FriendshipEvent.getInstance().init();
        GroupEvent.getInstance().init();
        LoginBusiness.loginIm(MySelfInfo.getInstance().getId(), MySelfInfo.getInstance().getUserSig(), new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                switch (i) {
                    case 6208:
                        //离线状态下被其他终端踢下线
                        LogUtil.LogD("IM", "==receive login error message");
                        EventBus.getDefault().post(new EntityEvent.IMStateEvent(STATE_LOGIN_ERROR));
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onSuccess() {
                LogUtil.LogD("IM", "==登录IM成功");
                pushSetting();
                //初始化程序后台后消息推送
                PushUtil.getInstance();
                //初始化消息监听
                MessageEvent.getInstance();
                //初始化avsdk
                startAVSDK();
                EventBus.getDefault().post(new EntityEvent.IMStateEvent(STATE_LOGIN_SUC));
            }
        });
    }

    private void pushSetting() {
        TIMOfflinePushSettings settings = new TIMOfflinePushSettings();
        //开启离线推送
        settings.setEnabled(true);
        //设置收到C2C离线消息时的提示声音，这里把声音文件放到了res/raw文件夹下
        //settings.setC2cMsgRemindSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.dudulu));
        //设置收到群离线消息时的提示声音，这里把声音文件放到了res/raw文件夹下
        //settings.setGroupMsgRemindSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.dudulu));
        TIMManager.getInstance().configOfflinePushSettings(settings);
    }

    /**
     * IM登出
     */
    public void logoutIM() {
        TIMManager.getInstance().logout(new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                LogUtil.LogD("IM", "==IMLogout fail ：" + i + " msg " + s);
            }

            @Override
            public void onSuccess() {
                LogUtil.LogD("IM", "==IMLogout succ !");
                exitIM();
                //反向初始化avsdk
                stopAVSDK();
            }
        });
    }

    public void exitIM() {
        //清除本地缓存
        MySelfInfo.getInstance().setId(null);
        MySelfInfo.getInstance().clearCache(mCtx);
        MessageEvent.getInstance().clear();
        FriendshipInfo.getInstance().clear();
        GroupInfo.getInstance().clear();
    }

    /**
     * 初始化AVSDK
     */
    private void startAVSDK() {
        QavsdkControl.getInstance().setAvConfig(IMConstant.SDK_APPID, "" + IMConstant.ACCOUNT_TYPE, MySelfInfo.getInstance().getId(), MySelfInfo.getInstance().getUserSig());
        QavsdkControl.getInstance().startContext();
    }

    /**
     * 反初始化AVADK
     */
    private void stopAVSDK() {
        QavsdkControl.getInstance().stopContext();
    }

    //回话列表
    private List<NomalConversation> conversationList;

    public List<NomalConversation> getConversationList() {
        return conversationList;
    }

    public void setConversationList(List<NomalConversation> conversationList) {
        this.conversationList = conversationList;
    }
}
