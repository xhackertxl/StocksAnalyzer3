package com.alex.develop.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.alex.develop.entity.Stock;

/**
 * Created by alex on 15-6-6.
 * Create SQLite database
 */
public class SQLiteHelper extends SQLiteOpenHelper {

    public static void init(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        if(null == sqLiteHelper) {
            sqLiteHelper = new SQLiteHelper(context, name, factory, version);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Stock.Table.SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        
    }

    public static SQLiteHelper getInstance() {
        return sqLiteHelper;
    }

    private SQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    private static SQLiteHelper sqLiteHelper;
}
