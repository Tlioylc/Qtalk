package qumi.com.qtalk.listener;

import qumi.com.qtalk.service.MsfService;
import qumi.com.qtalk.util.ToastUtil;
import qumi.com.qumitalk.service.Imp.QMCheckConnectionListenerImp;
import qumi.com.qumitalk.service.Listener.QMCheckConnectionListener;


/**
 * @author baiyuliang
 */
public class CheckConnectionListener extends QMCheckConnectionListenerImp {
	
	private MsfService context;
	
	public CheckConnectionListener(MsfService context){
		this.context=context;
	}

	@Override
	public void connectionClosedOnError(Exception e) {  
		if (e.getMessage().equals("stream:error (conflict)")) {
			ToastUtil.showLongToast(context, "您的账号在异地登录");
		}
	}
}
