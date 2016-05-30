package com.xiuxiu.db;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.xiuxiu.XiuxiuApplication;
import com.xiuxiu.bean.ChatNickNameAndAvatarBean;
import com.xiuxiu.provider.XiuxiuProvider;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by huzhi on 16-5-18.
 */
public class ChatNameAndAvatarTable implements TableHelper{

    public static final String TABLE_NAME = "chat_nickname_avatar";

    private final static String TABLE_URI_STR = XiuxiuProvider.BASE_URI_STR + "/" + TABLE_NAME;

    private final static Uri TABLE_URI = Uri.parse(TABLE_URI_STR);

    private static final String _NICK_NAME = "nick_name";

    private static final String _XIU_XIU_ID = "xiu_xiu_id";

    private static final String _AVATAR = "avatar";

    private final String CREATE_TABLE =  String.format("create table %s(%s text primary key, %s text, %s text);",
            TABLE_NAME,
            _XIU_XIU_ID,
            _NICK_NAME,
            _AVATAR);

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
     * 插入数据
     * @param bean
     */
    public static synchronized void insert(ChatNickNameAndAvatarBean info){
        ContentResolver resolver = getResolver();
        ContentValues values = getContentValues(info);
        resolver.insert(TABLE_URI, values);
    }

    /**
     * 更新数据
     * @param bean
     */
    public static synchronized void update(ChatNickNameAndAvatarBean info){
        String whereClause =  _XIU_XIU_ID + " = ? ";
        ContentValues values = getContentValues(info);
        getResolver().update(TABLE_URI, values, whereClause, new String[]{info.getXiuxiu_id()});
    }

    /**
     * 是否存在
     */
    public static boolean isExist(String xiuXiuId){
        int count = 0;
        String whereClause =  _XIU_XIU_ID + " = ? ";
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
     * 查询所有数据
     * @return cursor
     */
    public static Map<String,ChatNickNameAndAvatarBean> queryAll() {
        ContentResolver resolver = getResolver();
        Uri queryUri = TABLE_URI;
        queryUri = TABLE_URI.buildUpon().build();
        Cursor cursor = resolver.query(queryUri, null, null, null, null);

        Map<String,ChatNickNameAndAvatarBean> map
                = new HashMap<String, ChatNickNameAndAvatarBean>();
        if(cursor!=null && cursor.getCount()!=0){
            cursor.moveToFirst();
            do{
                ChatNickNameAndAvatarBean info = new ChatNickNameAndAvatarBean();
                info.setXiuxiu_id(cursor.getString(cursor.getColumnIndex(_XIU_XIU_ID)));
                info.setNickName(cursor.getString(cursor.getColumnIndex(_NICK_NAME)));
                info.setAvatar(cursor.getString(cursor.getColumnIndex(_AVATAR)));
                map.put(info.getXiuxiu_id(),info);
            }while (cursor.moveToNext());
        }
        return map;
    }


    private static ContentResolver getResolver() {
        return XiuxiuApplication.getInstance().getContentResolver();
    }

    private static ContentValues getContentValues(ChatNickNameAndAvatarBean info) {
        ContentValues cv = new ContentValues();
        cv.put(_XIU_XIU_ID, info.getXiuxiu_id());
        cv.put(_NICK_NAME, info.getNickName());
        cv.put(_AVATAR, info.getAvatar());
        return cv;
    }
}
