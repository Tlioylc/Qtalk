package qumi.com.qumitalk.service.Imp;

/**
 * Created by mwang on 2018/5/21.
 */

public interface QMContactListenerImp {
    void onContactAgreed(String username);

    void onContactRefused(String username);

    void onContactInvited(String username);

    void onContactDeleted(String username);

    void onUnavailable(String username);


    void onAvailable(String username);
}
