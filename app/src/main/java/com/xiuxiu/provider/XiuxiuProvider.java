package com.xiuxiu.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.xiuxiu.db.ChatNameAndAvatarTable;
import com.xiuxiu.db.XiuxiuDatabaseHelper;
import com.xiuxiu.db.XiuxiuUserInfoTable;
import com.xiuxiu.easeim.xiuxiumsg.XiuxiuActionMsgManager;
import com.xiuxiu.easeim.xiuxiumsg.XiuxiuActionMsgTable;
import com.xiuxiu.utils.CollectionUtil;

import java.util.List;

/**
 * Created by huzhi on 16-5-18.
 */
public class XiuxiuProvider extends ContentProvider {

    public static final String AUTHORITY = "com.xiuxiu";
    public static final String BASE_URI_STR = "content://" + AUTHORITY;

    private SQLiteOpenHelper mOpenHelper;

    private static final UriMatcher sURLMatcher = new UriMatcher(
            UriMatcher.NO_MATCH);

    private static final int ALL = 0;
    private static final int CHAT_NICKNAME_AVATAR = 1000;
    private static final int XIUXIU_USER_INFO = 1001;
    private static final int XIUXIU_ACTION_MESSAGE = 1002;

    static {
        sURLMatcher.addURI(AUTHORITY, null, ALL);
        sURLMatcher.addURI(AUTHORITY, ChatNameAndAvatarTable.TABLE_NAME, CHAT_NICKNAME_AVATAR);
        sURLMatcher.addURI(AUTHORITY, XiuxiuUserInfoTable.TABLE_NAME, XIUXIU_USER_INFO);
        sURLMatcher.addURI(AUTHORITY, XiuxiuActionMsgTable.TABLE_NAME, XIUXIU_ACTION_MESSAGE);
    }

    public static final String PARAM_RAW_QUERY_STRING = "rawQuery";
    public static final String PARAM_GROUP_BY = "groupBy";
    public static final String PARAM_LIMIT = "limit";

    @Override
    public boolean onCreate() {
        mOpenHelper = XiuxiuDatabaseHelper.getInstance(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int match = sURLMatcher.match(uri);
        if (match == -1) {
            throw new IllegalArgumentException("Unknown URL");
        }

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String limit = uri.getQueryParameter(PARAM_LIMIT);
        String groupBy = uri.getQueryParameter(PARAM_GROUP_BY);


        switch (match) {
            case CHAT_NICKNAME_AVATAR:
            case XIUXIU_USER_INFO:
            default:
                break;
        }

        String tableName = getTableName(uri);
        qb.setTables(tableName);
        Cursor cursor = qb.query(db, projection, selection, selectionArgs,
                groupBy, null, sortOrder, limit);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        int match = sURLMatcher.match(uri);
        if (match == -1) {
            throw new IllegalArgumentException("Unknown URL");
        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long ret = 0;
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        String tableName = getTableName(uri);

        ret = db.insert(tableName, null, values);
        if (ret > 0) {
            Uri tableUri = getTableUri(tableName);
            getContext().getContentResolver().notifyChange(tableUri, null);
            return ContentUris.withAppendedId(uri, ret);
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int match = sURLMatcher.match(uri);
        if (match == -1) {
            throw new IllegalArgumentException("Unknown URL");
        }
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        String tableName = getTableName(uri);

        int ret = 0;
        ret = db.delete(tableName, selection, selectionArgs);
        if (ret > 0) {
            Uri tableUri = getTableUri(tableName);
            getContext().getContentResolver().notifyChange(tableUri, null);
        }
        return ret;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int match = sURLMatcher.match(uri);
        if (match == -1) {
            throw new IllegalArgumentException("Unknown URL");
        }
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        String tableName = getTableName(uri);

        int ret = 0;
        ret = db.update(tableName, values, selection, selectionArgs);
        if (ret > 0) {
            Uri tableUri = getTableUri(tableName);
            getContext().getContentResolver().notifyChange(tableUri, null);
        }
        return ret;
    }

    public static String getTableName(Uri uri) {
        List<String> segments = uri.getPathSegments();
        if(CollectionUtil.isEmpty(segments)) {
            return "";
        }
        String tableName = segments.get(0);
        return tableName;
    }

    public static Uri getTableUri(String tableName) {
        return Uri.parse(BASE_URI_STR + "/" + tableName);
    }
}
