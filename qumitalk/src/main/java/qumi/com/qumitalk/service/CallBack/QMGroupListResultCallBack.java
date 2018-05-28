package qumi.com.qumitalk.service.CallBack;

import java.util.List;

public  interface QMGroupListResultCallBack {

    /**
     * 获取已加入群聊列表回调
     * */
    void onGetResult(List<String> groups);
}
