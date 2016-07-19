package com.xiuxiu.db;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.xiuxiu.XiuxiuApplication;
import com.xiuxiu.api.XiuxiuUserInfoResult;
import com.xiuxiu.base.Constant;
import com.xiuxiu.provider.XiuxiuProvider;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by huzhi on 16-6-20.
 */
public class XiuxiuUserInfoTable implements TableHelper{

    private static final String TAG = "XiuxiuUserInfoTable";

    public static final String TABLE_NAME = "xiuxiu_user_info";

    private final static String TABLE_URI_STR = XiuxiuProvider.BASE_URI_STR + "/" + TABLE_NAME;

    private final static Uri TABLE_URI = Uri.parse(TABLE_URI_STR);

    public static final String XIUXIU_ID = "xiuxiu_id";

    public static final String XIUXIU_NAME = "xiuxiu_name";

    public static final String SIGN = "sign";

    public static final String AGE = "age";

    public static final String CITY = "city";

    public static final String CHARM = "charm";

    public static final String BIRTHDAY = "birthday";

    public static final String PIC = "pic";

    public static final String SEX = "sex";

    public static final String VOICE = "voice";

    public static final String ACTIVE_TIME = "active_time";

    public static final String UPDATE_TIME = "update_time";

    private final String CREATE_TABLE =  String.format("create table %s(%s text primary key, " +
                    "%s text, %s text, %s text, %s text, %s text, %s integer, %s integer, %s text, %s text, %s long, %s long);",
            TABLE_NAME,
            XIUXIU_ID,
            XIUXIU_NAME,
            SIGN,
            CITY,
            PIC,
            VOICE,
            AGE,
            CHARM,
            SEX,
            BIRTHDAY,
            ACTIVE_TIME,
            UPDATE_TIME);

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
     * 删除按_SORT_ID 正序排序的   保留1000条
     */
    public static synchronized void deleteByExceedLimit(){
        try {
            ContentResolver resolver = getResolver();
            int result = resolver.delete(TABLE_URI, UPDATE_TIME + " not in (select " +
                            UPDATE_TIME + " from " + TABLE_NAME + " order by " + UPDATE_TIME + " desc limit " + Constant.EASE_USER_LIMIT_COUNT + ")",
                    null);
            android.util.Log.d(TAG,"result = " + result);
        } catch (Exception e) {
            android.util.Log.d(TAG,"e = " + e.getMessage());
        }
    }

    /**
     * 插入数据
     * @param bean
     */
    public static synchronized void insert(XiuxiuUserInfoResult info){
        ContentResolver resolver = getResolver();
        ContentValues values = getContentValues(info);
        resolver.insert(TABLE_URI, values);
    }

    /**
     * 更新数据
     * @param bean
     */
    public static synchronized void update(XiuxiuUserInfoResult info){
        String whereClause =  XIUXIU_ID + " = ? ";
        ContentValues values = getContentValues(info);
        getResolver().update(TABLE_URI, values, whereClause, new String[]{info.getXiuxiu_id()});
    }

    /**
     * 是否存在
     */
    public static boolean isExist(String xiuXiuId){
        int count = 0;
        String whereClause =  XIUXIU_ID + " = ? ";
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
        android.util.Log.d(TAG,"count = " + count);
        return count;
    }

    /**
     * 查询所有数据
     * @return cursor
     */
    public static Map<String,XiuxiuUserInfoResult> queryAll() {
        ContentResolver resolver = getResolver();
        Uri queryUri = TABLE_URI;
        queryUri = TABLE_URI.buildUpon().build();
        Cursor cursor = resolver.query(queryUri, null, null, null, null);

        Map<String,XiuxiuUserInfoResult> map
                = new HashMap<String, XiuxiuUserInfoResult>();
        if(cursor!=null && cursor.getCount()!=0){
            cursor.moveToFirst();
            do{
                XiuxiuUserInfoResult info = new XiuxiuUserInfoResult();
                info.setXiuxiu_id(cursor.getString(cursor.getColumnIndex(XIUXIU_ID)));
                info.setXiuxiu_name(cursor.getString(cursor.getColumnIndex(XIUXIU_NAME)));
                info.setSign(cursor.getString(cursor.getColumnIndex(SIGN)));
                info.setCity(cursor.getString(cursor.getColumnIndex(CITY)));
                info.setPic(cursor.getString(cursor.getColumnIndex(PIC)));
                info.setVoice(cursor.getString(cursor.getColumnIndex(VOICE)));
                info.setAge(String.valueOf(cursor.getInt(cursor.getColumnIndex(AGE))));
                info.setCharm(cursor.getInt(cursor.getColumnIndex(CHARM)));
                info.setSex(cursor.getString(cursor.getColumnIndex(SEX)));
                info.setBirthday(cursor.getString(cursor.getColumnIndex(BIRTHDAY)));
                info.setActive_time(cursor.getLong(cursor.getColumnIndex(ACTIVE_TIME)));
                map.put(info.getXiuxiu_id(),info);
            }while (cursor.moveToNext());
        }
        return map;
    }


    private static ContentResolver getResolver() {
        return XiuxiuApplication.getInstance().getContentResolver();
    }

    private static ContentValues getContentValues(XiuxiuUserInfoResult info) {
        ContentValues cv = new ContentValues();
        cv.put(XIUXIU_ID, info.getXiuxiu_id());
        cv.put(XIUXIU_NAME, info.getXiuxiu_name());
        cv.put(SIGN, info.getSign());
        cv.put(CITY, info.getCity());
        cv.put(PIC, info.getPic());
        cv.put(VOICE, info.getVoice());
        cv.put(AGE,info.getAge());
        cv.put(CHARM,info.getCharm());
        cv.put(SEX,info.getSex());
        cv.put(BIRTHDAY,info.getBirthday());
        cv.put(ACTIVE_TIME,info.getActive_time());
        cv.put(UPDATE_TIME,System.currentTimeMillis());
        return cv;
    }
}
