package usi.Nokia3210.local.database.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;

import usi.Nokia3210.R;
import usi.Nokia3210.local.database.db.LocalSQLiteDBHelper;

/**
 * Created by usi on 03/01/17.
 */

public class SQLiteController implements LocalStorageController {
    private Context context;
    private String dbName;
    private SQLiteDatabase localDb;
    private LocalSQLiteDBHelper dbHelper;
    private static SQLiteController object;


    private SQLiteController(Context context) {
        this.context = context;
        initDatabse();

        this.dbName = context.getString(R.string.dbName);
    }

    public static SQLiteController getInstance(Context context) {
        if(object == null) {
            object = new SQLiteController(context);
//            object.initDatabse();
        }
        return object;
    }

    public void initDatabse() {
        dbHelper = new LocalSQLiteDBHelper(context);
        localDb = dbHelper.getReadableDatabase();
    }

    @Override
    public Cursor rawQuery(String query, String[] args) {
        Cursor cursor = localDb.rawQuery(query, args);
        return cursor;
    }

    @Override
    public long insertRecord(String tableName, ContentValues record) {
        return localDb.insert(tableName, null, record);
    }

    @Override
    public long getDbSize() {
        File f = context.getDatabasePath(dbName);
        return f.length();
    }

    @Override
    public void delete(String tableName, String clause) {
        localDb.delete(tableName, clause, null);
    }

    @Override
    public void update(String tableName, ContentValues values, String clause) {
        localDb.update(tableName, values, clause, null);
    }

    @Override
    public void truncate(String tableName) {
        localDb.execSQL("delete from "+ tableName);
    }
}