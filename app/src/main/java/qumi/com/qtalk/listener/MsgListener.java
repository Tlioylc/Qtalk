package qumi.com.qtalk.listener;

import qumi.com.qtalk.R;
import qumi.com.qumitalk.service.DataBean.QMMessageBean;
import qumi.com.qumitalk.service.Db.Session;
import qumi.com.qumitalk.service.QtalkClient;
import qumi.com.qtalk.service.MsfService;
import qumi.com.qtalk.util.Const;
import qumi.com.qtalk.util.PreferencesUtils;
import qumi.com.qumitalk.service.Imp.QMMessageListenerImp;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;

/**
 * @author baiyuliang
 */

@SuppressWarnings("static-access")
public class MsgListener implements QMMessageListenerImp {
	
	private MsfService context;
	private NotificationManager mNotificationManager;
	
	
	private Notification mNotification;
	private KeyguardManager mKeyguardManager = null;
	
	private boolean isShowNotice=false;
	public MsgListener(MsfService context,NotificationManager mNotificationManager){
		this.context=context;
		this.mNotificationManager=mNotificationManager;
		mKeyguardManager = (KeyguardManager) context.getSystemService(context.KEYGUARD_SERVICE);    
	}

	
	void sendNewMsg(QMMessageBean QMMessageBean){
		Intent intent=new Intent(Const.ACTION_NEW_MSG);//发送广播到聊天界面
		Bundle b=new Bundle();
		b.putSerializable("QMMessageBean", QMMessageBean);
		intent.putExtra("QMMessageBean", b);
		context.sendBroadcast(intent);
	}
	
	@SuppressWarnings("deprecation")
	public void showNotice(String content) {
		// 更新通知栏
		CharSequence tickerText = content;
		mNotification = new Notification(R.drawable.ic_notice, tickerText, System.currentTimeMillis());
		mNotification.flags |= Notification.FLAG_AUTO_CANCEL;
		if(PreferencesUtils.getSharePreBoolean(context, Const.MSG_IS_VOICE)){
			// 设置默认声音
			mNotification.defaults |= Notification.DEFAULT_SOUND;
		}
		if(PreferencesUtils.getSharePreBoolean(context, Const.MSG_IS_VIBRATE)){
			// 设定震动(需加VIBRATE权限)
			mNotification.defaults |= Notification.DEFAULT_VIBRATE;
		}
		// LED灯
		mNotification.defaults |= Notification.DEFAULT_LIGHTS;
		mNotification.ledARGB = 0xff00ff00;
		mNotification.ledOnMS = 500;
		mNotification.ledOffMS = 1000;
		mNotification.flags |= Notification.FLAG_SHOW_LIGHTS;
//		mNotification.setLatestEventInfo(context, "新消息", tickerText, null);
//		mNotificationManager.notify(Const.NOTIFY_ID, mNotification);// 通知
	}
	@Override
	public void onMessageReceived(QMMessageBean messageBean) {
		String to= messageBean.getToUser();
		String from= messageBean.getFromUser();;//发送者，谁给你发的消息
		int msgtype= messageBean.getType();//消息类型
		String msgcontent= messageBean.getContent();//消息内容
		String msgtime= messageBean.getDate();//消息时间

		Session session=new Session();

		if(messageBean.getChatType() == 1){
			session.setChatType(1);
			session.setToUser(QtalkClient.getInstance().getUser());
			session.setFromUser(to);
		}else {
			session.setChatType(0);
			session.setFromUser(from);
			session.setToUser(to);
		}
		session.setNotReadCount("");//未读消息数量
		session.setDate(msgtime);
		if(msgtype == Const.MSG_TYPE_ADD_FRIEND){//添加好友的请求
			session.setType(msgtype);
			session.setContent(msgcontent);
			session.setIsdispose("0");
			QtalkClient.getInstance().getQMConversationManager().addConversation(session);
		}else	if(msgtype ==  Const.MSG_TYPE_ADD_FRIEND_SUCCESS){//对方同意添加好友的请求
			session.setType(Const.MSG_TYPE_TEXT);
			session.setContent("我们已经是好友了，快来和我聊天吧！");
			QtalkClient.getInstance().getQMConversationManager().addConversation(session);
			//发送广播更新好友列表
			Intent intent=new Intent(Const.ACTION_FRIENDS_ONLINE_STATUS_CHANGE);
			context.sendBroadcast(intent);
		}else if(msgtype ==  Const.MSG_TYPE_TEXT){//文本类型
			QMMessageBean qMMessageBean = QMMessageBean.createEmptyMessage();
			qMMessageBean.setIsComing(0);
			qMMessageBean.setContent(msgcontent);
			qMMessageBean.setChatType(messageBean.getChatType());
			qMMessageBean.setDate(msgtime);
			qMMessageBean.setIsReaded("0");
			qMMessageBean.setType(msgtype);

			if(messageBean.getChatType() == 1){
				if(!from.equals(QtalkClient.getInstance().getUser())) {
					qMMessageBean.setSendUser(from);
					qMMessageBean.setToUser(QtalkClient.getInstance().getUser());
					qMMessageBean.setFromUser(to);
				}
			}else {
				qMMessageBean.setToUser(to);
				qMMessageBean.setFromUser(from);
			}


			if(!from.equals(QtalkClient.getInstance().getUser())){
				QtalkClient.getInstance().getQmChatMessageManager().addMessage(qMMessageBean);
				sendNewMsg(qMMessageBean);
			}
			if(messageBean.getChatType() == 1){
				from = to;
				to = QtalkClient.getInstance().getUser();
			}
			session.setType(Const.MSG_TYPE_TEXT);
			session.setContent(msgcontent);

			if(QtalkClient.getInstance().getQMConversationManager().isHaveConversation(from, to)){//判断最近联系人列表是否已存在记录
				QtalkClient.getInstance().getQMConversationManager().updateConversation(session);
			}else{
				QtalkClient.getInstance().getQMConversationManager().addConversation(session);
			}
		}else if(msgtype == Const.MSG_TYPE_IMG){
			QMMessageBean qMMessageBean = QMMessageBean.createEmptyMessage();
			qMMessageBean.setToUser(to);
			qMMessageBean.setFromUser(from);
			qMMessageBean.setIsComing(0);
			qMMessageBean.setContent(msgcontent);
			qMMessageBean.setDate(msgtime);
			qMMessageBean.setIsReaded("0");
			qMMessageBean.setType(msgtype);
			QtalkClient.getInstance().getQmChatMessageManager().addMessage(qMMessageBean);
			sendNewMsg(qMMessageBean);

			session.setType(Const.MSG_TYPE_TEXT);
			session.setContent("[图片]");
			if(QtalkClient.getInstance().getQMConversationManager().isHaveConversation(from, to)){
				QtalkClient.getInstance().getQMConversationManager().updateConversation(session);
			}else{
				QtalkClient.getInstance().getQMConversationManager().addConversation(session);
			}
		}else if(msgtype == Const.MSG_TYPE_LOCATION){//位置
			QMMessageBean qMMessageBean = QMMessageBean.createEmptyMessage();
			qMMessageBean.setToUser(to);
			qMMessageBean.setFromUser(from);
			qMMessageBean.setIsComing(0);
			qMMessageBean.setContent(msgcontent);
			qMMessageBean.setDate(msgtime);
			qMMessageBean.setIsReaded("0");
			qMMessageBean.setType(msgtype);
			QtalkClient.getInstance().getQmChatMessageManager().addMessage(qMMessageBean);
			sendNewMsg(qMMessageBean);

			session.setType(Const.MSG_TYPE_TEXT);
			session.setContent("[位置]");
			if(QtalkClient.getInstance().getQMConversationManager().isHaveConversation(from, to)){
				QtalkClient.getInstance().getQMConversationManager().updateConversation(session);
			}else{
				QtalkClient.getInstance().getQMConversationManager().addConversation(session);
			}
		}

		Intent intent=new Intent(Const.ACTION_ADDFRIEND);//发送广播，通知消息界面更新
		context.sendBroadcast(intent);

		showNotice(session.getFromUser()+":"+session.getContent());

	}

	@Override
	public void onCmdMessageReceived(QMMessageBean messages) {

	}

	@Override
	public void onMessageRead(QMMessageBean messages) {

	}

	@Override
	public void onMessageDelivered(QMMessageBean message) {

	}

	@Override
	public void onMessageRecalled(QMMessageBean messages) {

	}

	@Override
	public void onMessageChanged(QMMessageBean message, Object change) {

	}
}

