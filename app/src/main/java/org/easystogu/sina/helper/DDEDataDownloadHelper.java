package org.easystogu.sina.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.Proxy.Type;
import java.util.ArrayList;
import java.util.List;

import org.easystogu.config.Constants;
import org.easystogu.config.FileConfigurationService;
import org.easystogu.sina.common.RealTimePriceVO;
import org.easystogu.utils.Strings;

import com.alex.develop.entity.Remote;

public class DDEDataDownloadHelper {
	private static final String baseUrl = "http://ddx.gubit.cn/ddelist.html?code=";
	private static FileConfigurationService configure = FileConfigurationService.getInstance();

	public RealTimePriceVO fetchDataFromWeb(String stockId) {

		StringBuffer urlStr = new StringBuffer(baseUrl + stockId);

		HttpURLConnection urlConnection = null;

		URL url;
		try {
			url = new URL(urlStr.toString());
			urlConnection = (HttpURLConnection) url.openConnection();
			InputStream inputStream = urlConnection.getInputStream();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Remote.GIT_CHARSET));
			String contents = bufferedReader.toString();

			if (Strings.isEmpty(contents)) {
				System.out.println("Contents is empty");
				return null;
			}

			System.out.println(contents);

			String[] content = contents.trim().split("\n");
			for (int index = 0; index < content.length; index++) {

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

		return null;
	}

	public List<RealTimePriceVO> fetchDataFromWeb(List<String> stockIds) {
		List<RealTimePriceVO> list = new ArrayList<RealTimePriceVO>();
		for (String stockId : stockIds) {
			list.add(this.fetchDataFromWeb(stockId));
		}
		return list;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DDEDataDownloadHelper ins = new DDEDataDownloadHelper();
		ins.fetchDataFromWeb("600175");
	}

}
