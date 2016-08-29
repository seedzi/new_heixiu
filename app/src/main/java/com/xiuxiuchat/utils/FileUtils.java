package com.xiuxiuchat.utils;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by huzhi on 16-6-6.
 */
public class FileUtils {

    private static String TAG = "FileUtils";

    public static String getFileNameBySuffix(String pathandname){
        int start=pathandname.lastIndexOf("/");
        int end=pathandname.length();
        if(start!=-1 && end!=-1){
            return pathandname.substring(start+1,end);
        }else{
            return null;
        }

    }

    public static String getFileSuffix(String pathandname){
        try {
            return pathandname.substring(pathandname.lastIndexOf(".")+1,pathandname.length());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * example: abc.txtt -> abc
     * @param pathandname
     * @return
     */
    public static String getFileName(String pathandname){
        int start=0;
        int end=pathandname.lastIndexOf(".");
        if(start!=-1 && end!=-1){
            return pathandname.substring(start,end);
        }else{
            return null;
        }

    }
    public static boolean  saveBitmap2file(Bitmap bmp,String filePath){
        android.util.Log.d(TAG,"filePath = " + filePath);
        File f = new File(filePath);
        if(f.exists()){
            android.util.Log.d(TAG,"f.delete()");
            f.delete();
        }
        if(!f.exists()){
            try {
                f.createNewFile();
            } catch (IOException e) {
                android.util.Log.d(TAG,"e = " + e.getMessage());
                e.printStackTrace();
            }
        }

        Bitmap.CompressFormat format= Bitmap.CompressFormat.JPEG;
        int quality = 100;
        OutputStream stream = null;
        try {
            stream = new FileOutputStream(filePath);
        } catch (FileNotFoundException e) {
            android.util.Log.d(TAG,"e = " + e.getMessage());
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bmp.compress(format, quality, stream);
    }


    public static byte[] bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }
}
