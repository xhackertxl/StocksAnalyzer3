package org.easystogu.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.easystogu.utils.Strings;

import com.alex.develop.util.ApplicationHelper;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

public class FileConfigurationService {
	static Context mContext = null;

	private final Properties properties;
	private String TAG = "FileConfigurationService";
	private static FileConfigurationService instance = null;

	public static FileConfigurationService getInstance() {
		mContext = ApplicationHelper.getActivitys().get(0);
		
		if (instance == null) {
			instance = new FileConfigurationService();
		}
		return instance;
	}
	
 

	private FileConfigurationService() {
		String[] resourcesPaths = new String[2];
		resourcesPaths[0] = "application.properties";
		if (Strings.isNotEmpty(System.getProperty("easystogu.config"))) {
			resourcesPaths[1] = System.getProperty("easystogu.config");
		} else {
			resourcesPaths[1] = "application.properties";
		}
		properties = loadProperties(resourcesPaths);
		Log.v(TAG , properties.toString());
	}

	private Properties loadProperties(String...  resourcesPaths) {
		Properties props = new Properties();

		for (String location : resourcesPaths) {

			Log.v("Loading properties file from path:{}", location);

			InputStream is = null;
			try {
	            AssetManager assetManager = mContext.getAssets();  				
				is = assetManager.open(location);
				props.load(is);
			} catch (IOException ex) {
				Log.v("Could not load properties from path:{}, {} ",
						location + ex.getMessage());
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
					}
				}
			}
		}
		return props;
	}

	private String getValue(String key) {
		String systemProperty = System.getProperty(key);
		if (systemProperty != null) {
			return systemProperty; 
		}
		return properties.getProperty(key);
	}

	public boolean getBoolean(String key) {
		String value = getValue(key);
		if (value == null) { 
			throw new RuntimeException("Property " + key + " is not exist");
		}
		return Boolean.valueOf(value);
	}

	public boolean getBoolean(String key, boolean defaultValue) {
		String value = getValue(key);
		if (value == null) {
			return defaultValue;
		}
		return Boolean.valueOf(value);
	}

	public double getDouble(String key) {
		String value = getValue(key);
		if (value == null) {
			throw new RuntimeException("Property " + key + " is not exist");
		}
		return Double.valueOf(value);
	}

	public double getDouble(String key, double defaultValue) {
		String value = getValue(key);
		if (value == null) {
			return defaultValue;
		}
		return Double.valueOf(value);
	}

	public int getInt(String key) {
		String value = getValue(key);
		if (value == null) {
			throw new RuntimeException("Property " + key + " is not exist");
		}
		return Integer.valueOf(value);
	}

	public int getInt(String key, int defaultValue) {
		String value = getValue(key);
		if (value == null) {
			return defaultValue;
		}
		return Integer.valueOf(value);
	}

	public Object getObject(String key) {
		return getString(key);
	}

	public String getString(String key) {
		return getValue(key);
	}

	public String getString(String key, String defaultValue) {
		String value = getValue(key);
		if (value == null) {
			return defaultValue;
		}
		return value;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
