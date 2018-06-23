package com.dearzs.app.chat.model;

import android.content.Context;
import android.text.TextUtils;

import com.dearzs.app.R;
import com.dearzs.app.chat.ui.ChatActivity;
import com.tencent.TIMConversation;
import com.tencent.TIMConversationType;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;

import java.util.ArrayList;
import java.util.List;

/**
 * 好友或群聊的会话
 */
public class NomalConversation extends Conversation {


    private TIMConversation conversation;
    protected String avatar;

    //最后一条消息
    private Message lastMessage;


    public NomalConversation(TIMConversation conversation) {
        this.conversation = conversation;
        type = conversation.getType();
        identify = conversation.getPeer();
        if (type == TIMConversationType.Group) {
            name = GroupInfo.getInstance().getGroupName(identify);
            if (name.equals("")) name = identify;
        } else {
            FriendProfile profile = FriendshipInfo.getInstance().getProfile(identify);
            name = profile == null ? identify : profile.getName();
            avatar = profile == null ? "" : profile.getAvatarUrl();
        }
    }

    public void getFriendProfile(String peer, TIMValueCallBack<List<TIMUserProfile>> var2) {
        if (TextUtils.isEmpty(peer) || var2 == null) {
            return;
        }
        List<String> users = new ArrayList<>();
        users.add(peer);
        TIMFriendshipManager.getInstance().getUsersProfile(users, var2);
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }


    @Override
    public int getAvatar() {
        switch (type) {
            case C2C:
                return R.mipmap.head_other;
            case Group:
                return R.mipmap.head_group;
        }
        return 0;
    }

    /**
     * 跳转到聊天界面或会话详情
     *
     * @param context 跳转上下文
     */
    @Override
    public void navToDetail(Context context) {
        ChatActivity.startIntent(context, identify, false, type);
    }

    /**
     * 获取最后一条消息摘要
     */
    @Override
    public String getLastMessageSummary() {
        if (lastMessage == null) return "";
        return lastMessage.getSummary();
    }

    /**
     * 获取未读消息数量
     */
    @Override
    public long getUnreadNum() {
        if (conversation == null) return 0;
        return conversation.getUnreadMessageNum();
    }

    /**
     * 将所有消息标记为已读
     */
    @Override
    public void readAllMessage() {
        if (conversation != null) {
            conversation.setReadMessage();
        }
    }


    /**
     * 获取最后一条消息的时间
     */
    @Override
    public long getLastMessageTime() {
        if (lastMessage == null) return 0;
        return lastMessage.getMessage().timestamp();
    }

    /**
     * 获取会话类型
     */
    public TIMConversationType getType() {
        return conversation.getType();
    }

    public String getAvatarUrl() {
        return avatar;
    }

    public String getPeer(){
        if (conversation != null) {
            return conversation.getPeer();
        }
        return "";
    }
}
