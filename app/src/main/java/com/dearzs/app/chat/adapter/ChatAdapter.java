package com.dearzs.app.chat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dearzs.app.R;
import com.dearzs.app.chat.model.Message;
import com.dearzs.commonlib.utils.GetViewUtil;

import java.util.List;

/**
 * 聊天界面adapter
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private Context context;
    private List<Message> mDataList;

    public ChatAdapter(Context context, List<Message> objects) {
        this.context = context;
        this.mDataList = objects;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_message, null, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mDataList == null) return;
        Message entity = mDataList.get(position);
        if (entity != null) {
            entity.showMessage(holder, context);
        }
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
            leftMessage = GetViewUtil.getView(itemView, R.id.leftMessage);
            rightMessage = GetViewUtil.getView(itemView, R.id.rightMessage);
            leftPanel = GetViewUtil.getView(itemView, R.id.leftPanel);
            rightPanel = GetViewUtil.getView(itemView, R.id.rightPanel);
            sending = GetViewUtil.getView(itemView, R.id.sending);
            error = GetViewUtil.getView(itemView, R.id.sendError);
            sender = GetViewUtil.getView(itemView, R.id.sender);
            systemMessage = GetViewUtil.getView(itemView, R.id.systemMessage);
            leftAvatar = GetViewUtil.getView(itemView, R.id.leftAvatar);
            rightAvatar = GetViewUtil.getView(itemView, R.id.rightAvatar);
        }

        public RelativeLayout leftMessage;
        public RelativeLayout rightMessage;
        public RelativeLayout leftPanel;
        public RelativeLayout rightPanel;
        public ProgressBar sending;
        public ImageView error;
        public TextView sender;
        public TextView systemMessage;
        public ImageView leftAvatar;
        public ImageView rightAvatar;
    }
}
