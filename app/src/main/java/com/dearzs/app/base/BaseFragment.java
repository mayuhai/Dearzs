package com.dearzs.app.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.dearzs.app.R;
import com.dearzs.app.activity.LoginActivity;
import com.dearzs.app.entity.EntityBase;
import com.dearzs.app.util.Constant;
import com.dearzs.app.widget.ErrorLayoutView;
import com.dearzs.app.widget.TitleBarView;
import com.dearzs.app.widget.sweetalert.OnSweetClickListener;
import com.dearzs.app.widget.sweetalert.SweetAlertDialog;

/**
 * 所有fragment的基类
 * @version 1.0
 */
abstract public class BaseFragment extends Fragment implements TitleBarView.OnTitleBarClickListener ,View.OnClickListener, ErrorLayoutView.OnErrorLayoutClickListener{

    public SweetAlertDialog mSweetAlertDialog;
    /**
     * title布局
     */
    private TitleBarView mTitleBar;

    abstract protected int inflateResource();

    abstract protected void initViews(View rootView);

    abstract public void initData();

    /**
     * 子activity layout内容
     */
    private FrameLayout mContentLayout;
    /**
     * 错误视图
     */
    private ErrorLayoutView mErrorLayout;
    /**
     * 是否有数据
     */
    private boolean mHasData;
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // 获取公共的Fragment视图
        View rootView = inflater.inflate(R.layout.fragment_base, container, false);
        mErrorLayout = getView(rootView, R.id.layout_error);

        mContentLayout = getView(rootView, R.id.base_layout_content);

        mErrorLayout.setOnErrorLayoutClickListener(this);
        // 获取指定的子视图
        int resourceID = inflateResource();
        if (resourceID != 0) {
            // 将子视图进行初始化,并且加入到容器中
            View childView = inflater.inflate(resourceID, container, false);
            childView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            mContentLayout.addView(childView);

            initViews(rootView);
            initData();
            return rootView;
        } else {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }

    /**
     * 显示错误视图
     */
    public void showErrorLayout(String errMsg, int resId) {
        mErrorLayout.showErrorLayout(errMsg, resId);
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
     * 显示无数据视图
     */
    public void showEmptyLayout(String errMsg, int resId) {
        mErrorLayout.showEmptyLayout(errMsg, resId);
        mContentLayout.setVisibility(View.GONE);
    }

    /**
     * 获得对应的控件
     *
     * @param id
     * @return
     */
    @SuppressWarnings("unchecked")
    public final <E extends View> E getView(View view, int id) {
        try {
            return (E) view.findViewById(id);
        } catch (ClassCastException ex) {
            Log.e("View", "Could not cast View to concrete class.", ex);
            throw ex;
        }
    }

    @Override
    public void onLeftBtnClick(View view) {
    }

    @Override
    public void onLeftBtnClick() {
    }

    @Override
    public void onCenterBtnClick() {
    }

    @Override
    public void onRightBtnClick() {
    }

    @Override
    public void onRightBtn2Click() {
    }

    @Override
    public void onClick(View view) {
    }

    /**
     * 错误视图点击事件，子类可重写本方法直接响应点击事件
     */
    @Override
    public void onErrorClick() {
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isNeedRefresh()) return;
        lazyLoad();
    }

    /**
     * 页面显示了，或需要强制刷新时
     */
    public void lazyLoad() {
        mHasData = true;

        if (getActivity() == null) return;
    }

    /**
     * 是否需要刷新数据
     *
     * @return
     */
    private boolean isNeedRefresh() {
        if (!getUserVisibleHint()) {
            return false;
        }
        if (!mHasData) {
            return true;
        }
        if (getActivity() == null) {
            return false;
        }
        return false;
    }

    public void showProgressDialog() {
        if(getBaseActivity()!=null){
            getBaseActivity().showProgressDialog();
        }
    }

    public void showProgressDialog(String content) {
        if(getBaseActivity()!=null){
            getBaseActivity().showProgressDialog(content, true, false, 30000);
        }
    }

    public void  closeProgressDialog() {
        if(getBaseActivity()!=null){
            getBaseActivity().closeProgressDialog();
        }
    }

    public void showProgressDialog(String content, boolean isSupportCancel) {
        if(getBaseActivity()!=null){
            getBaseActivity().showProgressDialog(content, isSupportCancel, false, 30000);
        }
    }

    public void showErrorDialog(String content){
        if(getBaseActivity()!=null){
            getBaseActivity().showErrorDialog(content);
        }
    }

    public void showErrorDialog(String content, final View.OnClickListener mDismissListener){
        if(getBaseActivity()!=null){
            getBaseActivity().showErrorDialog(content, mDismissListener);
        }
    }

    public void showConfirmDialog(String content,
                                  final View.OnClickListener mConfirmClickListener) {
        if(getBaseActivity()!=null){
            getBaseActivity().showConfirmDialog(getBaseActivity(),content,
            mConfirmClickListener);
        }
    }

    public BaseActivity getBaseActivity(){
        if(getActivity()==null)return null;
        return (BaseActivity)getActivity();
    }

    public SweetAlertDialog showConfirmDialog(final Context mContext, int alertType, String title, String content,
                                              String mConfirmText,
                                              String mCancelText,
                                              final View.OnClickListener mConfirmClickListener,
                                              final View.OnClickListener mCancelClickListener) {
        mSweetAlertDialog = new SweetAlertDialog(mContext, alertType);
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

    public SweetAlertDialog showConfirmDialog(final Context mContext, int alertType, String title, String content,
                                              int inputType,
                                              String mEditText,
                                              String mConfirmText,
                                              String mCancelText,
                                              final View.OnClickListener mConfirmClickListener,
                                              final View.OnClickListener mCancelClickListener) {
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
                        LoginActivity.startIntent(getActivity());
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
}
