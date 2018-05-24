package qumi.com.qumitalk.service.Listener;

import android.text.TextUtils;

import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;

import qumi.com.qumitalk.service.DataBean.QMMessageBean;
import qumi.com.qumitalk.service.Imp.QMMessageListenerImp;

/**
 * Created by mwang on 2018/5/17.
 */

public class QMChatMessageListener implements ChatMessageListener {
    private QMMessageListenerImp qmMessageListenerImp;

    public QMChatMessageListener(QMMessageListenerImp qmMessageListenerImp){
        this.qmMessageListenerImp = qmMessageListenerImp;
    }

    @Override
    public void processMessage(Chat chat, Message message) {
        if(message == null){
            return;
        }
        String messageBody = message.getBody();
        qumi.com.qumitalk.service.Util.LogUtil.e(message.getBody()+"---"+message.getSubject()+"---"+message.getType());
        if (TextUtils.isEmpty(messageBody))
            return;
        QMMessageBean qmMessageBean = QMMessageBean.decodeBase64Json(messageBody);
        qmMessageBean.setChatType(0);
        qmMessageListenerImp.onMessageReceived(qmMessageBean);

    }
}
