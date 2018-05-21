package qumi.com.qumitalk.service;

import android.content.Context;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.xdata.Form;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.realm.Realm;
import qumi.com.qumitalk.service.Config.StaticConfig;
import qumi.com.qumitalk.service.DataBean.Session;
import qumi.com.qumitalk.service.CallBack.LoginResultCallBack;
import qumi.com.qumitalk.service.Db.QMChatMessageManager;
import qumi.com.qumitalk.service.Db.QMConversationManager;
import qumi.com.qumitalk.service.Listener.QMContactListener;
import qumi.com.qumitalk.service.Listener.QMFriendsPacketListener;
import qumi.com.qumitalk.service.Listener.QMCheckConnectionListener;
import qumi.com.qumitalk.service.Listener.QMMChatMessageListener;
import qumi.com.qumitalk.service.Listener.QMMessageListener;

/**
 * Created by mwang on 2018/5/16.
 */

public class QtalkClient  extends XMPPTCPConnection {
    private static XMPPTCPConnectionConfiguration.Builder builder;

    private ExecutorService executor = null;
    private QMCheckConnectionListener checkConnectionListener;
    private QMFriendsPacketListener friendsPacketListener;
    private Context mcontext;
    private QMChat qmChat;
    private QMConversationManager qmConversationManager;
    private QMChatMessageManager qmChatMessageManager;
    private QMMContactsManager qmmContactsManager;

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
        this.executor = Executors.newCachedThreadPool();
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
    public boolean Login(String userId,String pwd, QMCheckConnectionListener checkConnectionListener,QMContactListener friendsPacketListener ){
       return Login(userId, pwd , checkConnectionListener, friendsPacketListener ,null);
    }

    public boolean Login(String userId, String pwd , QMCheckConnectionListener checkConnectionListener, QMContactListener qmContactListener , LoginResultCallBack loginResultCallBack){
        if(checkConnectionListener != null){
            this.checkConnectionListener = checkConnectionListener;
        }

//        if(friendsPacketListener != null){
//            this.friendsPacketListener = friendsPacketListener;
//        }
        this.friendsPacketListener = new QMFriendsPacketListener(qmContactListener);
        if(loginResultCallBack != null)
            loginResultCallBack.onProgress();
        try{
            if(!connected)
                connect();
            login(userId, pwd);

            Presence presence = new Presence(Presence.Type.available);
            sendStanza(presence);

//            ChatManager chatmanager = ChatManager.getInstanceFor(getInstance());
//            chatmanager.addChatListener(new ChatManagerListener() {
//                @Override
//                public void chatCreated(Chat chat, boolean createdLocally) {
//                    chat.addMessageListener(new ChatMessageListener() {
//                        @Override
//                        public void processMessage(Chat chat, Message message) {
//                            String content=message.getBody();
//                            if (content!=null){
//                                LogUtil.e("from:" + message.getFromUser() + " to:" + message.getToUser() + " message:" + message.getBody());
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
        if(!isAuthenticated()){
            return;
        }
            //链接状态监听
        if(checkConnectionListener != null)
            addConnectionListener(checkConnectionListener);

        // 注册好友状态更新监听
        if(friendsPacketListener != null){
            AndFilter filter = new AndFilter(new StanzaTypeFilter(Presence.class));
            addAsyncStanzaListener(friendsPacketListener, filter);
        }

    }

    public void  addMessageListener(final QMMessageListener qmMessageListener){
        getChatManager().addChatListener(new ChatManagerListener() {
            @Override
            public void chatCreated(Chat arg0, boolean arg1) {
                arg0.addMessageListener(new QMMChatMessageListener(qmMessageListener));
            }
        });
    }

    public boolean logOut(){//退出登录
        return true;
    }

    public int creatAccount(String name , String pwd){
        try {
            if(!connected){
                try {
                    connect();
                } catch (SmackException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XMPPException e) {
                    e.printStackTrace();
                }
            }
            AccountManager accountManager = AccountManager.getInstance(this);
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

    public QMChat getChatClient(){
        if(qmChat == null){
            qmChat = new QMChat(this);
        }
        return qmChat;
    }

    protected ChatManager getChatManager(){
        return ChatManager.getInstanceFor(this);
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

    public QMMContactsManager getQMMContactsManager(){
        if(qmmContactsManager == null){
            qmmContactsManager = new QMMContactsManager(this);
        }
        return qmmContactsManager;
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
                 sendStanza(presence);
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        }
    }


    public  void changeSign(int code , String content) throws SmackException.NotConnectedException {
        Presence presence = getOnlineStatus(code);
        presence.setStatus(content);
        sendStanza(presence);
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
