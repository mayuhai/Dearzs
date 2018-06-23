package com.dearzs.app.chat.model;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;

import com.dearzs.app.chat.adapter.ChatAdapter;
import com.dearzs.app.chat.utils.TimeUtil;
import com.dearzs.app.util.ImageLoaderManager;
import com.tencent.TIMConversationType;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMMessage;
import com.tencent.TIMMessageStatus;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;

import java.util.ArrayList;
import java.util.List;

/**
 * 消息数据基类
 */
public abstract class Message {

    protected final String TAG = "Message";

    TIMMessage message;

    private boolean hasTime;


    public TIMMessage getMessage() {
        return message;
    }


    /**
     * 显示消息
     *
     * @param viewHolder 界面样式
     * @param context    显示消息的上下文
     */
    public abstract void showMessage(ChatAdapter.ViewHolder viewHolder, Context context);

    /**
     * 获取显示气泡
     *
     * @param viewHolder 界面样式
     */
    public RelativeLayout getBubbleView(final ChatAdapter.ViewHolder viewHolder) {
        viewHolder.systemMessage.setVisibility(hasTime ? View.VISIBLE : View.GONE);
        viewHolder.systemMessage.setText(TimeUtil.getChatTimeStr(message.timestamp()));
        if (message.isSelf()) {
            viewHolder.leftPanel.setVisibility(View.GONE);
            viewHolder.rightPanel.setVisibility(View.VISIBLE);
            ImageLoaderManager.getInstance().displayImage(MySelfInfo.getInstance().getAvatar(), viewHolder.rightAvatar);
            return viewHolder.rightMessage;
        } else {
            viewHolder.leftPanel.setVisibility(View.VISIBLE);
            viewHolder.rightPanel.setVisibility(View.GONE);
            //群聊显示名称，群名片>个人昵称>identify
            viewHolder.sender.setVisibility(View.VISIBLE);
            String name = "";
            String avatar = "";
            if (message.getConversation().getType() == TIMConversationType.Group) {
                if (message.getSenderGroupMemberProfile() != null)
                    name = message.getSenderGroupMemberProfile().getNameCard();
                if (name.equals("") && message.getSenderProfile() != null)
                    name = message.getSenderProfile().getNickName();
                if (name.equals("")) name = message.getSender();

                if (message.getSenderProfile() != null)
                    avatar = message.getSenderProfile().getFaceUrl();

            } else if (message.getConversation().getType() == TIMConversationType.C2C) {
                if (message.getSenderProfile() != null) {
                    if (name.equals("") && message.getSenderProfile() != null)
                        name = message.getSenderProfile().getNickName();
                    if (name.equals("")) name = message.getSender();
                    avatar = message.getSenderProfile().getFaceUrl();
                }
            }
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(avatar)) {
                final String sender = message.getSender();
                if (!TextUtils.isEmpty(sender)) {
                    List<String> users = new ArrayList<String>();
                    users.add(sender);
                    TIMFriendshipManager.getInstance().getUsersProfile(users, new TIMValueCallBack<List<TIMUserProfile>>() {
                        @Override
                        public void onError(int i, String s) {
                        }

                        @Override
                        public void onSuccess(List<TIMUserProfile> timUserProfiles) {
                            for (TIMUserProfile profile : timUserProfiles) {
                                if (profile.getIdentifier().equals(sender)) {
                                    viewHolder.sender.setText(profile.getNickName());
                                    ImageLoaderManager.getInstance().displayImage(profile.getFaceUrl(), viewHolder.leftAvatar);
                                    break;
                                }
                            }
                        }
                    });
                }
            } else {
                viewHolder.sender.setText(name);
                ImageLoaderManager.getInstance().displayImage(avatar, viewHolder.leftAvatar);
            }
            return viewHolder.leftMessage;
        }
    }

    /**
     * 显示消息状态
     *
     * @param viewHolder 界面样式
     */

    public void showStatus(ChatAdapter.ViewHolder viewHolder) {
        switch (message.status()) {
            case Sending:
                viewHolder.error.setVisibility(View.GONE);
                viewHolder.sending.setVisibility(View.VISIBLE);
                break;
            case SendSucc:
                viewHolder.error.setVisibility(View.GONE);
                viewHolder.sending.setVisibility(View.GONE);
                break;
            case SendFail:
                viewHolder.error.setVisibility(View.VISIBLE);
                viewHolder.sending.setVisibility(View.GONE);
                viewHolder.leftPanel.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 判断是否是自己发的
     */
    public boolean isSelf() {
        return message.isSelf();
    }

    /**
     * 获取消息摘要
     */
    public abstract String getSummary();

    /**
     * 保存消息或消息文件
     */
    public abstract void save();


    /**
     * 删除消息
     */
    public void remove() {
        if (message != null) {
            message.remove();
        }
    }

    /**
     * 是否需要显示时间获取
     */
    public boolean getHasTime() {
        return hasTime;
    }

    /**
     * 是否需要显示时间设置
     *
     * @param message 上一条消息
     */
    public void setHasTime(TIMMessage message) {
        if (message == null) {
            hasTime = true;
            return;
        }
        hasTime = this.message.timestamp() - message.timestamp() > 300;
    }


    /**
     * 消息是否发送失败
     */
    public boolean isSendFail() {
        return message.status() == TIMMessageStatus.SendFail;
    }

    /**
     * 清除气泡原有数据
     */
    protected void clearView(ChatAdapter.ViewHolder viewHolder) {
        getBubbleView(viewHolder).removeAllViews();
        getBubbleView(viewHolder).setOnClickListener(null);
    }

    /**
     * 获取发送者
     */
    public String getSender() {
        if (message.getSender() == null) return "";
        return message.getSender();
    }


}
