package qumi.com.qumitalk.service.Util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.ActivityCompat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class BaseUtil {
    private BaseUtil() {

    }

    private static final class Factory {
        public static final BaseUtil instance = new BaseUtil();
    }

    public static final BaseUtil obtain() {
        return Factory.instance;
    }



    // Storage Permissions variables
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    //persmission method.
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have read or write permission
        int writePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);


        if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public String getFilename(String filePath) {
        File path = new File(filePath);
        return path.getName();
    }

    public static String toUtf8(String str) {
        String result = null;
        try {
            result = new String(str.getBytes("UTF-8"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }
    public boolean fileIsExists(String strFile)
    {
        try
        {
            File f=new File(strFile);
            if(!f.exists())
            {
                return false;
            }

        }
        catch (Exception e)
        {
            return false;
        }

        return true;
    }

    public final static byte[] compressBitmap(Bitmap bitmap, long maxBytes) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            int options = 100;
            while (baos.toByteArray().length > maxBytes && options > 30) {
                baos.reset();
                bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
                options -= 10;
            }
            byte[] bts = baos.toByteArray();
            baos.close();
            return bts;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
                bitmap = null;
            }
        }
    }

    public static byte[] compressBitmap(String path, long maxBytes) {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        if (bitmap == null || bitmap.isRecycled()) {
            return null;
        }
        return compressBitmap(bitmap, maxBytes);
    }


}



