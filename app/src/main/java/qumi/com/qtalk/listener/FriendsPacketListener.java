package qumi.com.qtalk.listener;


import qumi.com.qtalk.service.MsfService;
import qumi.com.qtalk.util.Const;
import qumi.com.qumitalk.service.Imp.QMContactListenerImp;
import android.content.Intent;
import android.util.Log;

public class FriendsPacketListener implements QMContactListenerImp {
	MsfService context;
	public FriendsPacketListener(MsfService context){
		this.context=context;
	}

	@Override
	public void onContactAgreed(String username) {
		Log.e("jj", "同意添加好友");
	}

	@Override
	public void onContactRefused(String username) {
		Log.e("jj", "拒绝添加好友");
	}

	@Override
	public void onContactInvited(String username) {
		Log.e("jj", "好友申请");
	}

	@Override
	public void onContactDeleted(String username) {

	}

	@Override
	public void onUnavailable(String username) {
		Log.e("jj", "好友下线");
		Intent intent=new Intent(Const.ACTION_FRIENDS_ONLINE_STATUS_CHANGE);
		intent.putExtra("from", username);
		intent.putExtra("status", 0);
		context.sendBroadcast(intent);
	}

	@Override
	public void onAvailable(String username) {
		Log.e("jj", "好友上线");
		Intent intent=new Intent(Const.ACTION_FRIENDS_ONLINE_STATUS_CHANGE);
		intent.putExtra("from", username);
		intent.putExtra("status",1);
		context.sendBroadcast(intent);
	}
}

