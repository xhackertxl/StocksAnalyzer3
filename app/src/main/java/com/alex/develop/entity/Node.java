package com.alex.develop.entity;

/**
 * Created by alex on 15-7-31.
 * 存储一次查询获取的K线数据集合
 */
public class Node {

    public Node(int size) {
        index = -1;
        candlesticks = new Candlestick[size];
    }

    public int size() {
        return candlesticks.length;
    }

    public void add(Candlestick candle) {
        candlesticks[++index] = candle;
    }

    public Candlestick get(int index) {
        return candlesticks[index];
    }

    /**
     * 取得日期最靠前的一天的K线数据
     * @return
     */
    public Candlestick getOldest() {
        return candlesticks[0];
    }

    /**
     * 取得日期最靠后的一天的K线的数据
     * @return
     */
    public Candlestick getNewest() {
        return candlesticks[index];
    }

    public void setScope(int st, int ed) {

        float lp = Float.MAX_VALUE;
        float hp = 0.0f;
        volume = 0;

        for(int i=st; i<=ed; ++i) {
            Candlestick candle = get(i);

            // 取得最小价格
            if(lp > candle.getLow()) {
                lp = candle.getLow();
                low = candle;
            }

            // 取得最高价格
            if(hp < candle.getHigh()) {
                hp = candle.getHigh();
                high = candle;
            }

            // 取得最高成交量
            if(volume < candle.getVolume()) {
                volume = candle.getVolume();
            }
        }
    }

    public Candlestick getCandlestickLow() {
        return low;
    }

    public Candlestick getCandlestickHigh() {
        return high;
    }

    public long getVolume() {
        return volume;
    }

    private Candlestick low;// 在可视区域内，股价最低的K线
    private Candlestick high;// 在可视区域内，股价最高的K线
    private long volume;// 在可视区域内，成交量最大值

    private int index;

    private Candlestick[] candlesticks;
}

