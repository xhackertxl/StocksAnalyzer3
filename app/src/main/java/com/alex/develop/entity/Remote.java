package com.alex.develop.entity;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Remote {
	
	public static void init (String manifest) {
		HttpURLConnection urlConnection = null;
		try {
			URL url = new URL(manifest);
			urlConnection = (HttpURLConnection) url.openConnection();
			InputStream inputStream = urlConnection.getInputStream();

			// 使用Pull解析manifest数据
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(inputStream, GIT_CHARSET);

			int eventType = parser.getEventType();
			while(XmlPullParser.END_DOCUMENT != eventType) {

				if(eventType == XmlPullParser.START_TAG) {
					String tag = parser.getName();
					if("code".equals(tag)) {
						versionCode = Integer.valueOf(parser.nextText());
					} else if("name".equals(tag)) {
						versionName = parser.nextText();
					} else if("log".equals(tag)) {
						versionLog = parser.nextText();
					} else if("amount".equals(tag)) {
						stockAmount = Integer.valueOf(parser.nextText());
					}
				}

				eventType = parser.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(null != urlConnection) {
				urlConnection.disconnect();
			}
		}
	}

	public static int versionCode;// 服务器App的版本号
	public static String versionName;// 服务器App的版本名称
	public static String versionLog;// 服务器App更新的新特性

	public static int stockAmount;// 股票数量

	public final static String GIT_STORE_URL = "https://code.csdn.net/zxfhacker/zxdstore/blob/master/StocksAnalyzer/";
	public final static String GIT_STOCK_LIST = GIT_STORE_URL + "data/sh_sz_list.csv";
	public final static String GIT_MANIFEST = GIT_STORE_URL + "manifest.xml";
	public final static String GIT_CHARSET = "utf-8";
}
