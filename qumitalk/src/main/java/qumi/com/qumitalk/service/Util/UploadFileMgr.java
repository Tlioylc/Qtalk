package qumi.com.qumitalk.service.Util;

import com.qiniu.android.common.FixedZone;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONObject;

public class UploadFileMgr {
    private UploadFileMgr() {

    }

    private static final class INSTANCE {
        public static final UploadFileMgr instance = new UploadFileMgr();
    }

    public static UploadFileMgr obtain() {
        return UploadFileMgr.INSTANCE.instance;
    }



    public void upLoadFile(String path, String token, UploadFileMgr.UpLoadCallBack back) {
        this.callBack = back;
        path = path.replace("file://", "");
        String name = BaseUtil.obtain().getFilename(path);

        Configuration configuration = new Configuration.Builder().build();
        configuration.zone = FixedZone.zone2;
        UploadManager upLoadImgMgr = new UploadManager(configuration);
        upLoadImgMgr.put(path, null, token, new UploadFileMgr.UpLoadResultHandler(name), null);
    }


    private class UpLoadResultHandler implements UpCompletionHandler {
        private String fileName;
        public  UpLoadResultHandler(String name){
            fileName = name;
        }

        @Override
        public void complete(String arg0, ResponseInfo info, JSONObject arg2) {
            if (info.isOK()) {
                String name = getFileName(arg2);
                if (callBack != null) {
                    callBack.onSuccess(name ,fileName);
                }
            }
            else {
                if (callBack != null) {
                    callBack.onFail(fileName);
                }
            }
            System.gc();
        }
    }

    private String getFileName(JSONObject jasonData){
        String name = "";
        if (jasonData == null) {
            return name;
        }

        try {
            name = jasonData.getString("key");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return name;
    }


    public interface UpLoadCallBack {
        void onSuccess(String name, String fileName);

        void onFail(String fileName);
    }

    private UploadFileMgr.UpLoadCallBack callBack;

}
