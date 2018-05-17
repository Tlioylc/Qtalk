package qumi.com.qumitalk.service.listener;

import java.util.List;

import qumi.com.qumitalk.service.DataBean.QMMessageBean;

/**
 * Created by mwang on 2018/5/17.
 */

public interface  QMMessageListener{
    /*
    *  收到消息
    */
    void onMessageReceived(QMMessageBean messages);

    /*
    *  收到透传消息
    */
    void onCmdMessageReceived(QMMessageBean messages);

    /*
    *  收到已读回执
    */
    void onMessageRead(QMMessageBean messages);

    /*
    *  收到已送达回执
    */
    void onMessageDelivered(QMMessageBean message);

    /*
    *  消息被撤回
    */
    void onMessageRecalled(QMMessageBean messages);

    /*
    *  消息状态变动
    */
    void onMessageChanged(QMMessageBean message, Object change);

}
