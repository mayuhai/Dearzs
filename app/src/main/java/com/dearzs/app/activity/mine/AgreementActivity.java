package com.dearzs.app.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.dearzs.app.R;
import com.dearzs.app.base.BaseActivity;
import com.dearzs.app.widget.TitleBarView;

/**
 * Created by luyanlong on 2016/5/29.
 */
public class AgreementActivity extends BaseActivity {
    public static final String KEY_WHERH_FROM = "key_where";
    public static final String KEY_FROM_SECURITY = "key_security";
    public static final String KEY_FROM_PRIVATY = "key_privaty";
//    public static final String KEY_WHERH_FROM = "key_where";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_agreement);
        addLeftBtn(TitleBarView.TITLE_TYPE_LEFT_IV_BACK, null);
    }

    @Override
    public void initView() {
        super.initView();
    }

    @Override
    public void initData() {
        super.initData();
        if(getIntent() != null){
            String where = getIntent().getExtras().getString(KEY_WHERH_FROM);
            if(KEY_FROM_SECURITY.equals(where)){
                addCenter(TitleBarView.TITLE_TYPE_CENTER_TXT, "安全协议");
            } else if(KEY_FROM_PRIVATY.equals(where)){
                addCenter(TitleBarView.TITLE_TYPE_CENTER_TXT, "隐私协议");
            }
        }
    }

    /**
     * Activity跳转
     *
     * @param ctx
     */
    public static void startIntent(Context ctx, Bundle b) {
        Intent intent = new Intent();
        intent.setClass(ctx, AgreementActivity.class);
        if(b != null) {
            intent.putExtras(b);
        }
        ctx.startActivity(intent);
    }
}
