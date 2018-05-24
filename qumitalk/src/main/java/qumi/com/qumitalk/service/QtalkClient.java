package qumi.com.qumitalk.service;

import android.content.Context;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.realm.Realm;
import qumi.com.qumitalk.service.Config.StaticConfig;
import qumi.com.qumitalk.service.CallBack.LoginResultCallBack;
import qumi.com.qumitalk.service.Db.QMChatMessageManager;
import qumi.com.qumitalk.service.Db.QMConversationManager;
import qumi.com.qumitalk.service.Imp.QMCheckConnectionListenerImp;
import qumi.com.qumitalk.service.Imp.QMContactListenerImp;
import qumi.com.qumitalk.service.Listener.QMFriendsPacketListener;
import qumi.com.qumitalk.service.Listener.QMChatMessageListener;
import qumi.com.qumitalk.service.Imp.QMMessageListenerImp;

/**
 * Created by mwang on 2018/5/16.
 */

public class QtalkClient{

    private ExecutorService executor = null;
    private QMCheckConnectionListenerImp qmCheckConnectionListenerImp;
    private QMFriendsPacketListener friendsPacketListener;
    private Context mcontext;
    private QMChatManager qmChat;
    private QMConversationManager qmConversationManager;
    private QMChatMessageManager qmChatMessageManager;
    private QMContactsManager qmmContactsManager;
    private QMGoupChatManager qmGoupChatManager;
    private static QMXMPPConnectClient client;
    private static class LazyHolder {
        private static final QtalkClient INSTANCE = new QtalkClient();
    }

    public void init(Context context){
        mcontext = context;
        Realm.init(mcontext);
        this.executor = Executors.newCachedThreadPool();
    }
    public static final QtalkClient getInstance() {
        if(client == null){
            String server= StaticConfig.host;
            int port=5222;
            XMPPTCPConnectionConfiguration.Builder builder = XMPPTCPConnectionConfiguration.builder();
            builder.setServiceName(StaticConfig.serviceName);
            builder.setHost(server);
            builder.setPort(port);
            builder.setCompressionEnabled(false);
            builder.setDebuggerEnabled(true);
            builder.setSendPresence(true);
            builder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
            Roster.setDefaultSubscriptionMode(Roster.SubscriptionMode.accept_all);
            client = new QMXMPPConnectClient(builder.build());
        }
        return LazyHolder.INSTANCE;
    }

    public boolean Login(String userId,String pwd){
        return Login(userId, pwd , null, null );
    }
    public boolean Login(String userId,String pwd, QMCheckConnectionListenerImp checkConnectionListener,QMContactListenerImp friendsPacketListener ){
       return Login(userId, pwd , checkConnectionListener, friendsPacketListener ,null);
    }

    public boolean Login(String userId, String pwd , QMCheckConnectionListenerImp qmCheckConnectionListenerImp, QMContactListenerImp qmContactListenerImp, LoginResultCallBack loginResultCallBack){
        if(qmCheckConnectionListenerImp != null){
            this.qmCheckConnectionListenerImp = qmCheckConnectionListenerImp;
        }

//        if(friendsPacketListener != null){
//            this.friendsPacketListener = friendsPacketListener;
//        }
        this.friendsPacketListener = new QMFriendsPacketListener(qmContactListenerImp);

        client.setListener(qmCheckConnectionListenerImp,friendsPacketListener);
        if(loginResultCallBack != null)
            loginResultCallBack.onProgress();
        try{
            if(!client.isConnected())
                client.connect();
            client.login(userId, pwd);

            Presence presence = new Presence(Presence.Type.available);
            client.sendStanza(presence);

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



    protected QMXMPPConnectClient getClient(){
        return client;
    }

    public void  addMessageListener(final QMMessageListenerImp qmMessageListenerImp){
        getChatManager().addChatListener(new ChatManagerListener() {
            @Override
            public void chatCreated(Chat arg0, boolean arg1) {
                arg0.addMessageListener(new QMChatMessageListener(qmMessageListenerImp));
            }
        });
        getQMGoupChatManager().addMessageListener(qmMessageListenerImp);
    }

    public boolean logOut()  {//退出登录
        qmChat = null;
        qmConversationManager = null;
        qmChatMessageManager = null;
        qmmContactsManager = null;
        qmGoupChatManager = null;
        qmCheckConnectionListenerImp = null;
        friendsPacketListener = null;
        Presence presence = new Presence(Presence.Type.unavailable);
        try {
            client.sendStanza(presence);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            return false;
        }
        client.disconnect();
        client = null;

        return true;
    }

    public int creatAccount(String name , String pwd){
        try {
            if(!client.isConnected()){
                try {
                    client.connect();
                } catch (SmackException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XMPPException e) {
                    e.printStackTrace();
                }
            }
            AccountManager accountManager = AccountManager.getInstance(client);
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

    public QMChatManager getChatClient(){
        if(qmChat == null){
            qmChat = new QMChatManager(this);
        }
        return qmChat;
    }

    protected ChatManager getChatManager(){
        return ChatManager.getInstanceFor(client);
    }

    public QMConversationManager getQMConversationManager(){
        if(qmConversationManager == null){
            qmConversationManager = new QMConversationManager(mcontext);
        }
        return qmConversationManager;
    }

    public QMChatMessageManager getQmChatMessageManager(){
        if(qmChatMessageManager == null){
            qmChatMessageManager = new QMChatMessageManager(mcontext);
        }
        return qmChatMessageManager;
    }

    public QMContactsManager getQMMContactsManager(){
        if(qmmContactsManager == null){
            qmmContactsManager = new QMContactsManager(this);
        }
        return qmmContactsManager;
    }

    public QMGoupChatManager getQMGoupChatManager(){
        if(qmGoupChatManager == null){
            qmGoupChatManager = new QMGoupChatManager(this);
        }
        return qmGoupChatManager;
    }

    public String getUser(){
        return client.getUser().split("@")[0];
    }

    /**
     * 更改用户状态
     */
    public  void setPresence(Context context,int code) {
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
//                presence.setFromUser(con.getUser());
//                presence.setToUser(entry.getUser());
//            }
//            // 向同一用户的其他客户端发送隐身状态
//            presence = new Presence(Presence.Type.unavailable);
//            presence.setStanzaId(Packet.ID_NOT_AVAILABLE);
//            presence.setFromUser(con.getUser());
//            presence.setToUser(StringUtils.parseBareAddress(con.getUser()));
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
                client.sendStanza(presence);
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        }
    }


    public  boolean changeSign(int code , String content) {
        Presence presence = getOnlineStatus(code);
        presence.setStatus(content);
        try {
            client.sendStanza(presence);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public  Presence getOnlineStatus(int code){
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
                presence = new Presence(Presence.Type.unavailable);
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
        return presence;
    }


    void execute(Runnable var1) {
        this.executor.execute(var1);
    }
}
