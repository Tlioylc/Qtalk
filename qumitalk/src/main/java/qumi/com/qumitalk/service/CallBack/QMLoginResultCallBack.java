package qumi.com.qumitalk.service.CallBack;

/**
 * Created by mwang on 2018/5/16.
 */

public interface QMLoginResultCallBack {
    /**
     *  登陆成功回调
     */
    void onSuccess();

    /**
     *   登陆失败回调
     */
    void onError(String s);

    /**
     *   登陆过程中回调
     */
    void onProgress();
}
