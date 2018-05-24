package qumi.com.qumitalk.service.Listener;

import android.text.TextUtils;

import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

import qumi.com.qumitalk.service.DataBean.QMMessageBean;
import qumi.com.qumitalk.service.Imp.QMMessageListenerImp;
import qumi.com.qumitalk.service.Util.LogUtil;

/**
 * Created by mwang on 2018/5/22.
 */

public class QMGroupChatListener implements MessageListener {
    private QMMessageListenerImp qmMessageListenerImp;

    public QMGroupChatListener(QMMessageListenerImp qmMessageListenerImp){
        this.qmMessageListenerImp = qmMessageListenerImp;
    }
    @Override
    public void processMessage(Message message) {
        LogUtil.e("-----------2-------------"+message.toXML().toString());
        if(message == null){
            return;
        }
        String messageBody = message.getBody();
        if (TextUtils.isEmpty(messageBody))
            return;

        LogUtil.e("----------3--------------"+messageBody);
        QMMessageBean qmMessageBean = QMMessageBean.decodeBase64Json(messageBody);
        qmMessageBean.setChatType(1);
        qmMessageListenerImp.onMessageReceived(qmMessageBean);
    }
}
