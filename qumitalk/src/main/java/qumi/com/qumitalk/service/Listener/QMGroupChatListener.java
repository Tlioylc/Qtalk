package qumi.com.qumitalk.service.Listener;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

import qumi.com.qumitalk.service.Config.StaticConfig;
import qumi.com.qumitalk.service.DataBean.QMMessageBean;
import qumi.com.qumitalk.service.DataBean.Session;
import qumi.com.qumitalk.service.ListenerImp.QMMessageListenerImp;
import qumi.com.qumitalk.service.QtalkClient;
import qumi.com.qumitalk.service.Util.LogUtil;

/**
 * Created by mwang on 2018/5/22.
 */

public class QMGroupChatListener extends QMHandleReceiveMsgListener implements MessageListener {


    public QMGroupChatListener(QMMessageListenerImp qmMessageListenerImp, Context context) {
        super(qmMessageListenerImp, context);
    }

    @Override
    public void processMessage(Message message) {
        handleMessage(message);
    }
}
