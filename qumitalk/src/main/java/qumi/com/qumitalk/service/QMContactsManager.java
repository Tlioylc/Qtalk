package qumi.com.qumitalk.service;

import android.text.TextUtils;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jivesoftware.smackx.xdata.Form;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import qumi.com.qumitalk.service.Db.Session;
import qumi.com.qumitalk.service.DataBean.UserBean;
import qumi.com.qumitalk.service.Util.LogUtil;

/**
 * Created by mwang on 2018/5/18.
 */

public class QMContactsManager {
    private Roster roster;
    private QMXMPPConnectClient qmxmppConnectClient;

    public static final int NONE = 0;//没有任何好友关系
    public static final int APPLIED = 1; //已向对方提出好友申请
    public static final int RECEIEVED = 2;//已收到对方好友申请
    public static final int FRIEND = 3;//已建立好友关系
    public static final int removed = 4;//已删除

    protected QMContactsManager(QtalkClient qtalkClient){
        this.qmxmppConnectClient = qtalkClient.getClient();
        if(roster == null){
            roster = Roster.getInstanceFor(qmxmppConnectClient);
        }
    }

    private int getRelationShip(String name){
        switch (name){
            case "none":
                return  NONE;
            case "to":
                return  APPLIED;
            case "from":
                return  RECEIEVED;
            case "both":
                return  FRIEND;
            case "remove":
                return  removed;
            default:
                return NONE;
        }
    }
    public List<UserBean> getAllContactsFromServer(){
        List<UserBean> contacts = new ArrayList<>();
        Collection<RosterEntry> rosterEntry = roster.getEntries();
        Iterator<RosterEntry> i = rosterEntry.iterator();
        while (i.hasNext()){
            RosterEntry rosterentry=  i.next();
            Presence presence = roster.getPresence(rosterentry.getUser());
            UserBean userBean = new UserBean();
            userBean.setNickName(rosterentry.getName());
            userBean.setUid(rosterentry.getUser().split("@")[0]);
            userBean.setMood(presence.getStatus());
            userBean.setFriendRelationship(getRelationShip(rosterentry.getType().name()));
            if(!TextUtils.isEmpty(presence.getStatus())){
                userBean.setMood(presence.getStatus());
            }else{
                userBean.setMood("Xxxxxxxxxx");
            }
            userBean.setAvailable(presence.isAvailable());
            contacts.add(userBean);
        }
        return contacts;
    }


    public void addContanct(final String userName){
        QtalkClient.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                addUser(userName);
            }
        });
    }
    public void deleteContanct(final String userName){
        QtalkClient.getInstance().getQmChatMessageManager().deleteConversationAllMessage(qmxmppConnectClient.getUser().split("@")[0],userName);
        QtalkClient.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                removeUser(userName);
            }
        });
    }


    /**
     * 删除一个好友
     * @param userName
     * @return
     */
    public  boolean removeUser(String userName)
    {
        try {
            if(userName.contains("@"))
            {
                userName = userName.split("@")[0];
            }
            RosterEntry entry = roster.getEntry(userName);
            roster.removeEntry(entry);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * 同意好友请求并添加对方为好友
     */
    public void agreeContanct(final String userName){
        QtalkClient.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                agreeApply(userName);
            }
        });
    }
    public boolean agreeApply(String userName){
        Presence presenceRes = new Presence(Presence.Type.subscribed);
        presenceRes.setTo(userName+"@"+qmxmppConnectClient.getServiceName());
        LogUtil.e(userName+"@"+qmxmppConnectClient.getServiceName());
        try {
            qmxmppConnectClient.sendStanza(presenceRes);
            addUser(userName);
            return true;
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 拒绝好友请求
     */
    public void rejectContanct(final String userName){
        QtalkClient.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                rejectApply(userName);
            }
        });
    }
    public boolean rejectApply(String userName){
        Presence presenceRes = new Presence(Presence.Type.unsubscribe);
        presenceRes.setTo(userName);
        try {
            qmxmppConnectClient.sendStanza(presenceRes);
            return true;
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 添加一个好友  无分组
     */
    public  boolean addUser(String userName)
    {
        try {
            roster.createEntry(userName+"@"+qmxmppConnectClient.getServiceName(), userName, null);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    /**
     * 添加一个好友到分组
     * @param userName
     * @param groupName
     * @return
     */
    public  boolean addUser(String userName,String groupName)
    {
        try {
            roster.createEntry(userName+"@"+qmxmppConnectClient.getServiceName(), userName,new String[]{ groupName});
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 查找用户
     * @param userName
     * @return
     */
    public  List<Session> searchUsers(String userName) {
        List<Session> listUser=new ArrayList<Session>();
        try{
            UserSearchManager search = new UserSearchManager(qmxmppConnectClient);
            //此处一定要加上 search.
            Form searchForm = search.getSearchForm("search."+qmxmppConnectClient.getServiceName());
            Form answerForm = searchForm.createAnswerForm();
            answerForm.setAnswer("Username", true);
            answerForm.setAnswer("search", userName);
            ReportedData data = search.getSearchResults(answerForm,"search."+qmxmppConnectClient.getServiceName());
            Iterator<ReportedData.Row> it = data.getRows().iterator();
            ReportedData.Row row=null;
            while(it.hasNext()){
                row=it.next();
                Session session=new Session();
                session.setFromUser(row.getValues("Username").iterator().next().toString());

//                VCardManager vCardManager = VCardManager.getInstanceFor(qmxmppConnectClient);
//                VCard vCard = vCardManager.loadVCard(userName+"@"+qmxmppConnectClient.getServiceName());
//                LogUtil.e(vCard.getNickName()+"--------nickName-----------"+vCard.toXML().toString());

                listUser.add(session);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listUser;
    }
}
