package com.dearzs.app.base;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.dearzs.app.BaseApplication;
import com.dearzs.app.R;
import com.dearzs.app.activity.LoginActivity;
import com.dearzs.app.chat.model.EntityEvent;
import com.dearzs.app.chat.model.TimManager;
import com.dearzs.app.entity.EntityBase;
import com.dearzs.app.widget.ErrorLayoutView;
import com.dearzs.app.widget.RefreshLayout;
import com.dearzs.app.widget.TitleBarView;
import com.dearzs.app.widget.sweetalert.OnSweetClickListener;
import com.dearzs.app.widget.sweetalert.SweetAlertDialog;
import com.dearzs.commonlib.utils.log.LogUtil;

import cn.jpush.android.api.JPushInterface;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;


/**
 * Activity基类，无特殊情况，所有Activity必须继承本类<br/>
 * 主要提供以下功能 ：
 * 1.对页面TitleBar的操作如，添加左侧按钮，添加中间内容，添加右侧按钮等(mTitleBar)<br/>
 * 2.数据加载提示（状态栏）(tvTip)<br/>
 * 3.数据请求过程UI(loadingView)<br/>
 * 4.请求出错页面处理(mErrorLayout)<br/>
 * 5.中间内容区域(mContentLayout)<br/>
 * 注意：具体怎么调用请参考示例
 *
 * @version 1.0
 */
public class BaseActivity extends FragmentActivity implements
        OnClickListener, TitleBarView.OnTitleBarClickListener,
        ErrorLayoutView.OnErrorLayoutClickListener, RefreshLayout.OnRefreshListener {

    protected String TAG;
    /**
     * title布局
     */
    private TitleBarView mTitleBar;
    /**
     * 子activity layout内容
     */
    private FrameLayout mContentLayout;
    /**
     * 页面上方的提示信息
     */
    private TextView tvTip;

    /**
     * 错误视图
     */
    private ErrorLayoutView mErrorLayout;

    public SweetAlertDialog mSweetAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 注册自身实例
        BaseApplication.getInstance().addActivity(this);

        TAG = this.getClass().getSimpleName();
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_base);
        initContentView();
        initBaseListener();
    }

    private void initBaseListener() {
        mTitleBar.setOnTitleBtnClickListener(this);
        mErrorLayout.setOnErrorLayoutClickListener(this);
    }

    public ErrorLayoutView getErrorLayout() {
        return mErrorLayout;
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BaseApplication.getInstance().deleteActivity(this);
        EventBus.getDefault().unregister(this);
    }

    /**
     * 初始化视图控件
     */
    private void initContentView() {
        mContentLayout = getView(R.id.base_layout_content);
        tvTip = getView(R.id.base_tv_tip);
        mTitleBar = getView(R.id.base_layout_title);
        mErrorLayout = getView(R.id.layout_error);
    }

    /**
     * 设置错误信息
     *
     * @param errMsg
     */
    private void setErrorText(String errMsg) {
        mErrorLayout.setErrorText(errMsg);
    }

    /**
     * 设置错误图片
     *
     * @param resId
     */
    private void setErrorImg(int resId) {
        mErrorLayout.setErrorImg(resId);
    }

    /**
     * 显示错误视图
     */
    public void showErrorLayout(String errMsg, int resId) {
        mErrorLayout.showErrorLayout(errMsg, resId);

        mContentLayout.setVisibility(View.GONE);
    }

    /**
     * 显示无数据视图
     */
    public void showEmptyLayout(String errMsg, int resId) {
        mErrorLayout.showEmptyLayout(errMsg, resId);

        mContentLayout.setVisibility(View.GONE);
    }

    /**
     * 隐藏错误视图
     */
    public void hideErrorLayout() {
        mErrorLayout.hideErrorLayout();
        mContentLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 请求成功后显示根视图
     */
    private void showSuccessLayout() {
        hideErrorLayout();
        mContentLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 获取TitleBar
     *
     * @return
     */
    public TitleBarView getTitleBar() {
        return mTitleBar;
    }

    /**
     * 设置中间文字右侧图片
     *
     * @param resId
     */
    public void setCenTxtRightImg(int resId) {
        if (mTitleBar != null) {
            mTitleBar.setCenTxtRightImg(resId);
        }
    }

    /**
     * 左侧按钮点击事件，子类可重写本方法直接响应点击事件
     */
    public void onLeftBtnClick() {
        this.finish();
    }

    /**
     * 左侧按钮点击事件，子类可重写本方法直接响应点击事件
     */
    public void onLeftBtnClick(View view) {
    }

    /**
     * 右侧按钮点击事件，子类可重写本方法直接响应点击事件
     */
    public void onRightBtnClick() {
    }

    /**
     * 最右侧按钮点击事件，子类可重写本方法直接响应点击事件
     */
    public void onRightBtn2Click() {
    }

    /**
     * 中间按钮点击事件，子类可重写本方法直接响应点击事件
     */
    public void onCenterBtnClick() {
    }

    /**
     * 错误视图点击事件，子类可重写本方法直接响应点击事件
     */
    public void onErrorClick() {
    }

    /**
     * 添加标题左侧按钮
     *
     * @param type 按钮样式
     */
    public void addLeftBtn(int type, String msg) {
        mTitleBar.addLeftBtn(type, msg);
    }

    /**
     * 添加标题右侧按钮
     *
     * @param type 按钮样式
     */
    public void addRightBtn(int type, String msg) {
        mTitleBar.addRightBtn(type, msg);
    }

    /**
     * 添加中间标题
     *
     * @param type 标题样式
     * @param msg  标题文字内容
     */
    public void addCenter(int type, String msg) {
        mTitleBar.addCenter(type, msg);
    }

    /**
     * 去除右侧按钮
     */
    public void removeRightBtn() {
        mTitleBar.removeRightBtn();
    }

    /**
     * 去除左侧按钮
     */
    public void removeLeftBtn() {
        mTitleBar.removeLeftBtn();
    }

    /**
     * 设置右侧ImageView图片资源
     */
    public void setRightIvResource(int resource) {
        mTitleBar.setRightIvResource(resource);
    }

    /**
     * 设置最右侧ImageView图片资源
     */
    public void setRightIv2Resource(int resource) {
        mTitleBar.setRightIv2Resource(resource);
    }

    public void setTitleRightTxtBackground(int backgroundResource) {
        mTitleBar.setTitleRightTxtBackground(backgroundResource);
    }

    public void setTitleRightTxt(String rightTxt) {
        mTitleBar.setTitleRightTxt(rightTxt);
    }

    /**
     * 设置子Activity布局
     *
     * @param rsId layout资源ID
     */
    public View setContentLayout(int rsId) {
        View contentView = getLayoutInflater().inflate(rsId, null);
        contentView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        mContentLayout.addView(contentView);
        initView();
        initListener();
        initData();
        return contentView;
    }

    /**
     * 初始化控件
     */
    public void initView() {
    }

    /**
     * 初始化控件监听
     */
    public void initListener() {
    }

    /**
     * 初始化数据
     */
    public void initData() {
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }

    /**
     * 获得对应的控件
     *
     * @param id
     * @return
     */
    @SuppressWarnings("unchecked")
    public final <E extends View> E getView(int id) {
        try {
            return (E) findViewById(id);
        } catch (ClassCastException ex) {
            Log.e("View", "Could not cast View to concrete class.", ex);
            throw ex;
        }
    }

    /**
     * 获得对应的控件
     *
     * @param id
     * @return
     */
    @SuppressWarnings("unchecked")
    public final <E extends View> E getView(View v, int id) {
        try {
            return (E) v.findViewById(id);
        } catch (ClassCastException ex) {
            Log.e("View", "Could not cast View to concrete class.", ex);
            throw ex;
        }
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void finish() {
        closeProgressDialog();
        super.finish();
    }

    private SweetAlertDialog mProgressDialog;

    public void closeProgressDialog() {
        if (mProgressDialog == null) return;
        mProgressDialog.dismiss();
        mProgressDialog = null;
    }

    public void showProgressDialog() {
        showProgressDialog("数据加载中...", true, false, 30000);
    }

    public void showProgressDialog(String content) {
        showProgressDialog(content, true, false, 30000);
    }

    public void showProgressDialog(int content) {
        showProgressDialog(getString(content), true, false, 30000);
    }

    public void showProgressDialog(String content, boolean isSupportCancel) {
        showProgressDialog(content, isSupportCancel, false, 30000);
    }

    protected void showProgressDialog(String content, boolean isSupportCancel, boolean handleTimeOut, long timeoutMillis) {
        if (mProgressDialog != null) {
            return;
        }
        mProgressDialog = new SweetAlertDialog(BaseActivity.this,
                SweetAlertDialog.PROGRESS_BAR_TYPE);
        mProgressDialog.setCancelable(isSupportCancel);
        mProgressDialog.show();
        mProgressDialog.setTitleText("");
        mProgressDialog.setProgressDialogText(content);
        //TODO 临时添加30秒的加载框超时操作
        if (handleTimeOut) {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    //execute the task
                    if (mProgressDialog == null) {
                        return;
                    }
                    closeProgressDialog();
                    //showErrorDialog("加载超时...");
                }
            }, timeoutMillis);
        }
    }

    private SweetAlertDialog mErrorDialog;

    /**
     * 弹出错误提示
     */
    public void showErrorDialog(String content) {
        showErrorDialog(content, null);
    }

    /**
     * 弹出错误提示
     */
    public void showErrorDialog(String content, final OnClickListener mDismissListener) {
        closeProgressDialog();
        if (mErrorDialog != null && mErrorDialog.isShowing()) return;
        try {
            mErrorDialog = new SweetAlertDialog(this);
            mErrorDialog.setTitleText("提示");
            mErrorDialog.setConfirmText("知道了");
            mErrorDialog.setContentText(content);
            mErrorDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    mErrorDialog = null;
                    if (mDismissListener != null) {
                        mDismissListener.onClick(null);
                        mErrorDialog = null;
                    }
                }
            });
            mErrorDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 确认框
     */
    public void showConfirmDialog(final BaseActivity mContext, String content,
                                  final OnClickListener mConfirmClickListener, final OnClickListener mCancelClickListener) {
        showConfirmDialog(mContext, content, null, null, mConfirmClickListener, mCancelClickListener);
    }

    public void showConfirmDialog(final BaseActivity mContext, String content,
                                  final OnClickListener mConfirmClickListener) {
        showConfirmDialog(mContext, content, null, null, mConfirmClickListener, null);
    }

    public void showConfirmDialog(final Context mContext, String content,
                                  String mConfirmText,
                                  String mCancelText,
                                  final OnClickListener mConfirmClickListener,
                                  final OnClickListener mCancelClickListener) {
        showConfirmDialog(mContext, "提示", content, mConfirmText, mCancelText, mConfirmClickListener, mCancelClickListener);
    }

    public void showConfirmDialog(final Context mContext, String title, String content,
                                  String mConfirmText,
                                  String mCancelText,
                                  final OnClickListener mConfirmClickListener,
                                  final OnClickListener mCancelClickListener) {
        showConfirmDialog(mContext, 0, title, content, mConfirmText, mCancelText, mConfirmClickListener, mCancelClickListener);
    }

    public SweetAlertDialog showConfirmDialog(final Context mContext, int alertType, String title, String content,
                                              String mConfirmText,
                                              String mCancelText,
                                              final OnClickListener mConfirmClickListener,
                                              final OnClickListener mCancelClickListener) {
        SweetAlertDialog mSweetAlertDialog = new SweetAlertDialog(mContext, alertType);
        if (TextUtils.isEmpty(title)) {
            title = "提示";
        }
        mSweetAlertDialog.setTitleText(title);
        mSweetAlertDialog.setContentText(content);
        // 设置确认按钮
        if (TextUtils.isEmpty(mConfirmText)) {
            mConfirmText = "确定";
        }
        mSweetAlertDialog.setConfirmText(mConfirmText);
        mSweetAlertDialog.setConfirmClickListener(new OnSweetClickListener() {

            public void onClick(SweetAlertDialog sweetAlertDialog) {
                if (sweetAlertDialog.getOwnerActivity() == null || sweetAlertDialog.getOwnerActivity().isFinishing()) {
                    return;
                }
                sweetAlertDialog.dismiss();
                mConfirmClickListener.onClick(null);
            }
        });
        // 设置取消按钮
        mSweetAlertDialog.showCancelButton(true);
        if (TextUtils.isEmpty(mCancelText)) {
            mCancelText = "取消";
        }
        mSweetAlertDialog.setCancelText(mCancelText);
        mSweetAlertDialog.setCancelClickListener(new OnSweetClickListener() {

            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                if (sweetAlertDialog.getOwnerActivity() == null || sweetAlertDialog.getOwnerActivity().isFinishing()) {
                } else {
                    sweetAlertDialog.dismiss();
                }
                if (mCancelClickListener != null) {
                    mCancelClickListener.onClick(null);
                }
            }
        });
        mSweetAlertDialog.show();
        return mSweetAlertDialog;
    }

    /**
     * 隐藏输入法
     *
     * @param view
     */
    public void hideInputMethod(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS |
                            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        } else {
            getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN |
                            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
    }

    /**
     * 显示输入法
     *
     * @param view
     */
    public void showInputMethod(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.RESULT_UNCHANGED_SHOWN);
        } else {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    /**
     * 按键响应“返回”事件处理及吃掉返回键,不让用户在启动时,退出
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            boolean isHandler = handleBack();
            if (isHandler == false) {
                finish();
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onRefresh() {
    }

    public boolean handleBack() {
        return false;
    }


    /**
     * 统一数据异常错误管理
     */
    public void onFailure(String errorMsg) {
        EntityBase resp = new EntityBase();
        String msg = "数据发生错误,请检查网络";
        if (errorMsg != null && errorMsg.contains("Failed to connect")) {
            msg = "网络连接失败,请检查网络";
        }
        resp.setMsg(msg);
        hasFailure(resp, true, true);
    }

    /**
     * 统一数据异常错误管理
     */
    public boolean onSuccess(EntityBase resp) {
        return !hasFailure(resp, true, true);
    }

    /**
     * 统一数据异常错误管理
     */
    public boolean onSuccess(EntityBase resp, boolean closeDia) {
        return !hasFailure(resp, true, closeDia);
    }

    private boolean hasFailure(EntityBase resp, boolean showErrorTip, boolean closeDia) {
        if (closeDia) {
            closeProgressDialog();
        }
        boolean hasFailure = false;
        if (resp == null) {
            hasFailure = true;
            if (showErrorTip) {
                showErrorDialog("服务端异常,获取数据失败!");
            }
        } else {
            if (resp.isSuccess()) {
                hasFailure = false;
            } else if (resp.isTokenError()) {
                hasFailure = true;
                showErrorDialog("账号登录已过期，请重新登录", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LoginActivity.startIntent(BaseActivity.this);
                    }
                });
            } else {
                hasFailure = true;
                String respMsg = resp.getMsg();
                if (TextUtils.isEmpty(respMsg)) {
                    if (showErrorTip) {
                        showErrorDialog("服务端异常,获取数据失败!");
                    }
                } else {
                    if (showErrorTip) {
                        showErrorDialog(respMsg);
                    }
                }
            }
        }
        return hasFailure;
    }

    public void showConfirmDialog(final BaseActivity mContext, String title, String content, int inputType, String editText,
                                  final OnClickListener mConfirmClickListener) {
        showConfirmDialog(mContext, title, content, inputType, editText, null, null, mConfirmClickListener, null);
    }

    public void showConfirmDialog(final Context mContext, String title, String content,
                                  int inputType,
                                  String mEditText,
                                  String mConfirmText,
                                  String mCancelText,
                                  final OnClickListener mConfirmClickListener,
                                  final OnClickListener mCancelClickListener) {
        showConfirmDialog(mContext, SweetAlertDialog.INPUT_TEXT_TYPE, title, content, inputType, mEditText, mConfirmText, mCancelText, mConfirmClickListener, mCancelClickListener);
    }

    public SweetAlertDialog showConfirmDialog(final Context mContext, int alertType, String title, String content,
                                              int inputType,
                                              String mEditText,
                                              String mConfirmText,
                                              String mCancelText,
                                              final OnClickListener mConfirmClickListener,
                                              final OnClickListener mCancelClickListener) {
        mSweetAlertDialog = new SweetAlertDialog(mContext, alertType);
        if (TextUtils.isEmpty(title)) {
            title = "提示";
        }
        mSweetAlertDialog.setTitleText(title);
        mSweetAlertDialog.setContentText(content);
        mSweetAlertDialog.setInputText(mEditText);
        mSweetAlertDialog.setInputType(inputType);
        // 设置确认按钮
        if (TextUtils.isEmpty(mConfirmText)) {
            mConfirmText = "确定";
        }
        mSweetAlertDialog.setConfirmText(mConfirmText);
        mSweetAlertDialog.setConfirmClickListener(new OnSweetClickListener() {

            public void onClick(SweetAlertDialog sweetAlertDialog) {
                if (sweetAlertDialog.getOwnerActivity() == null || sweetAlertDialog.getOwnerActivity().isFinishing()) {
                    return;
                }
                sweetAlertDialog.dismiss();
                mConfirmClickListener.onClick(null);
            }
        });
        // 设置取消按钮
        mSweetAlertDialog.showCancelButton(true);
        if (TextUtils.isEmpty(mCancelText)) {
            mCancelText = "取消";
        }
        mSweetAlertDialog.setCancelText(mCancelText);
        mSweetAlertDialog.setCancelClickListener(new OnSweetClickListener() {

            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                if (sweetAlertDialog.getOwnerActivity() == null || sweetAlertDialog.getOwnerActivity().isFinishing()) {
                } else {
                    sweetAlertDialog.dismiss();
                }
                if (mCancelClickListener != null) {
                    mCancelClickListener.onClick(null);
                }
            }
        });
        mSweetAlertDialog.show();
        return mSweetAlertDialog;
    }


    @Subscribe(threadMode = ThreadMode.MainThread)
    public void imStateEvent(EntityEvent.IMStateEvent imStateEvent) {
        if (imStateEvent != null) {
            LogUtil.LogD("IM", "==IM登录状态" + imStateEvent.getState());
            switch (imStateEvent.getState()) {
                case TimManager.STATE_OFFLINE://强制下线
                    showErrorDialog("你的账号已在其他终端登录,重新登录", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            TimManager.getInstance().exitIM();
                            LoginActivity.startIntent(BaseActivity.this);
                        }
                    });
                    break;
                case TimManager.STATE_EXPIRED://票据过期
                    showErrorDialog("账号登录已过期，请重新登录", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            TimManager.getInstance().exitIM();
                            LoginActivity.startIntent(BaseActivity.this);
                        }
                    });
                    break;
                case TimManager.STATE_LOGIN_ERROR://登录出错
                    TimManager.getInstance().exitIM();
                    LoginActivity.startIntent(BaseActivity.this);
                    break;
            }
        }
    }
}
