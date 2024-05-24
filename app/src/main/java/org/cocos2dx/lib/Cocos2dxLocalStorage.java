package org.cocos2dx.lib;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.google.android.gms.measurement.api.AppMeasurementSdk;

/* loaded from: classes2.dex */
public class Cocos2dxLocalStorage {
    private static final int DATABASE_VERSION = 1;
    private static final String TAG = "Cocos2dxLocalStorage";
    private static String DATABASE_NAME = "jsb.sqlite";
    private static String TABLE_NAME = "data";
    private static DBOpenHelper mDatabaseOpenHelper = null;
    private static SQLiteDatabase mDatabase = null;

    public static boolean init(String dbName, String tableName) {
        if (Cocos2dxActivity.getContext() != null) {
            DATABASE_NAME = dbName;
            TABLE_NAME = tableName;
            DBOpenHelper dBOpenHelper = new DBOpenHelper(Cocos2dxActivity.getContext());
            mDatabaseOpenHelper = dBOpenHelper;
            mDatabase = dBOpenHelper.getWritableDatabase();
            return true;
        }
        return false;
    }

    public static void destroy() {
        SQLiteDatabase sQLiteDatabase = mDatabase;
        if (sQLiteDatabase != null) {
            sQLiteDatabase.close();
        }
    }

    public static void setItem(String key, String value) {
        try {
            String sql = "replace into " + TABLE_NAME + "(key,value)values(?,?)";
            mDatabase.execSQL(sql, new Object[]{key, value});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getItem(String key) {
        String ret = null;
        try {
            String sql = "select value from " + TABLE_NAME + " where key=?";
            Cursor c = mDatabase.rawQuery(sql, new String[]{key});
            while (true) {
                if (!c.moveToNext()) {
                    break;
                }
                if (ret != null) {
                    Log.e(TAG, "The key contains more than one value.");
                    break;
                }
                ret = c.getString(c.getColumnIndex(AppMeasurementSdk.ConditionalUserProperty.VALUE));
            }
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static void removeItem(String key) {
        try {
            String sql = "delete from " + TABLE_NAME + " where key=?";
            mDatabase.execSQL(sql, new Object[]{key});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void clear() {
        try {
            String sql = "delete from " + TABLE_NAME;
            mDatabase.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* loaded from: classes2.dex */
    private static class DBOpenHelper extends SQLiteOpenHelper {
        DBOpenHelper(Context context) {
            super(context, Cocos2dxLocalStorage.DATABASE_NAME, (SQLiteDatabase.CursorFactory) null, 1);
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + Cocos2dxLocalStorage.TABLE_NAME + "(key TEXT PRIMARY KEY,value TEXT);");
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(Cocos2dxLocalStorage.TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        }
    }
}
