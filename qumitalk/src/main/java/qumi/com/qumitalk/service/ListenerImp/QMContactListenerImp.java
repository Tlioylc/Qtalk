package qumi.com.qumitalk.service.ListenerImp;

/**
 * Created by mwang on 2018/5/21.
 */

public interface QMContactListenerImp {

    /**
     * 同意订阅
     * */
    void onContactAgreed(String username);

    /**
     * 拒绝订阅
     * */
    void onContactRefused(String username);

    /**
     * 订阅邀请
     * */
    void onContactInvited(String username);

    /**
     * 取消订阅
     * */
    void onContactDeleted(String username);


    /**
     * 下线
     * */
    void onUnavailable(String username);


    /**
     * 上线
     * */
    void onAvailable(String username);
}
