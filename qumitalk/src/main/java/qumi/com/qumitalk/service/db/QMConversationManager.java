package qumi.com.qumitalk.service.Db;

import android.content.Context;

import java.util.List;

import qumi.com.qumitalk.service.DataBean.Session;

/**
 * Created by mwang on 2018/5/17.
 */

public class QMConversationManager {
    private Context mContext;
    private SessionDao sessionDao;

    public QMConversationManager(Context context){
        mContext = context;
        sessionDao = new SessionDao(context);
    }

    public boolean isHaveConversation(String fromUser,String toUser){
        return sessionDao.isContent(fromUser,toUser);
    }

    public long addConversation(Session session){
        return sessionDao.insertSession(session);
    }

    public List<Session> getAllConversation(String user_id){
        return sessionDao.queryAllSessions(user_id);
    }

    public long updateConversation(Session session){
        return sessionDao.updateSession(session);
    }

    public long deleteConversation(Session session){
        return sessionDao.deleteSession(session);
    }

    public void updateFriendRequestHandle(String sessionid){
        sessionDao.updateSessionToDisPose(sessionid);
    }

    public void clearConversation(){
        sessionDao.deleteTableData();
    }
}
