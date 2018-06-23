package com.dearzs.app.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.dearzs.app.R;
import com.dearzs.app.base.BaseActivity;
import com.dearzs.app.util.Utils;
import com.dearzs.app.widget.TitleBarView;

/**
 * Created by luyanlong on 2016/5/29.
 */
public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_about);
        addCenter(TitleBarView.TITLE_TYPE_CENTER_TXT, "关于");
        addLeftBtn(TitleBarView.TITLE_TYPE_LEFT_IV_BACK, null);

        TextView version = (TextView) findViewById(R.id.version);
        version.setText(getString(R.string.app_name) + Utils.getVersionName(AboutActivity.this));
    }

    /**
     * Activity跳转
     *
     * @param ctx
     */
    public static void startIntent(Context ctx) {
        Intent intent = new Intent();
        intent.setClass(ctx, AboutActivity.class);
        ctx.startActivity(intent);
    }
}
