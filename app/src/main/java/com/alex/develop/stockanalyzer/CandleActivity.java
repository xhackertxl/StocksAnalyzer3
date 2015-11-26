package com.alex.develop.stockanalyzer;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alex.develop.entity.Candlestick;
import com.alex.develop.entity.Stock;
import com.alex.develop.ui.CandleView;
import com.alex.develop.util.DateHelper;

import java.util.List;
/**
 * Created by alex on 15-6-15.
 * 绘制蜡烛图（K线图）
 */
public class CandleActivity extends BaseActivity implements CandleView.onCandlestickSelectedListener {

    public static void start(Context context, int index, int from) {
        Intent intent = new Intent();
        intent.setClass(context, CandleActivity.class);
        intent.putExtra(CandleActivity.INDEX, index);
        intent.putExtra(CandleActivity.FROM, from);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.candle_activity);
        Analyzer.setCandleActivityLoadView(findViewById(R.id.loading));
        Bundle bundle = getIntent().getExtras();
        index = bundle.getInt(INDEX);
        from = bundle.getInt(FROM);

        if(COLLECT_LIST == from) {
            stock = Analyzer.getCollectStockList(false).get(index);
        } else {
            stock = Analyzer.getStockList().get(index);
        }

        ActionBar actionBar = getActionBar();
        if(null != actionBar) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.stock_header_layout);
            actionBar.setDisplayHomeAsUpEnabled(true);

            if (null == holder) {
                holder = new ViewHolder();
            }

            View view = actionBar.getCustomView();

            holder.candleClose = (TextView) view.findViewById(R.id.candleClose);
            holder.candleOpen = (TextView) view.findViewById(R.id.candleOpen);
            holder.candleHigh = (TextView) view.findViewById(R.id.candleHigh);
            holder.candleVolume = (TextView) view.findViewById(R.id.candleVolume);

            holder.candleChange = (TextView) view.findViewById(R.id.candleChange);
            holder.candleExchange = (TextView) view.findViewById(R.id.candleExchange);
            holder.candleLow = (TextView) view.findViewById(R.id.candleLow);
            holder.candleAmount = (TextView) view.findViewById(R.id.candleAmount);

            holder.stockName = (TextView) view.findViewById(R.id.stockName);
            holder.stockCode = (TextView) view.findViewById(R.id.stockCode);

            final Button prev = (Button) view.findViewById(R.id.prev);
            final Button next = (Button) view.findViewById(R.id.next);
            prev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    --index;

                    List<Stock> data;
                    if(COLLECT_LIST == from) {
                        data = Analyzer.getCollectStockList(false);
                    } else {
                        data = Analyzer.getStockList();
                    }

                    stock = data.get(index);
                    candleView.setStock(stock);

                    if(0 == index) {
                        v.setEnabled(false);
                    } else {
                        if (data.size() == index + 2) {
                            next.setEnabled(true);
                        }
                    }

                    updateHeaderInfo(stock.getToday());
                }
            });
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ++index;

                    List<Stock> data;
                    if(COLLECT_LIST == from) {
                        data = Analyzer.getCollectStockList(false);
                    } else {
                        data = Analyzer.getStockList();
                    }

                    stock = data.get(index);
                    candleView.setStock(stock);

                    if(1 == index) {
                        prev.setEnabled(true);
                    } else if(data.size() == index + 1) {
                        v.setEnabled(false);
                    }

                    updateHeaderInfo(stock.getToday());
                }
            });

            updateHeaderInfo(stock.getToday());
        }

        candleView = (CandleView) findViewById(R.id.candleView);
        candleView.setOnCandlestickSelectedListener(this);
        candleView.setStock(stock);
    }

    @Override
    protected void onStop() {
        super.onStop();

        // 退出时，取消尚未完成的异步任务
        if (null != candleView) {
            candleView.cancelTask();
        }
    }

    @Override
    public void onSelected(Candlestick candlestick) {
        updateHeaderInfo(candlestick);
    }

    public void updateHeaderInfo(Candlestick candlestick) {

        String today = DateHelper.today();
        String closeStr = getString(R.string.candle_close);

        try {
            if(DateHelper.isMarketOpen() && candlestick.getDate().equals(today)) {
                closeStr = getString(R.string.candle_new);
            }
        }catch (Exception e){
            e.printStackTrace();
        }



        holder.stockName.setText(stock.getName());
        holder.stockCode.setText(stock.getCode());

        // 最新
        String close = String.format(closeStr, candlestick.getCloseString());
        holder.candleClose.setText(close);

        // 今开
        String open = String.format(getString(R.string.candle_open), candlestick.getOpenString());
        holder.candleOpen.setText(open);

        // 最高
        String high = String.format(getString(R.string.candle_high), candlestick.getHighString());
        holder.candleHigh.setText(high);

        // 成交量
        String volume = String.format(getString(R.string.candle_volume), candlestick.getVolumeString());
        holder.candleVolume.setText(volume);

        // 涨幅
        String change = String.format(getString(R.string.candle_change), candlestick.getChangeString());
        holder.candleChange.setText(change);

        // 换手
        String exchange = String.format(getString(R.string.candle_exchange), candlestick.getExchangeString());
        holder.candleExchange.setText(exchange);

        // 最低
        String low = String.format(getString(R.string.candle_low), candlestick.getLowString());
        holder.candleLow.setText(low);

        // 成交额
        String amount = String.format(getString(R.string.candle_amount), candlestick.getAmountString());
        holder.candleAmount.setText(amount);
    }

    private void updateStock() {
        if(COLLECT_LIST == from) {
            stock = Analyzer.getCollectStockList(false).get(index);
        } else {
            stock = Analyzer.getStockList().get(index);
        }
    }

    private class ViewHolder {

        TextView stockName;
        TextView stockCode;

        TextView candleClose;
        TextView candleOpen;
        TextView candleHigh;
        TextView candleVolume;

        TextView candleChange;
        TextView candleExchange;
        TextView candleLow;
        TextView candleAmount;
    }

    public static final String INDEX = "stockIndex";
    public static final String FROM = "from";
    public static final int STOCK_LIST = 0;
    public static final int COLLECT_LIST = 1;
    public static final int OTHER_LIST = 2;

    private int index;
    private int from;
    private Stock stock;
    private ViewHolder holder;
    private CandleView candleView;
}
