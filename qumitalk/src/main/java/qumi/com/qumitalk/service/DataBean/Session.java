package qumi.com.qumitalk.service.DataBean;

import java.io.Serializable;

/**
 * Created by mwang on 2018/5/17.
 * 会话数据bean
 */
@SuppressWarnings("serial")
public class Session implements Serializable{
	private String id;
	private String fromUser;		//发送人
	private int type;		//消息类型
	private String date;		//接收时间
	private String content;		//发送内容
	private String notReadCount;//未读记录
	private String toUser;		//接收人

	private String sendUser; //当为群聊时，sendUser为发送者，fromUser为群ID
	private String isdispose;//是否已处理 0未处理，1已处理
	private int chatType;//聊天类型


	public int getChatType() {
		return chatType;
	}

	public void setChatType(int chatType) {
		this.chatType = chatType;
	}



	public String getSendUser() {
		return sendUser;
	}

	public void setSendUser(String sendUser) {
		this.sendUser = sendUser;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFromUser() {
		return fromUser;
	}

	public void setFromUser(String fromUser) {
		this.fromUser = fromUser;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getNotReadCount() {
		return notReadCount;
	}

	public void setNotReadCount(String notReadCount) {
		this.notReadCount = notReadCount;
	}

	public String getToUser() {
		return toUser;
	}

	public void setToUser(String toUser) {
		this.toUser = toUser;
	}

	public String getIsdispose() {
		return isdispose;
	}

	public void setIsdispose(String isdispose) {
		this.isdispose = isdispose;
	}
}
