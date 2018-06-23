package com.dearzs.commonlib.okhttp.callback;

import com.dearzs.commonlib.okhttp.utils.ReflectUtils;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

public abstract class Callback<T> {

    /**
     * UI Thread
     *
     * @param request
     */
    public void onBefore(Request request) {
    }

    /**
     * UI Thread
     *
     * @param
     */
    public void onAfter() {
    }

    /**
     * UI Thread
     *
     * @param progress
     */
    public void inProgress(float progress) {

    }

    /**
     * Thread Pool Thread
     *
     * @param response
     */
    public T parseNetworkResponse(Response response) throws Exception {
        return null;
    }

    public abstract void onError(Call call, Exception e);

    public abstract void onResponse(T response);


    public static Callback CALLBACK_DEFAULT = new Callback() {

        @Override
        public void onError(Call call, Exception e) {

        }

        @Override
        public void onResponse(Object response) {

        }
    };

    private Class<T> clazz;

    /**
     * @param mHasReflect <br/>mHasReflect=true表示把响应的内容反射成对应实体类
     */
    public Callback(boolean mHasReflect) {
        if (mHasReflect) {
            clazz = ReflectUtils.getClassGenricType(getClass());
        }
    }

    public Callback() {
        this(true);
    }

    public Class<T> getClazz() {
        return clazz;
    }
}