package com.alex.develop.task;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.alex.develop.entity.Stock;
import com.alex.develop.stockanalyzer.Analyzer;
import com.alex.develop.stockanalyzer.R;
import com.alex.develop.util.SQLiteHelper;

/**
 * Created by alex on 15-7-15.
 * 收藏或者取消收藏Stock，支持批量操作
 */
public class CollectStockTask extends AsyncTask<Stock, Void, Boolean> {

    /**
     * 构造方法
     * @param collect 是否收藏该Stock,1表示收藏；0表示取消收藏
     */
    public CollectStockTask(int collect) {
        this.collect = collect;
    }

    @Override
    protected Boolean doInBackground(Stock... params) {

        SQLiteHelper dbHelper = SQLiteHelper.getInstance();
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        boolean flag = true;
        for (Stock s : params) {
            s.setCollect(collect);

            ContentValues values = new ContentValues();
            values.put(Stock.Table.Column.COLLECT, collect);
            values.put(Stock.Table.Column.COLLECT_STAMP, s.getCollectStamp());

            String where = Stock.Table.Column.CODE + " = ?";
            String[] whereArgs = {s.getCode()};

            if(1 != db.update(Stock.Table.NAME, values, where, whereArgs)) {
                flag = false;
            }
        }

        if(1 == params.length) {
            tips = params[0].getName() + " ";
        } else {
            tips = Analyzer.getContext().getString(R.string.selected_stock);
        }

        return flag;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);

        String textSuccess = Analyzer.getContext().getString(R.string.collect_stock_success);
        String textFailure = Analyzer.getContext().getString(R.string.collect_stock_failure);

        if(0 == collect) {
            textSuccess = Analyzer.getContext().getString(R.string.remove_stock_success);
            textFailure = Analyzer.getContext().getString(R.string.remove_stock_failure);
        }

        if(aBoolean) {
            tips = String.format(textSuccess, tips);
        } else {
            tips = String.format(textFailure, tips);
        }

        Toast.makeText(Analyzer.getContext(), tips, Toast.LENGTH_SHORT).show();
    }

    private int collect;
    private String tips;// 加入自选的股票名称
}
