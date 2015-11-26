package com.alex.develop.entity;

import com.alex.develop.util.DateHelper;

import java.util.ArrayList;

/**
 * Created by alex on 15-7-31.
 * 存储K线数据的
 */
public class CandleList {

    public CandleList() {
        nodes = new ArrayList<>();
        oldest = DateHelper.today();
    }

    public int size() {
        return nodes.size();
    }

    public void add(Node node) {
        nodes.add(node);
        count += node.size();

        oldest = node.getOldest().getDate();

        if(1 == size()) {
            newest = node.getNewest().getDate();
        }
    }

    public Node get(int index) {
        return nodes.get(index);
    }

    public ArrayList<Node> getNodes() {
        return nodes;
    }

    public int getCount() {
        return count;
    }

    /**
     * 设置数据的可见区域，start中的索引数值肯定<=stop中的索引数值
     *
     * @param st 可见区域起始游标
     * @param ed  可见区域结束游标
     */
    public void setScope(Cursor st, Cursor ed) {

        if (st.node == ed.node) {// 同一个Node数据块内
            Node node = get(st.node);
            node.setScope(st.candle, ed.candle);

            low = node.getCandlestickLow();
            high = node.getCandlestickHigh();
            volume = node.getVolume();

        } else {// 不同Node数据块

            float lp = Float.MAX_VALUE;
            float hp = 0.0f;
            volume = 0;

            for (int i = st.node; i >= ed.node; --i) {

                Node node = get(i);

                if (i == st.node) {
                    node.setScope(st.candle, node.size()-1);
                } else if (i == ed.node) {
                    node.setScope(0, ed.candle);
                } else {
                    node.setScope(0, node.size()-1);
                }

                if(lp > node.getCandlestickLow().getLow()) {
                    lp = node.getCandlestickLow().getLow();
                    low = node.getCandlestickLow();
                }

                if(hp < node.getCandlestickHigh().getHigh()) {
                    hp = node.getCandlestickHigh().getHigh();
                    high = node.getCandlestickHigh();
                }

                volume = volume < node.getVolume() ? node.getVolume() : volume;
            }
        }
    }

    /**
     * 取得可视区域内的最低价
     * @return
     */
    public float getLowest() {
        return low.getLow();
    }

    public Candlestick getCandlestickLow() {
        return low;
    }

    /**
     * 取得可视区域内的最高价
     * @return
     */
    public float getHighest() {
        return high.getHigh();
    }

    public Candlestick getCandlestickHigh() {
        return high;
    }

    /**
     * 取得可视区域内的最大成交量
     * @return
     */
    public long getVolume() {
        return volume;
    }

    public String getOldestDate() {
        return oldest;
    }

    public String getNewestDate() {
        return newest;
    }

    private Candlestick low;// 在可视区域内，股价最低的K线
    private Candlestick high;// 在可视区域内，股价最高的K线
    private long volume;// 在可视区域内，成交量最大值

    private String oldest;// 记录该数据结构中存储数据的起始日期
    private String newest;// 记录该数据结构中存储数据的结束日期

    private int count;// 统计存储的K线数据量
    private ArrayList<Node> nodes;
}
