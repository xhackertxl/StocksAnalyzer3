package com.alex.develop.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alex.develop.entity.Stock;
import com.alex.develop.stockanalyzer.Analyzer;
import com.alex.develop.stockanalyzer.R;

import java.util.ArrayList;
import java.util.List;

import de.codecrafters.tableview.TableDataAdapter;

/**
 * Created by alex on 15-8-19. 呈现所有股票列表信息ListView的Adapter
 */
public class StockListTableAdapterAndMainFund extends TableDataAdapter<Stock> {

    private List<Stock> mSelectedStocks;
    private SparseBooleanArray mSelectedItems;// 记录被选中的ItemId
    private final int selectedColor = Analyzer.getContext().getResources().getColor(R.color.list_item_selected_bg);// 被选中的背景颜色

    public StockListTableAdapterAndMainFund(Context context, List<Stock> data) {
        super(context, data);

        mSelectedStocks = new ArrayList<>();
        mSelectedItems = new SparseBooleanArray();
    }

    @Override
    public View getCellView(int rowIndex, int columnIndex, ViewGroup convertView) {
        Stock stock = getRowData(rowIndex);
        View renderedView = null;

        int textColor = Analyzer.getContext().getResources().getColor(R.color.stock_rise);
        float increase = stock.getToday().getChange();
        String close = stock.getToday().getCloseString();
        String changeString = stock.getToday().getChangeString();

        float main_cost_one = stock.getMain_cost_one();// 主力最近一日成本
        float main_cost_twenty= stock.getMain_cost_twenty();// 主力最近20日成本
        double main_fund_main= stock.getMain_cost_one_change();     // 主力流入
        double main_fund_big_order= stock.getMain_cost_twenty_change();// 主力大单


        if (0 > increase) {
            textColor = Analyzer.getContext().getResources().getColor(R.color.stock_fall);
        }
        if (stock.isSuspended()) {
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

        switch (columnIndex) {
            case 0:
                renderedView = renderStockName(stock, convertView);
                break;
            case 1:
                // 股票价格（收盘价）
                renderedView = new TextView(this.getContext());
                ((TextView) renderedView).setText(close);
                ((TextView) renderedView).setTextColor(textColor);
                break;
            case 2:
                // 股票涨幅
                renderedView = new TextView(this.getContext());
                ((TextView) renderedView).setText(changeString);
                ((TextView) renderedView).setTextColor(textColor);
                break;
            case 3:
                // 股票涨幅
                renderedView = new TextView(this.getContext());
                ((TextView) renderedView).setText(Float.toString(main_cost_one));
                ((TextView) renderedView).setTextColor(textColor);
                break;
            case 4:
                // 股票涨幅
                renderedView = new TextView(this.getContext());
                ((TextView) renderedView).setText(Float.toString(main_cost_twenty));
                ((TextView) renderedView).setTextColor(textColor);
                break;
            case 5:
                // 股票涨幅
                renderedView = new TextView(this.getContext());
                ((TextView) renderedView).setText(Double.toString(main_fund_main));
                ((TextView) renderedView).setTextColor(textColor);
                break;
            case 6:
                // 股票涨幅
                renderedView = new TextView(this.getContext());
                ((TextView) renderedView).setText(Double.toString(main_fund_big_order));
                ((TextView) renderedView).setTextColor(textColor);
                break;
        }

        int color = mSelectedItems.get(rowIndex) ? selectedColor : Color.TRANSPARENT;
        convertView.setBackgroundColor(color);

        return renderedView;
    }

    public void selectView(int position, boolean isSelect) {
        Stock stock = getData().get(position);
        if (isSelect) {
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

    private View renderStockName(Stock stock, ViewGroup parentView) {
        View view = getLayoutInflater().inflate(R.layout.stock_name, parentView, false);
        TextView stockName = (TextView) view.findViewById(R.id.stockName);
        TextView stockCode = (TextView) view.findViewById(R.id.stockCode);
        // 股票名称
        stockName.setText(stock.getName());
        // 股票代码
        stockCode.setText(stock.getCode());
        return view;
    }

}
