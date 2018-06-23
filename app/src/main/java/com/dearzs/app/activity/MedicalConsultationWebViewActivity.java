package com.dearzs.app.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dearzs.app.R;
import com.dearzs.app.base.BaseActivity;
import com.dearzs.app.entity.EntityBase;
import com.dearzs.app.entity.EntityConsultation;
import com.dearzs.app.entity.resp.RespGetConsultationDetail;
import com.dearzs.app.entity.resp.RespGetConsultationList;
import com.dearzs.app.util.Constant;
import com.dearzs.app.util.ReqManager;
import com.dearzs.app.util.ShareUtil;
import com.dearzs.app.util.ToastUtil;
import com.dearzs.app.util.Utils;
import com.dearzs.app.widget.TitleBarView;
import com.dearzs.commonlib.okhttp.callback.Callback;

import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Request;

/**
 * 内嵌webView根据传输数据、显示相应内容和与网页交互
 */
public class MedicalConsultationWebViewActivity extends BaseActivity {
    private EntityConsultation mConsultation;
    /**
     * 内嵌浏览器
     */
    private WebView mWebView;
    private TextView mTvPraiseCount;
    private ImageView mIvCollection;
    private ImageView mIvShare;
    private LinearLayout mBottomLayout;
    private long mPraiseCount = 0;
    private boolean mIsPraised = false;
    private boolean mIsCollected = false;
    /**
     * WebView顶部滚动条
     **/
    private ProgressBar mProgress;

    /**
     * 页面加载的地址
     */
    private String mPageUrl;
    /**
     * 跳转到该页面的链接的类型
     */
    private String mRedirectType;
    /**
     * 分享时,分享的内容
     */
    private String mShareContent = "活动链接:";
    /**
     * 分享时,分享的图片
     */
    private String mShareImage;

    private Drawable mImgPraised, mImgNotPraised;
    private long mConsultationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_webview);
        addCenter(TitleBarView.TITLE_TYPE_CENTER_TXT, "详情");
        addLeftBtn(TitleBarView.TITLE_TYPE_LEFT_IV_BACK, null);
    }

    @Override
    public void initView() {
        super.initView();
        mWebView = getView(R.id.wv_content);
        mBottomLayout = getView(R.id.bottom_layout);
        mProgress = getView(R.id.wv_pb);
        mIvCollection = getView(R.id.iv_webview_collection);
        mIvShare = getView(R.id.iv_webview_share);
        mTvPraiseCount = getView(R.id.medical_consultation_priase_count);
        mTvPraiseCount.setOnClickListener(this);
        mIvCollection.setOnClickListener(this);
        mIvShare.setOnClickListener(this);
        mBottomLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLeftBtnClick() {
        setResult(RESULT_OK);
        super.onLeftBtnClick();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }

    //获取咨询列表回调
    private class ReqGetConsultationCallback extends Callback<RespGetConsultationDetail> {

        @Override
        public void onError(Call call, Exception e) {
            closeProgressDialog();
        }

        @Override
        public void onResponse(RespGetConsultationDetail response) {
            closeProgressDialog();
            if (response != null && response.getResult() != null) {
                mConsultation = response.getResult().getNews();
                // 页面加载地址
                if(mConsultation != null){
                    mPageUrl = mConsultation.getShareUrl();
                    mPraiseCount = mConsultation.getPraise();
                    mIsPraised = mConsultation.getIsPraise() == 1;
                    mIsCollected = mConsultation.getIsFav() == 1;
                    mTvPraiseCount.setCompoundDrawables(mIsPraised ? mImgPraised : mImgNotPraised, null, null, null); //设置左图标
                    mTvPraiseCount.setText(String.valueOf(mPraiseCount));
                    mIvCollection.setImageResource(mIsCollected ? R.mipmap.ic_collection_selected : R.mipmap.ic_collection_normal);
                    mConsultationId = mConsultation.getId();
                }

                if (TextUtils.isEmpty(mPageUrl)) {
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
                mWebView.loadUrl(mPageUrl);
            }
        }

        @Override
        public void onBefore(Request request) {
            super.onBefore(request);
            showProgressDialog();
        }
    }

    @Override
    public void initData() {
        long newId = getIntent().getLongExtra(Constant.KEY_NEWS_ID, -1);
        if(newId > 0){
            showProgressDialog();
            ReqManager.getInstance().reqConsultationDetail(new ReqGetConsultationCallback(), newId, Utils.getUserToken(MedicalConsultationWebViewActivity.this));
        }

        mImgNotPraised = getResources().getDrawable(R.mipmap.ic_webview_unpraise);
        mImgPraised = getResources().getDrawable(R.mipmap.ic_webview_priased);
        mImgNotPraised.setBounds(0, 0, mImgNotPraised.getMinimumWidth(), mImgNotPraised.getMinimumHeight());
        mImgPraised.setBounds(0, 0, mImgPraised.getMinimumWidth(), mImgPraised.getMinimumHeight());
        // 页面类型
        mRedirectType = getIntent().getStringExtra(Constant.WEB_REDIRECT_TYPE);
        mShareContent = getIntent().getStringExtra(Constant.WEB_PAGE_PARAM2);
        mShareImage = getIntent().getStringExtra(Constant.WEB_PAGE_PARAM3);

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
        //自动加载图片
        ws.setLoadsImagesAutomatically(true);
        //设置webview的插件转状态
        ws.setPluginState(WebSettings.PluginState.ON);
        //设置默认的字符编码
        ws.setDefaultTextEncodingName("utf-8");
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
    }

    //收藏处理接口回调
    Callback<EntityBase> reqDealCollectionCallback = new Callback<EntityBase>() {
        @Override
        public void onError(Call call, Exception e) {
            closeProgressDialog();
            onFailure(e.toString());
        }

        @Override
        public void onResponse(EntityBase response) {
            closeProgressDialog();
            //请求成功，设置加载和刷新完毕
            if(response.getCode().equals("000000")){
                mIsCollected = !mIsCollected;
                mIvCollection.setImageResource(mIsCollected ? R.mipmap.ic_collection_selected : R.mipmap.ic_collection_normal);
                String toast = mIsCollected ? "收藏成功" : "取消收藏成功";
                ToastUtil.showLongToast(toast);
            } else {
                ToastUtil.showLongToast(response.getMsg());
            }
        }

        @Override
        public void onBefore(Request request) {
            showProgressDialog();
            super.onBefore(request);
        }
    };

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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_webview_collection:
                int optsType = mIsCollected ? 0 : 1;
                ReqManager.getInstance().reqDealCollection(reqDealCollectionCallback, Utils.getUserToken(MedicalConsultationWebViewActivity.this), mConsultationId, Constant.COLLECTION_CONSULTATION, optsType);
                break;
            case R.id.iv_webview_share:
                if(mConsultation != null){
                    ShareUtil.getInstence(MedicalConsultationWebViewActivity.this).openShare(mConsultation.getTitle(), mConsultation.getTitle(), mConsultation.getShareUrl(), mConsultation.getImg());
                }
                break;
            case R.id.medical_consultation_priase_count:
                int type = mIsPraised ? 0 : 1;
                if(mConsultation != null){
                    ReqManager.getInstance().reqDealConsultationPraise(reqDealPraiseCallback, Utils.getUserToken(MedicalConsultationWebViewActivity.this),mConsultation.getId() ,type);
                }
                break;

        }
    }

    //赞和取消赞处理接口回调
    Callback<EntityBase> reqDealPraiseCallback = new Callback<EntityBase>() {
        @Override
        public void onError(Call call, Exception e) {
            closeProgressDialog();
            onFailure(e.toString());
        }

        @Override
        public void onResponse(EntityBase response) {
            closeProgressDialog();
            //请求成功，设置加载和刷新完毕
            if (response.getCode().equals("000000")) {
                mIsPraised = !mIsPraised;
                mPraiseCount = mIsPraised ? ++mPraiseCount : --mPraiseCount;
                mTvPraiseCount.setCompoundDrawables(mIsPraised ? mImgPraised : mImgNotPraised, null, null, null); //设置左图标
                mTvPraiseCount.setText(String.valueOf(mPraiseCount));
                String toast = mIsPraised ? "点赞成功" : "取消赞成功";
                ToastUtil.showShortToast(toast);
            } else {
                ToastUtil.showShortToast(response.getMsg());
            }
        }

        @Override
        public void onBefore(Request request) {
            showProgressDialog();
            super.onBefore(request);
        }
    };

    /**
     * Activity跳转
     */
    public static void startIntent(Context ctx, long newId) {
        Intent intent = new Intent();
        intent.putExtra(Constant.KEY_NEWS_ID,newId);
        intent.setClass(ctx, MedicalConsultationWebViewActivity.class);
        ctx.startActivity(intent);
    }
}
