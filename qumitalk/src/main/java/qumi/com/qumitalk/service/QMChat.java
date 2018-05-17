package qumi.com.qumitalk.service;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;

import qumi.com.qumitalk.service.Config.StaticConfig;
import qumi.com.qumitalk.service.DataBean.QMMessageBean;

/**
 * Created by mwang on 2018/5/17.
 */

public class QMChat {
    private QtalkClient qtalkClient;

    public QMChat(QtalkClient qtalkClient){
        this.qtalkClient = qtalkClient;
    }

    public void sendMessage(String content,String touser) {
		ChatManager chatmanager =qtalkClient.getChatManager();
		Chat chat =chatmanager.createChat(touser + "@" + StaticConfig.host, null);
		if (chat != null) {
            try {
                chat.sendMessage(content);
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(QMMessageBean qmMessageBean)  throws SmackException.NotConnectedException {
        ChatManager chatmanager = qtalkClient.getChatManager();
        Chat chat =chatmanager.createChat(qmMessageBean.getToUser() + "@" + StaticConfig.host, null);
        if (chat != null) {
            chat.sendMessage(qmMessageBean.toBase64Json());
        }
    }
}
