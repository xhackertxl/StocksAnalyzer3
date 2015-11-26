package com.alex.develop.util;

import com.alex.develop.entity.Stock;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by alex on 15-6-13.
 * 为List排序
 */
public class ListSorter {

    public static void sort(List<Stock> list, final String orderBy) {
        sort(list, Order.Sequence, orderBy);
    }

    public static void sort(List<Stock> list, final Order order, final String orderBy) {
        Collections.sort(list, new Comparator<Stock>() {
            @Override
            public int compare(Stock lt, Stock rt) {

                int result = 0;
                if("price".equals(orderBy)) {// 按最新价格排序
                    result = Float.compare(lt.getToday().getClose(), rt.getToday().getClose());
                } else if("increase".equals(orderBy)) {
                    result = Float.compare(lt.getToday().getChange(), rt.getToday().getChange());
                } else if(Stock.Table.Column.COLLECT.equals(orderBy)) {// 按照是否收藏排序
                    result = lt.getCollect() - rt.getCollect();
                } else if(Stock.Table.Column.SEARCH.equals(orderBy)) {// 按照被搜索的次数排序
                    result = lt.getSearch() - rt.getSearch();
                } else {// 默认按照股票代码排序
                    result = lt.getCode().compareTo(rt.getCode());
                }

                if(Order.Reverse == order) {
                    result = -result;
                }

                return result;
            }
        });
    }

    public enum Order {
        Sequence,
        Reverse
    }
}
