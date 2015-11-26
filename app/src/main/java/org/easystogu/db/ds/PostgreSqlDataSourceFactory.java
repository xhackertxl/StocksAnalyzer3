package org.easystogu.db.ds;

import java.util.logging.Logger;

import javax.sql.DataSource;

import org.easystogu.config.Constants;
import org.easystogu.config.FileConfigurationService;

import android.util.Log;

public class PostgreSqlDataSourceFactory {

	private static FileConfigurationService config = FileConfigurationService.getInstance();
	private static DataSource ds = null;

	public static javax.sql.DataSource createDataSource() {

		if (ds != null)
			return ds;
		
		Log.i("","build postgrel datasource.");
//		String driver = config.getString(Constants.JdbcDriver);
//		String url = config.getString(Constants.JdbcUrl);
//		String user = config.getString(Constants.JdbcUser);
//		String password = config.getString(Constants.JdbcPassword);
//		int active = config.getInt(Constants.JdbcMaxActive, 200);
//		int idle = config.getInt(Constants.JdbcMaxIdle, 100);
//
//		ds = new org.apache.tomcat.jdbc.pool.DataSource();
//		ds.setDriverClassName(driver);
//		ds.setUrl(url);
//		ds.setUsername(user);
//		ds.setPassword(password);
//		ds.setMaxActive(active);
//		ds.setMaxIdle(idle);
//		ds.setMaxWait(10000);

		return ds;
	}

	public static void shutdown() {
		Log.i("","close postgrel datasource.");
		if (ds != null) {
			//ds.close();
		}
	}
}
