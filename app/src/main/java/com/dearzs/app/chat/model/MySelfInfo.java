package com.dearzs.app.chat.model;

import android.content.Context;
import android.content.SharedPreferences;
import com.dearzs.tim.sdk.IMConstant;

/**
 * 自己的状态数据
 */
public class MySelfInfo {
    private static final String TAG = MySelfInfo.class.getSimpleName();
    private String id;
    private String userSig;
    private String nickName;    // 呢称
    private String avatar;      // 头像
    private String sign;      // 签名
    private String CosSig;
    private static boolean isCreateRoom = false;

    private boolean bLiveAnimator;  // 渐隐动画

    private int id_status;

    private int myRoomNum = -1;

    private static MySelfInfo ourInstance = new MySelfInfo();

    public static MySelfInfo getInstance() {

        return ourInstance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserSig() {
        return userSig;
    }

    public void setUserSig(String userSig) {
        this.userSig = userSig;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getMyRoomNum() {
        return myRoomNum;
    }

    public void setMyRoomNum(int myRoomNum) {
        this.myRoomNum = myRoomNum;
    }

    public String getCosSig() {
        return CosSig;
    }

    public void setCosSig(String cosSig) {
        CosSig = cosSig;
    }

    public boolean isbLiveAnimator() {
        return bLiveAnimator;
    }

    public void setbLiveAnimator(boolean bLiveAnimator) {
        this.bLiveAnimator = bLiveAnimator;
    }

    public void writeToCache(Context context) {
        SharedPreferences settings = context.getSharedPreferences(IMConstant.USER_INFO, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(IMConstant.USER_ID, id);
        editor.putString(IMConstant.USER_SIG, userSig);
        editor.putString(IMConstant.USER_NICK, nickName);
        editor.putString(IMConstant.USER_AVATAR, avatar);
        editor.putString(IMConstant.USER_SIGN, sign);
        editor.putInt(IMConstant.USER_ROOM_NUM, myRoomNum);
        editor.putBoolean(IMConstant.LIVE_ANIMATOR, bLiveAnimator);
        editor.commit();
    }

    public void clearCache(Context context) {
        SharedPreferences settings = context.getSharedPreferences(IMConstant.USER_INFO, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.commit();
    }

    public void getCache(Context context) {
        SharedPreferences sharedata = context.getSharedPreferences(IMConstant.USER_INFO, 0);
        id = sharedata.getString(IMConstant.USER_ID, null);
        userSig = sharedata.getString(IMConstant.USER_SIG, null);
        myRoomNum = sharedata.getInt(IMConstant.USER_ROOM_NUM, -1);
        nickName = sharedata.getString(IMConstant.USER_NICK, null);
        avatar = sharedata.getString(IMConstant.USER_AVATAR, null);
        sign = sharedata.getString(IMConstant.USER_SIGN, null);
        bLiveAnimator = sharedata.getBoolean(IMConstant.LIVE_ANIMATOR, false);
    }

    public int getIdStatus() {
        return id_status;
    }

    public void setIdStatus(int id_status) {
        this.id_status = id_status;
    }

    public boolean isCreateRoom() {
        return isCreateRoom;
    }

    public void setJoinRoomWay(boolean isCreateRoom) {
        this.isCreateRoom = isCreateRoom;
    }
}