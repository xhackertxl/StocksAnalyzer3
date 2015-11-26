package com.alex.develop.entity;

/**
 * Created by alex on 15-6-17.
 */
public final class Constant {

    public static final String MARKET_OPEN = "09:30:00";
    public static final String MARKET_CLOSE = "15:00:00";

    public static final int DATE_OFFSET_DAY = 180;
    public static final int DATE_OFFSET_WEEK = 180;
    public static final int DATE_OFFSET_MONTH = 730;

    /**
     * 上证指数
     */
    public static final String ZS_SZZS_CODE = "1A0001";

    /**
     * 深证成指
     */
    public static final String ZS_SZCZ_CODE = "399001";

    /**
     * 将成交量的单位由？股转为？万手
     *
     * SINA 的API获得的成交量的单位为 [股]
     * SOHU 的API获得的成交量的单位为 [手]
     * 1[手] = 100[股]
     */
    public static final float SINA_VOLUME_FACTOR  = 1000000.0F;

    public static final float SOHU_VOLUME_FACTOR = 10000.0F;

    /**
     * 将成交额的单位由？元股转为？亿元
     *
     * SINA 的API获得的成交额的单位为 [元]
     * SOHU 的API获得的成交额的单位为 [万元]
     */
    public static final float SINA_MONEY_FACTOR = 100000000.0F;

    public static final float SOHU_MONEY_FACTOR = 10000.0F;

    /**
     * 默认绘制的均线数量
     * 5，10，20，30，60
     */
    public static final int MA_COUNT = 5;

    public static final int[] MA_DAY = new int[] {5, 10, 20, 30, 60};
}
