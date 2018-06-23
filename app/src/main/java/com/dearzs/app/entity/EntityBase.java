package com.dearzs.app.entity;

import android.text.TextUtils;

import com.dearzs.app.util.ToastUtil;

import java.io.Serializable;

public class EntityBase implements Serializable{
//    000000成功
//    100001	失败
//    100000	其他错误
//    100099	参数错误
//    100098	参数为空
//    100097  tokenId已过期

    private static final int DEFAULT_CODE = -1;
    private static final int ERROR_CODE = -999;
    private static final int SUCCESS_CODE = 000000;
    private static final int TOKEN_ERROR_CODE = 100097;

    private String msg = "";
    private String code = "";

    public String getMsg() {
        return msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isSuccess() {
        int code = DEFAULT_CODE;
        if (!TextUtils.isEmpty(this.code) && !"null".equals(this.code)) {
            try {
                code = Integer.parseInt(this.code);
            } catch (NumberFormatException e) {
                code = DEFAULT_CODE;
            }
        }else{
            code = ERROR_CODE;
        }

        if (code == SUCCESS_CODE) {
            return true;
        }else if (code == TOKEN_ERROR_CODE) {
            return false;
        } else if (code > SUCCESS_CODE || code == DEFAULT_CODE) {
            return false;
        } else {
            ToastUtil.showLongToast("服务器获取数据失败！");
            return false;
        }
    }

    public boolean isTokenError() {
        return String.valueOf(TOKEN_ERROR_CODE).equals(this.code);
    }
}
