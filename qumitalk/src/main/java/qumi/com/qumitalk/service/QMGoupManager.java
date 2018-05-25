package qumi.com.qumitalk.service;

import android.text.TextUtils;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.muc.InvitationListener;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.FormField;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import qumi.com.qumitalk.service.CustomElement.GroupElement;
import qumi.com.qumitalk.service.Db.Session;
import qumi.com.qumitalk.service.Imp.QMGroupChatInviteListenerImp;
import qumi.com.qumitalk.service.Listener.QMGroupChatListener;
import qumi.com.qumitalk.service.Imp.QMMessageListenerImp;
import qumi.com.qumitalk.service.Util.LogUtil;
import qumi.com.qumitalk.service.CallBack.QMGroupListResultCallBack;


/**
 * Created by mwang on 2018/5/21.
 */

public class QMGoupManager {
    private QMXMPPConnectClient qmxmppConnectClient;
    private MultiUserChatManager multiUserChatManager ;
    private QMMessageListenerImp qmMessageListenerImp;
    protected QMGoupManager(QtalkClient qtalkClient){
        this.qmxmppConnectClient = qtalkClient.getClient();
        this.multiUserChatManager = MultiUserChatManager.getInstanceFor(qmxmppConnectClient);
    }

    public void addMessageListener(QMMessageListenerImp qmMessageListenerImp){
        this.qmMessageListenerImp = qmMessageListenerImp;
    }

    public boolean createChatGroup(String roomName, String password){
        return createChatGroup(roomName, password,null,null);
    }

    public boolean createChatGroup(String roomName, String password,ArrayList<String> inviteList,String reason){
        if(!qmxmppConnectClient.isConnected()) {
            throw new NullPointerException("服务器连接失败，请先连接服务器");
        }
        MultiUserChat muc = null;
        try {
            // 创建一个MultiUserChat
            muc = MultiUserChatManager.getInstanceFor(qmxmppConnectClient).getMultiUserChat(roomName + "@conference." + qmxmppConnectClient.getServiceName());
            // 创建聊天室
            boolean isCreated = muc.createOrJoin( qmxmppConnectClient.getUser().split("@")[0]);
            if(qmMessageListenerImp != null) {
                muc.addMessageListener(new QMGroupChatListener(qmMessageListenerImp));
            }
            if(isCreated) {
                // 获得聊天室的配置表单
                Form form = muc.getConfigurationForm();
                // 根据原始表单创建一个要提交的新表单。
                Form submitForm = form.createAnswerForm();
                // 向要提交的表单添加默认答复
                for (Iterator fields = form.getFields().iterator(); fields.hasNext();) {
                    FormField field = (FormField) fields.next();
                    if(FormField.Type.hidden !=  field.getType() &&
                            field.getVariable() != null) {
                        // 设置默认值作为答复
                        submitForm.setDefaultAnswer(field.getVariable());
                    }
                }
                // 设置聊天室的新拥有者
                List owners = new ArrayList();
                owners.add(qmxmppConnectClient.getUser());// 用户JID
                submitForm.setAnswer("muc#roomconfig_roomowners", owners);
                // 设置聊天室是持久聊天室，即将要被保存下来
                submitForm.setAnswer("muc#roomconfig_persistentroom", true);
                // 房间仅对成员开放
                submitForm.setAnswer("muc#roomconfig_membersonly", false);
                // 允许占有者邀请其他人
                submitForm.setAnswer("muc#roomconfig_allowinvites", true);
                if(password != null && password.length() != 0) {
                    // 进入是否需要密码
                    submitForm.setAnswer("muc#roomconfig_passwordprotectedroom",  true);
                    // 设置进入密码
                    submitForm.setAnswer("muc#roomconfig_roomsecret", password);
                }
                // 能够发现占有者真实 JID 的角色
                // submitForm.setAnswer("muc#roomconfig_whois", "anyone");
                // 登录房间对话
                submitForm.setAnswer("muc#roomconfig_enablelogging", true);
                // 仅允许注册的昵称登录
                submitForm.setAnswer("x-muc#roomconfig_reservednick", true);
                // 允许使用者修改昵称
                submitForm.setAnswer("x-muc#roomconfig_canchangenick", false);
                // 允许用户注册房间
                submitForm.setAnswer("x-muc#roomconfig_registration", false);
                // 发送已完成的表单（有默认值）到服务器来配置聊天室
                muc.sendConfigurationForm(submitForm);


                if(inviteList != null && inviteList.size() > 0){
                    for(String name : inviteList )
                        if(TextUtils.isEmpty(reason)){
                            muc.invite(name+"@" +qmxmppConnectClient.getServiceName(),"");
                        }else {
                            muc.invite(name+"@"+qmxmppConnectClient.getServiceName(),reason);
                        }
                }
            }
        } catch (XMPPException | SmackException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    /**
     * 加入会议室
     *
     * @param password 会议室密码
     * @param roomsName 会议室名
     *
     */
    public  boolean joinMultiUserChat(String password, String roomsName) {
        try {
            // 使用XMPPConnection创建一个MultiUserChat窗口
            MultiUserChat multiUserChat = multiUserChatManager.getMultiUserChat(roomsName +"@conference." +qmxmppConnectClient.getServiceName());
            // 聊天室服务将会决定要接受的历史记录数量
            DiscussionHistory history = new DiscussionHistory();
            history.setMaxStanzas(0);
            //history.setSince(new Date());
            // 用户加入聊天室
            multiUserChat.join(QtalkClient.getInstance().getUser(), password, history,5000);
            if(qmMessageListenerImp != null)
                multiUserChat.addMessageListener(new QMGroupChatListener(qmMessageListenerImp));

            System.out.println("会议室加入成功........");
            return true;
        } catch (XMPPException e) {
            e.printStackTrace();
            System.out.println("会议室加入失败........");
            return false;
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            return false;
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 查询会议室成员名字
     *
     */
    public  List<String> findMulitUser(String roomsName){
        List<String> listUser = new ArrayList<String>();
        MultiUserChat muc = multiUserChatManager.getMultiUserChat(roomsName +"@conference." +qmxmppConnectClient.getServiceName());
        Iterator<String> it = muc.getOccupants().iterator();
        //遍历出聊天室人员名称
        while (it.hasNext()) {
            // 聊天室成员名字
            String name =  it.next();
            listUser.add(name);
        }
        return listUser;
    }


    /**
     * 获取已加入聊天室列表
     *
     */
    public  void findMulitGroup(final QMGroupListResultCallBack qmGroupListResultCallBack){

        GroupElement qmCostomIQ = new GroupElement();
        LogUtil.e(qmCostomIQ.toXML().toString());
        try {
            qmxmppConnectClient.sendIqWithResponseCallback(qmCostomIQ, new StanzaListener() {
                @Override
                public void processPacket(Stanza packet) {
                    String resultData = packet.toXML().toString();
                    String groups= resultData.substring(resultData.indexOf("<groups xmlns='com:qumi:group'>")+31,resultData.indexOf("</groups>"));
                    String[] groupArray = groups.split("&");

                    List<String>  groupList =  new ArrayList<>();
                    for(String groupName : Arrays.asList(groupArray)){
                        groupList.add(groupName.split("@")[0]);
                    }

                    qmGroupListResultCallBack.onGetResult(groupList);
                }
            });
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
//        List<String> tempRoomList = null;

//        try {
//            tempRoomList = multiUserChatManager.getJoinedRooms(qmxmppConnectClient.getUser());
//            for (String roomId : tempRoomList) {
//                RoomInfo roomInfo = multiUserChatManager.getRoomInfo(roomId);
//                Group.add(roomInfo.getName());
//            }
//        } catch (SmackException.NoResponseException e) {
//            e.printStackTrace();
//        } catch (XMPPException.XMPPErrorException e) {
//            e.printStackTrace();
//        } catch (SmackException.NotConnectedException e) {
//            e.printStackTrace();
//        }

    }

    /**
     * 获取已加入聊天室列表
     *
     */
    public  List<Session> findMulitGroup(String groupName){
        List<Session> Group = new ArrayList<>();
        try {
            List<HostedRoom> hostedRoomList = multiUserChatManager.getHostedRooms(multiUserChatManager.getServiceNames().get(0));
            for (HostedRoom room : hostedRoomList) {
                String roomName = room.getName();
                if(roomName.contains(groupName)){
                    Session session=new Session();
                    session.setFromUser(roomName);
                    Group.add(session);
                }
                String roomJid = room.getJid();
            }

        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
        return Group;
    }


    public void addInviteListener(final QMGroupChatInviteListenerImp groupChatInviteListener){
        multiUserChatManager.addInvitationListener(new InvitationListener() {
            @Override
            public void invitationReceived(XMPPConnection conn, MultiUserChat room, String inviter, String reason, String password, Message message) {
                groupChatInviteListener.onInviteToJoinGroup(room.getRoom().split("@")[0],reason,password);
            }
        });
    }


}
