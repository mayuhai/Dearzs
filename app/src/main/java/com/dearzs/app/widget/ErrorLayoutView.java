package com.dearzs.app.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dearzs.app.R;
import com.dearzs.commonlib.utils.GetViewUtil;

/**
 * 自定义的错误视图
 */
public class ErrorLayoutView extends LinearLayout {

    /**
     * 错误视图
     **/
    private View layout_error;
    /**
     * 错误视图提示文字
     **/
    private TextView tv_error;
    /**
     * 错误视图提示提示
     **/
    private ImageView iv_error;
    /**
     * 错误视图提示重新加载文字
     **/
    private TextView tv_error_reload;

    /**
     * 上下文对象
     */
    private Context mContext;
    private OnErrorLayoutClickListener listener;

    public ErrorLayoutView(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public ErrorLayoutView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        layout_error = mInflater.inflate(R.layout.error_layout, this);

        tv_error = GetViewUtil.getView(layout_error, R.id.error_tv);
        tv_error_reload = GetViewUtil.getView(layout_error, R.id.error_tv_reload);
        iv_error = GetViewUtil.getView(layout_error, R.id.error_iv);
        layout_error.setOnClickListener(onErrorClickListener);
    }

    /**
     * 错误视图点击监听
     */
    private OnClickListener onErrorClickListener = new OnClickListener() {

        @Override
        public void onClick(View view) {
            if (listener != null) {
                listener.onErrorClick();
            }
        }
    };

    /**
     * 设置错误信息
     * @param errMsg
     */
    public void setErrorText(String errMsg) {
        tv_error.setVisibility(View.VISIBLE);
        tv_error.setText(errMsg);
    }

    /**
     * 设置错误图片
     *
     * @param resId
     */
    public void setErrorImg(int resId) {
        iv_error.setVisibility(View.VISIBLE);
        iv_error.setImageResource(resId);
    }

    /**
     * 显示错误视图
     */
    public void showErrorLayout(String errMsg, int resId) {
        layout_error.setVisibility(View.VISIBLE);
        setErrorText(errMsg);
        setErrorImg(resId);
        tv_error_reload.setVisibility(View.VISIBLE);
    }

    /**
     * 显示空数据视图
     */
    public void showEmptyLayout(String errMsg, int resId) {
        layout_error.setVisibility(View.VISIBLE);
        tv_error.setText(errMsg);
        tv_error.setVisibility(TextUtils.isEmpty(errMsg) ? View.GONE : View.VISIBLE);
        tv_error_reload.setVisibility(View.GONE);

        iv_error.setVisibility(resId > 0 ? View.VISIBLE : View.GONE);
        if (resId > 0) {
            iv_error.setImageResource(resId);
        }
    }

    /**
     * 隐藏错误视图
     */
    public void hideErrorLayout() {
        layout_error.setVisibility(View.GONE);

    }

    public interface OnErrorLayoutClickListener {
        public void onErrorClick();
    }

    public void setOnErrorLayoutClickListener(OnErrorLayoutClickListener listener) {
        this.listener = listener;
    }
}
