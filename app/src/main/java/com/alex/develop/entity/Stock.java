package com.alex.develop.entity;

import android.provider.BaseColumns;
import android.util.Log;
import android.widget.Toast;

import com.alex.develop.stockanalyzer.Analyzer;
import com.alex.develop.task.CollectStockTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by alex on 15-5-22.
 * 一只股票
 */
public final class Stock extends BaseObject {

    public static class Table {

        public static abstract class Column implements BaseColumns {
            public static final String CODE = "code";// 股票代码
            public static final String CODE_CN = "code_cn";// 股票拼音首字母代码
            public static final String NAME = "name";// 股票名称
            public static final String FULL_NAME = "full_name";// 上市公司全称
            public static final String OFFICE_ADDR = "office_address";// 办公地址

            public static final String LIST_STATUS = "list_status";// 上市状态
            public static final String LIST_DATE = "list_date";// 上市日期

            public static final String TOTAL_SHARE = "total_shares";// 总股本
            public static final String NONREST_FLOAT_A = "nonrest_float_a";// 无限售流通股本，如果为A股，该列为最新无限售流通A股股本数量；如果为B股，该列为最新流通B股股本数量
            public static final String PRIME_OPERATING = "prime_operating";// 主营业务范围

            public static final String COLLECT = "collect";// 股票是否被收藏
            public static final String COLLECT_STAMP = "collect_stamp";// 股票被收藏的时间戳
            public static final String SEARCH = "search";// 股票被搜索的次数

            public static final String MAIN_COST_ONE = "main_cost_one";// 主力最近一日成本
            public static final String MAIN_COST_TWENTY = "main_cost_twenty";// 主力最近一日成本

            public static final String MAIN_FUND_MAIN = "main_cost_one_change";           // 主力流入
            public static final String MAIN_FUND_BIG_ORDER = "main_cost_twenty_change";// 主力大单
        }

        public static final String NAME = "stock_list";
        public static final String SQL_CREATE =
                "CREATE TABLE " + NAME + " (" +
                        Column._ID + " INTEGER PRIMARY KEY NOT NULL," +
                        Column.CODE + " TEXT NOT NULL," +
                        Column.CODE_CN + " TEXT NOT NULL," +
                        Column.NAME + " TEXT NOT NULL," +
                        Column.FULL_NAME + " TEXT DEFAULT ('NULL')," +
                        Column.OFFICE_ADDR + " TEXT DEFAULT ('NULL')," +

                        Column.LIST_STATUS + " TEXT DEFAULT ('NULL')," +
                        Column.LIST_DATE + " NUMERIC NOT NULL," +

                        Column.TOTAL_SHARE + " INTEGER DEFAULT (0)," +
                        Column.NONREST_FLOAT_A + " INTEGER DEFAULT (0)," +
                        Column.PRIME_OPERATING + " TEXT DEFAULT ('NULL')," +

                        Column.COLLECT + " INTEGER DEFAULT (0)," +
                        Column.COLLECT_STAMP + " INTEGER DEFAULT (0)," +
                        Column.SEARCH + " INTEGER DEFAULT (0) ,"

                        + Column.MAIN_COST_ONE + " NUMERIC (24, 4) DEFAULT (0) , "
                        + Column.MAIN_COST_TWENTY + " NUMERIC (24, 4) DEFAULT (0)  ,"
                        + Column.MAIN_FUND_MAIN + " NUMERIC (24, 4) DEFAULT (0) , "
                        + Column.MAIN_FUND_BIG_ORDER + " NUMERIC (24, 4) DEFAULT (0)  "

                        +
                        ")";
    }

    public Stock(String code, String name) {
        this.code = code;
        this.name = name;

        if (null == candleList) {
            candleList = new CandleList();

            if (null == today) {
                today = new Candlestick();
                today.setApiFrom(Enum.API.Sina);
            }
        }

        if (null == salePrice) {
            salePrice = new float[ApiStore.SINA_ENTRUST_LEVEL];
        }

        if (null == saleVolume) {
            saleVolume = new long[ApiStore.SINA_ENTRUST_LEVEL];
        }

        if (null == buyPrice) {
            buyPrice = new float[ApiStore.SINA_ENTRUST_LEVEL];
        }

        if (null == buyVolume) {
            buyVolume = new long[ApiStore.SINA_ENTRUST_LEVEL];
        }

        st = new Cursor(candleList);
        ed = new Cursor(candleList);
        tp = new Cursor(candleList);
    }

    public String getCodeCN() {
        return codeCN;
    }

    public void setCodeCN(String codeCN) {
        this.codeCN = codeCN;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public float[] getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(float[] salePrice) {
        this.salePrice = salePrice;
    }

    public long[] getSaleVolume() {
        return saleVolume;
    }

    public void setSaleVolume(long[] saleVolume) {
        this.saleVolume = saleVolume;
    }

    public float[] getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(float[] buyPrice) {
        this.buyPrice = buyPrice;
    }

    public long[] getBuyVolume() {
        return buyVolume;
    }

    public void setBuyVolume(long[] buyVolume) {
        this.buyVolume = buyVolume;
    }

    public String getTime() {
        return null == time ? "" : time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isSuspended() {
        return suspend;
    }

    public long getStamp() {
        return stamp;
    }

    public Candlestick getToday() {
        return today;
    }

    public CandleList getCandleList() {
        return candleList;
    }

    public long getCollectStamp() {
        return collectStamp;
    }

    public void setCollectStamp(long collectStamp) {
        this.collectStamp = collectStamp;
    }

    public int getCollect() {
        return collect;
    }

    public boolean isCollected() {
        return 1 == collect;
    }

    public void setCollect(int collect) {
        this.collect = collect;
    }

    public void collect(int collect) {
        this.collect = collect;
        // 更新数据库
        new CollectStockTask(collect).execute(this);
    }

    public int getSearch() {
        return search;
    }

    public void setSearch(int search) {
        this.search = search;
    }

    public void search() {

    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Cursor getStart() {
        return st;
    }

    public Candlestick getStartCandle() {
        return candleList.get(st.node).get(st.candle);
    }

    public Cursor getEnd() {
        return ed;
    }

    public Candlestick getEndCandle() {
        return candleList.get(ed.node).get(ed.candle);
    }

    public String getListDate() {
        return listDate;
    }

    public void setListDate(String listDate) {
        this.listDate = listDate;
    }

    public void setAllDataIsDownload(boolean allDataIsDownload) {
        this.allDataIsDownload = allDataIsDownload;
    }

    /**
     * 将游标{st}和{ed}同时向左或向右移动{day}个数据单位
     *
     * @param day
     */
    public void moveCursor(int day) {
        if (0 < day) {

            final Cursor.Move type = ed.move(day);

            // 移动游标
            if (Cursor.Move.None != type) {
                tp.copy(ed);
                tp.move(-Config.ITEM_AMOUNTS);
                st.copy(tp);
            }

            if (Cursor.Move.ArriveNewest == type) {
                Toast.makeText(Analyzer.getContext(), "已达到最新数据", Toast.LENGTH_SHORT).show();
            }

        } else {

            final Cursor.Move type = st.move(day);

            // 移动游标
            if (Cursor.Move.None != type) {
                tp.copy(st);
                tp.move(Config.ITEM_AMOUNTS);
                ed.copy(tp);
            }

            if (Cursor.Move.ArriveOldest == type) {
                Toast.makeText(Analyzer.getContext(), "需要下载数据", Toast.LENGTH_SHORT).show();
            }
        }

        candleList.setScope(st, ed);
    }

    /**
     * 重置数据可视区域的游标
     *
     * @return boolean，如果{candleList}中所含的数据量
     * 少于要显示的数据量{Config.ITEM_AMOUNTS}返回false，
     * 否则返回true
     */
    public boolean resetCursor() {

        if (candleList.getCount() == 0) {
            return false;
        }

        ed.node = 0;
        ed.candle = candleList.getNodes().get(0).size() - 1;

        st.node = ed.node;
        st.candle = ed.candle;

        // 程序第一次下载的数据量 保证至少要{>=Config.ITEM_AMOUNTS}
        st.move(-Config.ITEM_AMOUNTS);
        Log.d("Print-resetCursor", st.node + ", " + st.candle + ", " + ed.node + ", " + ed.candle);

        candleList.setScope(st, ed);

        return true;
    }

    public void fromSina(String[] data) {
        if(null == data || data.length <= 1 )
        {
            return;
        }
        today.setOpen(Float.valueOf(data[1]));// 开盘价
        today.setLastClose(Float.valueOf(data[2])); // 昨日收盘价
        today.setClose(Float.valueOf(data[3]));// 当前价格
        today.setHigh(Float.valueOf(data[4]));// 今日最高价
        today.setLow(Float.valueOf(data[5]));// 今日最低价
        today.setVolume(Long.valueOf(data[8]));// 成交量(单位：股)
        today.setAmount(Float.valueOf(data[9]));// 成交额(单位：元)


        today.initialize();

        for (int i = 10, j = 11, m = 20, n = 21, k = 0; k < ApiStore.SINA_ENTRUST_LEVEL; i += 2, j += 2, m += 2, n += 2, ++k) {

            // 委买
            buyVolume[k] = Long.valueOf(data[i]);// 买k+1数量(单位：股)
            buyPrice[k] = Float.valueOf(data[j]);// 买k+1报价(单位：元)

            // 委卖
            saleVolume[k] = Long.valueOf(data[m]);// 卖k+1数量(单位：股)
            salePrice[k] = Float.valueOf(data[n]);// 卖k+1报价（单位：元）
        }

        today.setDate(data[30].replace(ApiStore.SBL_MINUS, ""));
        time = data[31];
        suspend = data[32].equals(ApiStore.SINA_SUSPEND);

        stamp = System.currentTimeMillis();
    }

    /**
     * 根据sohu股票行情接口读取数据
     *
     * @param data sohu提供的json 数据
     * @return 读取到的数据量, <0, 表示没有读到任何数据(无此股票)；>0,为一共读取到的行情数量
     */
    public int formSohu(JSONArray data) {

        int flag = -1;
        try {
            JSONObject info = data.getJSONObject(0);

            String code = info.optString(ApiStore.SOHU_JSON_CODE);
            if (code.endsWith(this.code)) {
                JSONArray candle = info.optJSONArray(ApiStore.SOHU_JSON_HQ);

                int size = candle.length();

//                if (DateHelper.isMarketOpen() && 0 == candleList.size()) {
//                    ++size;
//                }

                Node node = new Node(size);
                for (int i = 0; i < candle.length(); ++i) {
                    Candlestick candlestick = new Candlestick(candle.optJSONArray(i));
                    node.add(candlestick);
                }

//                if (0 == candleList.size()) {// K线日期最新的结点
//
//                    /**
//                     * 在开盘期间，由于搜狐的历史数据没有当天的数据，此时使用新浪的当日数据；
//                     * 收盘后，搜狐的数据才包含当天的数据，此时使用搜狐的数据
//                     */
//
//                    if (DateHelper.isMarketOpen()) {
//
//                        String lastCandleDate = node.get(size - 2).getDate();
//                        if (!lastCandleDate.equals(today.getDate())) {
//                            node.add(today);
//                        }
//
//                    } else {
//                        today = node.get(size - 1);
//                    }
//                }

                candleList.add(node);
                flag = size;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return flag;
    }

    private String code;  // 股票代码
    private String codeCN;// 股票拼音首字母代码
    private String name;// 股票名称
    private float[] salePrice;// 委卖价格（单位：元）
    private long[] saleVolume;// 委卖数量(单位：手)
    private float[] buyPrice;// 委买价格（单位：元）
    private long[] buyVolume;// 委买数量(单位：手)
    private String time;// 时间
    private boolean suspend;// 是否停牌
    private long stamp;// 查询时间戳
    private long collectStamp;// 被收藏的时间戳
    private int collect;// 股票是否被收藏
    private int search;// 股票被搜索的次数
    private int index;// 在{Analyzer.stockList}中的索引
    private String listDate;// 股票上市日期

    private float main_cost_one;// 主力最近一日成本
    private float main_cost_twenty;// 主力最近一日成本
    private double main_cost_one_change;     // 主力流入
    private double main_cost_twenty_change;// 主力大单

    public float getMain_cost_one() {
        return main_cost_one;
    }

    public void setMain_cost_one(float main_cost_one) {
        this.main_cost_one = main_cost_one;
    }

    public float getMain_cost_twenty() {
        return main_cost_twenty;
    }

    public void setMain_cost_twenty(float main_cost_twenty) {
        this.main_cost_twenty = main_cost_twenty;
    }

    public double getMain_cost_one_change() {
        return main_cost_one_change;
    }

    public void setMain_cost_one_change(double main_cost_one_change) {
        this.main_cost_one_change = main_cost_one_change;
    }

    public double getMain_cost_twenty_change() {
        return main_cost_twenty_change;
    }

    public void setMain_cost_twenty_change(double main_cost_twenty_change) {
        this.main_cost_twenty_change = main_cost_twenty_change;
    }


    private boolean allDataIsDownload;// 该股票的历史数据已经被全部下载

    private Cursor st;// 被绘制的K线的起始位置
    private Cursor ed;// 被绘制的K线的结束位置
    private Cursor tp;// 中转Cursor

    private Candlestick today;// 今日行情

    private CandleList candleList;// 蜡烛线（链表）
}