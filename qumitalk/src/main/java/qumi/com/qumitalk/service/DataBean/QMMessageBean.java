package qumi.com.qumitalk.service.DataBean;

import android.text.TextUtils;
import android.util.Base64;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import qumi.com.qumitalk.service.Config.StaticConfig;

/**
 * Created by mwang on 2018/5/17.
 * 聊天数据bean
 */
@SuppressWarnings("serial")
public class QMMessageBean implements Serializable {
	private int msgId;//id
	private String fromUser;//发送者
	private String toUser;//接收者
	private int type;//信息类型
	private String content;//信息内容
	private int isComing;//0表接收的消息，1表发送的消息
	private String date;//时间
	private String isReaded;//是否已读
	private int chatType;//聊天类型
	private String sendUser; //当为群聊时，sendUser为发送者，fromUser为群ID

	public String getSendUser() {
		return sendUser;
	}

	public void setSendUser(String sendUser) {
		this.sendUser = sendUser;
	}

	public int getChatType() {
		return chatType;
	}

	public void setChatType(int chatType) {
		this.chatType = chatType;
	}

	private JSONObject attributeJson;

	private QMMessageBean(){}

	public String toBase64Json(){
		String json = JSON.toJSONString(this);
		return new String(android.util.Base64.encode(json.getBytes(), Base64.NO_WRAP));
	}

	public static QMMessageBean decodeBase64Json(String s){
		String json = new String(android.util.Base64.decode( s.getBytes(), Base64.NO_WRAP));
		return JSONObject.parseObject(json,QMMessageBean.class);
	}

	public static QMMessageBean createEmptyMessage(){
		QMMessageBean qmMessageBean = new QMMessageBean();
		return qmMessageBean;
	}
	public static QMMessageBean createTextMessage(String content,String toUser,String fromUser){
		SimpleDateFormat sd = new SimpleDateFormat("MM-dd HH:mm");
		String time=sd.format(new Date());
		QMMessageBean qmMessageBean = new QMMessageBean();
		qmMessageBean.setContent(content);
		qmMessageBean.setFromUser(fromUser);
		qmMessageBean.setToUser(toUser);
		qmMessageBean.setDate(time);
		qmMessageBean.setType(StaticConfig.MSG_TYPE_TEXT);
		return qmMessageBean;
	}

	public static QMMessageBean createFriendMessage(String content,String toUser,String fromUser){
		SimpleDateFormat sd = new SimpleDateFormat("MM-dd HH:mm");
		String time=sd.format(new Date());
		QMMessageBean qmMessageBean = new QMMessageBean();
		qmMessageBean.setContent(content);
		qmMessageBean.setFromUser(fromUser);
		qmMessageBean.setToUser(toUser);
		qmMessageBean.setDate(time);
		qmMessageBean.setType(StaticConfig.MSG_TYPE_ADD_FRIEND);
		return qmMessageBean;
	}

	public static QMMessageBean createReceivedFriendMessage(String toUser,String fromUser){
		SimpleDateFormat sd = new SimpleDateFormat("MM-dd HH:mm");
		String time=sd.format(new Date());
		QMMessageBean qmMessageBean = new QMMessageBean();
		qmMessageBean.setFromUser(fromUser);
		qmMessageBean.setToUser(toUser);
		qmMessageBean.setDate(time);
		qmMessageBean.setType(StaticConfig.MSG_TYPE_ADD_FRIEND_SUCCESS);
		return qmMessageBean;
	}
	public void setAttributeJson(String attributeJson) {
		if(TextUtils.isEmpty(attributeJson)){
			return;
		}
		this.attributeJson = JSONObject.parseObject(attributeJson);
	}

	public String getAttributeJson() {
		if(attributeJson == null){
			return "";
		}
		return attributeJson.toJSONString();
	}

	public void setAttribute(String attribute, Object value){
		if(attributeJson == null){
			attributeJson  = new JSONObject();
		}
		attributeJson.put(attribute,value);
	}

	public Object getAttribute(String key){
		if(attributeJson == null || !attributeJson.containsKey(key)){
			throw new IllegalArgumentException("cannot find the corresponding value");
		}

		return getAttributeWithDefaultValue(key , "");
	}

	public Object getAttributeWithDefaultValue(String key ,Object def){
		if(attributeJson == null || !attributeJson.containsKey(key)){
			 return def;
		}
		return attributeJson.get(key);
	}

	public int getMsgId() {
		return msgId;
	}

	public void setMsgId(int msgId) {
		this.msgId = msgId;
	}

	public String getFromUser() {
		return fromUser;
	}

	public void setFromUser(String fromUser) {
		this.fromUser = fromUser;
	}

	public String getToUser() {
		return toUser;
	}

	public void setToUser(String toUser) {
		this.toUser = toUser;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getIsComing() {
		return isComing;
	}

	public void setIsComing(int isComing) {
		this.isComing = isComing;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getIsReaded() {
		return isReaded;
	}

	public void setIsReaded(String isReaded) {
		this.isReaded = isReaded;
	}
}
