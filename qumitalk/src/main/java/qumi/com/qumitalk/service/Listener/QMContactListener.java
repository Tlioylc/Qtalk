package qumi.com.qumitalk.service.Listener;

/**
 * Created by mwang on 2018/5/21.
 */

public interface  QMContactListener {
    void onContactAgreed(String username);

    void onContactRefused(String username);

    void onContactInvited(String username);

    void onContactDeleted(String username);

    void onUnavailable(String username);


    void onAvailable(String username);
}
