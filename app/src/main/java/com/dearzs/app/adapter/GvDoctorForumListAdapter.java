package com.dearzs.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dearzs.app.R;
import com.dearzs.app.activity.forum.MediaPlayerActivity;
import com.dearzs.app.entity.EntityDoctorForumInfo;
import com.dearzs.app.entity.EntityExpertInfo;
import com.dearzs.app.entity.EntityOrg;
import com.dearzs.app.util.ImageLoaderManager;
import com.dearzs.commonlib.utils.GetViewUtil;

import java.util.List;

/**
 * 名医讲堂列表适配器
 */
public class GvDoctorForumListAdapter extends Adapter<GvDoctorForumListAdapter.ViewHolder> {
    private Context mCtx;
    private List<EntityDoctorForumInfo> mDataList;


    public GvDoctorForumListAdapter(Context context, List<EntityDoctorForumInfo> carList){
        mCtx = context;
        this.mDataList = carList;
    }

    public void notifyData(List<EntityDoctorForumInfo> dataList, boolean isRefresh) {
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
        View view = LayoutInflater.from(mCtx).inflate(R.layout.item_gv_doctor_forum, null, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(mDataList == null) return;
        EntityDoctorForumInfo entity = mDataList.get(position);
        if(entity != null){
            ImageLoaderManager.getInstance().displayImage(entity.getImg(), holder.mImage);
            holder.mTitle.setText(entity.getTitle());
            holder.mViews.setText(String.format("已观看%d次", entity.getViews()));
            if(entity.getExpert() != null){
                holder.mLine.setVisibility(View.VISIBLE);
                EntityExpertInfo expert = entity.getExpert();
                holder.mDoctorName.setText(expert.getName());
                holder.mDoctorJob.setText(expert.getJob());
                if(expert.getHospital() != null){
                    holder.mDoctorHospital.setText(expert.getHospital().getName());
                }
                if(expert.getDepartment() != null){
                    holder.mDoctorDepartment.setText(expert.getDepartment().getName());
                }
            } else if(entity.getOrg() !=null){
                EntityOrg org = entity.getOrg();
                holder.mLine.setVisibility(View.GONE);
                holder.mDoctorName.setText(org.getOrgName());
                holder.mDoctorJob.setText(org.getOrgIndustry());
                holder.mDoctorHospital.setText(org.getOrgCity());
                holder.mDoctorDepartment.setText("");
            }
            setListener(holder.mLayout, entity);
        }
    }

    private void setListener(View view, final EntityDoctorForumInfo item){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(item != null){
                    MediaPlayerActivity.startIntent(mCtx, item.getId());
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
        private ImageView mImage;
        private TextView mTitle;
        private TextView mViews;
        private TextView mDoctorName;
        private TextView mDoctorJob;
        private TextView mDoctorHospital;
        private TextView mDoctorDepartment;
        private View mLine;

        public ViewHolder(View itemView) {
            super(itemView);
            mLayout = GetViewUtil.getView(itemView, R.id.doctor_forum_list_item);
            mImage = GetViewUtil.getView(itemView, R.id.iv_item_doctor_forum_img);
            mTitle = GetViewUtil.getView(itemView, R.id.tv_item_doctor_forum_title);
            mViews = GetViewUtil.getView(itemView, R.id.tv_item_doctor_forum_views);
            mDoctorName = GetViewUtil.getView(itemView, R.id.tv_item_doctor_forum_doctor_name);
            mDoctorJob = GetViewUtil.getView(itemView, R.id.tv_item_doctor_forum_doctor_job);
            mDoctorHospital = GetViewUtil.getView(itemView, R.id.tv_item_doctor_forum_doctor_hospital);
            mDoctorDepartment = GetViewUtil.getView(itemView, R.id.tv_item_doctor_forum_doctor_department);
            mLine = GetViewUtil.getView(itemView, R.id.item_doctor_forum_line);
        }

    }
}
