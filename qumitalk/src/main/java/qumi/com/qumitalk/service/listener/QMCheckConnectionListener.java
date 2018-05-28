package qumi.com.qumitalk.service.Listener;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;

import qumi.com.qumitalk.service.ListenerImp.QMCheckConnectionListenerImp;

/**
 * Created by mwang on 2018/5/16.
 */

public class QMCheckConnectionListener implements ConnectionListener {
    private QMCheckConnectionListenerImp qmCheckConnectionListenerImp;

    public QMCheckConnectionListener(QMCheckConnectionListenerImp qmCheckConnectionListenerImp){
        this.qmCheckConnectionListenerImp = qmCheckConnectionListenerImp;
    }

    @Override
    public void connected(XMPPConnection connection) {

    }

    @Override
    public void authenticated(XMPPConnection connection, boolean resumed) {

    }

    @Override
    public void connectionClosed() {

    }

    @Override
    public void connectionClosedOnError(Exception e) {
        qmCheckConnectionListenerImp.connectionClosedOnError(e);
    }

    @Override
    public void reconnectionSuccessful() {

    }

    @Override
    public void reconnectingIn(int seconds) {

    }

    @Override
    public void reconnectionFailed(Exception e) {

    }
}
