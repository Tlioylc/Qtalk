package qumi.com.qumitalk.service.Listener;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.packet.Message;

import qumi.com.qumitalk.service.Config.StaticConfig;
import qumi.com.qumitalk.service.DataBean.QMMessageBean;
import qumi.com.qumitalk.service.DataBean.Session;
import qumi.com.qumitalk.service.ListenerImp.QMMessageListenerImp;
import qumi.com.qumitalk.service.QtalkClient;
import qumi.com.qumitalk.service.Util.LogUtil;

public class QMHandleReceiveMsgListener {
    private QMMessageListenerImp qmMessageListenerImp;
    private Context context;

    public QMHandleReceiveMsgListener(QMMessageListenerImp qmMessageListenerImp, Context context){
        this.qmMessageListenerImp = qmMessageListenerImp;
        this.context = context;
    }

    void sendNewMsg(QMMessageBean QMMessageBean){
        Intent intent=new Intent(StaticConfig.ACTION_NEW_MSG);//发送广播到聊天界面
        Bundle b=new Bundle();
        b.putSerializable("QMMessageBean", QMMessageBean);
        intent.putExtra("QMMessageBean", b);
        context.sendBroadcast(intent);
    }

    public void handleMessage(Message message) {
        LogUtil.e("-----------2-------------"+message.toXML().toString());
        if(message == null){
            return;
        }
        String messageBody = message.getBody();
        if (TextUtils.isEmpty(messageBody))
            return;

        QMMessageBean qmMessageBean = QMMessageBean.decodeBase64Json(messageBody);
        qmMessageBean.setChatType(1);
        qmMessageListenerImp.onMessageReceived(qmMessageBean);

        //回话设置
        String to= qmMessageBean.getToUser();
        String from= qmMessageBean.getFromUser();;//发送者，谁给你发的消息
        int msgtype= qmMessageBean.getType();//消息类型
        String msgcontent= qmMessageBean.getContent();//消息内容
        String msgtime= qmMessageBean.getDate();//消息时间

        Session session=new Session();

        if(qmMessageBean.getChatType() == 1){
            session.setChatType(1);
            session.setToUser(QtalkClient.getInstance().getUser());
            session.setFromUser(to);
        }else {
            session.setChatType(0);
            session.setFromUser(from);
            session.setToUser(to);
        }
        session.setNotReadCount("");//未读消息数量
        session.setDate(msgtime);

        if(msgtype == StaticConfig.MSG_TYPE_ADD_FRIEND){//添加好友的请求
            session.setType(msgtype);
            session.setContent(msgcontent);
            session.setIsdispose("0");
            QtalkClient.getInstance().getQMConversationManager().addConversation(session);
        }else	if(msgtype ==  StaticConfig.MSG_TYPE_ADD_FRIEND_SUCCESS){//对方同意添加好友的请求
            session.setType(StaticConfig.MSG_TYPE_TEXT);
            session.setContent("我们已经是好友了，快来和我聊天吧！");
            QtalkClient.getInstance().getQMConversationManager().addConversation(session);
            //发送广播更新好友列表
            Intent intent=new Intent(StaticConfig.ACTION_FRIENDS_ONLINE_STATUS_CHANGE);
            context.sendBroadcast(intent);
        }else if(msgtype ==  StaticConfig.MSG_TYPE_TEXT){//文本类型
            handleMessage(qmMessageBean, to, from, msgtype, msgcontent, msgtime, session, msgcontent);
        }else if(msgtype == StaticConfig.MSG_TYPE_IMG){//图片类型
            handleMessage(qmMessageBean, to, from, msgtype, msgcontent, msgtime, session, "[图片]");
        }else if(msgtype == StaticConfig.MSG_TYPE_LOCATION){//位置
            handleMessage(qmMessageBean, to, from, msgtype, msgcontent, msgtime, session, "[位置]");
        }

        Intent intent=new Intent(StaticConfig.ACTION_ADDFRIEND);//发送广播，通知消息界面更新
        context.sendBroadcast(intent);

//        showNotice(session.getFromUser()+":"+session.getContent());
    }

    private void handleMessage(QMMessageBean qmMessageBean, String to, String from, int msgtype, String msgcontent, String msgtime, Session session, String s) {
        QMMessageBean qMMessageBean = QMMessageBean.createEmptyMessage();
        qMMessageBean.setIsComing(0);
        qMMessageBean.setContent(msgcontent);
        qMMessageBean.setChatType(qmMessageBean.getChatType());
        qMMessageBean.setDate(msgtime);
        qMMessageBean.setIsReaded("0");
        qMMessageBean.setType(msgtype);


        if (qmMessageBean.getChatType() == 1) {
            if (!from.equals(QtalkClient.getInstance().getUser())) {
                qMMessageBean.setSendUser(from);
                qMMessageBean.setToUser(QtalkClient.getInstance().getUser());
                qMMessageBean.setFromUser(to);
            }
        } else {
            qMMessageBean.setToUser(to);
            qMMessageBean.setFromUser(from);
        }


        if (!from.equals(QtalkClient.getInstance().getUser())) {
            QtalkClient.getInstance().getQmChatMessageManager().addMessage(qMMessageBean);
            sendNewMsg(qMMessageBean);
        }
        if (qmMessageBean.getChatType() == 1) {
            from = to;
            to = QtalkClient.getInstance().getUser();
        }

        session.setType(msgtype);
        session.setContent(s);
        updateSession(to, from, session);
    }

    private void updateSession(String to, String from, Session session) {
        if(QtalkClient.getInstance().getQMConversationManager().isHaveConversation(from, to)){
            QtalkClient.getInstance().getQMConversationManager().updateConversation(session);
        }else{
            QtalkClient.getInstance().getQMConversationManager().addConversation(session);
        }
    }
}
