package com.xiuxiu.db;

import android.database.sqlite.SQLiteDatabase;

/**
 * 数据库表操作辅助类 添加数据库时除了继承该类，还得再SogouDatabaseHelper.init中将对应的表注册进去
 */
public interface TableHelper {

    /**
     * 表创建时调用，正常情况下都返回true。只有异常分支才让返回false
     */
    boolean create(SQLiteDatabase db);

    /**
     * 表升级时调用，正常情况下都返回true。只有异常分支才让返回false
     */
    void upgrade(SQLiteDatabase db, int oldVersion, int newVersion);

}
