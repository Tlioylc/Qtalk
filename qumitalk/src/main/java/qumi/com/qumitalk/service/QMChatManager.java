package qumi.com.qumitalk.service;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import qumi.com.qumitalk.service.CallBack.QMSendMessageCallBack;
import qumi.com.qumitalk.service.Config.StaticConfig;
import qumi.com.qumitalk.service.DataBean.QMMessageBean;
import qumi.com.qumitalk.service.DataBean.Session;
import qumi.com.qumitalk.service.Util.LogUtil;
import qumi.com.qumitalk.service.Util.UploadFileMgr;

/**
 * Created by mwang on 2018/5/17.
 */

public class QMChatManager {
    private QMXMPPConnectClient qmxmppConnectClient;
    private QtalkClient qtalkClient;
    private HashMap<String,Chat> chatMap;
    private HashMap<String,MultiUserChat> multiUserChatHashMap;
    private Context context;


    protected QMChatManager(QtalkClient qtalkClient){
        this.qmxmppConnectClient = qtalkClient.getClient();
        this.qtalkClient = qtalkClient;
        this.context = qtalkClient.getContext();

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
        LogUtil.e(qmMessageBean.getToUser()+"---msg-----");
        Chat chat = getChat(qmMessageBean.getToUser());
        if (chat != null) {
            try {
                chat.sendMessage(qmMessageBean.toBase64Json());
                updateSession(qmMessageBean);
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public void sendImageMessage(final QMMessageBean qmMessageBean, final QMSendMessageCallBack qmSendMessageCallBack) {
        if(TextUtils.isEmpty(StaticConfig.qiniutoken)){//获取七牛token
            String url = "http://192.168.1.249/qiniu/gettoken";
            final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            final Request request=new Request.Builder().url(url).build();
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    LogUtil.e("onFailure: "+e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String body=response.body().string();
                    com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(body);
                    StaticConfig.qiniutoken = jsonObject.getString("msg");
                    sendImageMessage(qmMessageBean,qmSendMessageCallBack);
                }
            });

            return;
        }


        LogUtil.e(qmMessageBean.getToUser()+"---img-----");
        final Chat chat = getChat(qmMessageBean.getToUser());
        if (chat != null) {
            UploadFileMgr.obtain().upLoadFile(qmMessageBean.getContent(), StaticConfig.qiniutoken, new UploadFileMgr.UpLoadCallBack() {
                @Override
                public void onSuccess(String name, String fileName) {
                    try {
                        qmMessageBean.setContent(name);
                        chat.sendMessage(qmMessageBean.toBase64Json());
                        updateSession(qmMessageBean);
                    } catch (SmackException.NotConnectedException e) {
                        qmSendMessageCallBack.onFailed();
                        e.printStackTrace();
                    }
                    qmSendMessageCallBack.onSuccess();
                }

                @Override
                public void onFail(String fileName) {
                    qmSendMessageCallBack.onFailed();
                }
            });

        }
    }


    public boolean sendGroupMessage(QMMessageBean qmMessageBean) {
        MultiUserChat muc = getMultiuserChat(qmMessageBean.getToUser());
        try {
            muc.sendMessage(qmMessageBean.toBase64Json());
            updateSession(qmMessageBean);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    //更新会话信息
    void updateSession(QMMessageBean qmMessageBean){
        SimpleDateFormat sd=new SimpleDateFormat("MM-dd HH:mm");

        Session session=new Session();
        session.setFromUser(qmMessageBean.getToUser());
        session.setToUser(qmMessageBean.getFromUser());
        session.setChatType(qmMessageBean.getChatType());
        session.setNotReadCount("");//未读消息数量
        session.setContent(qmMessageBean.getContent());
        session.setDate(sd.format(new Date()));
        session.setType(qmMessageBean.getType());
        if(QtalkClient.getInstance().getQMConversationManager().isHaveConversation(qmMessageBean.getToUser(), qmMessageBean.getFromUser())){
            QtalkClient.getInstance().getQMConversationManager().updateConversation(session);
        }else{
            QtalkClient.getInstance().getQMConversationManager().addConversation(session);
        }
        Intent intent=new Intent(StaticConfig.ACTION_ADDFRIEND);//发送广播，通知消息界面更新
        if(context != null)
            context.sendBroadcast(intent);
    }

}
