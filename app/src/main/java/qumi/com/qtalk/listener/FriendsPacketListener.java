package qumi.com.qtalk.listener;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;

import de.measite.minidns.util.NameUtil;
import qumi.com.qtalk.service.MsfService;
import qumi.com.qtalk.util.Const;
import qumi.com.qumitalk.service.listener.QMFriendsPacketListener;

import android.content.Intent;
import android.util.Log;

public class FriendsPacketListener extends QMFriendsPacketListener {
	MsfService context;
	public FriendsPacketListener(MsfService context){
		this.context=context;
	}


	public void processPacket(Stanza packet)  throws SmackException.NotConnectedException {
		Log.e("test",packet.toXML().toString());
		if(packet != null && (packet.getFrom() == null || packet.getTo() == null || packet.getFrom().equals(packet.getTo()))){
			return;
		}
		 if (packet instanceof Presence) {  
             Presence presence = (Presence) packet;  
             final String from = presence.getFrom().split("@")[0];//发送方  
             String to = presence.getTo().split("@")[0];//接收方  
             if(from.equals(to)){
            	 return;
             }
             if (presence.getType().equals(Presence.Type.subscribe)) {//好友申请  
                   Log.e("jj", "好友申请");
             } else if (presence.getType().equals(Presence.Type.subscribed)) {//同意添加好友  
            	 Log.e("jj", "同意添加好友");
             } else if (presence.getType().equals(Presence.Type.unsubscribe)) {//拒绝添加好友  和  删除好友  
            	 Log.e("jj", "拒绝添加好友");
             } else if (presence.getType().equals(Presence.Type.unsubscribed)){
            	 
             } else if (presence.getType().equals(Presence.Type.unavailable)) {//好友下线   要更新好友列表，可以在这收到包后，发广播到指定页面   更新列表  
            	 Log.e("jj", "好友下线");
				 Intent intent=new Intent(Const.ACTION_FRIENDS_ONLINE_STATUS_CHANGE);
            	 intent.putExtra("from", from);
            	 intent.putExtra("status", 0);
            	 context.sendBroadcast(intent);
             } else if(presence.getType().equals(Presence.Type.available)){//好友上线  
            	 Log.e("jj", "好友上线");
				 Intent intent=new Intent(Const.ACTION_FRIENDS_ONLINE_STATUS_CHANGE);
            	 intent.putExtra("from", from);
            	 intent.putExtra("status",1);
            	 context.sendBroadcast(intent);
             }  else{
            	 Log.e("jj", "error");
             }
         }  
       };  
	}

