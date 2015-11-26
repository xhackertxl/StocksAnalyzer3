package com.alex.develop.entity;

import java.util.ArrayList;

/**
 * Created by alex on 15-7-31.
 * 用于精确定位某个K线数据的游标
 */
public class Cursor {

    public Cursor() {}

    public Cursor(CandleList candleList) {
        this.candleList = candleList;
    }

    public void setCandleList(CandleList candleList) {
        this.candleList = candleList;
    }

    /**
     * 从{csr}复制数据，纳为己用
     * @param csr 被复制得数据
     */
    public void copy(Cursor csr) {
        node = csr.node;
        candle = csr.candle;
    }

    public void calculateMA() {
        Candlestick candlestick = candleList.get(node).get(candle);

        boolean cal = true;
        int index = 0;
        for(final int day : Constant.MA_DAY) {
            if (0.0f < candlestick.getMaByIndex(index)) {
            } else {
                cal = false;
            }
            ++index;
        }

        // 计算过了就不重复计算
        if(cal) {
            return;
        }

        int count = 1;
        float total = 0.0f;
        for(int i=node; i<candleList.getNodes().size(); ++i) {
            Node nde = candleList.get(i);

            // 计算特殊情况下遍历的开始和结束的位置
            for(int j = i == node ? candle : nde.size()-1;j>=0;--j) {
                Candlestick cs = nde.get(j);
                total += cs.getClose();

                index = 0;
                for(final int day : Constant.MA_DAY) {
                    if(day == count) {
                        candlestick.setMaByIndex(index, total/day);
                    }
                    ++index;
                }
                count++;
            }
        }
    }

    public boolean isOldest() {
        final int node = candleList.size() - 1;
        final int candle = 0;
        return node == this.node && candle == this.candle;
    }

    public boolean isNewest() {
        final int node = 0;
        final int candle = candleList.get(node).size() - 1;
        return node == this.node && candle == this.candle;
    }

    /**
     * 将游标{cursor}移动{day}个单位
     * @param day day > 0，向右移动；day < 0，向左移动
     * @return
     *
     * Move.None，未发生移动
     * Move.Moved，发生了移动
     * Move.ArriveOldest，移动到最旧数据（提示要下载数据）
     * Move.ArriveNewest，移动到最新数据（提示已是最新数据）
     */
    public Move move(int day) {

        ArrayList<Node> nodes = candleList.getNodes();
        Move flag = Move.None;

        // 不需要移动
        if(0 == day) {
            return flag;
        }

        if(day > 0) {// View向右移动(股票数据越来越新)

            // 数据已经最新，无需移动
            if(isNewest()) {
                return flag;
            }

            int nIndex = node;
            int cIndex = candle + day - 1;

            // 同一个Node内
            if(nodes.get(nIndex).size() > cIndex) {
                candle = cIndex;
                flag = Move.Moved;
            } else {

                cIndex -= nodes.get(nIndex).size();

                while (true) {

                    --nIndex;

                    // 如果超出左侧界限，则将{cursor}设置为最左侧（CandleList）的元素
                    if(0 > nIndex) {
                        node = 0;
                        candle = nodes.get(0).size() - 1;
                        flag = Move.Moved;
                        break;
                    }

                    Node data = nodes.get(nIndex);

                    if (data.size() > cIndex) {
                        node = nIndex;
                        candle = cIndex;
                        flag = Move.Moved;
                        break;
                    } else {
                        cIndex -= data.size();
                    }
                }
            }
        } else {// View向左移动(股票数据越来越旧)

            if(isOldest()) {
                return flag;
            }

            // 负数取绝对值
            day = -day;

            int nIndex = node;
            int cIndex = candle - day + 1;

            // 同一个Node内
            if(0 <= cIndex) {
                candle = cIndex;
                flag = Move.Moved;
            } else {
                while (true) {
                    ++nIndex;

                    // 如果超出右侧界限，则将{cursor}设置为最右侧（CandleList）的元素
                    if(nodes.size() <= nIndex) {
                        node = nodes.size() - 1;
                        candle = 0;
                        flag = Move.Moved;
                        break;
                    }

                    Node data = nodes.get(nIndex);
                    cIndex += data.size();

                    if(0 < cIndex) {
                        node = nIndex;
                        candle = cIndex;
                        flag = Move.Moved;
                        break;
                    }
                }
            }
        }

        flag = isOldest() ? Move.ArriveOldest : flag;
        flag = isNewest() ? Move.ArriveNewest : flag;

        return flag;
    }

    public int node;// CandleList中用于定位Node的索引

    public int candle;// Node中用于定位Candlestick的索引

    public enum Move {
        None,
        Moved,
        ArriveOldest,
        ArriveNewest
    }
    private CandleList candleList;
}
