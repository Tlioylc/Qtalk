package qumi.com.qumitalk.service;

import android.content.Context;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.xdata.Form;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import io.realm.Realm;
import qumi.com.qumitalk.service.DataBean.Session;
import qumi.com.qumitalk.service.callBack.LoginResultCallBack;
import qumi.com.qumitalk.service.listener.QMFriendsPacketListener;
import qumi.com.qumitalk.service.listener.QMCheckConnectionListener;

/**
 * Created by mwang on 2018/5/16.
 */

public class QtalkClient  extends XMPPTCPConnection {
    private static XMPPTCPConnectionConfiguration.Builder builder;

    private QMCheckConnectionListener checkConnectionListener;
    private QMFriendsPacketListener friendsPacketListener;
    private Context mcontext;

    private QtalkClient(XMPPTCPConnectionConfiguration config) {
        super(config);
    }

    private QtalkClient(CharSequence jid, String password) {
        super(jid, password);
    }

    private QtalkClient(CharSequence username, String password, String serviceName) {
        super(username, password, serviceName);
    }

    private static class LazyHolder { 
        private static final QtalkClient INSTANCE = new QtalkClient(builder.build());
    }

    public void init(Context context){
        mcontext = context;
        Realm.init(mcontext);
    }
    public static final QtalkClient getInstance() {

        if(builder == null){
            String server= StaticConfig.host;
            int port=5222;
            builder = XMPPTCPConnectionConfiguration.builder();
            builder.setServiceName(server);
            builder.setHost(server);
            builder.setPort(port);
            builder.setCompressionEnabled(false);
            builder.setDebuggerEnabled(true);
            builder.setSendPresence(true);
            builder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
            Roster.setDefaultSubscriptionMode(Roster.SubscriptionMode.accept_all);
        }
        return LazyHolder.INSTANCE;
    }

    public boolean Login(String userId,String pwd){
        return Login(userId, pwd , null, null );
    }
    public boolean Login(String userId,String pwd, QMCheckConnectionListener checkConnectionListener,QMFriendsPacketListener friendsPacketListener ){
       return Login(userId, pwd , checkConnectionListener, friendsPacketListener ,null);
    }

    public boolean Login(String userId, String pwd , QMCheckConnectionListener checkConnectionListener, QMFriendsPacketListener friendsPacketListener , LoginResultCallBack loginResultCallBack){
        if(checkConnectionListener != null){
            this.checkConnectionListener = checkConnectionListener;
        }

        if(friendsPacketListener != null){
            this.friendsPacketListener = friendsPacketListener;
        }
        if(loginResultCallBack != null)
            loginResultCallBack.onProgress();
        try{
            if(!getInstance().connected)
                getInstance().connect();
            getInstance().login(userId, pwd);

            Presence presence = new Presence(Presence.Type.available);
            getInstance().sendStanza(presence);

//            ChatManager chatmanager = ChatManager.getInstanceFor(getInstance());
//            chatmanager.addChatListener(new ChatManagerListener() {
//                @Override
//                public void chatCreated(Chat chat, boolean createdLocally) {
//                    chat.addMessageListener(new ChatMessageListener() {
//                        @Override
//                        public void processMessage(Chat chat, Message message) {
//                            String content=message.getBody();
//                            if (content!=null){
//                                LogUtil.e("from:" + message.getFrom() + " to:" + message.getTo() + " message:" + message.getBody());
//                            }
//
//                        }
//                    });
//                }
//            });
            if(loginResultCallBack != null)
                loginResultCallBack.onSuccess();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if(loginResultCallBack != null)
                loginResultCallBack.onError(e.getMessage());
            return false;
        }
    }

    @Override
    protected void afterSuccessfulLogin(boolean resumed) throws SmackException.NotConnectedException {
        super.afterSuccessfulLogin(resumed);

        if(!getInstance().isAuthenticated()){
            return;
        }
            //链接状态监听
        if(checkConnectionListener != null)
            getInstance().addConnectionListener(checkConnectionListener);

        // 注册好友状态更新监听
        if(friendsPacketListener != null){
            AndFilter filter = new AndFilter(new StanzaTypeFilter(Presence.class));
            getInstance().addPacketSendingListener(friendsPacketListener, filter);
        }

    }


    public boolean logOut(){//退出登录
        return true;
    }

    public int creatAccount(String name , String pwd){
        try {
            if(!getInstance().connected){
                try {
                    getInstance().connect();
                } catch (SmackException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XMPPException e) {
                    e.printStackTrace();
                }
            }
            AccountManager accountManager = AccountManager.getInstance(getInstance());
            accountManager.createAccount(name,pwd);
            return 1;
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
            return 0;
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
            return 2;
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            return 3;
        }
    }

    public ChatManager getChatManager(){
        return ChatManager.getInstanceFor(getInstance());
    }

    public static List<Session> searchUsers(String userName) {
        List<Session> listUser=new ArrayList<Session>();
        try{
			UserSearchManager search = new UserSearchManager(getInstance());
			//此处一定要加上 search.
			Form searchForm = search.getSearchForm("search."+getInstance().getServiceName());
			Form answerForm = searchForm.createAnswerForm();
			answerForm.setAnswer("Username", true);
			answerForm.setAnswer("search", userName);
			ReportedData data = search.getSearchResults(answerForm,"search."+getInstance().getServiceName());
			Iterator<ReportedData.Row> it = data.getRows().iterator();
			ReportedData.Row row=null;
			while(it.hasNext()){
				row=it.next();
				Session session=new Session();
				session.setFrom(row.getValues("Username").get(0).toString());
				listUser.add(session);
			}
		}catch(Exception e){
		}
        return listUser;
    }


    /**
     * 更改用户状态
     */
    public static void setPresence(Context context,int code) {
        Presence presence=null;
        switch (code) {
            case 0:
                presence = new Presence(Presence.Type.available);  //在线
                break;
            case 1:
                presence = new Presence(Presence.Type.available);  //设置Q我吧
                presence.setMode(Presence.Mode.chat);
                break;
            case 2:                                                                                      //隐身
//            Roster roster = Roster.getInstanceFor(con);
//            Collection<RosterEntry> entries = roster.getEntries();
//            for (RosterEntry entry : entries) {
//                presence = new Presence(Presence.Type.unavailable);
//                presence.setStanzaId(ID_NOT_AVAILABLE);
//                presence.setFrom(con.getUser());
//                presence.setTo(entry.getUser());
//            }
//            // 向同一用户的其他客户端发送隐身状态
//            presence = new Presence(Presence.Type.unavailable);
//            presence.setStanzaId(Packet.ID_NOT_AVAILABLE);
//            presence.setFrom(con.getUser());
//            presence.setTo(StringUtils.parseBareAddress(con.getUser()));
                break;
            case 3:
                presence = new Presence(Presence.Type.available);  //设置忙碌
                presence.setMode(Presence.Mode.dnd);
                break;
            case 4:
                presence = new Presence(Presence.Type.available);  //设置离开
                presence.setMode(Presence.Mode.away);
                break;
            case 5:
                presence = new Presence(Presence.Type.unavailable);  //离线
                break;
            default:
                break;
        }
        if(presence!=null){
//            presence.setStatus(PreferencesUtils.getSharePreStr(context, "sign"));
            try {
                getInstance().sendStanza(presence);
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        }
    }

}
