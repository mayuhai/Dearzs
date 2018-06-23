package com.dearzs.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dearzs.app.R;
import com.dearzs.app.chat.ui.ChatActivity;
import com.dearzs.app.chat.utils.TimeUtil;
import com.dearzs.app.entity.EntityMessage;
import com.dearzs.app.util.ImageLoaderManager;
import com.dearzs.app.util.Utils;
import com.dearzs.app.widget.CircleImageView;
import com.dearzs.commonlib.utils.GetViewUtil;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;

import java.util.List;

/**
 * 消息列表适配器
 */
public class LvMessageListAdapter extends Adapter<LvMessageListAdapter.ViewHolder> {
    private Context mCtx;
    private List<EntityMessage> mDataList;


    public LvMessageListAdapter(Context context, List<EntityMessage> carList) {
        mCtx = context;
        this.mDataList = carList;
    }

    public void notifyData(List<EntityMessage> dataList, boolean isRefresh) {
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
        View view = LayoutInflater.from(mCtx).inflate(R.layout.item_lv_message_list, null, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (mDataList == null) return;
        final EntityMessage entity = mDataList.get(position);
        if (entity != null) {
            if (entity.getType() == EntityMessage.TYPE_CVS) {
                final String peer = entity.getConversation().getPeer();
                entity.getConversation().getFriendProfile(peer, new TIMValueCallBack<List<TIMUserProfile>>() {
                    @Override
                    public void onError(int i, String s) {
                    }

                    @Override
                    public void onSuccess(List<TIMUserProfile> timUserProfiles) {
                        if (timUserProfiles != null) {
                            for (TIMUserProfile profile : timUserProfiles) {
                                if (profile.getIdentifier().equals(peer)) {
                                    ImageLoaderManager.getInstance().displayImage(profile.getFaceUrl(), holder.mMessageIcon);
                                    holder.mMessageConstent.setText(entity.getConversation().getLastMessageSummary());
                                    holder.mMessageTitle.setText(profile.getNickName());
                                    holder.mTime.setText(TimeUtil.getYmd(entity.getConversation().getLastMessageTime()));
                                    break;
                                }
                            }
                        }
                    }
                });
            } else {
                if (1 == entity.getType()) {
                    holder.mMessageIcon.setImageResource(R.mipmap.ic_message_other);
                } else {
                    holder.mMessageIcon.setImageResource(R.mipmap.ic_message_other);
                }
                holder.mMessageConstent.setText(entity.getContent());
                holder.mMessageTitle.setText(entity.getTitle());
                holder.mTime.setText(Utils.getTimeStamp(entity.getCreateTime()));
            }
            setListener(holder.mLayout, entity);
        }
    }

    private void setListener(View view, final EntityMessage consultation) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (consultation != null) {
                    if (consultation.getType() == EntityMessage.TYPE_CVS) {
                        ChatActivity.startIntent(mCtx, consultation.getConversation().getIdentify(), false, consultation.getConversation().getType());
                    } else {
                        //Bundle bundle = new Bundle();
                        //bundle.putSerializable(Constant.WEB_PAGE_URL, consultation);
                        //Intent intent = new Intent(mCtx, MedicalConsultationWebViewActivity.class);
                        //intent.putExtras(bundle);
                        //((BaseActivity)mCtx).startActivityForResult(intent, Activity.RESULT_OK);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout mLayout;
        private CircleImageView mMessageIcon;
        private TextView mMessageTitle;
        private TextView mMessageConstent;
        private TextView mTime;

        public ViewHolder(View itemView) {
            super(itemView);
            mLayout = GetViewUtil.getView(itemView, R.id.message_list_item);
            mMessageIcon = GetViewUtil.getView(itemView, R.id.iv_item_message_icon);
            mMessageTitle = GetViewUtil.getView(itemView, R.id.tv_message_type);
            mTime = GetViewUtil.getView(itemView, R.id.tv_message_time);
            mMessageConstent = GetViewUtil.getView(itemView, R.id.tv_message_constent);
        }

    }
}
