package qumi.com.qtalk.listener;

import qumi.com.qumitalk.service.ListenerImp.QMGroupChatInviteListenerImp;
import qumi.com.qumitalk.service.QtalkClient;

public class groupChatInviteListenerImp implements QMGroupChatInviteListenerImp {
    @Override
    public void onInviteToJoinGroup(String groupName, String reason, String joinPassword) {
        QtalkClient.getInstance().getQMGoupChatManager().joinMultiUserChat(joinPassword,groupName);
    }
}
