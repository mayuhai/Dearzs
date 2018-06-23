package com.dearzs.app.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.dearzs.app.R;
import com.dearzs.app.base.BaseActivity;
import com.dearzs.app.util.Constant;
import com.dearzs.app.util.ToastUtil;
import com.dearzs.app.widget.TitleBarView;

/**
 * Created by luyanlong on 2016/9/23.
 * 公共WebView
 */

public class CommonWebViewActivity extends BaseActivity {
    /**
     * 内嵌浏览器
     */
    private WebView mWebView;
    private ProgressBar mProgress;

    private String mPageUrl;
    private String mPageTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_webview);
        addLeftBtn(TitleBarView.TITLE_TYPE_LEFT_IV_BACK, null);
    }

    @Override
    public void initView() {
        super.initView();
        mWebView = getView(R.id.wv_content);
        mProgress = getView(R.id.wv_pb);
    }

    @Override
    public void initData() {
        mPageUrl = getIntent().getStringExtra(Constant.WEB_PAGE_URL);
        mPageTitle = getIntent().getStringExtra(Constant.WEB_PAGE_TITLE);
        super.initData(); if (TextUtils.isEmpty(mPageUrl)) {
            ToastUtil.showLongToast( "地址异常、无法打开~");
            finish();
            return;
        }
        if (!mPageUrl.startsWith("http")) {
            mPageUrl = "http://".concat(mPageUrl);
        }
        // 页面加载的详情
        String mPageTitle = getIntent().getStringExtra(Constant.WEB_PAGE_TITLE);
        if (TextUtils.isEmpty(mPageTitle)) {
            mPageTitle = "详情";
        }
        addCenter(TitleBarView.TITLE_TYPE_CENTER_TXT, mPageTitle);
        // webView 相关逻辑处理
        mWebView.setWebViewClient(new MWebViewClient());
        mWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        WebSettings ws = mWebView.getSettings();
        // 不使用缓存
        ws.setCacheMode(WebSettings.LOAD_NO_CACHE);
        ws.setJavaScriptEnabled(true);
        // 设置可以支持缩放
        ws.setSupportZoom(false);
        WebChromeClient chromeClient = new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                setWebProgress(newProgress);
            }
        };
        mWebView.setWebChromeClient(chromeClient);
        if (isFinishing()) return;
        mWebView.loadUrl(mPageUrl);
    }

    @Override
    public void finish() {
        if (handleBack()) return;
        super.finish();
    }

    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }

    @Override
    public boolean handleBack() {
        if (mWebView != null && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return false;
    }

    private class MWebViewClient extends WebViewClient {
        @Override
        // 在WebView中而不是默认浏览器中显示页面
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith("mailto:") || url.startsWith("tel:")
                    || url.startsWith("sms:")) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                view.loadUrl(url);
            }
            return true;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            hideProgress();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            showProgress();
        }
    }

    /**
     * 显示进度条
     */
    private void showProgress() {
        mProgress.setProgress(0);
        mProgress.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏进度条
     */
    private void hideProgress() {
        mProgress.setVisibility(View.GONE);
    }

    /**
     * 设置进度
     *
     * @param progress
     */
    private void setWebProgress(int progress) {
        mProgress.setProgress(progress);
    }

    /**
     * Activity跳转
     */
    public static void startIntent(Context ctx, String mPageUrl) {
        startIntent(ctx,mPageUrl,null);
    }

    /**
     * Activity跳转
     * @param ctx
     * @param mPageUrl  页面地址
     * @param mPageTitle  页面标题   默认"详情"
     */
    public static void startIntent(Context ctx, String mPageUrl,String mPageTitle) {
        Intent intent = new Intent();
        intent.putExtra(Constant.WEB_PAGE_URL,mPageUrl);
        intent.putExtra(Constant.WEB_PAGE_TITLE,mPageTitle);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setClass(ctx, CommonWebViewActivity.class);
        ctx.startActivity(intent);
    }
}
