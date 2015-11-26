package com.alex.develop.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.alex.develop.entity.Candlestick;
import com.alex.develop.stockanalyzer.R;

/**
 * Created by alex on 15-7-19.
 * 股票对应某日的行情数据
 */
@Deprecated
public class StockHeader extends LinearLayout {

    public StockHeader(Context context, AttributeSet attrs) {
        super(context, attrs);

        int padding = (int) getContext().getResources().getDimension(R.dimen.stock_header_padding);
        setPadding(padding, padding, padding, padding);

        LayoutInflater.from(context).inflate(R.layout.stock_header_layout, this);

        if (null == holder) {
            holder = new ViewHolder();
        }

        holder.candleClose = (TextView) findViewById(R.id.candleClose);
        holder.candleOpen = (TextView) findViewById(R.id.candleOpen);
        holder.candleHigh = (TextView) findViewById(R.id.candleHigh);
        holder.candleVolume = (TextView) findViewById(R.id.candleVolume);

        holder.candleChange = (TextView) findViewById(R.id.candleChange);
        holder.candleExchange = (TextView) findViewById(R.id.candleExchange);
        holder.candleLow = (TextView) findViewById(R.id.candleLow);
        holder.candleAmount = (TextView) findViewById(R.id.candleAmount);
    }

    public void setStock(String name, String code) {
        TextView stockName = (TextView) findViewById(R.id.stockName);
        stockName.setText(name);
        TextView stockCode = (TextView) findViewById(R.id.stockCode);
        stockCode.setText(code);
    }

    public void updateHeaderInfo(Candlestick candlestick) {

        if(null == candlestick) {
            String data = getContext().getString(R.string.stock_default);

            holder.candleClose.setText(String.format(getContext().getString(R.string.candle_close), data));
            holder.candleOpen.setText(String.format(getContext().getString(R.string.candle_open), data));
            holder.candleHigh.setText(String.format(getContext().getString(R.string.candle_high), data));
            holder.candleVolume.setText(String.format(getContext().getString(R.string.candle_volume), data));

            holder.candleChange.setText(String.format(getContext().getString(R.string.candle_change), data));
            holder.candleExchange.setText(String.format(getContext().getString(R.string.candle_exchange), data));
            holder.candleLow.setText(String.format(getContext().getString(R.string.candle_low), data));
            holder.candleAmount.setText(String.format(getContext().getString(R.string.candle_amount), data));

            return;
        }

        // 最新
        String close = String.format(getContext().getString(R.string.candle_close), candlestick.getCloseString());
        holder.candleClose.setText(close);

        // 今开
        String open = String.format(getContext().getString(R.string.candle_open), candlestick.getOpenString());
        holder.candleOpen.setText(open);

        // 最高
        String high = String.format(getContext().getString(R.string.candle_high), candlestick.getHighString());
        holder.candleHigh.setText(high);

        // 成交量
        String volume = String.format(getContext().getString(R.string.candle_volume), candlestick.getVolumeString());
        holder.candleVolume.setText(volume);

        // 涨幅
        String change = String.format(getContext().getString(R.string.candle_change), candlestick.getChangeString());
        holder.candleChange.setText(change);

        // 换手
        String exchange = String.format(getContext().getString(R.string.candle_exchange), candlestick.getExchangeString());
        holder.candleExchange.setText(exchange);

        // 最低
        String low = String.format(getContext().getString(R.string.candle_low), candlestick.getLowString());
        holder.candleLow.setText(low);

        // 成交额
        String amount = String.format(getContext().getString(R.string.candle_amount), candlestick.getAmountString());
        holder.candleAmount.setText(amount);

    }

    private class ViewHolder {
        TextView candleClose;
        TextView candleOpen;
        TextView candleHigh;
        TextView candleVolume;
        TextView candleChange;
        TextView candleExchange;
        TextView candleLow;
        TextView candleAmount;
    }

    private ViewHolder holder;
}
