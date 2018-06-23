package com.dearzs.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dearzs.app.BaseApplication;
import com.dearzs.app.R;
import com.dearzs.app.base.BaseActivity;
import com.dearzs.app.entity.EntityBase;
import com.dearzs.app.entity.EntityComment;
import com.dearzs.app.entity.EntityDynamicComment;
import com.dearzs.app.entity.EntityUser;
import com.dearzs.app.util.ImageLoaderManager;
import com.dearzs.app.util.ReqManager;
import com.dearzs.app.util.ToastUtil;
import com.dearzs.app.util.Utils;
import com.dearzs.app.widget.CircleImageView;
import com.dearzs.commonlib.okhttp.callback.Callback;
import com.dearzs.commonlib.utils.GetViewUtil;

import java.util.List;

import de.greenrobot.event.EventBus;
import okhttp3.Call;
import okhttp3.Request;

/**
 * 动态详情页-动态评论列表适配器
 */
public class LvDynamicCommentListAdapter extends Adapter<LvDynamicCommentListAdapter.ViewHolder> {
    private Context mCtx;
    private List<EntityDynamicComment> mDataList;
    private int mPosition = -1;

    public LvDynamicCommentListAdapter(Context context, List<EntityDynamicComment> carList){
        mCtx = context;
        mDataList = carList;
    }

    public void notifyData(List<EntityDynamicComment> dataList, boolean isRefresh) {

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
        View view = LayoutInflater.from(mCtx).inflate(R.layout.item_lv_dynamic_comment, null, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if(mDataList == null) return;
        final EntityDynamicComment entityComment = mDataList.get(position);
        if(entityComment != null){
            holder.mTime.setText(Utils.getTimeStamp(entityComment.getCreateTime()));
            holder.mComment.setText(entityComment.getComment());
            EntityUser user = entityComment.getUser();
            if(user != null){
                holder.mName.setText(user.getName());
                ImageLoaderManager.getInstance().displayImage(user.getAvatar(), holder.mPhoto);
                if(BaseApplication.getInstance().getUserInfo() != null){
                    holder.mDelImg.setVisibility((String.valueOf(BaseApplication.getInstance().getUserInfo().getId()).equals(user.getId())) ? View.VISIBLE : View.GONE);
                    //TODO 改为Id的对比
                } else {
                    holder.mDelImg.setVisibility(View.GONE);
                }
            }
            holder.mDelImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((BaseActivity)mCtx).showConfirmDialog(mCtx, "确认要删除该条评论吗？", "确定", "取消", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mPosition = position;
                            ReqManager.getInstance().reqDelDynamicComment(reqDelCommentCallback, entityComment.getId(), Utils.getUserToken(mCtx));
                        }
                    }, null);
                }
            });
        }
    }


    //赞和取消赞处理接口回调
    Callback<EntityBase> reqDelCommentCallback = new Callback<EntityBase>() {
        @Override
        public void onError(Call call, Exception e) {
            ((BaseActivity)mCtx).closeProgressDialog();
            //请求失败，设置加载和刷新完毕
            ((BaseActivity)mCtx).onFailure(e.toString());
        }

        @Override
        public void onResponse(EntityBase response) {
            ((BaseActivity)mCtx).closeProgressDialog();
            //请求成功，设置加载和刷新完毕
            if (response.getCode().equals("000000")) {
                ToastUtil.showShortToast("删除评论成功");
                if(mPosition >= 0 && mDataList.size() > mPosition){
                    mDataList.remove(mPosition);
                }
                EventBus.getDefault().post("delete");
                notifyDataSetChanged();
            } else {
                ToastUtil.showShortToast(response.getMsg());
            }
        }

        @Override
        public void onBefore(Request request) {
            ((BaseActivity)mCtx).showProgressDialog();
            super.onBefore(request);
        }
    };

    @Override
    public int getItemCount() {
        return mDataList != null ? mDataList.size() : 0;
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder{
        CircleImageView mPhoto;
        ImageView mDelImg;
        TextView mTime;
        TextView mName;
        TextView mComment;

        public ViewHolder(View itemView) {
            super(itemView);
            mName = GetViewUtil.getView(itemView, R.id.item_dynamic_comment_user_nickname);
            mPhoto = GetViewUtil.getView(itemView, R.id.item_dynamic_comment_user_photo);
            mDelImg = GetViewUtil.getView(itemView, R.id.item_dynamic_comment_del);
            mTime = GetViewUtil.getView(itemView, R.id.item_dynamic_comment_evaluation_time);
            mComment = GetViewUtil.getView(itemView, R.id.item_dynamic_comment_content);
        }
    }
}