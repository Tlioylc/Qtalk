package qumi.com.qumitalk.service;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import qumi.com.qumitalk.service.CustomElementProvider.GroupsExtensionProvider;
import qumi.com.qumitalk.service.ListenerImp.QMCheckConnectionListenerImp;
import qumi.com.qumitalk.service.Listener.QMCheckConnectionListener;
import qumi.com.qumitalk.service.Listener.QMFriendsPacketListener;

/**
 * Created by mwang on 2018/5/22.
 */

public class QMXMPPConnectClient extends XMPPTCPConnection {
    private QMCheckConnectionListener checkConnectionListener;
    private QMFriendsPacketListener friendsPacketListener;

    public QMXMPPConnectClient(XMPPTCPConnectionConfiguration config) {
        super(config);
    }

    public QMXMPPConnectClient(CharSequence jid, String password) {
        super(jid, password);
    }

    public QMXMPPConnectClient(CharSequence username, String password, String serviceName) {
        super(username, password, serviceName);
    }

    public void setListener(QMCheckConnectionListenerImp checkConnectionListenerImp, QMFriendsPacketListener friendsPacketListener){
        this.checkConnectionListener = new QMCheckConnectionListener(checkConnectionListenerImp);
        this.friendsPacketListener = friendsPacketListener;
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

        // 注册订阅关系更新监听
        if(friendsPacketListener != null){
            AndFilter filter = new AndFilter(new StanzaTypeFilter(Presence.class));
            addAsyncStanzaListener(friendsPacketListener, filter);
        }

        //添加IQ解析
        ProviderManager.addIQProvider("groups","com:qumi:group", new GroupsExtensionProvider());

    }
}
