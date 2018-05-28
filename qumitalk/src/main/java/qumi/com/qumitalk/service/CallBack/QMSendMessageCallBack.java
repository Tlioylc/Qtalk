package qumi.com.qumitalk.service.CallBack;

public interface QMSendMessageCallBack {

    /**
     * 发送消息成功回调
     * */
    void onSuccess();

    /**
     * 发送消息失败回调，需要的时候可以添加参数
     * */
    void onFailed();
}
