package com.refresh.www.OtherUtils.FileUtils;

import android.os.Environment;

import com.refresh.www.UiShowUtils.PopMessageUtil;

import java.io.File;

/**
 * Created by Administrator on 2016/3/21.
 */
public class FileUtils {
    private final static String TAG = FileUtils.class.getSimpleName();
    public final static String FirstFolder = "Refresh";                                               //一级目录名称
    private final static String MY_PATH = Environment.getExternalStorageDirectory()
            + File.separator + FirstFolder + File.separator;

    public static String getFilePath(){
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//            Log.e(TAG, "无sd卡");
            return null;
        }
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        PopMessageUtil.Log("path------>>" + path);
        PopMessageUtil.Log("ALBUM_PATH--------->>" + MY_PATH);
        File dirFirstFile = new File(MY_PATH);                                                      //新建一级主目录
        if (!dirFirstFile.exists()) {                                                               //判断文件夹目录是否存在
            dirFirstFile.mkdir();                                                                   //如果不存在则创建
            PopMessageUtil.Log("dirFirstFile.mkdir()===");
        }
        else
            PopMessageUtil.Log("文件存在===");
        return MY_PATH;                                                                             //sdcard/0/idami.store/
    }
}
