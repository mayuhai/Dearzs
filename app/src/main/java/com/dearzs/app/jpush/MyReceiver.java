package com.dearzs.app.jpush;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.dearzs.app.BaseApplication;
import com.dearzs.app.activity.HomeActivity;
import com.dearzs.app.activity.MedicalConsultationWebViewActivity;
import com.dearzs.app.activity.StartActivity;
import com.dearzs.app.activity.mine.OrderDataDetailActivity;
import com.dearzs.app.chat.ui.ChatActivity;
import com.dearzs.app.util.Constant;
import com.dearzs.app.util.Utils;
import com.dearzs.commonlib.utils.PfUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;
import de.greenrobot.event.EventBus;

/**
 * 自定义接收器
 * <p>
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {
    private static final String TAG = "JPush";
    private Context mContext;
    private static String mExtrasType;
    private static String mExtrasOrderNO;
    private static String mExtrasAuth;
    private static String mExtrasConsultationId;
    private static String mExtrasNewsId;
    public static final String JPUSHEXTRASKEY_TYPE = "type";

    public static final String JPUSHEXTRASKEY_ORDERNO = "orderNo";
    public static final String JPUSHEXTRASKEY_CONSULID = "consultId";
    public static final String JPUSHEXTRASKEY_NEWS_ID = "newsId";

    public static final String JPUSHEXTRASVALUE = "ORDER";
    public static final String JPUSHEXTRASVALUE_CONSULTATION = "CONSULT";
    public static final String JPUSHEXTRASVALUE_AUTH = "AUTH";
    public static final String JPUSHEXTRASVALUE_NEWS = "NEWS";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        mContext = context;
        Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

        //消息推送开关
        boolean isPush = PfUtils.getBoolean(context, Constant.DEARZS_SP, Constant.KEY_PUSH_SWITCH, true);

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
            //send the Registration Id to your server...

        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
            processCustomMessage(context, bundle);

        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);

        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户点击打开了通知");
            //打开自定义的Activity
            Intent i = null;
            if (JPUSHEXTRASVALUE.equals(mExtrasType)) {
                bundle.putString(Constant.KEY_ORDER_NO, mExtrasOrderNO);
                i = new Intent(context, OrderDataDetailActivity.class);
            } else if (JPUSHEXTRASVALUE_CONSULTATION.equals(mExtrasType)) {
                i = new Intent(context, ChatActivity.class);
                i.putExtra(Constant.KEY_IS_FROM_NOTIFY, true);
                i.putExtra(Constant.KEY_CONSULTION_ID, mExtrasConsultationId);
            } else if(JPUSHEXTRASVALUE_NEWS.equals(mExtrasType)){
                i = new Intent(context, MedicalConsultationWebViewActivity.class);
                try{
                    i.putExtra(Constant.KEY_NEWS_ID, Long.parseLong(mExtrasNewsId));
                } catch (Exception e){
                    i.putExtra(Constant.KEY_NEWS_ID, 0);
                    e.printStackTrace();
                }
            } else if(JPUSHEXTRASVALUE_AUTH.equals(mExtrasType)){
                if(Utils.isForeground(mContext) && BaseApplication.getInstance() != null && BaseApplication.getInstance().getLoginInfo() != null){
                    i = new Intent(context, HomeActivity.class);
                    i.putExtra(Constant.KEY_CURRENT_PAGE, 3);
                } else {
                    i = new Intent(context, StartActivity.class);
                    i.putExtra(Constant.KEY_CURRENT_PAGE, 3);
                }
            } else {
                i = new Intent(context, StartActivity.class);
            }
            i.putExtras(bundle);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(i);

        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

        } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
            boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            Log.w(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
        } else {
            Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
        }
    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (bundle.getString(JPushInterface.EXTRA_EXTRA).isEmpty()) {
                    Log.i(TAG, "This message has no Extra data");
                    continue;
                }

                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next().toString();
                        sb.append("\nkey:" + key + ", value: [" + myKey + " - " + json.optString(myKey) + "]");

                        if (MyReceiver.JPUSHEXTRASKEY_TYPE.equals(myKey)) {
                            mExtrasType = json.optString(myKey);
                        }

                        if (MyReceiver.JPUSHEXTRASKEY_CONSULID.equals(myKey)) {
                            mExtrasConsultationId = json.optString(myKey);
                        }

                        if (MyReceiver.JPUSHEXTRASKEY_ORDERNO.equals(myKey)) {
                            mExtrasOrderNO = json.optString(myKey);
                        }

                        if(JPUSHEXTRASKEY_NEWS_ID.equals(myKey)){
                           mExtrasNewsId = json.optString(myKey);
                        }

                        if(MyReceiver.JPUSHEXTRASVALUE_AUTH.equals(mExtrasType)) {
                            //应用在前台，则会发送消息去更新数据，否则不会（应用退出了，则EvevtBus取消了注册，就不会受到消息）
                            EventBus.getDefault().post(Constant.EVENT_UPDATE_USER_INFO);
                        } else if(MyReceiver.JPUSHEXTRASVALUE_CONSULTATION.equals(mExtrasType)){
                            EventBus.getDefault().post(Constant.EVENT_UPDATE_CONSUL_STATE);
                        }
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Get message extra JSON error!");
                }

            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }

    //send msg to MainActivity
    private void processCustomMessage(Context context, Bundle bundle) {
        if (JpushActivity.isForeground) {
            String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
            Intent msgIntent = new Intent(JpushActivity.MESSAGE_RECEIVED_ACTION);
            msgIntent.putExtra(JpushActivity.KEY_MESSAGE, message);
            if (!TextUtils.isEmpty(extras)) {
                try {
                    JSONObject extraJson = new JSONObject(extras);
                    if (null != extraJson && extraJson.length() > 0) {
                        msgIntent.putExtra(JpushActivity.KEY_EXTRAS, extras);
                    }
                } catch (JSONException e) {

                }

            }
            context.sendBroadcast(msgIntent);
        }
    }
}
