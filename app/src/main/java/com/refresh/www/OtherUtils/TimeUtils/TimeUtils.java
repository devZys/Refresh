package com.refresh.www.OtherUtils.TimeUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yy on 2017/11/1.
 */
public class TimeUtils {
    public static String getNowTime(){
        SimpleDateFormat formatter   =   new SimpleDateFormat("MMddHHmmss");
        Date curDate =  new Date(System.currentTimeMillis());
        return formatter.format(curDate);
    }
    public static String getYearMonth(){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMM");
        return sdf.format(new java.util.Date());
    }
    public static String getYearMonthDay(){
        SimpleDateFormat formatter   =   new SimpleDateFormat("yyyyMMdd");
        Date curDate =  new Date(System.currentTimeMillis());
        return formatter.format(curDate);
    }
    public static String getStandardTime(){
        SimpleDateFormat formatter   =   new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate =  new Date(System.currentTimeMillis());
        return formatter.format(curDate);
    }
}
