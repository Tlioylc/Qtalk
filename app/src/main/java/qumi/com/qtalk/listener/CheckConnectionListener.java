package qumi.com.qtalk.listener;

import org.jivesoftware.smack.ConnectionListener;

import qumi.com.qtalk.service.MsfService;
import qumi.com.qtalk.util.ToastUtil;
import qumi.com.qumitalk.service.listener.QMCheckConnectionListener;


/**
 * @author baiyuliang
 */
public class CheckConnectionListener extends QMCheckConnectionListener {
	
	private MsfService context;
	
	public CheckConnectionListener(MsfService context){
		this.context=context;
	}

	@Override
	public void connectionClosed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void connectionClosedOnError(Exception e) {  
		if (e.getMessage().equals("stream:error (conflict)")) {
			ToastUtil.showLongToast(context, "您的账号在异地登录");
		}
	}

	@Override
	public void reconnectingIn(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reconnectionFailed(Exception arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reconnectionSuccessful() {
		// TODO Auto-generated method stub
		
	}

}
