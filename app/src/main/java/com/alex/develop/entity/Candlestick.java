package com.alex.develop.entity;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.alex.develop.stockanalyzer.Analyzer;
import com.alex.develop.stockanalyzer.R;

import org.json.JSONArray;

/**
 * Created by alex on 15-5-22.
 * 蜡烛线（K线）
 */
public class Candlestick extends BaseObject {

    public Candlestick() {
        kArea = new RectF();
        vArea = new RectF();
        ma = new float[Constant.MA_COUNT];
    }

    public Candlestick(String[] yahoo) {
        this();
        fromYahoo(yahoo);
    }

    public Candlestick(JSONArray data) {
        this();
        fromSohu(data);
    }

    public float getOpen() {
        return open;
    }

    public String getOpenString() {
        return String.format("%.2f", open);
    }

    public void setOpen(float open) {
        this.open = open;
    }

    public void setClose(float close) {
        this.close = close;
    }

    public void setLastClose(float lastClose) {
        this.lastClose = lastClose;
    }

    public float getLastClose() {
        return lastClose;
    }

    public String getLastCloseString() {
        return String.format("%.2f", lastClose);
    }

    public float getHigh() {
        return high;
    }

    public String getHighString() {
        return String.format("%.2f", high);
    }

    public void setHigh(float high) {
        this.high = high;
    }

    public float getLow() {
        return low;
    }

    public String getLowString() {
        return String.format("%.2f", low);
    }

    public void setLow(float low) {
        this.low = low;
    }

    public long getVolume() {
        return volume;
    }

    public String getVolumeString() {

        float value = volume / Constant.SOHU_VOLUME_FACTOR;

        if(Enum.API.Sina == from) {
            value = volume / Constant.SINA_VOLUME_FACTOR;
        }

        return String.format("%.2f", value) + Analyzer.getContext().getString(R.string.candle_volume_unit_million);
    }

    public void setVolume(long volume) {
        this.volume = volume;
    }

    public float getClose() {
        return close;
    }

    public String getCloseString() {
        return String.format("%.2f", close);
    }

    public float getChange() {
        return change;
    }

    public String getChangeString() {
        return String.format("%.2f", change) + "%";
    }

    public float getAmount() {
        return amount;
    }

    public String getAmountString() {

        float value = amount / Constant.SOHU_VOLUME_FACTOR;
        if(Enum.API.Sina == from) {
            value = amount / Constant.SINA_MONEY_FACTOR;
        }

        return String.format("%.2f", value) + Analyzer.getContext().getString(R.string.candle_amount_unit_hundred_million);
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getExchange() {
        return exchange;
    }

    public String getExchangeString() {
        return exchange + "%";
    }

    public float getVary() {
        return vary;
    }

    public float getCenterXofArea() {
        return kArea.left + kArea.width() / 2;
    }

    public float getCenterYofArea() {
        return kArea.top + kArea.height() / 2;
    }

    public float getMaByIndex(int index) {
        return ma[index];
    }

    public void setMaByIndex(int index, float value) {
        ma[index] = value;
    }

    public void setApiFrom(Enum.API from) {
        this.from = from;
    }

    public void initialize() {
        change = 100 * (close - lastClose) / lastClose;
    }

    public void fromYahoo(String[] data) {
        date = data[0].replace(ApiStore.SBL_MINUS, "");// 日期
        open = Float.valueOf(data[1]);// 开盘价
        high = Float.valueOf(data[2]);// 最高价
        low = Float.valueOf(data[3]);// 最低价
        close = Float.valueOf(data[4]);// 收盘价
        volume = Long.valueOf(data[5]);// 成交量
        adjClose = Float.valueOf(data[6]);// Adj Close

        from = Enum.API.Yahoo;

//        Log.d("Print", date + ", " + open + ", " + high + ", " + low + ", " + close + ", " + volume);
    }

    public void fromSohu(JSONArray data) {
        date = data.optString(0).replace(ApiStore.SBL_MINUS, "");// 日期
        open = (float) data.optDouble(1);// 开盘价
        close = (float) data.optDouble(2);// 收盘价
        vary = (float) data.optDouble(3);// 涨跌

        // 涨幅(%)
        String inStr = data.optString(4);
        inStr = inStr.substring(0, inStr.length() - 1);
        change = Float.valueOf(inStr);

        low = (float) data.optDouble(5);// 最低价
        high = (float) data.optDouble(6);// 最高价
        volume = data.optInt(7);// 成交量(手)
        amount = (float) data.optDouble(8);// 成交额(万元)

        // 换手率(%)
        String exStr = data.optString(9);
        exStr = exStr.substring(0, exStr.length() - 1);
        exchange = Float.valueOf(exStr);

        from = Enum.API.Sohu;

//        Log.d("Print-fromSohu", date + ", " + open + ", " + close + ", " + getChangeString());
    }

    public void drawCandle(float x, Config kCfg, Canvas canvas, Paint pen) {

        pen.setStyle(Paint.Style.FILL_AND_STROKE);

        int colorRise = Analyzer.getContext().getResources().getColor(R.color.stock_rise);
        int colorFall = Analyzer.getContext().getResources().getColor(R.color.stock_fall);

        kArea.left = x;
        kArea.right = x + Config.getItemWidth();

        if (open < close) {
            pen.setColor(colorRise);
            kArea.top = kCfg.val2px(close);
            kArea.bottom = kCfg.val2px(open);
        } else if(open == close) {
            if(0.0f < change) {
                pen.setColor(colorRise);
            } else if(0.0f == change) {
                // TODO 股价收十字星时候的颜色
            } else {
                pen.setColor(colorFall);
            }

            kArea.top = kCfg.val2px(close);
            kArea.bottom = kArea.top + 1;
        } else {
            pen.setColor(colorFall);
            kArea.top = kCfg.val2px(open);
            kArea.bottom = kCfg.val2px(close);
        }

//        Log.d("Debug-Candlestick-Draw", date + ", " + kArea.left + ", " + kArea.top);

        float x1 = x + Config.getItemWidth() / 2;
        float y1 = kCfg.val2px(high);
        float y2 = kCfg.val2px(low);

        // 绘制K线影线
        canvas.drawLine(x1, y1, x1, y2, pen);

        // 绘制K线实体
        canvas.drawRect(kArea, pen);
    }

    public void drawVOL(float x, Config qCfg, Canvas canvas, Paint pen) {
        pen.setStyle(Paint.Style.FILL_AND_STROKE);

        int colorRise = Analyzer.getContext().getResources().getColor(R.color.stock_rise);
        int colorFall = Analyzer.getContext().getResources().getColor(R.color.stock_fall);

        if (open < close) {
            pen.setColor(colorRise);
        } else if(open == close) {
            if (0.0f < change) {
                pen.setColor(colorRise);
            } else if (0.0f == change) {
                // TODO 股价收十字星时候的颜色
            } else {
                pen.setColor(colorFall);
            }
        } else {
            pen.setColor(colorFall);
        }

        vArea.left = x;
        vArea.right = x + Config.getItemWidth();
        vArea.bottom = qCfg.val2px(0);
        vArea.top = qCfg.val2px(volume);

        canvas.drawRect(vArea, pen);
    }

    private float open;// 开盘价
    private float close;// 收盘价
    private float vary;// 涨跌变化 (Sohu)
    private float adjClose;// Adj Close (Yahoo)
    private float lastClose;// 昨日收盘价
    private float high;// 最高价
    private float low;// 最低价
    private float change;// 当日涨幅
    private long volume;// 成交量
    private float amount;// 成交额
    private float exchange;// 换手率

    private String date;// 日期
    private RectF kArea;// K线的实体部分
    private RectF vArea;// VOL的实体部分
    private float[] ma;// 平均值




    private Enum.API from;// 数据来源与哪个API
}
