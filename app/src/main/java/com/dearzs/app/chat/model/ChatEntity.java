package com.dearzs.app.chat.model;

/**
 * 消息体类
 */
public class ChatEntity {
	
	private String grpSendName;
	private String context;
	private int  type;

	public ChatEntity() {
	}

	public String getSenderName() {
		return grpSendName;
	}

	public void setSenderName(String grpSendName) {
		this.grpSendName = grpSendName;
	}
		
	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
}
