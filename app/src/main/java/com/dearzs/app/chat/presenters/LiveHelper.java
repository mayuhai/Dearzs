package com.dearzs.app.chat.presenters;


import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.dearzs.app.adapter.LvLiveMemberHeadListAdapter;
import com.dearzs.app.chat.avcontrollers.QavsdkControl;
import com.dearzs.app.chat.model.CurLiveInfo;
import com.dearzs.app.chat.model.MySelfInfo;
import com.dearzs.app.chat.presenters.iml.LiveView;
import com.dearzs.app.chat.presenters.iml.MembersDialogView;
import com.dearzs.commonlib.utils.log.LogUtil;
import com.dearzs.tim.sdk.IMConstant;
import com.tencent.TIMCallBack;
import com.tencent.TIMConversation;
import com.tencent.TIMConversationType;
import com.tencent.TIMCustomElem;
import com.tencent.TIMElem;
import com.tencent.TIMElemType;
import com.tencent.TIMGroupSystemElem;
import com.tencent.TIMGroupSystemElemType;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.tencent.TIMMessageListener;
import com.tencent.TIMTextElem;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;
import com.tencent.av.TIMAvManager;
import com.tencent.av.sdk.AVAudioCtrl;
import com.tencent.av.sdk.AVContext;
import com.tencent.av.sdk.AVEndpoint;
import com.tencent.av.sdk.AVError;
import com.tencent.av.sdk.AVRoomMulti;
import com.tencent.av.sdk.AVVideoCtrl;
import com.tencent.av.sdk.AVView;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * 直播的控制类presenter
 */
public class LiveHelper extends Presenter {
    private LiveView mLiveView;
    private MembersDialogView mMembersDialogView;
    public Context mContext;
    private static final String TAG = LiveHelper.class.getSimpleName();
    private static final int CAMERA_NONE = -1;
    private static final int FRONT_CAMERA = 0;
    private static final int BACK_CAMERA = 1;
    private static final int MAX_REQUEST_VIEW_COUNT = 4; //当前最大支持请求画面个数
    private static final boolean LOCAL = true;
    private static final boolean REMOTE = false;
    private TIMConversation mGroupConversation;
    private TIMConversation mC2CConversation;
    private boolean isMicOpen = false;
    private static final String UNREAD = "0";
    private AVView mRequestViewList[] = new AVView[MAX_REQUEST_VIEW_COUNT];
    private String mRequestIdentifierList[] = new String[MAX_REQUEST_VIEW_COUNT];
    private Boolean isOpenCamera = false;
    private boolean isBakCameraOpen, isBakMicOpen;      // 切后时备份当前camera及mic状态
    private LvLiveMemberHeadListAdapter mMemberHeadListAdapter;


    public LiveHelper(Context context, LiveView liveview, LvLiveMemberHeadListAdapter adapter) {
        mContext = context;
        mLiveView = liveview;
        mMemberHeadListAdapter = adapter;
    }


    private AVVideoCtrl.CameraPreviewChangeCallback mCameraPreviewChangeCallback = new AVVideoCtrl.CameraPreviewChangeCallback() {
        @Override
        public void onCameraPreviewChangeCallback(int cameraId) {
            QavsdkControl.getInstance().setMirror(FRONT_CAMERA == cameraId);
        }
    };

    public void setCameraPreviewChangeCallback() {
        try{
            AVVideoCtrl avVideoCtrl = QavsdkControl.getInstance().getAVContext().getVideoCtrl();
            if (avVideoCtrl != null)
                avVideoCtrl.setCameraPreviewChangeCallback(mCameraPreviewChangeCallback);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 开启摄像头和MIC
     */
    public void openCameraAndMic() {
        openCamera();
        AVAudioCtrl avAudioCtrl = QavsdkControl.getInstance().getAVContext().getAudioCtrl();//开启Mic
        avAudioCtrl.enableMic(true);
        isMicOpen = true;

    }


    /**
     * 打开摄像头
     */
    private void openCamera() {
        if (mIsFrontCamera) {
            enableCamera(FRONT_CAMERA, true);
        } else {
            enableCamera(BACK_CAMERA, true);
        }
    }


    public void closeCameraAndMic() {
        closeCamera();
        closeMic();
    }

    /**
     * 开关摄像头
     */
    public void toggleCamera() {
        if (isOpenCamera) {
            closeCamera();
        } else {
            openCamera();
        }
    }


    /**
     * 开关Mic
     */
    public void toggleMic() {
        if (!isMicOpen) {
            openMic();
        } else {
            muteMic();
        }
    }


    public void closeCamera() {
        if (mIsFrontCamera) {
            enableCamera(FRONT_CAMERA, false);
        } else {
            enableCamera(BACK_CAMERA, false);
        }
    }

    public void closeMic() {
        AVAudioCtrl avAudioCtrl = QavsdkControl.getInstance().getAVContext().getAudioCtrl();//开启Mic
        avAudioCtrl.enableMic(false);
        isMicOpen = false;
    }


    /**
     * 开启摄像头
     *
     * @param camera
     * @param isEnable
     */
    private void enableCamera(final int camera, boolean isEnable) {
        if (isEnable) {
            isOpenCamera = true;
        } else {
            isOpenCamera = false;
        }
        AVVideoCtrl avVideoCtrl = QavsdkControl.getInstance().getAVContext().getVideoCtrl();
        //打开摄像头
        int ret = avVideoCtrl.enableCamera(camera, isEnable, new AVVideoCtrl.EnableCameraCompleteCallback() {
            protected void onComplete(boolean enable, int result) {//开启摄像头回调
                super.onComplete(enable, result);
                if (result == AVError.AV_OK) {//开启成功

                    if (camera == FRONT_CAMERA) {
                        mIsFrontCamera = true;
                    } else {
                        mIsFrontCamera = false;
                    }
                }
            }
        });
    }

    /**
     * AVSDK 请求主播数据
     *
     * @param identifiers 主播ID
     */
    public void requestViewList(ArrayList<String> identifiers) {
        if (identifiers.size() == 0) return;
        AVEndpoint endpoint = ((AVRoomMulti) QavsdkControl.getInstance().getAVContext().getRoom()).getEndpointById(identifiers.get(0));
        if (endpoint != null) {
            ArrayList<String> alreadyIds = QavsdkControl.getInstance().getRemoteVideoIds();//已经存在的IDs
            for (String id : identifiers) {//把新加入的添加到后面
                if (!alreadyIds.contains(id)) {
                    alreadyIds.add(id);
                }
            }
            int viewindex = 0;
            for (String id : alreadyIds) {//一并请求
                if (viewindex >= 4) break;
                AVView view = new AVView();
                view.videoSrcType = AVView.VIDEO_SRC_TYPE_CAMERA;
                view.viewSizeType = AVView.VIEW_SIZE_TYPE_BIG;
                //界面数
                mRequestViewList[viewindex] = view;
                mRequestIdentifierList[viewindex] = id;
                viewindex++;
            }
            int ret = QavsdkControl.getInstance().getAvRoomMulti().requestViewList(mRequestIdentifierList, mRequestViewList, viewindex, mRequestViewListCompleteCallback);

        } else {
            if (null != mContext) {
                Toast.makeText(mContext, "Wrong Room!!!! Live maybe close already!", Toast.LENGTH_SHORT).show();
            }
        }


    }



    /**
     * AVSDK 请求主播数据
     *
     * @param identifiers 主播ID
     */
    public void requestScreenViewList(ArrayList<String> identifiers) {
        if (identifiers.size() == 0) return;
        AVEndpoint endpoint = ((AVRoomMulti) QavsdkControl.getInstance().getAVContext().getRoom()).getEndpointById(identifiers.get(0));
        if (endpoint != null) {
            ArrayList<String> alreadyIds = QavsdkControl.getInstance().getRemoteVideoIds();//已经存在的IDs
            for (String id : identifiers) {//把新加入的添加到后面
                if (!alreadyIds.contains(id)) {
                    alreadyIds.add(id);
                }
            }
            int viewindex = 0;
            for (String id : alreadyIds) {//一并请求
                if (viewindex >= 4) break;
                AVView view = new AVView();
                view.videoSrcType = AVView.VIDEO_SRC_TYPE_SCREEN;
                view.viewSizeType = AVView.VIEW_SIZE_TYPE_BIG;
                //界面数
                mRequestViewList[viewindex] = view;
                mRequestIdentifierList[viewindex] = id;
                viewindex++;
            }
            int ret = QavsdkControl.getInstance().getAvRoomMulti().requestViewList(mRequestIdentifierList, mRequestViewList, viewindex, mRequestViewListCompleteCallback);

        } else {
            if (null != mContext) {
                Toast.makeText(mContext, "Wrong Room!!!! Live maybe close already!", Toast.LENGTH_SHORT).show();
            }
        }


    }


    private AVRoomMulti.RequestViewListCompleteCallback mRequestViewListCompleteCallback = new AVRoomMulti.RequestViewListCompleteCallback() {
        public void OnComplete(String identifierList[], AVView viewList[], int count, int result) {
            String ids = "";

            for (String id : identifierList) {
                if(mMemberHeadListAdapter != null){
                    //所有进入直播的用户更改头像状态
                    mMemberHeadListAdapter.notifyItemData(id, true);
                }
                mLiveView.showVideoView(REMOTE, id);
                ids = ids + " " + id;
            }
        }
    };

    public void sendGroupText(TIMMessage Nmsg) {
        if (mGroupConversation != null)
            mGroupConversation.sendMessage(Nmsg, new TIMValueCallBack<TIMMessage>() {
                @Override
                public void onError(int i, String s) {
                    if (i == 85) { //消息体太长
                        if (null != mContext) {
                            Toast.makeText(mContext, "Text too long ", Toast.LENGTH_SHORT).show();
                        }
                    } else if (i == 6011) {//群主不存在
                        if (null != mContext) {
                            Toast.makeText(mContext, "Host don't exit ", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onSuccess(TIMMessage timMessage) {
                    //发送成回显示消息内容
                    for (int j = 0; j < timMessage.getElementCount(); j++) {
                        TIMElem elem = (TIMElem) timMessage.getElement(0);
                        if (timMessage.isSelf()) {
                            handleTextMessage(elem, MySelfInfo.getInstance().getNickName());
                        } else {
                            TIMUserProfile sendUser = timMessage.getSenderProfile();
                            String name;
                            if (sendUser != null) {
                                name = sendUser.getNickName();
                            } else {
                                name = timMessage.getSender();
                            }
                            //String sendId = timMessage.getSender();
                            handleTextMessage(elem, name);
                        }
                    }
                }
            });
    }

    public void sendGroupMessage(int cmd, String param, TIMValueCallBack<TIMMessage> callback) {
        JSONObject inviteCmd = new JSONObject();
        try {
            inviteCmd.put(IMConstant.CMD_KEY, cmd);
            inviteCmd.put(IMConstant.CMD_PARAM, param);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String cmds = inviteCmd.toString();
        TIMMessage Gmsg = new TIMMessage();
        TIMCustomElem elem = new TIMCustomElem();
        elem.setData(cmds.getBytes());
        elem.setDesc("");
        Gmsg.addElement(elem);

        if (mGroupConversation != null)
            mGroupConversation.sendMessage(Gmsg, callback);
    }

    public void sendGroupMessage(int cmd, String param) {
        sendGroupMessage(cmd, param, new TIMValueCallBack<TIMMessage>() {
            @Override
            public void onError(int i, String s) {
                if (i == 85) { //消息体太长
                    if (null != mContext) {
                        Toast.makeText(mContext, "Text too long ", Toast.LENGTH_SHORT).show();
                    }
                } else if (i == 6011) {//群主不存在
                    if (null != mContext) {
                        Toast.makeText(mContext, "Host don't exit ", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onSuccess(TIMMessage timMessage) {
            }
        });
    }

    /**
     * 初始化聊天室  设置监听器
     */
    public void initTIMListener(String chatRoomId) {
        mGroupConversation = TIMManager.getInstance().getConversation(TIMConversationType.Group, chatRoomId);
        TIMManager.getInstance().addMessageListener(msgListener);
        mC2CConversation = TIMManager.getInstance().getConversation(TIMConversationType.C2C, chatRoomId);
    }

    /**
     * 已经发完退出消息了
     */
    private void notifyQuitReady() {
        TIMManager.getInstance().removeMessageListener(msgListener);
        if (mLiveView != null)
            mLiveView.readyToQuit();
    }

    public void perpareQuitRoom(boolean bPurpose) {
        if (bPurpose) {
            sendGroupMessage(IMConstant.AVIMCMD_ExitLive, "", new TIMValueCallBack<TIMMessage>() {
                @Override
                public void onError(int i, String s) {
                    notifyQuitReady();
                }

                @Override
                public void onSuccess(TIMMessage timMessage) {
                    notifyQuitReady();
                }
            });
        } else {
            notifyQuitReady();
        }
    }

    public void pause() {
        isBakCameraOpen = isOpenCamera;
        isBakMicOpen = isMicOpen;
        if (isBakCameraOpen || isBakMicOpen) {    // 若摄像头或Mic打开
            sendGroupMessage(IMConstant.AVIMCMD_Host_Leave, "", new TIMValueCallBack<TIMMessage>() {
                @Override
                public void onError(int i, String s) {
                }

                @Override
                public void onSuccess(TIMMessage timMessage) {
                }
            });
            closeCameraAndMic();
        }
    }

    public void resume() {
        if (isBakCameraOpen || isBakMicOpen) {
            sendGroupMessage(IMConstant.AVIMCMD_Host_Back, "", new TIMValueCallBack<TIMMessage>() {
                @Override
                public void onError(int i, String s) {
                }

                @Override
                public void onSuccess(TIMMessage timMessage) {

                }
            });

            if (isBakCameraOpen) {
                openCamera();
            }
            if (isBakMicOpen) {
                openMic();
            }
        }
    }


    /**
     * 群消息回调
     */
    private TIMMessageListener msgListener = new TIMMessageListener() {
        @Override
        public boolean onNewMessages(List<TIMMessage> list) {
            //SxbLog.d(TAG, "onNewMessages readMessage " + list.size());
            //解析TIM推送消息
            parseIMMessage(list);
            return false;
        }
    };

    /**
     * 解析消息回调
     *
     * @param list 消息列表
     */
    private void parseIMMessage(List<TIMMessage> list) {
        List<TIMMessage> tlist = list;


        if (tlist.size() > 0) {
            if (mGroupConversation != null)
                mGroupConversation.setReadMessage(tlist.get(0));
        }
//        if (!bNeverLoadMore && (tlist.size() < mLoadMsgNum))
//            bMore = false;

        for (int i = tlist.size() - 1; i >= 0; i--) {
            TIMMessage currMsg = tlist.get(i);
//


            for (int j = 0; j < currMsg.getElementCount(); j++) {
                if (currMsg.getElement(j) == null)
                    continue;
                TIMElem elem = currMsg.getElement(j);
                TIMElemType type = elem.getType();
                String sendId = currMsg.getSender();

                //系统消息
                if (type == TIMElemType.GroupSystem) {
                    if (TIMGroupSystemElemType.TIM_GROUP_SYSTEM_DELETE_GROUP_TYPE == ((TIMGroupSystemElem) elem).getSubtype()) {
                        if (null != mContext) {
                            mContext.sendBroadcast(new Intent(IMConstant.ACTION_HOST_LEAVE));
                        }
                    }

                }
                //定制消息
                if (type == TIMElemType.Custom) {
                    String id, nickname;
                    if (currMsg.getSenderProfile() != null) {
                        id = currMsg.getSenderProfile().getIdentifier();
                        nickname = currMsg.getSenderProfile().getNickName();
                    } else {
                        id = sendId;
                        nickname = sendId;
                    }
                    handleCustomMsg(elem, id, nickname);
                    continue;
                }

                //其他群消息过滤

                if (currMsg.getConversation() != null && currMsg.getConversation().getPeer() != null)
                    if (!CurLiveInfo.getChatRoomId().equals(currMsg.getConversation().getPeer())) {
                        continue;
                    }

                //最后处理文本消息
                if (type == TIMElemType.Text) {
                    if (currMsg.isSelf()) {
                        handleTextMessage(elem, MySelfInfo.getInstance().getNickName());
                    } else {
                        String nickname;
                        if (currMsg.getSenderProfile() != null && (!currMsg.getSenderProfile().getNickName().equals(""))) {
                            nickname = currMsg.getSenderProfile().getNickName();
                        } else {
                            nickname = sendId;
                        }
                        handleTextMessage(elem, nickname);
                    }
                }
            }
        }
    }

    /**
     * 处理文本消息解析
     *
     * @param elem
     * @param name
     */
    private void handleTextMessage(TIMElem elem, String name) {
        TIMTextElem textElem = (TIMTextElem) elem;
//        Toast.makeText(mContext, "" + textElem.getText(), Toast.LENGTH_SHORT).show();

        mLiveView.refreshText(textElem.getText(), name);
//        sendToUIThread(REFRESH_TEXT, textElem.getText(), sendId);
    }


    /**
     * 处理定制消息 赞 关注 取消关注
     *
     * @param elem
     */
    private void handleCustomMsg(TIMElem elem, String identifier, String nickname) {
        try {
            String customText = new String(((TIMCustomElem) elem).getData(), "UTF-8");

            JSONTokener jsonParser = new JSONTokener(customText);
            // 此时还未读取任何json文本，直接读取就是一个JSONObject对象。
            // 如果此时的读取位置在"name" : 了，那么nextValue就是"yuanzhifei89"（String）
            JSONObject json = (JSONObject) jsonParser.nextValue();
            String userId = json.getString(IMConstant.CMD_PARAM);
            int action = json.getInt(IMConstant.CMD_KEY);
            switch (action) {
                case IMConstant.AVIMCMD_MUlTI_HOST_INVITE:
                    if (mLiveView != null)
                        mLiveView.showInviteDialog();
                    break;
                case IMConstant.AVIMCMD_MUlTI_JOIN:
                    Log.i(TAG, "handleCustomMsg " + identifier);
                    if (null != mLiveView)
                        mLiveView.cancelInviteView(identifier);
                    if(mMemberHeadListAdapter != null){
                        mMemberHeadListAdapter.notifyItemData(identifier, true);
                    }
                    break;
                case IMConstant.AVIMCMD_MUlTI_REFUSE:
                    if (null != mLiveView)
                        mLiveView.cancelInviteView(identifier);
                    if (null != mContext) {
                        Toast.makeText(mContext, identifier + " refuse !", Toast.LENGTH_SHORT).show();
                    }
                    if(mMemberHeadListAdapter != null){
                        mMemberHeadListAdapter.notifyItemData(identifier, false);
                    }
                    break;
                case IMConstant.AVIMCMD_Praise:
                    if (null != mLiveView)
                        mLiveView.refreshThumbUp();
                    break;
                case IMConstant.AVIMCMD_EnterLive:
                    //mLiveView.refreshText("Step in live", sendId);
                    if (mLiveView != null)
                        mLiveView.memberJoin(identifier, nickname);
                    break;
                case IMConstant.AVIMCMD_ExitLive:
                    //mLiveView.refreshText("quite live", sendId);
                    if (mLiveView != null)
                        mLiveView.memberQuit(identifier, nickname);
                    break;
                case IMConstant.AVIMCMD_MULTI_CANCEL_INTERACT://主播关闭摄像头命令
                    //如果是自己关闭Camera和Mic
                    String closeId = json.getString(IMConstant.CMD_PARAM);
                    if (closeId.equals(MySelfInfo.getInstance().getId())) {//是自己
                        //TODO 被动下麦 下麦 下麦
                        changeAuthandRole(false, IMConstant.NORMAL_MEMBER_AUTH, IMConstant.NORMAL_MEMBER_ROLE);

                    }
                    //其他人关闭小窗口
                    QavsdkControl.getInstance().closeMemberView(closeId);
                    if (mLiveView != null) {
                        mLiveView.hideInviteDialog();
                        mLiveView.refreshUI(closeId);
                    }
                    if(mMemberHeadListAdapter != null && !TextUtils.isEmpty(userId)){
                        mMemberHeadListAdapter.notifyItemData(userId, false);
                    }

                    break;
                case IMConstant.AVIMCMD_MULTI_HOST_CANCELINVITE:
                    if (null != mLiveView)
                        mLiveView.hideInviteDialog();
                    break;
                case IMConstant.AVIMCMD_MULTI_HOST_CONTROLL_CAMERA:
                    toggleCamera();
                    break;
                case IMConstant.AVIMCMD_MULTI_HOST_CONTROLL_MIC:
                    toggleMic();
                    break;
                case IMConstant.AVIMCMD_Host_Leave:
                    if (mLiveView != null)
                        mLiveView.hostLeave(identifier, nickname);
                    break;
                case IMConstant.AVIMCMD_Host_Back:
                    if (mLiveView != null)
                        mLiveView.hostBack(identifier, nickname);
                default:
                    break;
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException ex) {
            // 异常处理代码
        }
    }


    public boolean isFrontCamera() {
        return mIsFrontCamera;
    }

    private boolean mIsFrontCamera = true;

    /**
     * 转换前后摄像头
     *
     * @return
     */
    public int switchCamera() {
        AVVideoCtrl avVideoCtrl = QavsdkControl.getInstance().getAVContext().getVideoCtrl();
        int result = avVideoCtrl.switchCamera(mIsFrontCamera ? BACK_CAMERA : FRONT_CAMERA, mSwitchCameraCompleteCallback);
        return result;
    }


    /**
     * 装换摄像头回调
     */
    private AVVideoCtrl.SwitchCameraCompleteCallback mSwitchCameraCompleteCallback = new AVVideoCtrl.SwitchCameraCompleteCallback() {
        protected void onComplete(int cameraId, int result) {
            super.onComplete(cameraId, result);

            if (result == AVError.AV_OK) {
                mIsFrontCamera = !mIsFrontCamera;
            }
        }
    };

    public boolean isMicOpen() {
        return isMicOpen;
    }

    /**
     * 开启Mic
     */
    public void openMic() {
        AVAudioCtrl avAudioCtrl = QavsdkControl.getInstance().getAVContext().getAudioCtrl();//开启Mic
        avAudioCtrl.enableMic(true);
        isMicOpen = true;
    }

    /**
     * 关闭Mic
     */
    public void muteMic() {
        AVAudioCtrl avAudioCtrl = QavsdkControl.getInstance().getAVContext().getAudioCtrl();//关闭Mic
        avAudioCtrl.enableMic(false);
        isMicOpen = false;
    }


    /**
     * 开关闪光灯
     */
    private boolean flashLgihtStatus = false;

    public void toggleFlashLight() {
        AVVideoCtrl videoCtrl = QavsdkControl.getInstance().getAVContext().getVideoCtrl();
        if (null == videoCtrl) {
            return;
        }

        final Object cam = videoCtrl.getCamera();
        if ((cam == null) || (!(cam instanceof Camera))) {
            return;
        }
        final Camera.Parameters camParam = ((Camera) cam).getParameters();
        if (null == camParam) {
            return;
        }

        Object camHandler = videoCtrl.getCameraHandler();
        if ((camHandler == null) || (!(camHandler instanceof Handler))) {
            return;
        }

        //对摄像头的操作放在摄像头线程
        if (flashLgihtStatus == false) {
            ((Handler) camHandler).post(new Runnable() {
                public void run() {
                    try {
                        camParam.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                        ((Camera) cam).setParameters(camParam);
                        flashLgihtStatus = true;
                    } catch (RuntimeException e) {
                    }
                }
            });
        } else {
            ((Handler) camHandler).post(new Runnable() {
                public void run() {
                    try {
                        camParam.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        ((Camera) cam).setParameters(camParam);
                        flashLgihtStatus = false;
                    } catch (RuntimeException e) {
                    }

                }
            });
        }
    }


    public void sendC2CMessage(final int cmd, String Param, final String sendId) {
        JSONObject inviteCmd = new JSONObject();
        try {
            inviteCmd.put(IMConstant.CMD_KEY, cmd);
            inviteCmd.put(IMConstant.CMD_PARAM, Param);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String cmds = inviteCmd.toString();
        TIMMessage msg = new TIMMessage();
        TIMCustomElem elem = new TIMCustomElem();
        elem.setData(cmds.getBytes());
        elem.setDesc("");
        msg.addElement(elem);
        mC2CConversation = TIMManager.getInstance().getConversation(TIMConversationType.C2C, sendId);
        mC2CConversation.sendMessage(msg, new TIMValueCallBack<TIMMessage>() {
            @Override
            public void onError(int i, String s) {
            }

            @Override
            public void onSuccess(TIMMessage timMessage) {
                LogUtil.LogE(timMessage.toString());
            }
        });
    }


    private TIMAvManager.RoomInfo roomInfo;
    private long streamChannelID;

    public void pushAction(TIMAvManager.StreamParam mStreamParam) {
        int roomid = (int) QavsdkControl.getInstance().getAVContext().getRoom().getRoomId();
        roomInfo = TIMAvManager.getInstance().new RoomInfo();
        roomInfo.setRoomId(roomid);
        roomInfo.setRelationId(CurLiveInfo.getRoomNum());
        //推流的接口
        if (TIMAvManager.getInstance() != null) {
            TIMAvManager.getInstance().requestMultiVideoStreamerStart(roomInfo, mStreamParam, new TIMValueCallBack<TIMAvManager.StreamRes>() {
                @Override
                public void onError(int i, String s) {
                    if (null != mContext) {
                        Toast.makeText(mContext, "start stream error,try again " + i + " : " + s, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onSuccess(TIMAvManager.StreamRes streamRes) {
                    List<TIMAvManager.LiveUrl> liveUrls = streamRes.getUrls();
                    streamChannelID = streamRes.getChnlId();
                    mLiveView.pushStreamSucc(streamRes);

//                ClipToBoard(url, url2);

                }
            });
        }
    }

    public void stopPushAction() {
        List<Long> myList = new ArrayList<Long>();
        myList.add(streamChannelID);
        TIMAvManager.getInstance().requestMultiVideoStreamerStop(roomInfo, myList, new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                if (null != mContext) {
                    Toast.makeText(mContext, "stop stream error,try again " + i + " : " + s, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onSuccess() {
                if (null != mLiveView) {
                    mLiveView.stopStreamSucc();
                }
            }
        });
    }


    public void startRecord(TIMAvManager.RecordParam mRecordParam) {

        TIMAvManager.RoomInfo roomInfo = TIMAvManager.getInstance().new RoomInfo();
        roomInfo.setRelationId(CurLiveInfo.getRoomNum());
        roomInfo.setRoomId(CurLiveInfo.getRoomNum());

        TIMAvManager.getInstance().requestMultiVideoRecorderStart(roomInfo, mRecordParam, new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                mLiveView.startRecordCallback(false);
            }

            @Override
            public void onSuccess() {
                mLiveView.startRecordCallback(true);
            }
        });

    }


    public void stopRecord() {
        TIMAvManager.RoomInfo roomInfo = TIMAvManager.getInstance().new RoomInfo();
        roomInfo.setRelationId(CurLiveInfo.getRoomNum());
        roomInfo.setRoomId(CurLiveInfo.getRoomNum());
        TIMAvManager.getInstance().requestMultiVideoRecorderStop(roomInfo, new TIMValueCallBack<List<String>>() {
            @Override
            public void onError(int i, String s) {
                mLiveView.stopRecordCallback(false, null);
            }

            @Override
            public void onSuccess(List<String> files) {
                mLiveView.stopRecordCallback(true, files);


            }
        });
    }


    @Override
    public void onDestory() {
        mLiveView = null;
        mContext = null;
    }


    /**
     * 改变角色和权限 最终会控制自己Camera和Mic
     *
     * @param leverChange true代表上麦 false 代表下麦
     * @param auth_bits   权限字段
     * @param role        角色字段
     */
    public void changeAuthandRole(final boolean leverChange, long auth_bits, final String role) {
        changeAuthority(auth_bits, null, new AVRoomMulti.ChangeAuthorityCallback() {
            protected void onChangeAuthority(int retCode) {
                if (retCode == AVError.AV_OK) {
                    changeRole(role, leverChange);
                }

            }
        });
    }


    /**
     * 改变权限
     *
     * @param auth_bits   权限
     * @param auth_buffer 密钥
     * @param callback
     * @return
     */
    private boolean changeAuthority(long auth_bits, byte[] auth_buffer, AVRoomMulti.ChangeAuthorityCallback callback) {
        QavsdkControl qavsdk = QavsdkControl.getInstance();
        AVContext avContext = qavsdk.getAVContext();
        AVRoomMulti room = (AVRoomMulti) avContext.getRoom();
        if (null == room) {
            return false;
        }
        if (auth_buffer != null) {
            return room.changeAuthority(auth_bits, auth_buffer, auth_buffer.length, callback);
        } else {
            return room.changeAuthority(auth_bits, null, 0, callback);
        }
    }


    /**
     * 改变角色
     *
     * @param role 角色名
     */
    public void changeRole(final String role, final boolean leverupper) {
        ((AVRoomMulti) (QavsdkControl.getInstance().getAvRoomMulti())).changeAVControlRole(role, new AVRoomMulti.ChangeAVControlRoleCompleteCallback() {
                    @Override
                    public void OnComplete(int arg0) {
                        if (arg0 == AVError.AV_OK) {
                            if (leverupper == true) {
                                openCameraAndMic();//打开摄像头
                                sendC2CMessage(IMConstant.AVIMCMD_MUlTI_JOIN, "", CurLiveInfo.getHostID());//发送回应消息
                            } else {
                                closeCameraAndMic();
                            }
                            if (null != mContext) {
                                Toast.makeText(mContext, "change to VideoMember succ !", Toast.LENGTH_SHORT);
                            }
                        } else {
                            if (null != mContext) {
                                Toast.makeText(mContext, "change to VideoMember failed", Toast.LENGTH_SHORT);
                            }
                        }
                    }
                }

        );
    }


    public void autoFocuse() {
        AVVideoCtrl videoCtrl = QavsdkControl.getInstance().getAVContext().getVideoCtrl();
        if (null == videoCtrl) {
            return;
        }
        Camera cam = (Camera) videoCtrl.getCamera();
        Camera.Parameters parameters = cam.getParameters();
        parameters = (Camera.Parameters) videoCtrl.getCameraPara();
        List<String> focusModes = parameters.getSupportedFocusModes();
        if (focusModes != null && focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        } else {

            return;
        }
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);//1连续对焦
        cam.setParameters(parameters);
        cam.cancelAutoFocus();// 2如果要实现连续的自动对焦，这一句必须加上
//        initCamera();//实现相机的参数初始化
        if ((cam == null) || (!(cam instanceof Camera))) {
            return;
        } else {
            cam.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    if (success) {
//                        initCamera();//实现相机的参数初始化
                        camera.cancelAutoFocus();//只有加上了这一句，才会自动对焦。
                    }
                }

            });
        }
    }
}
