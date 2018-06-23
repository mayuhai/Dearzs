package com.dearzs.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dearzs.app.R;
import com.dearzs.app.activity.MedicalConsultationWebViewActivity;
import com.dearzs.app.base.BaseActivity;
import com.dearzs.app.entity.EntityConsultation;
import com.dearzs.app.util.Constant;
import com.dearzs.app.util.ImageLoaderManager;
import com.dearzs.app.util.Utils;
import com.dearzs.commonlib.utils.GetViewUtil;

import java.util.List;

/**
 * 医学咨询列表适配器
 */
public class LvMedicalConsultationListAdapter extends Adapter<LvMedicalConsultationListAdapter.ViewHolder> {
    private Context mCtx;
    private List<EntityConsultation> mDataList;


    public LvMedicalConsultationListAdapter(Context context, List<EntityConsultation> carList){
        mCtx = context;
        this.mDataList = carList;
    }

    public void notifyData(List<EntityConsultation> dataList, boolean isRefresh) {
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
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.item_lv_consultation_list, null, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(mDataList == null) return;
        EntityConsultation entity = mDataList.get(position);
        if(entity != null){
            ImageLoaderManager.getInstance().displayImage(entity.getImg(), holder.mNewszImage);
            holder.mNewsTitle.setText(entity.getTitle());
            holder.mCommentCount.setText(entity.getPraise() + "条赞");
            holder.mNewsTag.setText(entity.getTypeName());  //转诊状态，1开启0关闭
            holder.mTime.setText(Utils.getTimeStamp(entity.getCreateTime()));
            setListener(holder.mLayout, entity);
        }
    }

    private void setListener(View view, final EntityConsultation consultation){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(consultation != null){
                    MedicalConsultationWebViewActivity.startIntent(mCtx, consultation.getId());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder{
        private LinearLayout mLayout;
        private ImageView mNewszImage;
        private TextView mNewsTag;
        private TextView mNewsTitle;
        private TextView mCommentCount;
        private TextView mTime;

        public ViewHolder(View itemView) {
            super(itemView);
            mLayout = GetViewUtil.getView(itemView, R.id.medical_consultation_list_item);
            mNewszImage = GetViewUtil.getView(itemView, R.id.iv_item_consultation_icon);
            mNewsTag = GetViewUtil.getView(itemView, R.id.tv_consultation_type);
            mTime = GetViewUtil.getView(itemView, R.id.tv_consultation_time);
            mNewsTitle = GetViewUtil.getView(itemView, R.id.tv_consultation_title);
            mCommentCount = GetViewUtil.getView(itemView, R.id.tv_consultation_praise);
        }

    }
}
