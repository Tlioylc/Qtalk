package qumi.com.qumitalk.service.Util;

import android.util.Log;

import qumi.com.qumitalk.service.Config.StaticConfig;

/**
 * Created by mwang on 2018/5/16.
 */

public class LogUtil {
    public static void e(String s){
//        Log.e("test",s);

        if(StaticConfig.isdebug)
            Log.e("test",s);
    }
}
