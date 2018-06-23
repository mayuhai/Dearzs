package com.dearzs.app.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dearzs.app.BaseApplication;
import com.dearzs.app.R;
import com.dearzs.app.activity.communtity.DynamicDetailsActivity;
import com.dearzs.app.activity.communtity.ReleaseCommentActivity;
import com.dearzs.app.base.BaseActivity;
import com.dearzs.app.entity.EntityBase;
import com.dearzs.app.entity.EntityDynamicInfo;
import com.dearzs.app.entity.EntityUserInfo;
import com.dearzs.app.util.Constant;
import com.dearzs.app.util.DimenUtils;
import com.dearzs.app.util.ImageLoaderManager;
import com.dearzs.app.util.ReqManager;
import com.dearzs.app.util.ToastUtil;
import com.dearzs.app.util.Utils;
import com.dearzs.commonlib.okhttp.callback.Callback;
import com.dearzs.commonlib.utils.GetViewUtil;
import com.dearzs.commonlib.utils.LayoutUtil;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.List;

import okhttp3.Call;
import okhttp3.Request;

/**
 * 社区 -- 动态列表适配器
 */
public class LvCommuntityDynamicListAdapter extends RecyclerView.Adapter<LvCommuntityDynamicListAdapter.ViewHolder> {
    protected Context mContext;
    public List<EntityDynamicInfo> mData = null;
    private boolean mIsPraised = false;
    private int mPosition;
    private int mDelPosition;
    private long mPraiseCount = 0;
    private long mCommentCount = 0;
    private ViewHolder mViewHolder;
    private Drawable mImgPraised, mImgNotPraised;


    public LvCommuntityDynamicListAdapter(Context mCtx, List<EntityDynamicInfo> dataList) {
        this.mContext = mCtx;
        this.mData = dataList;
        Resources res = mContext.getResources();
        mImgNotPraised = res.getDrawable(R.mipmap.ic_zan_normal);
        mImgPraised = res.getDrawable(R.mipmap.ic_zan_praised);
        mImgNotPraised.setBounds(0, 0, mImgNotPraised.getMinimumWidth(), mImgNotPraised.getMinimumHeight());
        mImgPraised.setBounds(0, 0, mImgPraised.getMinimumWidth(), mImgPraised.getMinimumHeight());
    }

    public void notifyData(List<EntityDynamicInfo> dataList, boolean isRefresh) {
        if (dataList != null) {
            if (isRefresh) {      //是下拉刷新而不是上拉加载
                this.mData.clear();
                this.mData.addAll(dataList);
            } else {        //下拉加载更多
                this.mData.addAll(dataList);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_lv_communtity_dynamic_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mData == null) return;

        EntityDynamicInfo entity = mData.get(position);
        if (entity != null) {
            EntityDynamicInfo.EntityDynamicUser user = entity.getUser();
            if (user != null) {
                holder.mName.setText(user.getName());
                holder.mUserJob.setText(TextUtils.isEmpty(user.getJob()) ? "" : user.getJob());
                ImageLoaderManager.getInstance().displayImage(user.getAvatar(), holder.mUserPhoto);
            }
            holder.mContent.setText(entity.getContent());
            holder.mTvCommentCount.setText(entity.getComments() + "");
            holder.mTvPraiseCount.setText(entity.getPraise() + "");
            holder.mTvPraiseCount.setCompoundDrawables(entity.getIsPraise() == 1 ? mImgPraised : mImgNotPraised, null, null, null); //设置左图标
            holder.mTime.setText(Utils.getTimeStamp(entity.getCreateTime()));
            holder.bindData(mContext, entity);
            holder.mTvCommentCount.setTag(position);
            holder.mTvPraiseCount.setTag(position);
            EntityUserInfo currentUserInfo = BaseApplication.getInstance().getUserInfo();
            if(currentUserInfo != null && user != null){
                holder.mDelImg.setVisibility(currentUserInfo.getId() == user.getId() ? View.VISIBLE : View.INVISIBLE);
            }
            setListener(entity, holder, position);
        }
    }

    private void setListener(final EntityDynamicInfo entity, final ViewHolder holder, final int position) {
        if(entity == null || holder == null || position < 0){
            return;
        }
        holder.mDelImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //删除动态处理
                mDelPosition = position;
                ((BaseActivity)mContext).showConfirmDialog((BaseActivity) mContext, "确定要删除您的这条动态吗？", new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        ReqManager.getInstance().reqDelDynamicPraise(reqDelDynamicCallback, Utils.getUserToken(mContext), entity.getId());
                    }
                }, null);
            }
        });

        holder.mPraiseCountLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsPraised = entity.getIsPraise() == 1;
                mPraiseCount = entity.getPraise();
                mCommentCount = entity.getComments();
                mPosition = position;
                mViewHolder = holder;
                //点赞处理
                ReqManager.getInstance().reqDealDynamicPraise(reqDealPraiseCallback, Utils.getUserToken(mContext), entity.getId(), mIsPraised ? 0 : 1);
            }
        });

        holder.mCommentCountLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(entity.getComments() <= 0){
                    ReleaseCommentActivity.startIntent(mContext, entity.getId(), Constant.KEY_RELEASE_COMMET_TYPE_DYNAMIC);
                } else {
                    DynamicDetailsActivity.startIntent(mContext, entity.getId(), entity);
                }
            }
        });

        holder.mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DynamicDetailsActivity.startIntent(mContext, entity.getId(), entity);
            }
        });
    }

    //删除动态接口回调
    Callback<EntityBase> reqDelDynamicCallback = new Callback<EntityBase>() {
        @Override
        public void onError(Call call, Exception e) {
            ((BaseActivity) mContext).closeProgressDialog();
            //请求失败，设置加载和刷新完毕
            ((BaseActivity) mContext).onFailure(e.toString());
        }

        @Override
        public void onResponse(EntityBase response) {
            ((BaseActivity) mContext).closeProgressDialog();
            //请求成功，设置加载和刷新完毕
            if (response.getCode().equals("000000")) {
                ToastUtil.showShortToast("删除动态成功");
                mData.remove(mDelPosition);
                notifyDataSetChanged();
                mDelPosition = -1;
            } else {
                ToastUtil.showShortToast(response.getMsg());
            }
        }

        @Override
        public void onBefore(Request request) {
            ((BaseActivity) mContext).showProgressDialog();
            super.onBefore(request);
        }
    };

    //赞和取消赞处理接口回调
    Callback<EntityBase> reqDealPraiseCallback = new Callback<EntityBase>() {
        @Override
        public void onError(Call call, Exception e) {
            ((BaseActivity) mContext).closeProgressDialog();
            //请求失败，设置加载和刷新完毕
            ((BaseActivity) mContext).onFailure(e.toString());
        }

        @Override
        public void onResponse(EntityBase response) {
            ((BaseActivity) mContext).closeProgressDialog();
            //请求成功，设置加载和刷新完毕
            if (response.getCode().equals("000000")) {
                mIsPraised = !mIsPraised;
                mData.get(mPosition).setIsPraise(mIsPraised ? 1 : 0);
                mPraiseCount = mIsPraised ? ++mPraiseCount : --mPraiseCount;
                mData.get(mPosition).setPraise(mPraiseCount);
                if (mViewHolder.mTvPraiseCount.getTag() != null && mViewHolder.mTvPraiseCount.getTag().toString().equals(String.valueOf(mPosition))) {
                    mViewHolder.mTvPraiseCount.setCompoundDrawables(mIsPraised ? mImgPraised : mImgNotPraised, null, null, null); //设置左图标
                    mViewHolder.mTvPraiseCount.setText(mPraiseCount + "");
                }
                String toast = mIsPraised ? "点赞成功" : "取消赞成功";
                ToastUtil.showShortToast(toast);
            } else {
                ToastUtil.showShortToast(response.getMsg());
            }
        }

        @Override
        public void onBefore(Request request) {
            ((BaseActivity) mContext).showProgressDialog();
            super.onBefore(request);
        }
    };

    @Override
    public int getItemCount() {
        return (mData == null ? 0 : mData.size());
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout mLayout;
        private ImageView mUserPhoto;
        private TextView mName;
        private TextView mUserJob;
        private TextView mContent;
        private TextView mTime;
        private TextView mTvPraiseCount;
        private LinearLayout mPraiseCountLayout;
        private TextView mTvCommentCount;
        private LinearLayout mCommentCountLayout;
        private XRecyclerView mRecycleView;
        private ImageView mDelImg;

        public ViewHolder(View itemView) {
            super(itemView);
            mLayout = GetViewUtil.getView(itemView, R.id.communitity_dynamic_list_item);
            mUserPhoto = GetViewUtil.getView(itemView, R.id.tv_community_dynamic_photo);
            mUserJob = GetViewUtil.getView(itemView, R.id.tv_community_dynamic_user_job);
            mName = GetViewUtil.getView(itemView, R.id.tv_community_dynamic_name);
            mContent = GetViewUtil.getView(itemView, R.id.tv_community_dynamic_content);
            mTime = GetViewUtil.getView(itemView, R.id.tv_community_dynamic_time);
            mTvPraiseCount = GetViewUtil.getView(itemView, R.id.tv_community_dynamic_praise_count);
            mPraiseCountLayout = GetViewUtil.getView(itemView, R.id.lin_community_dynamic_praise_count);
            mTvCommentCount = GetViewUtil.getView(itemView, R.id.tv_community_dynamic_comment_count);
            mCommentCountLayout = GetViewUtil.getView(itemView, R.id.lin_community_dynamic_comment_count);
            mRecycleView = GetViewUtil.getView(itemView, R.id.lin_community_dynamic_img_layout);
            mDelImg = GetViewUtil.getView(itemView, R.id.iv_delect_dynamic);
        }

        public void bindData(Context mCtx, EntityDynamicInfo category) {
            if (category == null || category.getImages() == null || category.getImages().size() == 0) {
                mRecycleView.setVisibility(View.GONE);
                return;
            } else {
                mRecycleView.setVisibility(View.VISIBLE);
            }
            int imageSize = category.getImages().size();

            int columns = imageSize > 3 ? 2 : imageSize;     //小于三张，则三列，否则 2列

            GridLayoutManager layoutManager = new GridLayoutManager(mCtx, columns);
            mRecycleView.setLayoutManager(layoutManager);
            int line = imageSize > 3 ? 2 : 1;
            //100 位行高
            LayoutUtil.setLayout(mRecycleView, DimenUtils.dip2px(mCtx, 80) * line , DimenUtils.dip2px(mCtx, 85) * columns);
            mRecycleView.setAdapter(new GvDynamicImageAdapter(mCtx, category.getImages()));
        }
    }


}
