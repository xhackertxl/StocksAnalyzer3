package com.alex.develop.fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alex.develop.adapter.NewsListAdapter;
import com.alex.develop.cache.ImageCache;
import com.alex.develop.cache.ImageFetcher;
import com.alex.develop.cache.Utils;
import com.alex.develop.entity.News;
import com.alex.develop.stockanalyzer.R;
import com.alex.develop.task.QueryLatestNews;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alex on 15-9-21.
 * 新闻列表
 */
public class NewsFragment extends BaseFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(null == news) {
            news = new ArrayList<>();
        } else {
            news.clear();
        }

        ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams(act, IMAGE_CACHE_DIR);
        cacheParams.setMemCacheSizePercent(0.25f);

        mImageFetcher = new ImageFetcher(act, 300, 200);
        mImageFetcher.setLoadingImage(R.drawable.news_image_holder);
        mImageFetcher.addImageCache(act.getSupportFragmentManager(), cacheParams);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.news_fragment, container, false);

        if(null == news) {
            news = new ArrayList<>();
        } else {
            news.clear();
        }

        final ListView newsList = (ListView) view.findViewById(R.id.newsList);
        View header = inflater.inflate(R.layout.news_divider_view, null, false);

        View footer = inflater.inflate(R.layout.news_footer, null, false);
        footerText = (TextView) footer.findViewById(R.id.footerText);
        footerLoadCircle = (ProgressBar) footer.findViewById(R.id.footerLoadCircle);
        newsAdapter = new NewsListAdapter(news, mImageFetcher);
        newsList.addHeaderView(header);
        newsList.addFooterView(footer);
        newsList.setAdapter(newsAdapter);
        newsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                News item = (News) parent.getAdapter().getItem(position);

                if(null != item) {
                    Uri page = Uri.parse(item.getLink());
                    Intent intent = new Intent(Intent.ACTION_VIEW, page);

                    PackageManager pkgM = act.getPackageManager();
                    List<ResolveInfo> acts = pkgM.queryIntentActivities(intent, 0);

                    if(0 < acts.size()) {
                        startActivity(intent);
                    }
                }
            }
        });
        newsList.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

                if(SCROLL_STATE_IDLE == scrollState) {
                    final int lvp = newsList.getLastVisiblePosition();
                    final int size = news.size();
                    if(lvp == size + 1) {
                        queryNewsByShowAPI(++currentPageIndex);
                    }
                }

                if(SCROLL_STATE_FLING == scrollState) {
                    if(!Utils.hasHoneycomb()) {
                        mImageFetcher.setPauseWork(true);
                    }
                } else {
                    mImageFetcher.setPauseWork(false);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {}
        });

        // 下拉刷新组件
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);
        refreshLayout.setColorSchemeResources(android.R.color.holo_green_dark, android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
        refreshLayout.setSize(SwipeRefreshLayout.DEFAULT);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryNewsByShowAPI(1);
            }
        });

        queryNewsByShowAPI(currentPageIndex++);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mImageFetcher.setExitTasksEarly(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        mImageFetcher.setPauseWork(false);
        mImageFetcher.setExitTasksEarly(true);
        mImageFetcher.flushCache();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mImageFetcher.closeCache();
    }

    /**
     * 获取最新财经新闻
     * @param page 0，表示首次请求新闻数据；1，表示下拉刷新请求数据；>1表示上拉加载更多数据
     */
    private void queryNewsByShowAPI(final int page) {

        final int allPage = QueryLatestNews.getNewsPages();

        // 请求页码超出了最大页码，无效请求，忽略之
        if(allPage < page) {
            return;
        }

        new QueryLatestNews(news) {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                // 下拉加载最新数据
                if(1 == page) {
                    refreshLayout.setRefreshing(true);
                }

                // 上拉加载更多数据
                if(1 < page) {
                    footerText.setVisibility(View.GONE);
                    footerLoadCircle.setVisibility(View.VISIBLE);
                }
            }

            @Override
            protected void onPostExecute(Boolean flag) {
                super.onPostExecute(flag);

                // 下拉加载最新数据
                if(1 == page) {
                    refreshLayout.setRefreshing(false);
                }

                // 上拉加载更多数据
                if(1 < page) {

                    if (allPage == page) {
                        footerText.setText(getString(R.string.footer_no_move));
                    }

                    if(allPage > page){
                        footerText.setText(getString(R.string.footer_load_move));
                    }

                    footerText.setVisibility(View.VISIBLE);
                    footerLoadCircle.setVisibility(View.GONE);
                }

                newsAdapter.notifyDataSetChanged();
            }
        }.execute(page);
    }

    private int currentPageIndex;
    private ArrayList<News> news;

    private TextView footerText;
    private ProgressBar footerLoadCircle;

    private ImageFetcher mImageFetcher;
    private NewsListAdapter newsAdapter;
    private SwipeRefreshLayout refreshLayout;

    private static final String IMAGE_CACHE_DIR = "cache";
}
