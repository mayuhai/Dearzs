package com.dearzs.app.entity;

import com.dearzs.app.chat.model.NomalConversation;

import java.io.Serializable;

/**
 * 消息实体类
 */
public class EntityMessage implements Serializable {
    //回话类型
    public static final int TYPE_CVS = 9001;

    private String content;
    private long createTime;
    private long id;
    private String orderNo;
    private String title;
    private long uid;
    private int type;
    private NomalConversation conversation;

    public int getType() {
        return type;
    }

    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public String getTitle() {
        return title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public long getCreateTime() {
        return createTime;
    }

    public long getUid() {
        return uid;
    }


    //去重用的，所有需要下拉刷新上拉加载更多的实体类都需要重写equals方法
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o.getClass() == EntityMessage.class) {
            EntityMessage n = (EntityMessage) o;
            return n.id == id;
        }
        return false;
    }

    public NomalConversation getConversation() {
        return conversation;
    }

    public void setConversation(NomalConversation conversation) {
        this.conversation = conversation;
    }
}
