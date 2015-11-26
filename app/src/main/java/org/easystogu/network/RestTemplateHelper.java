package org.easystogu.network;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.easystogu.config.FileConfigurationService;

import com.alex.develop.entity.Remote;

public class RestTemplateHelper {
	private static FileConfigurationService configure = FileConfigurationService.getInstance();
 
 
	public String fetchDataFromWeb(String urlStr) {
		try {
			HttpURLConnection urlConnection = null;
			URL url = new URL(urlStr.toString());
			urlConnection = (HttpURLConnection) url.openConnection();
			InputStream inputStream = urlConnection.getInputStream();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Remote.GIT_CHARSET));

			return bufferedReader.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static void main(String[] args) {
		RestTemplateHelper runner = new RestTemplateHelper();
		runner.fetchDataFromWeb("http://data.eastmoney.com/zjlx/detail.html?cmd=C._A");
	}
}
