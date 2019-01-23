package com.refresh.www.OtherUtils.FileUtils;

import android.content.Context;
import android.content.SharedPreferences;

import com.refresh.www.Application.BaseApplication;

/**
 * Created by Administrator on 2015/10/21.
 */
public class PrefUtils {
    private static final String TAG = PrefUtils.class.getSimpleName();
    private static final String NAME = "config";
    private static SharedPreferences sp=null;

    //------------使用------------------//
    public static String getMemoryString(String key){ return getString(BaseApplication.getInstance(),key,""); }
    public static void setMemoryString(String key,String value) {   putString(BaseApplication.getInstance(),key,value); }

    public static Boolean getMemoryBooleanNo(String key) { return getBoolean(BaseApplication.getInstance(),key,false);}
    public static Boolean getMemoryBooleanYes(String key) { return getBoolean(BaseApplication.getInstance(),key,true);}
    public static void setMemoryBoolean(String key,Boolean value) { putBoolean(BaseApplication.getInstance(),key,value);}

    public static int getMemoryInt(String key) { return getInt(BaseApplication.getInstance(),key,0);}
    public static void setMemoryInt(String key,int value) { putInt(BaseApplication.getInstance(),key,value);}
    //---------------------------------//
    public static void putInt(Context context, String key, int value){
        sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        sp.edit().putInt(key, value).commit();
    }
    public static int getInt(Context context, String key, int defaultValue){
        sp=context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return sp.getInt(key, defaultValue);
    }

    public static void putString(Context context, String key, String value){
        sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        sp.edit().putString(key, value).commit();
    }
    public static String getString(Context context, String key, String defaultValue){
        sp=context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return sp.getString(key, defaultValue);
    }

    public static void putBoolean(Context context, String key, boolean value){
        sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        sp.edit().putBoolean(key, value).commit();
    }
    public static boolean getBoolean(Context context, String key, boolean defaultValue){
        sp=context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(key, defaultValue);
    }

}
