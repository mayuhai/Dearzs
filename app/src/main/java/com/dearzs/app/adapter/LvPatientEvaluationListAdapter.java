package com.dearzs.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.dearzs.app.R;
import com.dearzs.app.entity.EntityComment;
import com.dearzs.app.entity.EntityUser;
import com.dearzs.app.util.ImageLoaderManager;
import com.dearzs.app.util.Utils;
import com.dearzs.app.widget.CircleImageView;
import com.dearzs.commonlib.utils.GetViewUtil;

import java.util.List;

/**
 * 专家详情页--患者评论列表适配器
 */
public class LvPatientEvaluationListAdapter extends Adapter<RecyclerView.ViewHolder> {
    private Context mCtx;
    private List<EntityComment> mDataList;
    private int mType = 0;

    public LvPatientEvaluationListAdapter(Context context, List<EntityComment> carList){
        mCtx = context;
        mDataList = carList;
    }

    public LvPatientEvaluationListAdapter(Context context, List<EntityComment> carList, int type){
        mCtx = context;
        mDataList = carList;
        mType = type;
    }

    @Override
    public int getItemViewType(int position) {
        return mType;
    }

    public void notifyData(List<EntityComment> dataList, boolean isRefresh, int type) {
        mType = type;
        if(mDataList != null){
            if (dataList != null) {
                if (isRefresh) {      //是下拉刷新而不是上拉加载
                    this.mDataList.clear();
                    this.mDataList.addAll(dataList);
                } else {        //下拉加载更多
                    this.mDataList.addAll(dataList);
                }
            } else {
                this.mDataList.clear();
            }
        }
        notifyDataSetChanged();
    }

    public void notifyData(List<EntityComment> dataList, boolean isRefresh) {
        mType = 0;
        if(mDataList != null){
            if (dataList != null) {
                if (isRefresh) {      //是下拉刷新而不是上拉加载
                    this.mDataList.clear();
                    this.mDataList.addAll(dataList);
                } else {        //下拉加载更多
                    this.mDataList.addAll(dataList);
                }
            } else {
                this.mDataList.clear();
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mType == 0){
            return new NormalViewHolder(LayoutInflater.from(mCtx).inflate(R.layout.item_lv_patient_evaluation, null, false));
        } else {
            return new WebViewHolder(LayoutInflater.from(mCtx).inflate(R.layout.item_lv_family_doctor_webview, null, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(mDataList == null) return;
        EntityComment entityComment = mDataList.get(position);
        if(entityComment != null){
            if(mType == 0){
                NormalViewHolder mHolder = (NormalViewHolder) holder;
                mHolder.mRatingBar.setRating(entityComment.getStar());
                mHolder.mTime.setText(Utils.getTimeStamp(entityComment.getCreateTime()));
                mHolder.mComment.setText(entityComment.getComment());
                EntityUser user = entityComment.getUser();
                if(user != null){
                    mHolder.mName.setText(user.getName());
                    ImageLoaderManager.getInstance().displayImage(user.getAvatar(), mHolder.mPhoto);
                }
            } else {
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                WebViewHolder mHolder = (WebViewHolder) holder;
                mHolder.mWebView.setLayoutParams(lp);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mDataList != null ? mDataList.size() : 0;
    }

    protected static class NormalViewHolder extends RecyclerView.ViewHolder{
        CircleImageView mPhoto;
        RatingBar mRatingBar;
        TextView mTime;
        TextView mName;
        TextView mComment;

        public NormalViewHolder(View itemView) {
            super(itemView);
            mName = GetViewUtil.getView(itemView, R.id.item_patient_nickname);
            mPhoto = GetViewUtil.getView(itemView, R.id.item_patient_photo);
            mRatingBar = GetViewUtil.getView(itemView, R.id.item_patient_rating);
            mTime = GetViewUtil.getView(itemView, R.id.item_patient_evaluation_time);
            mComment = GetViewUtil.getView(itemView, R.id.item_patient_evaluation_content);
        }
    }

    protected static class WebViewHolder extends RecyclerView.ViewHolder{
        WebView mWebView;

        public WebViewHolder(View itemView) {
            super(itemView);
            mWebView = GetViewUtil.getView(itemView, R.id.item_web_family_doctor);
            WebSettings mSetting = mWebView.getSettings();
            mSetting.setJavaScriptEnabled(true);
            mWebView.setWebChromeClient(new WebChromeClient());
                mWebView.setWebViewClient(new WebViewClient(){
                });
            String html = "https://dev.api.dearzs.com/service/h5/news/3";
            mWebView.loadUrl(html);
        }
    }
}