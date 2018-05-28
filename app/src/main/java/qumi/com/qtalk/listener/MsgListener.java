package qumi.com.qtalk.listener;

import qumi.com.qtalk.R;
import qumi.com.qumitalk.service.DataBean.QMMessageBean;
import qumi.com.qtalk.service.MsfService;
import qumi.com.qtalk.util.Const;
import qumi.com.qtalk.util.PreferencesUtils;
import qumi.com.qumitalk.service.ListenerImp.QMMessageListenerImp;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;

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

