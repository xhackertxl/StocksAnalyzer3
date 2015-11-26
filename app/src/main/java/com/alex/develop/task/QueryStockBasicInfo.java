package com.alex.develop.task;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseArray;

import com.alex.develop.entity.ApiStore;
import com.alex.develop.entity.Stock;
import com.alex.develop.stockanalyzer.Analyzer;
import com.alex.develop.util.NetworkHelper;
import com.alex.develop.util.SQLiteHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by alex on 15-9-8.
 * 查询股票基本信息
 */
public class QueryStockBasicInfo extends AsyncTask<Stock, Void, Void> {

    @Override
    protected Void doInBackground(Stock... params) {

        HashMap<String, String> header = new HashMap<>();
        header.put(ApiStore.BAIDU_APISTORE_API_KEY, ApiStore.BAIDU_APISTORE_API_VALUE);

        /**
         * No Data Returned Stock
         */
        excp = new HashSet<>();
        excp.add("000033");
        excp.add("000520");

        queryInformationForAllStocks(header);

//        queryInformationForStocks(header, params);

        return null;
    }

    private void queryInformationForStocks(HashMap<String, String> header, Stock... stocks) {
        List<Stock> data = Arrays.asList(stocks);
        String url = ApiStore.getStockInfoUrl(data);
        String content = NetworkHelper.getWebContent(url, header, ApiStore.JDWX_CHARSET);
        fromJSON(content, data);
    }

    /**
     *
     * @param header
     */
    private void queryInformationForAllStocks(HashMap<String, String> header) {

        final int size = 100;

        List<Stock> data = Analyzer.getStockList();

        List<Stock> stocks = new ArrayList<>();

        int count = 0;
        int total = 0;
        boolean fetchData = false;
        boolean first = true;
        for (Stock stock : data) {

            ++total;

            if(first && stock.getCode().startsWith("0")) {
                fetchData = true;
                first = false;
            } else {
                fetchData = false;
            }

            if (fetchData || size == count || total == data.size()) {

                // 最后一只也要加入查询得队列
                if(total == data.size()) {
                    if (!excp.contains(stock.getCode())) {
                        stocks.add(stock);
                    }
                }

                String url = ApiStore.getStockInfoUrl(stocks);
                String content = NetworkHelper.getWebContent(url, header, ApiStore.JDWX_CHARSET);
                fromJSON(content, stocks);

                stocks.clear();
                count = 0;
            } else {
                ++count;
            }

            if (!excp.contains(stock.getCode())) {
                stocks.add(stock);
            }
        }

        Log.d("Print-Total", "查到的数据 " + statistics);
    }

    private void fromJSON(String content, List<Stock> stocks) {
        try {
            JSONObject jsonObject = new JSONObject(content);

            if (jsonObject.has(ApiStore.JDWX_JSON_RESULT)) {
                JSONObject result = jsonObject.optJSONObject(ApiStore.JDWX_JSON_RESULT);
                int retCode = result.optInt(ApiStore.JDWX_JSON_RETCODE);

                if (ApiStore.JDWX_JSON_STATUS_OK == retCode) {
                    JSONArray data = result.optJSONArray(ApiStore.JDWX_JSON_DATA);

                    for (int i = 0, j = 0; i < data.length(); ++i, ++j) {
                        JSONObject item = data.optJSONObject(i);
                        Stock stock = stocks.get(j);
                        write2SQLite(stock, item);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean write2SQLite(Stock stock, JSONObject data) {

        SQLiteHelper dbHelper = SQLiteHelper.getInstance();
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String ticker = data.optString(ApiStore.JDWX_JSON_TICKER);

        String fullName = data.optString(ApiStore.JDWX_JSON_FULLNAME);
        String officeAddr = data.optString(ApiStore.JDWX_JSON_OFFICE, ApiStore.JDWX_JSON_DEFAULT);
        String listStatus = data.optString(ApiStore.JDWX_JSON_LIST_STATUS);
        String listDate = data.optString(ApiStore.JDWX_JSON_LIST_DATE).replace(ApiStore.SBL_MINUS, "");

        String totalShare = data.optString(ApiStore.JDWX_JSON_TOTAL_SHARE);
        String nonrestFloatA = data.optString(ApiStore.JDWX_JSON_NONREST_FLOAT_A);
        String primeOperting = data.optString(ApiStore.JDWX_JSON_PRIME_OPERATING, ApiStore.JDWX_JSON_DEFAULT);

        ContentValues values = new ContentValues();
        values.put(Stock.Table.Column.FULL_NAME, fullName);
        values.put(Stock.Table.Column.OFFICE_ADDR, officeAddr);
        values.put(Stock.Table.Column.LIST_STATUS, listStatus);
        values.put(Stock.Table.Column.LIST_DATE, listDate);

        values.put(Stock.Table.Column.TOTAL_SHARE, totalShare);
        values.put(Stock.Table.Column.NONREST_FLOAT_A, nonrestFloatA);
        values.put(Stock.Table.Column.PRIME_OPERATING, primeOperting);

        String where = Stock.Table.Column.CODE + " = ?";
        String[] whereArgs = {stock.getCode()};

        if (ticker.equals(stock.getCode())) {
            Log.d("Print-write2SQLite", stock.getName() + ", " + listDate + "<======>" + stock.getCode() + ", " + ticker);
        } else {
            Log.e("Print-write2SQLite", stock.getName() + ", " + listDate + "<======>" + stock.getCode() + ", " + ticker);
        }

        boolean flag = 1 == db.update(Stock.Table.NAME, values, where, whereArgs);

        if (flag) {
            ++statistics;
        }

        return flag;
    }

    private HashSet<String> excp;
    private int statistics;
}
