package com.xiuxiu.utils;

import android.content.Context;
import android.text.TextUtils;

import com.xiuxiu.R;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by huzhi on 16-6-11.
 */
public class DateUtils {

    /**
     * 时间戳转换成日期
     * @param time
     * @return
     */
    public static String time2Date(String time){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
        String sd = sdf.format(new Date(Long.parseLong(time)));
        return sd;
    }

    public static String getTextByTime(Context context, Long time,
                                      int formatId) {
        return getTextByTime(context, String.valueOf(time), formatId);
    }

    private static final long DIFF_TIME = 24*60*60*1000;
    public static String getTextByTime(Context context, String time,
                                       int formatId) {
        try {
            if (Long.valueOf(time) < 0) {
                return context.getResources().getString(
                        R.string.rss_list_time_text_30_days);
            }
        }catch (Exception e){
        }
        long tranTimeWithLocalZone = tranTimeWithLocalZone(time);
        long currentLocalZoneTime = getCurrentLocalZoneTime();
        long diff = currentLocalZoneTime - tranTimeWithLocalZone;
        if (diff < 0) {
            return context.getResources().getString(
                    R.string.rss_list_time_text_just);
        }
        if (TextUtils.isEmpty(time)) {
            return getMessageByDate(context, new Date(currentLocalZoneTime),
                    formatId);
        }
        if(isSameDay(tranTimeWithLocalZone, currentLocalZoneTime)||diff<DIFF_TIME/*3600000*/){//如果是当天或者是小于24小时
            if (diff > 3600000) {
                return (int) Math.floor(diff / 3600000)
                        + context.getResources().getString(
                        R.string.rss_list_time_text_hour);
            } else if (diff > 60000) {
                return (int) Math.floor(diff / 60000)
                        + context.getResources().getString(
                        R.string.rss_list_time_text_min);
            } else {
                return context.getResources().getString(
                        R.string.rss_list_time_text_just);
            }
        }
        return getMessageByDate(context, new Date(tranTimeWithLocalZone),
                formatId);
    }

    /**
     * 将服务器时间戳转换为本地时区时间戳
     *
     * @param time
     * @return
     */
    private static long tranTimeWithLocalZone(String time) {
        try {
            if (TextUtils.isEmpty(time)){
                return  -1;
            }
            return transTimeWithLocalTimeZone(Long.parseLong(time) * 1000);
        }catch (Exception e){
            e.printStackTrace();
        }

        return  -1;
    }

    /**
     * 获取当前时区的时间戳
     *
     * @return
     */
    public static long getCurrentLocalZoneTime() {
        return System.currentTimeMillis();
        /*
        if(mCalendar==null){
            mCalendar = Calendar.getInstance();
        }
        return mCalendar.getTimeInMillis();
         */
    }

    public static long transTimeWithLocalTimeZone(long time) {
        return time - getDiffTimeZoneRawOffset(TimeZone.getDefault().getID());
    }

    private static int getDiffTimeZoneRawOffset(String timeZoneId) {
        return TimeZone.getDefault().getRawOffset()
                - TimeZone.getTimeZone(timeZoneId).getRawOffset();
    }


    private static ThreadLocal<Map<Integer,SimpleDateFormat>> mMessageDateThreadLocal = new ThreadLocal<Map<Integer, SimpleDateFormat>>();

    public static String getMessageByDate(Context context, Date date,
                                          int strFormatId) {
        Map<Integer,SimpleDateFormat> map = mMessageDateThreadLocal.get();
        if(map==null){
            map = Collections.synchronizedMap(new HashMap<Integer, SimpleDateFormat>());
            mMessageDateThreadLocal.set(map);
        }
        SimpleDateFormat simple = map.get(strFormatId);
        if(simple == null) {
            simple = new SimpleDateFormat(context.getResources()
                    .getString(strFormatId));
            map.put(strFormatId,simple);
        }
        return simple.format(date);
    }

    public static boolean isSameDay(long time1, long time2) {
        return isSameDay(new Date(time1), new Date(time2));
    }


    public static boolean isSameDay(Date day1, Date day2) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String ds1 = sdf.format(day1);
        String ds2 = sdf.format(day2);
        if (ds1.equals(ds2)) {
            return true;
        } else {
            return false;
        }
    }
}
