package com.alex.develop.stockanalyzer;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;

import com.alex.develop.fragment.NewsFragment;
import com.alex.develop.fragment.PositionFragment;
import com.alex.develop.fragment.SelectFragment;
import com.alex.develop.fragment.StockFragment;
import com.alex.develop.ui.NonSlidableViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * App入口
 *
 * @author Created by alex 2014/10/23
 */
public class MainActivity extends BaseActivity implements StockFragment.OnStockHandleListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        setBackTwice2Exit(true);

        ActionBar actionBar = getActionBar();
        if(null != actionBar) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (null == fragList) {

            fragList = new ArrayList<>();

            // 行情
            fragList.add(new StockFragment());

            // 自选
            StockFragment stockFragment = new StockFragment();
            Bundle bundle = new Bundle();
            bundle.putBoolean(StockFragment.ARG_IS_COLLECT_VIEW, true);
            stockFragment.setArguments(bundle);
            fragList.add(stockFragment);

            // 持仓
            fragList.add(new PositionFragment());

            // 选股
            fragList.add(new SelectFragment());

            // 新闻
            fragList.add(new NewsFragment());
        }

        Analyzer.setMainActivityLoadView(findViewById(R.id.loading));

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager = (NonSlidableViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOffscreenPageLimit(5);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                Intent intent = new Intent();
                intent.setClass(this, ShowActivity.class);
                startActivity(intent);
//                startActivityForResult(intent, StockFragment.REQUEST_SEARCH_STOCK);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(Activity.RESULT_OK == resultCode && StockFragment.REQUEST_SEARCH_STOCK == requestCode) {
            onCollected();
        }
    }

    public void onNavClicked(View view) {
        boolean isChecked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.marketStock:
                if (isChecked) {
                    viewPager.setCurrentItem(0);
                }
                break;
            case R.id.collectStock:
                if (isChecked) {
                    viewPager.setCurrentItem(1);
                }
                break;
            case R.id.positionStock:
                if (isChecked) {
                    viewPager.setCurrentItem(2);
                }
                break;
            case R.id.selectStock:
                if (isChecked) {
                    viewPager.setCurrentItem(3);
                }
                break;
            case R.id.financeNews:
                if (isChecked) {
                    viewPager.setCurrentItem(4);
                }
                break;
        }
    }

    @Override
    public void onSelected(int index, int from) {
        CandleActivity.start(this, index, from);
    }

    @Override
    public void onCollected() {
        StockFragment stockFragment = (StockFragment) fragList.get(1);
        stockFragment.updateCollectStockList();
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragList.get(position);
        }

        @Override
        public int getCount() {
            return fragList.size();
        }
    }

//    private void downloadBasicInformation() {
//        List<Stock> list = Analyzer.getStockList();
//        Stock[] stocks = list.toArray(new Stock[list.size()]);
//        new QueryStockBasicInfo().execute(stocks);
//    }

    private NonSlidableViewPager viewPager;
    private List<Fragment> fragList;
}