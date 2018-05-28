package qumi.com.qumitalk.service.Listener;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;

import qumi.com.qumitalk.service.ListenerImp.QMContactListenerImp;
import qumi.com.qumitalk.service.Util.LogUtil;

/**
 * Created by mwang on 2018/5/16.
 */

public class QMFriendsPacketListener implements StanzaListener {
    private QMContactListenerImp qmContactListenerImp;
    public QMFriendsPacketListener(QMContactListenerImp qmContactListenerImp){
        this.qmContactListenerImp = qmContactListenerImp;
    }
    @Override
    public void processPacket(Stanza packet) throws SmackException.NotConnectedException {

        if (packet instanceof Presence) {
            Presence presence = (Presence) packet;
            String from = presence.getFrom().split("@")[0];
            if (presence.getType().equals(Presence.Type.subscribe)) {
                LogUtil.e("-----请求添加好友-------"+from);
                qmContactListenerImp.onContactInvited(from);
            } else if (presence.getType().equals(Presence.Type.subscribed)) {//对方同意订阅
                LogUtil.e("------同意订阅------"+from);
                qmContactListenerImp.onContactAgreed(from);
            } else if (presence.getType().equals(Presence.Type.unsubscribe)) {//取消订阅
                LogUtil.e("-----取消订阅-------"+from);
                qmContactListenerImp.onContactDeleted(from);
            } else if (presence.getType().equals(Presence.Type.unsubscribed)) {//拒绝订阅
                LogUtil.e("----拒绝订阅--------"+from);
                qmContactListenerImp.onContactRefused(from);
            } else if (presence.getType().equals(Presence.Type.unavailable)) {//离线
                LogUtil.e("-----离线-------"+from);
                qmContactListenerImp.onUnavailable(from);
            } else if (presence.getType().equals(Presence.Type.available)) {//上线
                LogUtil.e("-----y上线-------"+from);
                qmContactListenerImp.onAvailable(from);
            }
        }
    }
}