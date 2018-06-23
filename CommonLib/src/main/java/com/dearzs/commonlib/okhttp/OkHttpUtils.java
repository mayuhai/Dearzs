package com.dearzs.commonlib.okhttp;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.dearzs.commonlib.utils.log.LogUtil;
import com.google.gson.Gson;
import com.dearzs.commonlib.okhttp.builder.GetBuilder;
import com.dearzs.commonlib.okhttp.builder.PostFileBuilder;
import com.dearzs.commonlib.okhttp.builder.PostFormBuilder;
import com.dearzs.commonlib.okhttp.builder.PostJsonBuilder;
import com.dearzs.commonlib.okhttp.builder.PostStringBuilder;
import com.dearzs.commonlib.okhttp.callback.Callback;
import com.dearzs.commonlib.okhttp.cookie.SimpleCookieJar;
import com.dearzs.commonlib.okhttp.https.HttpsUtils;
import com.dearzs.commonlib.okhttp.request.RequestCall;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * Created by zhy on 15/8/17.
 */
public class OkHttpUtils {
    public static final String TAG = "OkHttpUtils";
    public static final long DEFAULT_MILLISECONDS = 10000;
    private static OkHttpUtils mInstance;
    private OkHttpClient mOkHttpClient;
    private Handler mDelivery;

    private OkHttpUtils() {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        //cookie enabled
        okHttpClientBuilder.cookieJar(new SimpleCookieJar());
        mDelivery = new Handler(Looper.getMainLooper());


        if (true) {
            okHttpClientBuilder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        }

        mOkHttpClient = okHttpClientBuilder.build();
    }

    private boolean debug;
    private String tag;

    public OkHttpUtils debug(String tag) {
        debug = true;
        this.tag = tag;
        return this;
    }


    public static OkHttpUtils getInstance() {
        if (mInstance == null) {
            synchronized (OkHttpUtils.class) {
                if (mInstance == null) {
                    mInstance = new OkHttpUtils();
                }
            }
        }
        return mInstance;
    }

    public Handler getDelivery() {
        return mDelivery;
    }

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }


    public static GetBuilder get() {
        return new GetBuilder();
    }

    public static PostStringBuilder postString() {
        return new PostStringBuilder();
    }

    public static PostJsonBuilder postJson() {
        return new PostJsonBuilder();
    }

    public static PostFileBuilder postFile() {
        return new PostFileBuilder();
    }

    public static PostFormBuilder post() {
        return new PostFormBuilder();
    }

    public void execute(final RequestCall requestCall, Callback callback) {
        if (debug) {
            if (TextUtils.isEmpty(tag)) {
                tag = TAG;
            }
            LogUtil.LogE("{method:" + requestCall.getRequest().method() + ", detail:" + requestCall.getOkHttpRequest().toString() + "}");
        }

        if (callback == null)
            callback = Callback.CALLBACK_DEFAULT;
        final Callback finalCallback = callback;

        requestCall.getCall().enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                sendFailResultCallback(call, e, finalCallback);
            }

            @Override
            public void onResponse(final Call call, final Response response) {
                if (response.code() >= 400 && response.code() <= 599) {
                    try {
                        sendFailResultCallback(call, new RuntimeException(response.body().string()), finalCallback);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
                }

                try {
                    Object o = null;
                    if (finalCallback.getClazz() != null) {
                        o = new Gson().fromJson(response.body().string(), finalCallback.getClazz());

                        Gson gson = new Gson();
                        String body =  gson.toJson(o);
                        LogUtil.LogD("response", "response gson : " + body);
                    } else {
                        o = finalCallback.parseNetworkResponse(response);
                    }
                    if (o != null) {
                        sendSuccessResultCallback(o, finalCallback);
                    } else {
                        Log.d(tag, "数据发生异常");
                        sendFailResultCallback(call, new RuntimeException(response.body().string()), finalCallback);
                    }
                } catch (Exception e) {
                    LogUtil.LogE(e.toString());
                    sendFailResultCallback(call, e, finalCallback);
                }

            }
        });
    }

    public void sendFailResultCallback(final Call call, final Exception e, final Callback callback) {
        if (callback == null) return;

        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                callback.onError(call, e);
                callback.onAfter();
            }
        });
    }

    public void sendSuccessResultCallback(final Object object, final Callback callback) {
        if (callback == null) return;
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                callback.onResponse(object);
                callback.onAfter();
            }
        });
    }

    public void cancelTag(Object tag) {
        for (Call call : mOkHttpClient.dispatcher().queuedCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
        for (Call call : mOkHttpClient.dispatcher().runningCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
    }


    public void setCertificates(InputStream... certificates) {
        mOkHttpClient = getOkHttpClient().newBuilder()
                .sslSocketFactory(HttpsUtils.getSslSocketFactory(certificates, null, null))
                .build();
    }

    public void setConnectTimeout(int timeout, TimeUnit units) {
        mOkHttpClient = getOkHttpClient().newBuilder()
                .connectTimeout(timeout, units)
                .build();
    }
}

