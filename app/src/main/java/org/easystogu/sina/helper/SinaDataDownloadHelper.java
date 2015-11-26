package org.easystogu.sina.helper;

import android.util.Log;

import com.alex.develop.entity.Remote;

import org.easystogu.config.FileConfigurationService;
import org.easystogu.config.StockListConfigurationService;
import org.easystogu.sina.common.RealTimePriceVO;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SinaDataDownloadHelper {
	private static final String baseUrl = "http://hq.sinajs.cn/list=";
	private static FileConfigurationService configure = FileConfigurationService.getInstance();
	private StockListConfigurationService stockConfig = StockListConfigurationService.getInstance();
	private final static String TAG = "SinaDataDownloadHelper";

	// stockList is like: sh000001,sh601318
	// has prefix
	public List<RealTimePriceVO> fetchDataFromWeb(String stockList) {
		String[] stockIds = stockList.split(",");
		return this.fetchDataFromWeb(Arrays.asList(stockIds));
	}

	public List<RealTimePriceVO> fetchDataFromWeb(List<String> stockIds) {
		List<RealTimePriceVO> list = new ArrayList<RealTimePriceVO>();
		try {

			StringBuffer urlStr = new StringBuffer(baseUrl);
			for (String stockId : stockIds) {
				urlStr.append(stockId + ",");
			}
			HttpURLConnection urlConnection = null;
			URL url = new URL(urlStr.toString());
			urlConnection = (HttpURLConnection) url.openConnection();
			InputStream inputStream = urlConnection.getInputStream();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Remote.GIT_CHARSET));

			if ( null == bufferedReader ) {
				Log.i(TAG, "Contents is empty");
				return list;
			}
			String contents ;
			int index = 0;
			String[] content = bufferedReader.toString().trim().split("\n");
			while (null  != ( contents=bufferedReader.readLine()) ) {
				String[] items = contents.trim().split("\"");
				if (items.length <= 1) {
					continue;
				}
				// System.out.println(items[1]);
				String realStockId = stockConfig.getStockIdMapping(stockIds.get(index));
				RealTimePriceVO vo = new RealTimePriceVO(realStockId, items[1]);
				if (vo.isValidated()) {
					list.add(vo);
				}
				index++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public static void main(String[] args) {
		SinaDataDownloadHelper ins = new SinaDataDownloadHelper();
		List<RealTimePriceVO> list = ins.fetchDataFromWeb("sh000001");
		System.out.println(list.size());
		System.out.println(list.get(0));
	}
}
