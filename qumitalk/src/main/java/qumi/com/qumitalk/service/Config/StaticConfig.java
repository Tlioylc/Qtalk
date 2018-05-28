package qumi.com.qumitalk.service.Config;

/**
 * Created by mwang on 2018/5/15.
 */

public class StaticConfig {
   /**
    * 服务器地址和域名
    * */
   public static final String host = "192.168.1.222";
   public static final String serviceName = "openfire.qumitech.com";

//   public static final String host = "192.168.1.106";
//   public static final String serviceName = "192.168.1.65";

   public static final int MSG_TYPE_TEXT=1;//文本消息
   public static final int MSG_TYPE_IMG=2;//图片
   public static final int MSG_TYPE_VOICE=3;//语音
   public static final int MSG_TYPE_LOCATION=4;//位置

   public static final int MSG_TYPE_ADD_FRIEND=5;//添加好友
   public static final int MSG_TYPE_ADD_FRIEND_SUCCESS=6;//同意添加好友


   public static final boolean isdebug = true;


   public static String qiniutoken = null;

   public static final String ACTION_ADDFRIEND= "com.android.qq.addfriend";
   public static final String ACTION_FRIENDS_ONLINE_STATUS_CHANGE= "com.android.qq.friends_online_status_change";
   public static final String ACTION_NEW_MSG= "com.android.qq.newmsg";
}
