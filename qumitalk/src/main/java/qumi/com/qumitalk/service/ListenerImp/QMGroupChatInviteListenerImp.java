package qumi.com.qumitalk.service.ListenerImp;

public interface QMGroupChatInviteListenerImp {

    void onInviteToJoinGroup(String groupName, String reason, String joinPassword);
}
