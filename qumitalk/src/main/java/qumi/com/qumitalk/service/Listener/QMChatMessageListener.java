package qumi.com.qumitalk.service.Listener;

import android.text.TextUtils;

import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;

import qumi.com.qumitalk.service.DataBean.QMMessageBean;

/**
 * Created by mwang on 2018/5/17.
 */

public class QMChatMessageListener implements ChatMessageListener {
    private QMMessageListener qmMessageListener;

    public QMChatMessageListener(QMMessageListener qmMessageListener){
        this.qmMessageListener = qmMessageListener;
    }

    @Override
    public void processMessage(Chat chat, Message message) {
        if(message == null){
            return;
        }
        String messageBody = message.getBody();
        if (TextUtils.isEmpty(messageBody))
            return;
        QMMessageBean qmMessageBean = QMMessageBean.decodeBase64Json(messageBody);
        qmMessageBean.setChatType(0);
        qmMessageListener.onMessageReceived(qmMessageBean);

    }
}
