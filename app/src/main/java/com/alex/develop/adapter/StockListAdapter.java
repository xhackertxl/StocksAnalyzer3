package com.alex.develop.adapter;

import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.alex.develop.entity.Stock;
import com.alex.develop.stockanalyzer.Analyzer;
import com.alex.develop.stockanalyzer.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alex on 15-8-19.
 * 呈现所有股票列表信息ListView的Adapter
 */
public class StockListAdapter extends BaseAdapter {

    public StockListAdapter(List<Stock> stocks) {
        this.stocks = stocks;
        mSelectedStocks = new ArrayList<>();
        mSelectedItems = new SparseBooleanArray();
    }

    @Override
    public int getCount() {
        return null == stocks ? 0 : stocks.size();
    }

    @Override
    public Object getItem(int position) {
        return stocks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(null == convertView) {
            LayoutInflater inflater = LayoutInflater.from(Analyzer.getContext());
            convertView = inflater.inflate(R.layout.stock_details, null);
            ViewHolder holder = new ViewHolder();
            holder.stockName = (TextView) convertView.findViewById(R.id.stockName);
            holder.stockCode = (TextView) convertView.findViewById(R.id.stockCode);
            holder.stockClose = (TextView) convertView.findViewById(R.id.stockClose);
            holder.stockChange = (TextView) convertView.findViewById(R.id.stockChange);
            convertView.setTag(holder);
        }

        Stock stock = stocks.get(position);
        ViewHolder holder = (ViewHolder) convertView.getTag();

        int textColor = Analyzer.getContext().getResources().getColor(R.color.stock_rise);
        float increase = stock.getToday().getChange();
        String close = stock.getToday().getCloseString();
        String changeString = stock.getToday().getChangeString();

        if(0 > increase) {
            textColor = Analyzer.getContext().getResources().getColor(R.color.stock_fall);
        }
        if(stock.isSuspended()) {
            textColor = Analyzer.getContext().getResources().getColor(R.color.stock_suspended);
            close = stock.getToday().getLastCloseString();
            changeString = Analyzer.getContext().getString(R.string.trade_suspended);
        }

        // 如果没有查询到股票数据则显示默认的字符
        if ("".equals(stock.getTime())) {
            close = Analyzer.getContext().getString(R.string.stock_default);
            changeString = close;
            textColor = Analyzer.getContext().getResources().getColor(R.color.stock_suspended);
        }

        // 股票名称
        holder.stockName.setText(stock.getName());

        // 股票代码
        holder.stockCode.setText(stock.getCode());

        // 股票价格（收盘价）
        holder.stockClose.setText(close);
        holder.stockClose.setTextColor(textColor);

        // 股票涨幅
        holder.stockChange.setText(changeString);
        holder.stockChange.setTextColor(textColor);

        int color = mSelectedItems.get(position) ? selectedColor : Color.TRANSPARENT;
        convertView.setBackgroundColor(color);

        return convertView;
    }

    public void selectView(int position, boolean isSelect) {
        Stock stock = stocks.get(position);
        if(isSelect) {
            mSelectedItems.put(position, true);
            stock.setCollectStamp(System.currentTimeMillis());
            mSelectedStocks.add(stock);
        } else {
            mSelectedItems.delete(position);
            mSelectedStocks.remove(stock);
        }
        notifyDataSetChanged();
    }

    public void removeSelection() {
        mSelectedItems.clear();
        mSelectedStocks.clear();
        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return mSelectedItems.size();
    }

    public Stock[] getSelectedStocks() {
        Stock[] stocks = new Stock[mSelectedStocks.size()];
        return mSelectedStocks.toArray(stocks);
    }

    private static class ViewHolder {
        TextView stockName;
        TextView stockCode;
        TextView stockClose;
        TextView stockChange;
    }

    private List<Stock> stocks;
    private List<Stock> mSelectedStocks;
    private SparseBooleanArray mSelectedItems;// 记录被选中的ItemId
    private  final int selectedColor = Analyzer.getContext().getResources().getColor(R.color.list_item_selected_bg);// 被选中的背景颜色
}

