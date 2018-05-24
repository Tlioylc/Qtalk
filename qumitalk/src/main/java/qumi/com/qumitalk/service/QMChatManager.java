package qumi.com.qumitalk.service;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;

import java.util.HashMap;

import qumi.com.qumitalk.service.Config.StaticConfig;
import qumi.com.qumitalk.service.DataBean.QMMessageBean;
import qumi.com.qumitalk.service.Util.LogUtil;

/**
 * Created by mwang on 2018/5/17.
 */

public class QMChatManager {
    private QMXMPPConnectClient qmxmppConnectClient;
    private QtalkClient qtalkClient;
    private HashMap<String,Chat> chatMap;
    private HashMap<String,MultiUserChat> multiUserChatHashMap;

    protected QMChatManager(QtalkClient qtalkClient){
        this.qmxmppConnectClient = qtalkClient.getClient();
        this.qtalkClient = qtalkClient;
    }


    private Chat getChat(String touser) {
        Chat chat;
        if(chatMap == null){
            chatMap = new HashMap<>();
        }
        if(chatMap.containsKey(touser)){
            chat = chatMap.get(touser);
        }else {
            ChatManager chatmanager =qtalkClient.getChatManager();
            chat =chatmanager.createChat(touser + "@" + StaticConfig.serviceName, null);
            chatMap.put(touser,chat);
        }
        return chat;
    }

    private MultiUserChat getMultiuserChat(String touser) {
        MultiUserChat chat;
        if(multiUserChatHashMap == null){
            multiUserChatHashMap = new HashMap<>();
        }
        if(multiUserChatHashMap.containsKey(touser)){
            chat = multiUserChatHashMap.get(touser);
        }else {
            chat  = MultiUserChatManager.getInstanceFor(qmxmppConnectClient).getMultiUserChat(touser + "@conference."  +qmxmppConnectClient.getServiceName());
            multiUserChatHashMap.put(touser,chat);
        }
        return chat;
    }
    public void sendMessage(String content,String touser) {
        Chat chat = getChat(touser);
        if (chat != null) {
            try {
                chat.sendMessage(content);
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        }
    }



    public boolean sendMessage(QMMessageBean qmMessageBean) {
        Chat chat = getChat(qmMessageBean.getToUser());
        if (chat != null) {
            try {
                chat.sendMessage(qmMessageBean.toBase64Json());
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }


    public boolean sendGroupMessage(QMMessageBean qmMessageBean) {
        MultiUserChat muc = getMultiuserChat(qmMessageBean.getToUser());
        try {
            muc.sendMessage(qmMessageBean.toBase64Json());
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
