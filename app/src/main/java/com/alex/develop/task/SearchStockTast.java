package com.alex.develop.task;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.alex.develop.entity.Stock;
import com.alex.develop.util.SQLiteHelper;

/**
 * Created by alex on 15-8-21.
 * 搜索股票时候，添加搜索记录
 */
public class SearchStockTast extends AsyncTask<Stock,Void, Boolean> {

    // TODO not finished
    public SearchStockTast(int search) {
        this.search = search;
    }


    @Override
    protected Boolean doInBackground(Stock... params) {
        SQLiteHelper dbHelper = SQLiteHelper.getInstance();
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Stock stock = params[0];

        ContentValues values = new ContentValues();
        values.put(Stock.Table.Column.SEARCH, search);

        String where = Stock.Table.Column.CODE + " = ?";
        String[] whereArgs = {params[0].getCode()};

        return 0 < db.update(Stock.Table.NAME, values, where, whereArgs);
    }

    private int search;
}
