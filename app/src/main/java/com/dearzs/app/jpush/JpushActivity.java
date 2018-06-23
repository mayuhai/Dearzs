package com.dearzs.app.jpush;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.dearzs.app.R;
import com.dearzs.app.util.ToastUtil;

import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.InstrumentedActivity;
import cn.jpush.android.api.JPushInterface;

/**
 * 极光推送
 */
public class JpushActivity extends InstrumentedActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jpush);
        findViewById(R.id.jpust_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        registerMessageReceiver();

        BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(JpushActivity.this);
        builder.statusBarDrawable = R.mipmap.ic_launcher;
        builder.notificationFlags = Notification.FLAG_AUTO_CANCEL
                | Notification.FLAG_SHOW_LIGHTS;  //设置为自动消失和呼吸灯闪烁
        builder.notificationDefaults = Notification.DEFAULT_SOUND
                | Notification.DEFAULT_VIBRATE
                | Notification.DEFAULT_LIGHTS;  // 设置为铃声、震动、呼吸灯闪烁都要
        JPushInterface.setPushNotificationBuilder(1, builder);

    }

    //for receive customer msg from jpush server
    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";
    public static boolean isForeground = false;

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        registerReceiver(mMessageReceiver, filter);
    }

    public void unregisterMessageReceiver() {
        if (mMessageReceiver != null) {
            unregisterReceiver(mMessageReceiver);
//            ToastUtil.showLongToast("jpush unregisterMessageReceiver");
        }
    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                String messge = intent.getStringExtra(KEY_MESSAGE);
                String extras = intent.getStringExtra(KEY_EXTRAS);
                StringBuilder showMsg = new StringBuilder();
                showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
                if (!TextUtils.isEmpty(extras)) {
                    showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                }
                setCostomMsg(showMsg.toString());
            }
        }
    }

    private void setCostomMsg(String msg){
        ToastUtil.showShortToast(msg);
//        if (null != msgText) {
//            msgText.setText(msg);
//            msgText.setVisibility(android.view.View.VISIBLE);
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterMessageReceiver();
    }
}
