package qumi.com.qumitalk.service;

import android.content.Context;
import android.icu.lang.UScript;
import android.util.Log;

import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.xdata.Form;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import qumi.com.qumitalk.service.DataBean.Session;
import qumi.com.qumitalk.service.DataBean.UserBean;

/**
 * Created by mwang on 2018/5/18.
 */

public class QMMContactsManager {
    private Roster roster;
    private QtalkClient qtalkClient;

    QMMContactsManager(QtalkClient qtalkClient){
        this.qtalkClient = qtalkClient;
        if(roster == null){
            roster = Roster.getInstanceFor(qtalkClient);
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
            userBean.setAvailable(presence.isAvailable());

            contacts.add(userBean);
            Log.e("jj", "好友："+rosterentry.getUser()+","+rosterentry.getName()+","+rosterentry.getType().name());
        }
        return contacts;
    }

    public  List<Session> searchUsers(String userName) {
        List<Session> listUser=new ArrayList<Session>();
        try{
            UserSearchManager search = new UserSearchManager(qtalkClient);
            //此处一定要加上 search.
            Form searchForm = search.getSearchForm("search."+ qtalkClient.getServiceName());
            Form answerForm = searchForm.createAnswerForm();
            answerForm.setAnswer("Username", true);
            answerForm.setAnswer("search", userName);
            ReportedData data = search.getSearchResults(answerForm,"search."+ qtalkClient.getServiceName());
            Iterator<ReportedData.Row> it = data.getRows().iterator();
            ReportedData.Row row=null;
            while(it.hasNext()){
                row=it.next();
                Session session=new Session();
                session.setFromUser(row.getValues("Username").get(0).toString());
                listUser.add(session);
            }
        }catch(Exception e){
        }
        return listUser;
    }
}
