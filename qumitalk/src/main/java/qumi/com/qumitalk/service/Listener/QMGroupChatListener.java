package qumi.com.qumitalk.service.Listener;

import android.text.TextUtils;
import android.util.Log;

import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.packet.Message;

import qumi.com.qumitalk.service.DataBean.QMMessageBean;
import qumi.com.qumitalk.service.Util.LogUtil;

/**
 * Created by mwang on 2018/5/22.
 */

public class QMGroupChatListener implements MessageListener {
    private QMMessageListener qmMessageListener;

    public QMGroupChatListener(QMMessageListener qmMessageListener){
        this.qmMessageListener = qmMessageListener;
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
        qmMessageListener.onMessageReceived(qmMessageBean);
    }
}
