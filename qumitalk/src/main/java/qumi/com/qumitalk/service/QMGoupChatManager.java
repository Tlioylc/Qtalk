package qumi.com.qumitalk.service;

import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.muc.RoomInfo;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.FormField;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import qumi.com.qumitalk.service.Util.LogUtil;


/**
 * Created by mwang on 2018/5/21.
 */

public class QMGoupChatManager {
    private QtalkClient qtalkClient;
    private MultiUserChatManager multiUserChatManager ;
    protected QMGoupChatManager(QtalkClient qtalkClient){
        this.qtalkClient = qtalkClient;
        this.multiUserChatManager = MultiUserChatManager.getInstanceFor(qtalkClient);
    }

    public void createChatGroup(String roomName){
        try {
            // 创建一个MultiUserChat
            MultiUserChat muc = multiUserChatManager.getMultiUserChat(roomName +"@" +qtalkClient.getServiceName());

            // 创建聊天室
            muc.create(roomName);
            // 获得聊天室的配置表单
            Form form = muc.getConfigurationForm();
            // 根据原始表单创建一个要提交的新表单。
            Form submitForm = form.createAnswerForm();
            // 向要提交的表单添加默认答复
            for (Iterator fields = form.getFields().iterator(); fields.hasNext();) {
                FormField field = (FormField) fields.next();
                if (!FormField.Type.hidden.equals(field.getType())
                        && field.getVariable() != null) {
                    // 设置默认值作为答复
                    submitForm.setDefaultAnswer(field.getVariable());
                }
            }
            // 设置聊天室的新拥有者
            // List owners = new ArrayList();
            // owners.add("liaonaibo2\\40slook.cc");
            // owners.add("liaonaibo1\\40slook.cc");
            // submitForm.setAnswer("muc#roomconfig_roomowners", owners);
            // 设置聊天室是持久聊天室，即将要被保存下来
            submitForm.setAnswer("muc#roomconfig_persistentroom", true);
            // 房间仅对成员开放
            submitForm.setAnswer("muc#roomconfig_membersonly", false);
            // 允许占有者邀请其他人
            submitForm.setAnswer("muc#roomconfig_allowinvites", true);
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
            System.out.println("创建会议室成功！！！！！");
        } catch (XMPPException e) {
            e.printStackTrace();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (SmackException e) {
            e.printStackTrace();
        }
    }


    /**
     * 加入会议室
     *
     * @param user 昵称
     * @param password 会议室密码
     * @param roomsName 会议室名
     *
     */
    public  boolean joinMultiUserChat(String user, String password, String roomsName
                                                 ) {
//
//
//        //群jid
//        String jid = roomsName + "@conference." + qtalkClient.getServiceName();
//        //jid实体创建
//        EntityBareJid groupJid = JidCreate.entityBareFrom(jid);
//
//        //获取群管理对象
//        MultiUserChatManager multiUserChatManager = MultiUserChatManager.getInstanceFor(connection);
//        //通过群管理对象获取该群房间对象
//        MultiUserChat multiUserChat = multiUserChatManager.getMultiUserChat(groupJid);
//
//        MucEnterConfiguration.Builder builder = multiUserChat.getEnterConfigurationBuilder(Resourcepart.from(nickName));
//        //只获取最后99条历史记录
//        builder.requestMaxCharsHistory(99);
//        MucEnterConfiguration mucEnterConfiguration = builder.build();
//        //加入群
//        multiUserChat.join(mucEnterConfiguration);
        try {
            // 使用XMPPConnection创建一个MultiUserChat窗口
            MultiUserChat muc = multiUserChatManager.getMultiUserChat(roomsName +"@conference." +qtalkClient.getServiceName());
            // 聊天室服务将会决定要接受的历史记录数量
            DiscussionHistory history = new DiscussionHistory();
            history.setMaxStanzas(0);
            //history.setSince(new Date());
            // 用户加入聊天室
            muc.join("3333", password, history,5000);
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
        MultiUserChat muc = multiUserChatManager.getMultiUserChat(roomsName +"@" +qtalkClient.getServiceName());
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
    public  List<String> findMulitGroup(){
        List<String> tempRoomList = null;
        List<String> Group = new ArrayList<>();
        try {
            tempRoomList = multiUserChatManager.getJoinedRooms(qtalkClient.getUser());
            for (String roomId : tempRoomList) {
                RoomInfo roomInfo = multiUserChatManager.getRoomInfo(roomId);
                Group.add(roomInfo.getName());
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

}
