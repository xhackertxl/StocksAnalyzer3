package com.alex.develop.stockanalyzer;

import android.app.Application;
import android.content.Context;
import android.view.View;

import com.alex.develop.entity.Stock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created by alex on 15-6-11.
 * 存储全局数据
 */
public class Analyzer extends Application {
    //广播常量

    //数据更新
    public static String STOCK_UPDATE = "com.alex.develop.fragment.communication.RECEIVER_STOCK_UPDATE";

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }

    public static View getMainActivityLoadView() {
        return loadView1;
    }

    public static void setMainActivityLoadView(View loadView) {
        Analyzer.loadView1 = loadView;
    }

    public static View getCandleActivityLoadView() {
        return loadView2;
    }

    public static void setCandleActivityLoadView(View loadView) {
        Analyzer.loadView2 = loadView;
    }

    public static List<Stock> getStockList() {
        return stockList;
    }

    public static void setStockList(List<Stock> stockList) {
        Analyzer.stockList = stockList;
    }

    /**
     * 获取被收藏的股票列表(自选股)
     *
     * @param reSearch 是否重新检索自选股票（如果添加了自选股，则需要重新检索，否则，不需要）
     * @return 自选股列表
     */
    public static List<Stock> getCollectStockList(boolean reSearch) {

        if (reSearch) {
            if (null == stockCollect) {
                stockCollect = new ArrayList<>();
            } else {
                stockCollect.clear();
            }

            for (Stock stock : stockList) {
                if (stock.isCollected()) {
                    stockCollect.add(stock);
                }
            }

            // 按照收藏的先后顺序排列
            Collections.sort(stockCollect, new Comparator<Stock>() {
                @Override
                public int compare(Stock lhs, Stock rhs) {

                    int result;
                    long flag = lhs.getCollectStamp() - rhs.getCollectStamp();

                    if (0 > flag) {
                        result = -1;
                    } else if (0 < flag) {
                        result = 1;
                    } else {
                        result = 0;
                    }

                    return -result;
                }
            });
        }

        return stockCollect;
    }

    /**
     * 获取被搜索的股票列表
     *
     * @return
     */
    public static List<Stock> getSearchStockList() {

        List<Stock> stockSearched = new ArrayList<>();

        for (Stock stock : stockList) {
            if (0 < stock.getSearch()) {
                stockSearched.add(stock);
            }
        }

        return stockSearched;
    }

    public static Map<String, Stock> getStockListMap() {
        return stockListMap;
    }

    public static void setStockListMap(Map<String, Stock> stockListMap) {
        Analyzer.stockListMap = stockListMap;
    }

    public static ArrayList<Stock[]> getArraystockList() {
        return arraystockList;
    }

    public static void setArraystockList(ArrayList<Stock[]> arraystockList) {
        Analyzer.arraystockList = arraystockList;
    }

    private static Context context;
    private static List<Stock> stockList;
    private static Map<String, Stock> stockListMap;
    private static ArrayList<Stock[]> arraystockList = new ArrayList<>();

    private static List<Stock> stockCollect;
    private static View loadView1;
    private static View loadView2;


}
