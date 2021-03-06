package com.xiuxiuchat.easeim.xiuxiumsg;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.xiuxiuchat.XiuxiuApplication;
import com.xiuxiuchat.api.XiuxiuLoginResult;
import com.xiuxiuchat.base.Constant;
import com.xiuxiuchat.db.TableHelper;
import com.xiuxiuchat.provider.XiuxiuProvider;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by huzhi on 16-7-16.
 * 咻咻action消息数据库
 */
public class XiuxiuActionMsgTable implements TableHelper {


    public static final String TABLE_NAME = "xiuxiu_action_message";

    private final static String TABLE_URI_STR = XiuxiuProvider.BASE_URI_STR + "/" + TABLE_NAME;

    private final static Uri TABLE_URI = Uri.parse(TABLE_URI_STR);

    public static final String ASK_ID = "ask_id";

    public static final String STATUS = "status";

    public static final String UPDATE_TIME = "update_time";

    public static final String XIUXIU_ID = "xiuxiu_id";

    public static final String LOOCK_COUNT = "look_count";

    private final String CREATE_TABLE =  String.format("create table %s(%s text primary key, " +
                    "%s text, %s long, %s text, %s integer);",
            TABLE_NAME,
            ASK_ID,
            STATUS,
            UPDATE_TIME,
            XIUXIU_ID,
            LOOCK_COUNT);


    @Override
    public boolean create(SQLiteDatabase db) {
        if(db == null) {
            return false;
        }
        try {
            db.execSQL(CREATE_TABLE);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * 是否存在
     */
    public static boolean isExist(String xiuXiuId){
        int count = 0;
        String whereClause =  ASK_ID + " = ? ";
        ContentResolver resolver = getResolver();
        Cursor cursor = resolver.query(TABLE_URI,null,whereClause,new String[]{xiuXiuId},null);
        if(cursor!=null){
            count = cursor.getCount();
            cursor.close();
        }
        if(count>0){
            return true;
        }
        return false;
    }

    /**
     * 更新数据
     * @param bean
     */
    public static synchronized void update(XiuxiuActionInfo info){
        String whereClause =  ASK_ID + " = ? ";
        ContentValues values = getContentValues(info);
        getResolver().update(TABLE_URI, values, whereClause, new String[]{info.ask_id});
    }


    /**
     * 插入数据
     * @param bean
     */
    public static synchronized void insert(XiuxiuActionInfo info){
        ContentResolver resolver = getResolver();
        ContentValues values = getContentValues(info);
        resolver.insert(TABLE_URI, values);
    }

    private static ContentResolver getResolver() {
        return XiuxiuApplication.getInstance().getContentResolver();
    }

    private static ContentValues getContentValues(XiuxiuActionInfo info) {
        ContentValues cv = new ContentValues();
        cv.put(ASK_ID, info.ask_id);
        cv.put(STATUS, info.status);
        cv.put(UPDATE_TIME, System.currentTimeMillis());
        cv.put(XIUXIU_ID, XiuxiuLoginResult.getInstance().getXiuxiu_id());
        cv.put(LOOCK_COUNT,info.loock_count);
        return cv;
    }


    /**
     * 查询所有数据(本登录用户的)
     * @return cursor
     */
    public static Map<String,String> queryAll() {
        ContentResolver resolver = getResolver();
        Uri queryUri = TABLE_URI;
        queryUri = TABLE_URI.buildUpon().build();
        Cursor cursor = resolver.query(queryUri, null, "xiuxiu_id = ?", new String[]{XiuxiuLoginResult.getInstance().getXiuxiu_id()}, null);

        Map<String,String> map
                = new HashMap<String,String>();
        if(cursor!=null && cursor.getCount()!=0){
            cursor.moveToFirst();
            do{
                String message_id = cursor.getString(cursor.getColumnIndex(ASK_ID));
                String status = cursor.getString(cursor.getColumnIndex(STATUS));
                map.put(message_id,status);
            }while (cursor.moveToNext());
        }
        return map;
    }

    public static int queryLookCountById(String askId){
        ContentResolver resolver = getResolver();
        Uri queryUri = TABLE_URI;
        queryUri = TABLE_URI.buildUpon().build();
        Cursor cursor = resolver.query(queryUri, null, "xiuxiu_id = ? and ask_id = ? ", new String[]{XiuxiuLoginResult.getInstance().getXiuxiu_id(),askId},
                null);
        int  lookCount = 0;
        if(cursor!=null && cursor.getCount()!=0){
            cursor.moveToFirst();
            do{
                lookCount = cursor.getInt(cursor.getColumnIndex(LOOCK_COUNT));
                break;
            }while (cursor.moveToNext());
        }
        return lookCount;
    }

    /**
     * 查询所有数据(本登录用户的)
     * @return cursor
     */
    public static XiuxiuActionInfo queryById(String ask_id) {
        ContentResolver resolver = getResolver();
        Uri queryUri = TABLE_URI;
        queryUri = TABLE_URI.buildUpon().build();
        Cursor cursor = resolver.query(queryUri, null, "xiuxiu_id = ? and ask_id = ? ", new String[]{XiuxiuLoginResult.getInstance().getXiuxiu_id(),
                        ask_id},
                        null);
        XiuxiuActionInfo info = new XiuxiuActionInfo();
        if(cursor!=null && cursor.getCount()!=0){
            cursor.moveToFirst();
            do{
                info.loock_count = cursor.getInt(cursor.getColumnIndex(LOOCK_COUNT));
                info.ask_id = cursor.getString(cursor.getColumnIndex(ASK_ID));
                info.status = cursor.getString(cursor.getColumnIndex(STATUS));
                info.updateTime = cursor.getLong(cursor.getColumnIndex(UPDATE_TIME));
                break;
            }while (cursor.moveToNext());
        }
        return info;
    }

    public static synchronized void updateLookCountById(String askId){
        String whereClause =  ASK_ID + " = ? ";
        XiuxiuActionInfo info = queryById(askId);
        info.loock_count = info.loock_count +1;
        ContentValues values = getContentValues(info);
        int result = getResolver().update(TABLE_URI, values, whereClause, new String[]{info.ask_id});
        android.util.Log.d("12345","result = " + result);
    }


    /**
     * 删除按_SORT_ID 正序排序的   保留1000条
     */
    public static synchronized void deleteByExceedLimit(){

        try {
            ContentResolver resolver = getResolver();
            int result = resolver.delete(TABLE_URI, UPDATE_TIME + " not in (select " +
                            UPDATE_TIME + " from " + TABLE_NAME + " order by " + UPDATE_TIME + " desc limit " + Constant.EASE_USER_LIMIT_COUNT + ")",
                    null);
        } catch (Exception e) {
        }
    }

    /**
     * 获取记录总数
     * @return
     */
    public static int queryCount() {
        ContentResolver resolver = getResolver();
        Uri queryUri = TABLE_URI;
        queryUri = TABLE_URI.buildUpon().build();
        Cursor cursor = resolver.query(queryUri, null, null, null, null);
        int count  = 0;
        if(cursor!=null){
            try {
                count = cursor.getCount();
                cursor.close();
            } catch (Exception e) {
            } finally{
                if(cursor!=null){
                    cursor.close();
                }
            }
        }
        return count;
    }

}
