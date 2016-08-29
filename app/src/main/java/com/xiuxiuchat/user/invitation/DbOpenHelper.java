package com.xiuxiuchat.user.invitation;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.xiuxiuchat.easeim.ImHelper;

/**
 * 加好友消息数据库
 * Created by huzhi on 16-6-4.
 */
public class DbOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static DbOpenHelper instance;


    private static final String USERNAME_TABLE_CREATE = "CREATE TABLE "
            + UserDao.TABLE_NAME + " ("
            + UserDao.COLUMN_NAME_NICK + " TEXT, "
            + UserDao.COLUMN_NAME_AVATAR + " TEXT, "
            + UserDao.COLUMN_NAME_AGE + " INTEGER, "
            + UserDao.COLUMN_NAME_CHARM + " TEXT, "
            + UserDao.COLUMN_NAME_FORTUNE + " TEXT, "
            + UserDao.COLUMN_NAME_ONLINE_TIME + " LONG, "
            + UserDao.COLUMN_NAME_VOICE + " TEXT, "
            + UserDao.COLUMN_NAME_XIUXIU_ID + " TEXT, "
            + UserDao.COLUMN_NAME_SEX + " TEXT, "
            + UserDao.COLUMN_NAME_ACTIVE_TIME + " LONG, "
            + UserDao.COLUMN_NAME_ID + " TEXT PRIMARY KEY);";

    private static final String INIVTE_MESSAGE_TABLE_CREATE = "CREATE TABLE "
            + InviteMessgeDao.TABLE_NAME + " ("
            + InviteMessgeDao.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + InviteMessgeDao.COLUMN_NAME_FROM + " TEXT, "
            + InviteMessgeDao.COLUMN_NAME_GROUP_ID + " TEXT, "
            + InviteMessgeDao.COLUMN_NAME_GROUP_Name + " TEXT, "
            + InviteMessgeDao.COLUMN_NAME_REASON + " TEXT, "
            + InviteMessgeDao.COLUMN_NAME_STATUS + " INTEGER, "
            + InviteMessgeDao.COLUMN_NAME_ISINVITEFROMME + " INTEGER, "
            + InviteMessgeDao.COLUMN_NAME_UNREAD_MSG_COUNT + " INTEGER, "
            + InviteMessgeDao.COLUMN_NAME_TIME + " TEXT, "
            + InviteMessgeDao.COLUMN_NAME_GROUPINVITER + " TEXT); ";

    private DbOpenHelper(Context context) {
        super(context, getUserDatabaseName(), null, DATABASE_VERSION);
    }

    public static DbOpenHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DbOpenHelper(context.getApplicationContext());
        }
        return instance;
    }

    private static String getUserDatabaseName() {
        return  ImHelper.getInstance().getCurrentUsernName() + "_demo.db";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(USERNAME_TABLE_CREATE);
        db.execSQL(INIVTE_MESSAGE_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void closeDB() {
        if (instance != null) {
            try {
                SQLiteDatabase db = instance.getWritableDatabase();
                db.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            instance = null;
        }
    }
}
