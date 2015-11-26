package com.alex.develop.entity;

import java.util.List;

/**
 * Created by alex on 15-9-7.
 * 汇聚各种使用到的API
 */
public class ApiStore {

    public static final String SH_EXCHANGE = "XSHG";// 上海证券交易所
    public static final String SZ_EXCHANGE = "XSHE";// 深圳证券交易所

    public static final String SBL_DOT = ".";
    public static final String SBL_EQL = "=";
    public static final String SBL_AND = "&";
    public static final String SBL_CMA = ",";
    public static final String SBL_SEM = ";";
    public static final String SBL_MINUS = "-";

    /**
     * 生成查询当日股票行情的新浪API字符串
     * @param stocks 股票列表
     * @return
     */
    public static String getSinaTodayUrl(Stock... stocks) {
        StringBuilder builder = new StringBuilder();

        for(Stock stock : stocks) {

            String prefix = SINA_SZ_PREFIX;
            if(shOrsz(stock)) {
                prefix = SINA_SH_PREFIX;
            }

            builder.append(",");
            builder.append(prefix);
            builder.append(stock.getCode());
        }

        builder.delete(0, 1);
        builder.insert(0, SINA_TODAY);

        return builder.toString();
    }

    public static String getSohuHistoryUrl(String stockCode, String start, String end, Enum.Period period) {
        return getSohuHistoryUrl(stockCode, start, end, false, Enum.Order.ASC, period);
    }

    public static String getSohuHistoryUrl(String stockCode, String start, String end, boolean statistics, Enum.Order order, Enum.Period period) {
        StringBuilder builder = new StringBuilder();
        builder.append(SOHU_HISTORY);

        if(Constant.ZS_SZZS_CODE.equals(stockCode)) {// 上证指数
            builder.append("zs_000001");
        } else if(Constant.ZS_SZCZ_CODE.equals(stockCode)) {// 深证成指
            builder.append("zs_399001");
        } else {// 股票代码
            builder.append("cn_");
            builder.append(stockCode);
        }

        // 起始日期
        builder.append("&start=");
        builder.append(start);

        // 结束日期
        builder.append("&end=");
        builder.append(end);

        // 是否统计
        if(statistics) {
            builder.append("&stat=1");
        }

        // 排序方式
        builder.append("&order=");
        builder.append(order);

        // 查询周期
        builder.append("&period=");
        builder.append(period);

        return builder.toString();
    }

    public static String getStockInfoUrl(List<Stock> stocks) {

        StringBuilder secIdBuilder = new StringBuilder();
        secIdBuilder.append("secID");
        secIdBuilder.append(SBL_EQL);

        StringBuilder tickerBuilder = new StringBuilder();
        tickerBuilder.append(JDWX_JSON_TICKER);
        tickerBuilder.append(SBL_EQL);

        for (Stock stock : stocks) {

            secIdBuilder.append(stock.getCode());
            secIdBuilder.append(SBL_DOT);
            tickerBuilder.append(stock.getCode());

            if (shOrsz(stock)) {
                secIdBuilder.append(SH_EXCHANGE);
            } else {
                secIdBuilder.append(SZ_EXCHANGE);
            }

            secIdBuilder.append(SBL_CMA);
            tickerBuilder.append(SBL_CMA);
        }

        secIdBuilder.deleteCharAt(secIdBuilder.length() - 1);
        tickerBuilder.deleteCharAt(tickerBuilder.length() - 1);

        // equTypeCD 可以省略
//        tickerBuilder.append(SBL_AND);
//        tickerBuilder.append("equTypeCD");
//        tickerBuilder.append(SBL_EQL);
//        tickerBuilder.append("A");

        // field
        tickerBuilder.append(SBL_AND);
        tickerBuilder.append("field");
        tickerBuilder.append(SBL_EQL);
        tickerBuilder.append(JDWX_JSON_LIST_DATE);

        // 查询的字段
        tickerBuilder.append(SBL_CMA);
        tickerBuilder.append(JDWX_JSON_TICKER);

        tickerBuilder.append(SBL_CMA);
        tickerBuilder.append(JDWX_JSON_FULLNAME);

        tickerBuilder.append(SBL_CMA);
        tickerBuilder.append(JDWX_JSON_OFFICE);

        tickerBuilder.append(SBL_CMA);
        tickerBuilder.append(JDWX_JSON_LIST_STATUS);

        tickerBuilder.append(SBL_CMA);
        tickerBuilder.append(JDWX_JSON_TOTAL_SHARE);

        tickerBuilder.append(SBL_CMA);
        tickerBuilder.append(JDWX_JSON_NONREST_FLOAT_A);

        tickerBuilder.append(SBL_CMA);
        tickerBuilder.append(JDWX_JSON_PRIME_OPERATING);

        return JDWX_API_URL + secIdBuilder + SBL_AND + tickerBuilder;
    }

    public static String getFinanceNewsUrl(int page) {
        final String channelID = "5572a109b3cdc86cf39001e0";
        return getNewsUrl(channelID, page);
    }

    public static String getNewsUrl(String channelID, int page) {
        StringBuilder builder = new StringBuilder();
        builder.append(SHOW_API_FINANCE_NEWS_URL);

        // 频道ID
        builder.append("channelId");
        builder.append(SBL_EQL);
        builder.append(channelID);
        builder.append(SBL_AND);

        // 请求的页码
        builder.append("page");
        builder.append(SBL_EQL);
        builder.append(page);

        return builder.toString();
    }

    /**
     * 判断股票代码是上海A股还是深圳A股
     *
     * @param stock 股票
     * @return 上海A股，true; 深圳A股，false
     */
    private static boolean shOrsz(Stock stock) {
        boolean flag = false;
        if (stock.getCode().startsWith("6")) {
            flag = true;
        }
        return flag;
    }

    /**
     * 搜狐的股票历史数据接口
     * http://q.stock.sohu.com/hisHq?code=cn_601919&start=20150801&end=20150908&stat=1&order=A&period=d（上海A股）
     * http://q.stock.sohu.com/hisHq?code=cn_000783&start=20150801&end=20150908&stat=1&order=A&period=d（深圳A股）
     * http://q.stock.sohu.com/hisHq?code=zs_000001&start=20150801&end=20150908&stat=1&order=A&period=d（上证指数）
     * http://q.stock.sohu.com/hisHq?code=zs_399001&start=20150801&end=20150908&stat=1&order=A&period=d（深圳成指）
     */
    public final static String SOHU_CHARSET = "gbk";
    public final static String SOHU_JSON_STATUS = "status";
    public final static String SOHU_JSON_HQ = "hq";
    public final static String SOHU_JSON_CODE = "code";
    public final static int SOHU_JSON_STATUS_OK = 0;
    private final static String SOHU_HISTORY = "http://q.stock.sohu.com/hisHq?code=";

    /**
     * 新浪的股票行情实时接口
     * EG-SH : http://hq.sinajs.cn/list=sh601919（上海A股）
     * EG-SZ : http://hq.sinajs.cn/list=sz000783（深圳A股）
     *  MORE : http://hq.sinajs.cn/list=sz000783,sz000698,sh601919
     */
    public final static int SINA_REFRESH_INTERVAL = 5000;// 5秒刷新间隔
    public final static int SINA_ENTRUST_LEVEL = 5;// 5档委托数据
    public final static String SINA_SH_PREFIX = "sh";// 上海股票前缀
    public final static String SINA_SZ_PREFIX = "sz";// 深圳股票前缀
    public final static String SINA_CHARSET = "gb2312";// 字符编码
    public final static String SINA_SUSPEND = "03";// 停牌状态码
    private final static String SINA_TODAY = "http://hq.sinajs.cn/list=";

    /**
     * 使用[京东万象数据-from-百度APIStore]提供的API查询股票基本信息
     * 网址：http://apistore.baidu.com/apiworks/servicedetail/1033.html
     * EG-SH：http://apis.baidu.com/wxlink/getequ/getequ?secID=601919.XSHG&ticker=601919&field=primeOperating
     * EG-SZ：http://apis.baidu.com/wxlink/getequ/getequ?secID=000751.XSHE&ticker=000751&field=primeOperating
     */
    public static final String JDWX_JSON_LIST_DATE = "listDate";// 上市日期
    public static final String JDWX_JSON_TICKER = "ticker";// 股票代码
    public static final String JDWX_JSON_RETCODE = "retCode";
    public static final String JDWX_JSON_RESULT = "result";
    public static final String JDWX_JSON_DATA = "data";
    public static final String JDWX_JSON_FULLNAME = "secFullName";// 上市公司全称
    public static final String JDWX_JSON_OFFICE = "officeAddr";// 办公地址
    public static final String JDWX_JSON_LIST_STATUS = "listStatusCD";// 上市状态
    public static final String JDWX_JSON_TOTAL_SHARE = "totalShares";// 总股本
    public static final String JDWX_JSON_NONREST_FLOAT_A = "nonrestfloatA";// 无限售流通股本，如果为A股，该列为最新无限售流通A股股本数量；如果为B股，该列为最新流通B股股本数量
    public static final String JDWX_JSON_PRIME_OPERATING = "primeOperating";// 主营业务范围
    public static final String JDWX_JSON_DEFAULT = "NULL";

    public static final int JDWX_JSON_STATUS_OK = 1;
    public static final String JDWX_CHARSET = "UTF-8";

    private static final String JDWX_API_URL = "http://apis.baidu.com/wxlink/getequ/getequ?";

    /**
     * 使用[易源频道新闻]提供的API查询分类新闻信息
     * 网址：http://apistore.baidu.com/apiworks/servicedetail/688.html
     * EG-财经最新：http://apis.baidu.com/showapi_open_bus/channel_news/search_news?channelId=5572a109b3cdc86cf39001e0&page=1
     * EG-财经焦点：http://apis.baidu.com/showapi_open_bus/channel_news/search_news?channelId=5572a108b3cdc86cf39001d0&page=1
     */
    private static final String SHOW_API_FINANCE_NEWS_URL = "http://apis.baidu.com/showapi_open_bus/channel_news/search_news?";
    public static final String SHOW_API_CHARSET = "UTF-8";

    /**
     * 百度APIStore的API密钥
     */
    public static final String BAIDU_APISTORE_API_KEY = "apikey";
    public static final String BAIDU_APISTORE_API_VALUE = "7099530a107f136565aa4e1dafc3f74f";
}
