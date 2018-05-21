package qumi.com.qumitalk.service.Listener;

import android.util.Log;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;

import qumi.com.qumitalk.service.Util.LogUtil;

/**
 * Created by mwang on 2018/5/16.
 */

public class QMFriendsPacketListener implements StanzaListener {
    private  QMContactListener qmContactListener;
    public QMFriendsPacketListener(QMContactListener  qmContactListener){
        this.qmContactListener = qmContactListener;
    }
    @Override
    public void processPacket(Stanza packet) throws SmackException.NotConnectedException {

        if (packet instanceof Presence) {
            Presence presence = (Presence) packet;
            String from = presence.getFrom().split("@")[0];
            if (presence.getType().equals(Presence.Type.subscribe)) {
                LogUtil.e("-----请求添加好友-------"+from);
                qmContactListener.onContactInvited(from);
            } else if (presence.getType().equals(Presence.Type.subscribed)) {//对方同意订阅
                LogUtil.e("------同意订阅------"+from);
                qmContactListener.onContactAgreed(from);
            } else if (presence.getType().equals(Presence.Type.unsubscribe)) {//取消订阅
                LogUtil.e("-----取消订阅-------"+from);
                qmContactListener.onContactDeleted(from);
            } else if (presence.getType().equals(Presence.Type.unsubscribed)) {//拒绝订阅
                LogUtil.e("----拒绝订阅--------"+from);
                qmContactListener.onContactRefused(from);
            } else if (presence.getType().equals(Presence.Type.unavailable)) {//离线
                LogUtil.e("-----离线-------"+from);
                qmContactListener.onUnavailable(from);
            } else if (presence.getType().equals(Presence.Type.available)) {//上线
                LogUtil.e("-----y上线-------"+from);
                qmContactListener.onAvailable(from);
            }
        }
    }
}