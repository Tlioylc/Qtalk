package qumi.com.qtalk.service;


import java.net.DatagramSocket;
import java.net.SocketException;


import qumi.com.qtalk.listener.CheckConnectionListener;
import qumi.com.qtalk.listener.FriendsPacketListener;
import qumi.com.qtalk.listener.MsgListener;
import qumi.com.qtalk.util.Const;
import qumi.com.qtalk.util.PreferencesUtils;
import qumi.com.qtalk.util.XmppConnectionManager;
import qumi.com.qumitalk.service.QtalkClient;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;


/**
 * @author 白玉梁
 */
public class MsfService extends Service{

	private static MsfService mInstance = null;
	public static DatagramSocket ds = null;

	private NotificationManager mNotificationManager;

	private String mUserName, mPassword;
	private XmppConnectionManager mXmppConnectionManager;

	private QtalkClient mXMPPConnection;
	private final IBinder binder = new MyBinder();

	public class MyBinder extends Binder {
		public MsfService getService() {
			return MsfService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}
	

	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
		mUserName = PreferencesUtils.getSharePreStr(this, "username");
		mPassword = PreferencesUtils.getSharePreStr(this, "pwd");
		try {
			ds = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);         // 通知
		mXmppConnectionManager = XmppConnectionManager.getInstance();
		initXMPPTask();		
	}

	public static MsfService getInstance() {
		return mInstance;
	}
	

	/**
	 * 初始化xmpp和完成后台登录
	 */
	private void initXMPPTask() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try{
				    initXMPP();	
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * 初始化XMPP
	 */
	void initXMPP() {
		mXMPPConnection = QtalkClient.getInstance();
		loginXMPP();															//登录XMPP
		mXMPPConnection.addMessageListener(new MsgListener(MsfService.this, mNotificationManager));
	}

	/**
	 * 登录XMPP
	 */
	void loginXMPP() {
		mPassword = PreferencesUtils.getSharePreStr(this, "pwd");
		boolean ifSccess = QtalkClient.getInstance().Login(mUserName, mPassword,new CheckConnectionListener(this),new FriendsPacketListener(this));
		if(!ifSccess){
			stopSelf();
		}
		sendLoginBroadcast(ifSccess);
	}
	
	/**
	 * 发送登录状态广播
	 * @param isLoginSuccess
	 */
	void sendLoginBroadcast(boolean isLoginSuccess){
		Intent intent =new Intent(Const.ACTION_IS_LOGIN_SUCCESS);
		intent.putExtra("isLoginSuccess", isLoginSuccess);
		sendBroadcast(intent);
	}
	

	@Override
	public void onDestroy() {
		if(mNotificationManager!=null){
			
		}
		try {
			if (mXMPPConnection != null) {
				mXMPPConnection.logOut();
				mXMPPConnection = null;
			}
			if(mXmppConnectionManager!=null){
				mXmppConnectionManager = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onDestroy();
	}

}
