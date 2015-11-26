package com.alex.develop.stockanalyzer;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.alex.develop.entity.Remote;
import com.alex.develop.entity.Stock;
import com.alex.develop.util.SQLiteHelper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * App的启动画面，持续2.5s，可用于<br>
 * 1、展示App品牌LOGO<br>
 * 2、加载程序所需数据<br>
 * 3、介绍软件新特性
 * 4、广告展示
 *
 * @author Created by alex 2014/11/07
 */
@SuppressLint("NewApi")
public class Splash extends BaseActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);

		initialize();
		blockBack(false);

		if (isNetworkAvailable()) {
			startApp();
		}
	}

	/**
	 * 从网络读取数据
	 */
	private void startApp() {

		String preferFiles = getPackageName();
		SharedPreferences prefer = getSharedPreferences(preferFiles, Context.MODE_PRIVATE);

		final boolean firstLaunch = prefer.getBoolean(getString(R.string.key_first_launch), true);
		if (firstLaunch) {
			SharedPreferences.Editor editor = prefer.edit();
			editor.putBoolean(getString(R.string.key_first_launch), false);
			editor.apply();
		}

		new AsyncTask<Void, Void, Void>() {

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				start = System.currentTimeMillis();
			}

			@Override
			protected Void doInBackground(Void... params) {
				Remote.init(Remote.GIT_MANIFEST);

				// 从SQLite中读取数据
				if (!firstLaunch) {

					int count = 0;
					List<Stock> stocks = new ArrayList<>();

					/*-----------------------------*/
					ArrayList<Stock[]> arrayList = new ArrayList<>();
					Map stocksMap = new HashMap<String, Stock>();
					Analyzer.setStockListMap(stocksMap);
					ArrayList<Stock> temp = new ArrayList<>();
					Analyzer.setArraystockList(arrayList);
                    /*-----------------------------*/

					SQLiteDatabase db = SQLiteHelper.getInstance().getReadableDatabase();
					Cursor cursor = db.query(Stock.Table.NAME, null, null, null, null, null, null);

					if (null != cursor && cursor.moveToFirst()) {
						do {
							String stockCode = cursor.getString(cursor.getColumnIndex(Stock.Table.Column.CODE));
							String stockCodeCN = cursor.getString(cursor.getColumnIndex(Stock.Table.Column.CODE_CN));
							String stockName = cursor.getString(cursor.getColumnIndex(Stock.Table.Column.NAME));
							String listDate = cursor.getString(cursor.getColumnIndex(Stock.Table.Column.LIST_DATE));

							int collect = cursor.getInt(cursor.getColumnIndex(Stock.Table.Column.COLLECT));
							long collectStamp = cursor.getLong(cursor.getColumnIndex(Stock.Table.Column.COLLECT_STAMP));
							int search = cursor.getInt(cursor.getColumnIndex(Stock.Table.Column.SEARCH));

							Stock stock = new Stock(stockCode, stockName);
							stock.setCodeCN(stockCodeCN);
							stock.setListDate(listDate);
							stock.setCollect(collect);
							stock.setCollectStamp(collectStamp);
							stock.setSearch(search);
							stock.setIndex(count);
							stocks.add(stock);

							/*-----------------------*/
							stocksMap.put(stock.getCode(), stock);
							temp.add(stock);
							if (temp.size() >= 227) {
								arrayList.add(temp.toArray(new Stock[temp.size()]));
								System.out.println("size ----- " + temp.size());
								temp = new ArrayList<>();
							}
							/*-----------------------*/
							++count;
						} while (cursor.moveToNext());
					}
					if (null != cursor) {
						cursor.close();
					}
                    /*-----------------------*/
					if (temp.size() > 0) {
						arrayList.add(temp.toArray(new Stock[temp.size()]));
						System.out.println("size ----- " + temp.size() + " -- " + count);
						temp = null;
					}
                    /*-----------------------*/
					Analyzer.setStockList(stocks);
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void aVoid) {
				super.onPostExecute(aVoid);

				if (firstLaunch) {

					new AddStockAsync(getString(R.string.start_tips_install)).execute(Remote.GIT_STOCK_LIST);

				} else {
					long interval = System.currentTimeMillis() - start;
					int duration = getResources().getInteger(R.integer.splash_duration);
					if (interval > duration) {
						startActivity(false);
					} else {
						new Handler().postDelayed(new Runnable() {

							@Override
							public void run() {
								startActivity(false);
							}
						}, duration - interval);
					}
				}
			}

			private long start;
		}.execute();
	}

	private void startActivity(boolean isFirst) {
		if (isFirst) {

			/**
			 *  启动新特性介绍
			 */
			final ImageView splash = (ImageView) findViewById(R.id.splash);
			Animation splashAnim = AnimationUtils.loadAnimation(Splash.this, R.anim.out_from_left);
			splashAnim.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {
					Animation featureAnim = AnimationUtils.loadAnimation(Splash.this, R.anim.in_from_right);
					feature.startAnimation(featureAnim);
					feature.setVisibility(View.VISIBLE);
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationEnd(Animation animation) {
					splash.setVisibility(View.GONE);
				}
			});

			splash.startAnimation(splashAnim);

		} else {

			// 程序主界面
			intent = new Intent(Splash.this, MainActivity.class);
			startActivity(intent);
			finish();
		}
	}

	@SuppressLint("InflateParams")
	private void initialize() {
		LayoutInflater inflater = LayoutInflater.from(this);
		views = new View[]{
				inflater.inflate(R.layout.feature_1, null),
				inflater.inflate(R.layout.feature_2, null),
				inflater.inflate(R.layout.feature_3, null)
		};

		FeatureAdapter featureAdapter = new FeatureAdapter(views);

		feature = (ViewPager) findViewById(R.id.feature);
		feature.setAdapter(featureAdapter);
		feature.setOnPageChangeListener(featureAdapter);

		startTips = (TextView) findViewById(R.id.startTips);
	}

	private Intent intent;// 启动Activity
	private View[] views;// 存储ViewPager中的页面
	private ViewPager feature;// 新特性介绍
	private TextView startTips;

	private class FeatureAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener {

		public FeatureAdapter(View[] views) {
			this.views = views;
		}

		@Override
		public int getCount() {
			return views.length;
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {

			// 判断用户是否在拖拽画面
			isScolling = ViewPager.SCROLL_STATE_DRAGGING == arg0;
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

			// 到达ViewPager的最后一页，才可执行后面的操作
			if (views.length != (arg0 + 1)) {
				return;
			}

			// 在ViewPager的最后一页，向左滑动，进入主界面
			if (0.0f == arg1 && 0 == arg2 && isScolling) {
				if (!lastPageWasScolledLeft) {

					// 进入程序主界面
					startActivity(false);

					lastPageWasScolledLeft = true;
				}
			}
		}

		@Override
		public void onPageSelected(int arg0) {

			// 设置当前页面对应的指示器为激活状态
			ImageView indicator = (ImageView) views[arg0].findViewById(getIndicator(arg0));
			indicator.setImageResource(R.drawable.circle_dot_activited);

			// 设置其余的指示器为正常状态
			for (int i = 0; i < views.length; ++i) {
				if (i != arg0) {
					indicator = (ImageView) views[arg0].findViewById(getIndicator(i));
					indicator.setImageResource(R.drawable.circle_dot_normal);
				}
			}
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(views[position]);
			return views[position];
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(views[position]);
		}

		private int getIndicator(int index) {
			int resId = -1;
			switch (index) {
				case 0:
					resId = R.id.indicator_1;
					break;
				case 1:
					resId = R.id.indicator_2;
					break;
				case 2:
					resId = R.id.indicator_3;
					break;
			}
			return resId;
		}

		private View[] views;// 每个特性界面的布局
		private boolean lastPageWasScolledLeft;// 在ViewPager最后一页是否向左滑动
		private boolean isScolling;// 是否正在滑动
	}

	private class AddStockAsync extends AsyncTask<String, Integer, Void> {

		public AddStockAsync(String startTipsText) {
			this.startTipsText = startTipsText;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			startTips.setVisibility(View.VISIBLE);
			startTips.setText(String.format(startTipsText, 0) + "%");
		}

		@Override
		protected Void doInBackground(String... params) {

			// 第一次运行下载行情数据
			HttpURLConnection urlConnection = null;
			try {
				URL url = new URL(params[0]);
				urlConnection = (HttpURLConnection) url.openConnection();
				InputStream inputStream = urlConnection.getInputStream();
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Remote.GIT_CHARSET));

				int total = Remote.stockAmount;
				int count = 0;

				String line;
				List<Stock> stocks = new ArrayList<>();
				SQLiteDatabase db = SQLiteHelper.getInstance().getWritableDatabase();

                /*-----------------------------*/
				ArrayList<Stock[]> arrayList = new ArrayList<>();
				Map stocksMap = new HashMap<String, Stock>();
				Analyzer.setStockListMap(stocksMap);
				ArrayList<Stock> temp = new ArrayList<>();
				Analyzer.setArraystockList(arrayList);
				/*-----------------------------*/


				while (null != (line = bufferedReader.readLine())) {
					String[] data = line.split(",");

					ContentValues values = new ContentValues();
					values.put(Stock.Table.Column.CODE, data[0]);
					values.put(Stock.Table.Column.CODE_CN, data[1]);
					values.put(Stock.Table.Column.NAME, data[2]);
					values.put(Stock.Table.Column.LIST_DATE, data[3]);
					values.put(Stock.Table.Column.COLLECT, 0);
					values.put(Stock.Table.Column.COLLECT_STAMP, 0);
					values.put(Stock.Table.Column.SEARCH, 0);
					db.insert(Stock.Table.NAME, null, values);

					Stock stock = new Stock(data[0], data[2]);
					stock.setCodeCN(data[1]);
					stock.setIndex(count);
					stocks.add(stock);
					++count;
					publishProgress(count * 100 / total);

                   /*-----------------------*/
					stocksMap.put(stock.getCode(), stock);
					temp.add(stock);
					if (temp.size() >= 227) {
						arrayList.add(temp.toArray(new Stock[temp.size()]));
						System.out.println("size ----- " + temp.size());
						temp = new ArrayList<>();
					}
					/*-----------------------*/
				}
				Analyzer.setStockList(stocks);

                /*-----------------------*/
				if (temp.size() > 0) {
					arrayList.add(temp.toArray(new Stock[temp.size()]));
					System.out.println("size ----- " + temp.size() + " -- " + count);
					temp = null;
				}
                /*-----------------------*/
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (null != urlConnection) {
					urlConnection.disconnect();
				}
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			startTips.setText(String.format(startTipsText, values[0]) + "%");
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);
			startTips.setVisibility(View.GONE);
			startActivity(true);
		}

		private String startTipsText;
	}
}
