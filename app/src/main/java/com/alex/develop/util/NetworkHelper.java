package com.alex.develop.util;

import android.util.Log;

import com.alex.develop.entity.ApiStore;
import com.alex.develop.entity.Stock;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by alex on 15-5-24.
 * 读取网络数据
 */
public class NetworkHelper {

    private static final String TAG = "com.alex.develop.util.NetworkHelper";

    public static String getWebContent(String webUrl, String charset) {
        return getWebContent(webUrl, null, charset);
    }

    /**
     * 读取一张网页的内容
     *
     * @param webUrl 网页对应的URL
     * @return 网页内容字符串
     */
    public static String getWebContent(String webUrl, HashMap<String, String> header, String charset) {
        HttpURLConnection urlConnection = null;
        StringBuilder builder = new StringBuilder();
        try {
            URL url = new URL(webUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            if (null != header) {
                for (String key : header.keySet()) {
                    urlConnection.setRequestProperty(key, header.get(key));
                }
            }
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, charset));

            String line;
            while (null != (line = bufferedReader.readLine())) {
                builder.append(line);
            }
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != urlConnection) {
                urlConnection.disconnect();
            }
        }
        return builder.toString();
    }


    public static void LoadData(Stock... params) {

        try {

            String sinaApiUrl = ApiStore.getSinaTodayUrl(params);
            String data = NetworkHelper.getWebContent(sinaApiUrl, ApiStore.SINA_CHARSET);
            String[] lines = data.split(ApiStore.SBL_SEM);

            int i = 0;
            for (String line : lines) {
                Stock stock = params[i];
                String[] temp = line.substring(11).split(ApiStore.SBL_EQL);
                temp[0] = temp[0].substring(2);
                temp[1] = temp[1].substring(1, temp[1].length() - 1);
                String id = temp[0];
                String[] info = temp[1].split(ApiStore.SBL_CMA);

                if(info.length <= 1)
                {
                    continue;
                }
                if (id.equals(stock.getCode())) {
                    stock.fromSina(info);
                    //TheMainCost.fetchDataFromWeb(stock.getCode(),stock);
                }
                ++i;
            }
        }  catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG,e.getMessage());
        }

    }


    private NetworkHelper() {
    }
}
