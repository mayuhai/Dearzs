package com.dearzs.app.adapter;

import android.content.Context;
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
import com.dearzs.app.activity.expert.MedicalRecordActivity;
import com.dearzs.app.entity.EntityMedicalRecordHistoryInfo;
import com.dearzs.app.entity.EntityPatientInfo;
import com.dearzs.app.util.Constant;
import com.dearzs.app.util.ImageLoaderManager;
import com.dearzs.app.util.Utils;
import com.dearzs.app.widget.CircleImageView;
import com.dearzs.commonlib.utils.GetViewUtil;

import java.util.List;

/**
 * 病例更新列表适配器
 */
public class LvMedicalRecordHistoryListAdapter extends Adapter<LvMedicalRecordHistoryListAdapter.ViewHolder> {
    private Context mCtx;
    private List<EntityMedicalRecordHistoryInfo> mDataList;
    private EntityPatientInfo mPatientInfo;


    public LvMedicalRecordHistoryListAdapter(Context context, List<EntityMedicalRecordHistoryInfo> historyInfos, EntityPatientInfo patientInfo){
        mCtx = context;
        this.mDataList = historyInfos;
        mPatientInfo = patientInfo;
    }

    public void notifyData(List<EntityMedicalRecordHistoryInfo> dataList, boolean isRefresh) {
        if (dataList != null) {
            if (isRefresh) {      //是下拉刷新而不是上拉加载
                this.mDataList.clear();
                this.mDataList.addAll(dataList);
            } else {        //下拉加载更多
                this.mDataList.addAll(dataList);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.item_lv_medical_record_history_list, null, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(mDataList == null) return;
        EntityMedicalRecordHistoryInfo entity = mDataList.get(position);
        if(entity != null){
            EntityMedicalRecordHistoryInfo.EntityMedicalRecordHistoryExpertInfo expertInfo = entity.getExpert();
            if(expertInfo != null){
                ImageLoaderManager.getInstance().displayImage(expertInfo.getAvatar(), holder.mExpertPhoto);
                holder.mExpertName.setText(expertInfo.getName());
                holder.mExpertJob.setText(expertInfo.getJob());
                holder.mCanZhuanTag.setVisibility(expertInfo.getReferralState() == 1 ? View.VISIBLE : View.GONE);  //转诊状态，1开启0关闭
            }
            holder.mTime.setText(Utils.getTimeStamp(entity.getCreateTime()));
            setListener(holder.mLayout, entity);
        }
    }

    private void setListener(View view, final EntityMedicalRecordHistoryInfo entity){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(Constant.KEY_FROM, Constant.KEY_FROM_MEDICAL_RECORD_HISTORY);
                bundle.putSerializable(Constant.KEY_MEDICAL_RECORD_HISTORY_INFO, entity);
                bundle.putSerializable(Constant.KEY_PATIENT_INFO, mPatientInfo);
                Utils.startIntent(mCtx, MedicalRecordActivity.class, bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder{
        private LinearLayout mLayout;
        private CircleImageView mExpertPhoto;
        private ImageView mCanZhuanTag;
        private TextView mExpertName;
        private TextView mExpertJob;
        private TextView mTime;

        public ViewHolder(View itemView) {
            super(itemView);
            mLayout = GetViewUtil.getView(itemView, R.id.medical_record_history_list_item);
            mExpertPhoto = GetViewUtil.getView(itemView, R.id.medical_record_history_doctor_photo);
            mCanZhuanTag = GetViewUtil.getView(itemView, R.id.medical_record_history_can_zhuan);
            mTime = GetViewUtil.getView(itemView, R.id.medical_record_history_time);
            mExpertName = GetViewUtil.getView(itemView, R.id.medical_record_history_doctor_name);
            mExpertJob = GetViewUtil.getView(itemView, R.id.medical_record_history_doctor_job);
        }

    }
}
