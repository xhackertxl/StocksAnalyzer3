package com.alex.develop.easymoney.helper;

import com.alex.develop.entity.Stock;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class TheMainCost {
    private final static String baseUrl = "http://data.eastmoney.com/stockcomment/";


    public static void fetchDataFromWeb(String stockId, Stock stock) {
        if(stock.getMain_cost_one() > 0 )
        {
            return;
        }
        synchronized (TheMainCost.class) {
            StringBuffer urlStr = new StringBuffer(baseUrl + stockId + ".html");
            try {
                Connection conn = Jsoup.connect(urlStr.toString());
                // 修改http包中的header,伪装成浏览器进行抓取
                conn.header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:32.0) Gecko/    20100101 Firefox/32.0");
                Document doc = null;
                try {
                    doc = conn.get();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //取主力成本
                Element sp_zlcb = doc.getElementById("sp_zlcb");
                String[] zl = sp_zlcb.text().toString().split("，");
                String one = zl[0];
                String zh1day = one.substring(one.indexOf("成本") + 2, one.indexOf("元"));
                stock.setMain_cost_one(Float.parseFloat(zh1day));
                System.out.println(zh1day);

                String twenty = zl[0];
                String zh20day = one.substring(one.indexOf("成本") + 2, one.indexOf("元"));
                stock.setMain_cost_twenty(Float.parseFloat(zh20day));
                System.out.println(zh20day);

//            Element sp_zjlx = doc.getElementById("sp_zjlx");
//            Element mainRun = sp_zjlx.getElementsByIndexEquals(0).get(1);
//            stock.setMain_cost_one_change(Float.parseFloat(mainRun.text()));
//            System.out.println(mainRun.text());
//            Elements bigRun = sp_zjlx.getElementsByIndexEquals(1);
//            stock.setMain_cost_twenty_change(Float.parseFloat(bigRun.text()));
//            System.out.println(bigRun.text());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        fetchDataFromWeb("300152", null);
    }
}
