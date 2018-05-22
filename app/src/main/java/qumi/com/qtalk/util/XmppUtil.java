package qumi.com.qtalk.util;

import java.util.List;



import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import qumi.com.qtalk.R;
import qumi.com.qumitalk.service.Db.Session;
import qumi.com.qumitalk.service.QtalkClient;

public class XmppUtil {
	
	/** 
     * 注册 
     *  
     * @param account 
     *            注册帐号 
     * @param password 
     *            注册密码 
     * @return 1、注册成功 0、服务器没有返回结果2、注册失败3、服务器未连接
     */  
    public static int register(String account, String password) {
		return  QtalkClient.getInstance().creatAccount(account,password);
    }  
    
    /** 
     * 查询用户 
     *  
     * @param userName 
     * @return
     */  
    public static List<Session> searchUsers(String userName) {
        return QtalkClient.getInstance().getQMMContactsManager().searchUsers(userName);
    }  
    
	 /** 
     * 更改用户状态 
     */  
    public static void setPresence(Context context,int code) {
      	QtalkClient.getInstance().setPresence(context,code);
    }  
//	/**
//     * 删除当前用户
//     * @param connection
//     * @return
//     */
//    public static boolean deleteAccount(XMPPConnection connection)
//    {
////        try {
////            connection.getAccountManager().deleteAccount();
////            return true;
////        } catch (Exception e) {
////            return false;
////        }
//
//		return true;
//    }
////	/**
//	 * 返回所有组信息 <RosterGroup>
//	 * @return List(RosterGroup)
//	 */
//	public static List<RosterGroup> getGroups(Roster roster) {
//		List<RosterGroup> groupsList = new ArrayList<RosterGroup>();
//		Collection<RosterGroup> rosterGroup = roster.getGroups();
//		Iterator<RosterGroup> i = rosterGroup.iterator();
//		while (i.hasNext())
//			groupsList.add(i.next());
//		return groupsList;
//	}

//	/**
//	 * 返回相应(groupName)组里的所有用户<RosterEntry>
//	 * @return List(RosterEntry)
//	 */
//	public static List<RosterEntry> getEntriesByGroup(Roster roster,
//			String groupName) {
//		List<RosterEntry> EntriesList = new ArrayList<RosterEntry>();
//		RosterGroup rosterGroup = roster.getGroup(groupName);
//		Collection<RosterEntry> rosterEntry = rosterGroup.getEntries();
//		Iterator<RosterEntry> i = rosterEntry.iterator();
//		while (i.hasNext())
//			EntriesList.add(i.next());
//		return EntriesList;
//	}

//	/**
//	 * 返回所有用户信息 <RosterEntry>
//	 * @return List(RosterEntry)
//	 */
//	public static List<RosterEntry> getAllEntries(Roster roster) {
//		List<RosterEntry> EntriesList = new ArrayList<RosterEntry>();
//		Collection<RosterEntry> rosterEntry = roster.getEntries();
//		Iterator<RosterEntry> i = rosterEntry.iterator();
//		while (i.hasNext()){
//			RosterEntry rosterentry=  (RosterEntry) i.next();
//			Log.e("jj", "好友："+rosterentry.getUser()+","+rosterentry.getName()+","+rosterentry.getType().name());
//			EntriesList.add(rosterentry);
//		}
//		return EntriesList;
//	}
	
	
//	/**
//     * 创建一个组
//     */
//	public static boolean addGroup(Roster roster,String groupName)
//    {
//        try {
//            roster.createGroup(groupName);
//            return true;
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.e("jj", "创建分组异常："+e.getMessage());
//            return false;
//        }
//    }
//
//    /**
//     * 删除一个组
//     */
//    public static boolean removeGroup(Roster roster,String groupName)
//    {
//        return false;
//    }
//
//    /**
//	 * 添加一个好友  无分组
//	 */
//	public static boolean addUser(Roster roster,String userName,String name)
//	{
//		try {
//			roster.createEntry(userName, name, null);
//			return true;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return false;
//		}
//
//	}
	/**
	 * 添加一个好友到分组
	 * @param userName
	 * @param name
	 * @return
	 */
	public static boolean addUsers(String userName,String name,String groupName)
	{
		try {
			QtalkClient.getInstance().getQMMContactsManager().addContanct(userName);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("jj", "添加好友异常："+e.getMessage());
			return false;
		}

	}
	
	/**
	 * 删除一个好友
	 * @param userJid
	 * @return
	 */
	public static boolean removeUser(String userJid)
	{
		try {
		    QtalkClient.getInstance().getQMMContactsManager().removeUser(userJid);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
//	/**
//     * 把一个好友添加到一个组中
//     * @param userJid
//     * @param groupName
//     */
//    public static void addUserToGroup(final String userJid, final String groupName,
//            final XMPPConnection connection) {
////            	RosterGroup group = connection.getRoster().getGroup(groupName);
////                // 这个组已经存在就添加到这个组，不存在创建一个组
////                RosterEntry entry = connection.getRoster().getEntry(userJid);
////                try {
////                    if (group != null) {
////                        if (entry != null)
////                            group.addEntry(entry);
////                    } else {
////                        RosterGroup newGroup = connection.getRoster().createGroup("我的好友");
////                        if (entry != null)
////                            newGroup.addEntry(entry);
////                    }
////                } catch (Exception e) {
////                    e.printStackTrace();
////                }
//    }

//    /**
//     * 把一个好友从组中删除
//     * @param userJid
//     * @param groupName
//     */
//    public static void removeUserFromGroup(final String userJid,final String groupName, final XMPPConnection connection) {
////            RosterGroup group = connection.getRoster().getGroup(groupName);
////            if (group != null) {
////                try {
////                	RosterEntry entry = connection.getRoster().getEntry(userJid);
////                    if (entry != null)
////                        group.removeEntry(entry);
////                } catch (XMPPException e) {
////                    e.printStackTrace();
////                }
////            }
//     }
//
    /** 
     * 修改签名
     */  
    public static void changeSign(int code , String content) throws Exception {
        QtalkClient.getInstance().changeSign(code,content);
    }

	
	public static void setOnlineStatus(ImageView iv_stutas,int code,TextView tv_stutas,String[] items ){
		switch (code) {
		case 0://在线
			iv_stutas.setImageResource(R.drawable.evk);
			tv_stutas.setText(items[0]);
			break;
		case 1://q我吧
			iv_stutas.setImageResource(R.drawable.evm);
			tv_stutas.setText(items[1]);
			break;
		case 2://隐身
			iv_stutas.setImageResource(R.drawable.evf);
			tv_stutas.setText(items[2]);
			break;
		case 3://忙碌
			iv_stutas.setImageResource(R.drawable.evd);
			tv_stutas.setText(items[3]);
			break;
		case 4://离开
			iv_stutas.setImageResource(R.drawable.evp);
			tv_stutas.setText(items[4]);
			break;
		default:
			break;
		}
		
	}
	

    
}
