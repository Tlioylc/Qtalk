package qumi.com.qumitalk.service.db;

import android.content.Context;

import java.util.ArrayList;

import qumi.com.qumitalk.service.DataBean.QMMessageBean;
import qumi.com.qumitalk.service.db.ChatMsgDao;
import qumi.com.qumitalk.service.db.SessionDao;

/**
 * Created by mwang on 2018/5/17.
 */

public class QMChatMessageManager {
    private Context mContext;
    private ChatMsgDao chatMsgDao;

    QMChatMessageManager(Context context){
        mContext = context;
        chatMsgDao = new ChatMsgDao(context);
    }

    public int addMessage(QMMessageBean qmMessageBean) {
        return chatMsgDao.insert(qmMessageBean);
    }

    public void clearAllMessage() {
        chatMsgDao.deleteTableData();
    }

    public long deleteMessage(int msgid) {
        return chatMsgDao.deleteMsgById(msgid);
    }

    public ArrayList<QMMessageBean> getMessageList(String from, String to, int offset) {
        return chatMsgDao.queryMsg(from,to,offset);
    }

    public QMMessageBean getLastMessage() {
        return chatMsgDao.queryTheLastMsg();
    }

    public int getLastMessageId() {
        return chatMsgDao.queryTheLastMsgId();
    }

    public long markAllMessageAsRead(String from, String to) {
        return chatMsgDao.updateAllMsgToRead(from,to);
    }

    public int getUnreadCount() {
        return chatMsgDao.queryAllNotReadCount();
    }


    public long deleteConversationAllMessage(String from,String to) {
        return chatMsgDao.deleteAllMsg(from, to);
    }
}
