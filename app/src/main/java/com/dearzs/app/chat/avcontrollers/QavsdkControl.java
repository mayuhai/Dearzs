package com.dearzs.app.chat.avcontrollers;

import android.content.Context;
import android.view.View;

import com.dearzs.tim.sdk.IMConstant;
import com.tencent.av.sdk.AVAudioCtrl;
import com.tencent.av.sdk.AVContext;
import com.tencent.av.sdk.AVRoomMulti;
import com.tencent.av.sdk.AVVideoCtrl;
import com.tencent.av.sdk.AVView;

import java.util.ArrayList;

/**
 * AVSDK 总控制器类
 */
public class QavsdkControl {
    private static final String TAG = "QavsdkControl";
    private AVContextControl mAVContextControl = null;
    private AVUIControl mAVUIControl = null;
    /* 持有私有静态实例，防止被引用，此处赋值为null，目的是实现延迟加载 */
    private static QavsdkControl instance = null;
    private static Context mContext;
    private static AVRoomMulti avRoomMulti;

    public interface onSlideListener {
        void onSlideUp();

        void onSlideDown();
    }

    public static QavsdkControl getInstance() {
        if (instance == null) {
            instance = new QavsdkControl(mContext);
        }
        return instance;
    }


    public ArrayList<String> getRemoteVideoIds() {
        return remoteVideoIds;
    }

    private ArrayList<String> remoteVideoIds = new ArrayList<String>();


    public static void initQavsdk(Context context) {
        mContext = context;
    }

    private QavsdkControl(Context context) {
        mAVContextControl = new AVContextControl(context);
    }


    public void addRemoteVideoMembers(String id) {
        remoteVideoIds.add(id);
    }

    public void removeRemoteVideoMembers(String id) {
        if (remoteVideoIds.contains(id))
            remoteVideoIds.remove(id);
    }

    public void clearVideoMembers() {
        remoteVideoIds.clear();
    }


    /**
     * 启动SDK系统
     */
    public int startContext() {
        if (mAVContextControl == null)
            return IMConstant.DEMO_ERROR_NULL_POINTER;
        return mAVContextControl.startContext();
    }

    /**
     * 设置AVSDK参数
     *
     * @param appid
     * @param accountype
     * @param identifier
     * @param usersig
     */
    public void setAvConfig(int appid, String accountype, String identifier, String usersig) {
        if (mAVContextControl == null)
            return;
        mAVContextControl.setAVConfig(appid, accountype, identifier, usersig);
    }

    /**
     * 关闭SDK系统
     */
    public void stopContext() {
        if (mAVContextControl != null) {
            mAVContextControl.stopContext();
        }
    }

    public boolean hasAVContext() {
        if (mAVContextControl == null)
            return false;
        return mAVContextControl.hasAVContext();
    }

    public String getSelfIdentifier() {
        if (mAVContextControl == null)
            return null;
        return mAVContextControl.getSelfIdentifier();
    }

//    /**
//     * 创建房间
//     *
//     * @param relationId 讨论组号
//     */
//    public void enterRoom(int relationId, String roomRole, boolean isAutoCreateSDKRoom) {
//        if (mAVRoomControl != null) {
//            mAVRoomControl.enterRoom(relationId, roomRole, isAutoCreateSDKRoom);
//        }
//    }
//
//    public void setAudioCat(int audioCat) {
//        if (mAVRoomControl != null) {
//            mAVRoomControl.setAudioCat(audioCat);
//        }
//    }
//
//    /**
//     * 关闭房间
//     */
//    public int exitRoom() {
//        if (mAVRoomControl == null)
//            return AvConstants.DEMO_ERROR_NULL_POINTER;
//        return mAVRoomControl.exitRoom();
//    }
//
//    /**
//     * 获取成员列表
//     *
//     * @return 成员列表
//     */
//    public ArrayList<AvMemberInfo> getMemberList() {
//        if (mAVRoomControl == null) {
//            return null;
//        }
//        return mAVRoomControl.getMemberList();
//    }
//
//
//    public ArrayList<AvMemberInfo> getScreenMemberList() {
//        if (mAVRoomControl == null) {
//            return null;
//        }
//        return mAVRoomControl.getScreenMemberList();
//    }

//    public AVRoom getRoom() {
//        AVContext avContext = getAVContext();
//
//        return avContext != null ? avContext.getRoom() : null;
//    }

    public boolean getIsInStartContext() {
        if (mAVContextControl == null)
            return false;

        return mAVContextControl.getIsInStartContext();
    }

    public boolean getIsInStopContext() {
        if (mAVContextControl == null)
            return false;

        return mAVContextControl.getIsInStopContext();
    }

    public void setMirror(boolean isMirror) {
        if (null != mAVUIControl) {
            mAVUIControl.setMirror(isMirror, getSelfIdentifier());
        }
    }

//    public void setCameraPreviewChangeCallback() {
//        mAVVideoControl.setCameraPreviewChangeCallback();
//    }
//
//
//    public boolean setIsInStopContext(boolean isInStopContext) {
//        if (mAVContextControl == null)
//            return false;
//
//        return mAVContextControl.setIsInStopContext(isInStopContext);
//    }
//
//    public boolean getIsInEnterRoom() {
//        if (mAVRoomControl == null)
//            return false;
//        return mAVRoomControl.getIsInEnterRoom();
//    }
//
//    public boolean getIsInCloseRoom() {
//        if (mAVRoomControl == null)
//            return false;
//        return mAVRoomControl.getIsInCloseRoom();
//    }

    public AVContext getAVContext() {
        if (mAVContextControl == null)
            return null;
        return mAVContextControl.getAVContext();
    }

    public void setAvRoomMulti(AVRoomMulti room) {
        avRoomMulti = room;
    }

    public AVRoomMulti getAvRoomMulti() {
        return avRoomMulti;
    }


    public void setRemoteHasVideo(String identifier, int videoSrcType, boolean isRemoteHasVideo) {
        if (null != mAVUIControl) {
            mAVUIControl.setRemoteHasVideo(identifier, videoSrcType, isRemoteHasVideo, false, false);
        }
    }


    /**
     * 初始化UI层
     *
     * @param context
     * @param view    AVSDK UILayer层
     */
    public void initAvUILayer(Context context, View view) {
        mAVUIControl = new AVUIControl(context, view);
//        mAVVideoControl.initAVVideoSettings();
//        mAVAudioControl.initAVAudioSettings();
//		mAVEndpointControl.initMembersUI((MultiVideoMembersControlUI) contentView.findViewById(R.id.qav_gaudio_gridlayout));
    }

    public void setSlideListener(onSlideListener listener) {
        if (null != mAVUIControl) {
            mAVUIControl.setSlideLisenter(listener);
        }
    }

    public void clearVideoData() {
        if (null != mAVUIControl) {
            mAVUIControl.clearVideoData();
        }
    }

    public void onResume() {
        if (mAVUIControl != null) {
            mAVUIControl.onResume();
        }
    }

    public void onPause() {
        if (null != mAVUIControl) {
            mAVUIControl.onPause();
        }
    }

    public void onDestroy() {
        if (null != mAVUIControl) {
            mAVUIControl.onDestroy();
            mAVUIControl = null;
        }
    }


    public void setLocalHasVideo(boolean isLocalHasVideo, String selfIdentifier) {
        if (null != mAVUIControl) {
            mAVUIControl.setLocalHasVideo(isLocalHasVideo, false, selfIdentifier);
        }
    }

    public void setRemoteHasVideo(boolean isRemoteHasVideo, String identifier, int videoSrcType) {
        if (null != mAVUIControl) {
            mAVUIControl.setSmallVideoViewLayout(isRemoteHasVideo, identifier, videoSrcType);
        }
    }

    public void setSelfId(String key) {
        if (null != mAVUIControl) {

            mAVUIControl.setSelfId(key);
        }
    }

//    public int toggleEnableCamera() {
//        return mAVVideoControl.toggleEnableCamera();
//    }
//
//    public int toggleSwitchCamera() {
//        return mAVVideoControl.toggleSwitchCamera();
//    }
//
//    public boolean enableUserRender(boolean isEnable) {
//        if (isEnable) {
//            if (mAVVideoControl == null) {
//                return false;
//            }
//            return mAVVideoControl.StartRecordingVideo();
//        } else {
//            if (null == mAVUIControl) {
//                return false;
//            }
//            mAVUIControl.enableDefaultRender();
//        }
//
//        return true;
//    }
//
//    public boolean enableLocalPreProcess(boolean isEnable) {
//        if (mAVVideoControl == null)
//            return false;
//
//        return mAVVideoControl.enableLocalPreProcess(isEnable);
//    }
//
//    public void setIsOpenBackCameraFirst(boolean _isOpenBackCameraFirst) {
//        mAVVideoControl.setIsOpenBackCameraFirst(_isOpenBackCameraFirst);
//    }
//
//    public boolean getIsInOnOffCamera() {
//        return mAVVideoControl.getIsInOnOffCamera();
//    }
//
//    public boolean getIsInOnOffExternalCapture() {
//        return mAVVideoControl.getIsInOnOffExternalCapture();
//    }
//
//
//    public boolean getIsInSwitchCamera() {
//        return mAVVideoControl.getIsInSwitchCamera();
//    }
//
//    public void setIsInSwitchCamera(boolean isInSwitchCamera) {
//        mAVVideoControl.setIsInSwitchCamera(isInSwitchCamera);
//    }
//
//    public boolean getIsEnableCamera() {
//        return mAVVideoControl.getIsEnableCamera();
//    }
//
//    public void setIsInOnOffCamera(boolean isInOnOffCamera) {
//        mAVVideoControl.setIsInOnOffCamera(isInOnOffCamera);
//    }
//
//    public void setIsOnOffExternalCapture(boolean isOnOffExternalCapture) {
//        mAVVideoControl.setIsOnOffExternalCapture(isOnOffExternalCapture);
//    }
//
//    public boolean getIsFrontCamera() {
//        return mAVVideoControl.getIsFrontCamera();
//    }
//
//    public boolean getIsEnableExternalCapture() {
//        return mAVVideoControl.getIsEnableExternalCapture();
//    }
//
//    public void onMemberChange() {
//        if (mAVUIControl != null) {
//            mAVUIControl.onMemberChange();
//        }
//    }
//
//    public boolean getHandfreeChecked() {
//        return mAVAudioControl.getHandfreeChecked();
//    }
//
//
//    public AVVideoControl getAVVideoControl() {
//        return mAVVideoControl;
//    }
//
//    public AVAudioControl getAVAudioControl() {
//        return mAVAudioControl;
//    }

    public void setRotation(int rotation) {
        if (mAVUIControl != null) {
            mAVUIControl.setRotation(rotation);
        }
    }

//    public String getQualityTips() {
//        if (null != mAVUIControl) {
//            return mAVUIControl.getQualityTips();
//        } else {
//            return null;
//        }
//    }

//
//    public void setCreateRoomStatus(boolean status) {
//        if (mAVRoomControl != null) {
//            mAVRoomControl.setCreateRoomStatus(status);
//        }
//    }
//
//    public void setCloseRoomStatus(boolean status) {
//        if (mAVRoomControl != null) {
//            mAVRoomControl.setCloseRoomStatus(status);
//        }
//    }
//
//    public int enableExternalCapture(boolean isEnable) {
//        return mAVVideoControl.enableExternalCapture(isEnable);
//    }
//
//    public void setNetType(int netType) {
//        if (mAVRoomControl == null) return;
//        mAVRoomControl.setNetType(netType);
//    }
//
//    public boolean changeAuthority(long auth_bits, byte[] auth_buffer, AVRoomMulti.ChangeAuthorityCallback callback) {
//        return mAVRoomControl.changeAuthority(auth_bits, auth_buffer, callback);
//    }


    public int getAvailableViewIndex(int start) {
        if (null != mAVUIControl) {
            return mAVUIControl.getIdleViewIndex(start);
        }
        return -1;
    }

    public void closeMemberView(String id) {
        if (null != mAVUIControl) {
            removeRemoteVideoMembers(id);
            mAVUIControl.closeMemberVideoView(id);
        }
    }

    public boolean containIdView(String id) {
        if (null != mAVUIControl) {
            if (mAVUIControl.getViewIndexById(id, AVView.VIDEO_SRC_TYPE_CAMERA) != -1)
                return true;
        }
        return false;
    }

    public String getAudioQualityTips() {
        AVAudioCtrl avAudioCtrl;
        if (QavsdkControl.getInstance() != null && QavsdkControl.getInstance().getAVContext() != null) {
            avAudioCtrl = getAVContext().getAudioCtrl();
            return avAudioCtrl.getQualityTips();
        }

        return "";
    }

    public String getVideoQualityTips() {
        if (QavsdkControl.getInstance() != null) {
            AVVideoCtrl avVideoCtrl = QavsdkControl.getInstance().getAVContext().getVideoCtrl();
            return avVideoCtrl.getQualityTips();
        }
        return "";
    }


    public String getQualityTips() {
        QavsdkControl qavsdk = QavsdkControl.getInstance();
        String audioQos = "";
        String videoQos = "";
        String roomQos = "";

        if (qavsdk != null) {
            audioQos = getAudioQualityTips();

            videoQos = getVideoQualityTips();

            roomQos = getAvRoomMulti().getQualityTips();
        }

        if (audioQos != null && videoQos != null && roomQos != null) {
            return audioQos + videoQos + roomQos;
        } else {
            return "";
        }
    }
}