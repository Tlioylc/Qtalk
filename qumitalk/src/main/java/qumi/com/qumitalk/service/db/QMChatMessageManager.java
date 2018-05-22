package qumi.com.qumitalk.service.Db;

import android.content.Context;

import java.util.ArrayList;

import qumi.com.qumitalk.service.DataBean.QMMessageBean;

/**
 * Created by mwang on 2018/5/17.
 */

public class QMChatMessageManager {
    private Context mContext;
    private  ChatMsgDao chatMsgDao;

    public QMChatMessageManager(Context context){
        mContext = context;
        chatMsgDao = new ChatMsgDao(context);
    }

    public synchronized int addMessage(QMMessageBean qmMessageBean) {
        return chatMsgDao.insert(qmMessageBean);
    }

    public synchronized void clearAllMessage() {
        chatMsgDao.deleteTableData();
    }

    public synchronized long deleteMessage(int msgid) {
        return chatMsgDao.deleteMsgById(msgid);
    }

    public synchronized ArrayList<QMMessageBean> getMessageList(String from, String to, int offset) {
        return chatMsgDao.queryMsg(from,to,offset);
    }

    public synchronized QMMessageBean getLastMessage() {
        return chatMsgDao.queryTheLastMsg();
    }

    public synchronized int getLastMessageId() {
        return chatMsgDao.queryTheLastMsgId();
    }

    public synchronized long markAllMessageAsRead(String from, String to) {
        return chatMsgDao.updateAllMsgToRead(from,to);
    }

    public synchronized int getUnreadCount() {
        return chatMsgDao.queryAllNotReadCount();
    }


    public  synchronized long deleteConversationAllMessage(String from,String to) {
        return chatMsgDao.deleteAllMsg(from, to);
    }
}
