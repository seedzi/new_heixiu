package com.xiuxiu.db;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

import com.xiuxiu.XiuxiuApplication;
import com.xiuxiu.api.XiuxiuLoginResult;
import com.xiuxiu.bean.XiuxiuBroadcastMsg;
import com.xiuxiu.provider.XiuxiuProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huzhi on 16-8-10.
 */
public class XiuxiuBroadcastMsgTable implements TableHelper{

    private static final String TAG = "XiuxiuBroadcastMsgTable";

    public static final String TABLE_NAME = "xiuxiu_broadcast_msg";

    private final static String TABLE_URI_STR = XiuxiuProvider.BASE_URI_STR + "/" + TABLE_NAME;

    private final static Uri TABLE_URI = Uri.parse(TABLE_URI_STR);

    public static final String _ID = "_id";

    public static final String _XIUXIU_ID = "xiuxiu_id";

    public static final String _FROM_XIUXIU_ID = "from_xiuxiu_id";

    public static final String _UPDATE_TIME = "update_time";

    public static final String _CONTENT = "content";

    private final String CREATE_TABLE =  String.format("create table %s(%s integer primary key autoincrement, " +
                    "%s text, %s text, %s long, %s text);",
            TABLE_NAME,
            _ID,
            _XIUXIU_ID,
            _FROM_XIUXIU_ID,
            _UPDATE_TIME,
            _CONTENT);

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

    public static synchronized void insert(XiuxiuBroadcastMsg msg){
        ContentResolver resolver = getResolver();
        ContentValues values = getContentValues(msg);
        resolver.insert(TABLE_URI, values);
        android.util.Log.d(TAG,"insert() msg = " + msg);
    }

    private static ContentResolver getResolver() {
        return XiuxiuApplication.getInstance().getContentResolver();
    }

    private static ContentValues getContentValues(XiuxiuBroadcastMsg msg) {
        ContentValues cv = new ContentValues();
        cv.put(_XIUXIU_ID, msg.toXiuxiuId);
        cv.put(_FROM_XIUXIU_ID, msg.fromXiuxiuId);
        cv.put(_UPDATE_TIME, msg.updateTime);
        cv.put(_CONTENT, msg.content);
        return cv;
    }

    /**
     * 获取指定条数记录, 按_TOP_ID逆序排列
     * @return cursor
     * @see AnecdotInfo
     */
    public static Cursor queryCursor(int size) {
        Cursor cursor = null;
        try {
            ContentResolver resolver = getResolver();
            Uri queryUri = TABLE_URI;
            String orderBy = String.format(" %s desc", _UPDATE_TIME);

            String whereClause =  _XIUXIU_ID + " = ? ";
            String[] whereArgs = new String[]{XiuxiuLoginResult.getInstance().getXiuxiu_id()};

            cursor = resolver.query(queryUri, null, whereClause, whereArgs, orderBy + " limit " + size);
            return cursor;
        }catch (Exception e){
            if(cursor!=null){
                cursor.close();
            }
        }
        return null;
    }
}