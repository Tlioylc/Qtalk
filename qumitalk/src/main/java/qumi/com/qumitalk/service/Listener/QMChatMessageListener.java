package qumi.com.qumitalk.service.Listener;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;

import qumi.com.qumitalk.service.Config.StaticConfig;
import qumi.com.qumitalk.service.DataBean.QMMessageBean;
import qumi.com.qumitalk.service.DataBean.Session;
import qumi.com.qumitalk.service.ListenerImp.QMMessageListenerImp;
import qumi.com.qumitalk.service.QtalkClient;
import qumi.com.qumitalk.service.Util.LogUtil;

/**
 * Created by mwang on 2018/5/17.
 */

public class QMChatMessageListener extends QMHandleReceiveMsgListener implements ChatMessageListener {


    public QMChatMessageListener(QMMessageListenerImp qmMessageListenerImp, Context context) {
        super(qmMessageListenerImp, context);
    }
    /**
     *消息监听
     *
     * */

    @Override
    public void processMessage(Chat chat,Message message) {
        handleMessage(message);
    }
}
