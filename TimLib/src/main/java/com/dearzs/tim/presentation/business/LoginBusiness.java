package com.dearzs.tim.presentation.business;

import com.dearzs.tim.sdk.IMConstant;
import com.tencent.TIMCallBack;
import com.tencent.TIMManager;
import com.tencent.TIMUser;

/**
 * 登录
 */
public class LoginBusiness {

    private static final String TAG = "LoginBusiness";

    private LoginBusiness(){}



    /**
     * 登录imsdk
     *
     * @param identify 用户id
     * @param userSig 用户签名
     * @param callBack 登录后回调
     */
    public static void loginIm(String identify, String userSig, TIMCallBack callBack){
        if (identify == null || userSig == null) return;
        TIMUser user = new TIMUser();
        user.setAccountType(String.valueOf(IMConstant.ACCOUNT_TYPE));
        user.setAppIdAt3rd(String.valueOf(IMConstant.SDK_APPID));
        user.setIdentifier(identify);
        //发起登录请求
        TIMManager.getInstance().login(
                IMConstant.SDK_APPID,
                user,
                userSig,                    //用户帐号签名，由私钥加密获得，具体请参考文档
                callBack);
    }

    /**
     * 登出imsdk
     *
     * @param callBack 登出后回调
     */
    public static void logout(TIMCallBack callBack){
        TIMManager.getInstance().logout(callBack);
    }
}
