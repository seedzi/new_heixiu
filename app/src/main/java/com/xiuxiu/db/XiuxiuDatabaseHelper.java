package com.xiuxiu.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import java.util.Collection;
import java.util.LinkedList;

/**
 *
 * 数据库帮助类
 *
 * @author 高航
 * @date 2013-1-21
 *
 */
public class XiuxiuDatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DataBaseManager";

    private static final int DATABASE_VERSION = 1;

    /**
     * 数据库名称，用于创建数据库文件
     */
    private static final String DATABASE_NAME = "xiuxiu.db";

    private static XiuxiuDatabaseHelper mInstance;

    private final Context mContext;

    private final Collection<TableHelper> mTableHelpers;

    public static synchronized XiuxiuDatabaseHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new XiuxiuDatabaseHelper(context);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mInstance.setWriteAheadLoggingEnabled(true);
            }
        }
        return mInstance;
    }

    private XiuxiuDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
        mTableHelpers = new LinkedList<TableHelper>();
        init();
    }

    private void init() {
        registTableHelper(new XiuxiuUserInfoTable());
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if (!create(db)) {
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        upgrade(db, oldVersion, newVersion);
    }

    /**
     * 注册表辅助器，未做去重处理，且重复对象会对逻辑产生影响
     *
     * @param th
     *            待注册helper
     */
    private void registTableHelper(TableHelper th) {
        mTableHelpers.add(th);
    }

    private void upgrade(SQLiteDatabase db, int oldV, int newV) {
        for (TableHelper th : mTableHelpers) {
            th.upgrade(db, oldV, newV);
        }
    }

    private boolean create(SQLiteDatabase db) {
        for (TableHelper th : mTableHelpers) {
            if (!th.create(db)) {
                return false;
            }
        }
        return true;
    }
}