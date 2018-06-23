package com.dearzs.app.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.dearzs.app.R;
import com.dearzs.app.activity.expert.ExpertDetailsActivity;
import com.dearzs.app.base.BaseActivity;
import com.dearzs.app.entity.EntityDynamicInfo;
import com.dearzs.app.entity.EntityExpertInfo;
import com.dearzs.app.util.Constant;
import com.dearzs.app.util.ImageLoaderManager;
import com.dearzs.app.util.ToastUtil;
import com.dearzs.app.util.Utils;
import com.dearzs.commonlib.utils.GetViewUtil;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 订单列表适配器
 */
public class LvExpertListAdapter extends RecyclerView.Adapter<LvExpertListAdapter.ViewHolder> {
    private Context mCtx;
    private List<EntityExpertInfo> mDataList;
    private boolean mIsSelectDoctor = false;
    private long mSelectedExpertId = 0;    //选择的专家的id（选择的专家不能跟主治医生为同一个人）


    public LvExpertListAdapter(Context context, List<EntityExpertInfo> carList, boolean isSelectDoctor, long selectedExpertId){
        mCtx = context;
        this.mDataList = carList;
        this.mIsSelectDoctor = isSelectDoctor;
        this.mSelectedExpertId = selectedExpertId;
    }

    public LvExpertListAdapter(Context context, List<EntityExpertInfo> carList){
        mCtx = context;
        this.mDataList = carList;
    }


    public void notifyData(List<EntityExpertInfo> dataList, boolean isRefresh) {
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
        View view = LayoutInflater.from(mCtx).inflate(R.layout.item_lv_expert_list, null, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(mDataList == null) return;
        EntityExpertInfo entity = mDataList.get(position);
        if(entity != null){
            holder.mVisitFeeLayout.setVisibility(mIsSelectDoctor ? View.GONE : View.VISIBLE);
            holder.mSelectBtn.setVisibility(mIsSelectDoctor ? View.VISIBLE : View.GONE);
            ImageLoaderManager.getInstance().displayImage(entity.getAvatar(), holder.mPhoto);
            holder.mTitle.setText(entity.getJob());
            holder.mName.setText(entity.getName());
            holder.mCommentCount.setText(entity.getComments() + "条评论");
            if(entity.getHospital() != null){
                holder.mHospital.setText(entity.getHospital().getName());
            }
            if(entity.getDepartment() != null){
                holder.mDepartment.setText(entity.getDepartment().getName());
            }
            holder.mZhuanTag.setVisibility(entity.getReferralState() == 1 ? View.VISIBLE: View.GONE);  //转诊状态，1开启0关闭
            holder.mMoney.setText(entity.getVisitMoney() + "");
            holder.mTime.setText("预约" + entity.getOrderNumber() + "次");
            holder.mRatingBar.setRating((float) entity.getStar());
            setListener(holder, entity);
        }
    }

    private void setListener(ViewHolder holder, final EntityExpertInfo expertInfo){
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSelectedExpertId == expertInfo.getId()){
                    ToastUtil.showLongToast("主治医生和专家不能为同一人");
                    return;
                }
                if(Utils.getUserId() == expertInfo.getId()){
                    ToastUtil.showLongToast("不能选择自己作为主治医生");
                } else {
                    ExpertDetailsActivity.startIntent(mCtx, String.valueOf(expertInfo.getId()), Constant.KEY_CONSULT_NORMAL, mIsSelectDoctor);
                }
            }
        });
        holder.mSelectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSelectedExpertId == expertInfo.getId()){
                    ToastUtil.showLongToast("主治医生和专家不能为同一人");
                    return;
                }
                if(Utils.getUserId() == expertInfo.getId()){
                    ToastUtil.showLongToast("不能选择自己作为主治医生");
                } else {
                    ((BaseActivity)mCtx).finish();
                    EventBus.getDefault().post(expertInfo);
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
        private LinearLayout mVisitFeeLayout;
        private RatingBar mRatingBar;
        private ImageView mPhoto;
        private ImageView mZhuanTag;
        private TextView mName;
        private TextView mTitle;
        private TextView mCommentCount;
        private TextView mHospital;
        private TextView mDepartment;
        private TextView mMoney;
        private TextView mTime;
        private Button mSelectBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            mLayout = GetViewUtil.getView(itemView, R.id.consultation_list_item);
            mVisitFeeLayout = GetViewUtil.getView(itemView, R.id.lin_expert_visit_fee);
            mRatingBar = GetViewUtil.getView(itemView, R.id.tv_item_expert_rating);
            mPhoto = GetViewUtil.getView(itemView, R.id.iv_item_consultation_icon);
            mZhuanTag = GetViewUtil.getView(itemView, R.id.tv_item_expert_can);
            mName = GetViewUtil.getView(itemView, R.id.tv_item_expert_name);
            mTime = GetViewUtil.getView(itemView, R.id.tv_item_expert_visit_time);
            mTitle = GetViewUtil.getView(itemView, R.id.tv_item_expert_tile);
            mCommentCount = GetViewUtil.getView(itemView, R.id.tv_item_expert_comment_count);
            mHospital = GetViewUtil.getView(itemView, R.id.tv_item_expert_hospital);
            mDepartment = GetViewUtil.getView(itemView, R.id.tv_item_expert_department);
            mMoney = GetViewUtil.getView(itemView, R.id.tv_item_expert_visit_money);
            mSelectBtn = GetViewUtil.getView(itemView, R.id.bt_select_doctor);
        }

    }
}
