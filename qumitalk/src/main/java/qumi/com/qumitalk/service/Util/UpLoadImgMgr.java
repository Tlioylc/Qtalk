package qumi.com.qumitalk.service.Util;

import com.qiniu.android.common.FixedZone;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONObject;


public class UpLoadImgMgr {

    private final int MAX_SIE = 100  * 1024;
    private UpLoadImgMgr() {
    	
    }

    private static final class INSTANCE {
        public static final UpLoadImgMgr instance = new UpLoadImgMgr();
    }

    public static UpLoadImgMgr obtain() {
        return INSTANCE.instance;
    }



    public void upLoadFile(String path, String token, UpLoadCallBack back) {
        this.callBack = back;
        path = path.replace("file://", "");
        String name = BaseUtil.obtain().getFilename(path);

        Configuration configuration = new Configuration.Builder().build();
        configuration.zone = FixedZone.zone2;
        UploadManager upLoadImgMgr = new UploadManager(configuration);
        upLoadImgMgr.put(path, null, token, new UpLoadResultHandler(name), null);
    }


    private class UpLoadResultHandler implements UpCompletionHandler{
    	private String fileName;
        private int w;
        private int h;
    	public  UpLoadResultHandler(String name){
    		fileName = name;
    	}
    	
		@Override
		public void complete(String arg0, ResponseInfo info, JSONObject arg2) {
			
			if (info.isOK()) {
				String name = getFileName(arg2);
                String w = getFileWidth(arg2);
                String h = getFileHeight(arg2);
                if (callBack != null) {
                    callBack.onSuccess(name ,fileName,w ,h);
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

    private String getFileWidth(JSONObject jasonData){
        String w = "";
        if (jasonData == null) {
            return w;
        }

        try {
            w = jasonData.getString("w");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return w;
    }

    private String getFileHeight(JSONObject jasonData){
        String h = "";
        if (jasonData == null) {
            return h;
        }

        try {
            h = jasonData.getString("h");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return h;
    }


    public interface UpLoadCallBack {
        void onSuccess(String name, String fileName, String w, String h);

        void onFail(String fileName);
    }

    private UpLoadCallBack callBack;
    
}
