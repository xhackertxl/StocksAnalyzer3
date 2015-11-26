package com.alex.develop.task;

import android.os.AsyncTask;
import android.util.Log;

import com.alex.develop.entity.ApiStore;
import com.alex.develop.entity.News;
import com.alex.develop.util.NetworkHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by alex on 15年9月21日.
 * 查询新闻信息
 */
public class QueryLatestNews extends AsyncTask<Integer, Void, Boolean> {

    public QueryLatestNews(ArrayList<News> news) {
        this.news = news;
        NEWS_PAGES = Integer.MAX_VALUE;
        if (null == cache) {
            cache = new ArrayList<>();
        } else {
            cache.clear();
        }
    }

    public static int getNewsPages() {
        return NEWS_PAGES;
    }

    @Override
    protected Boolean doInBackground(Integer... params) {

        page = params[0];
        int pageCp = page;
        if (0 >= page) {
            pageCp = 1;
        }

        HashMap<String, String> header = new HashMap<>();
        header.put(ApiStore.BAIDU_APISTORE_API_KEY, ApiStore.BAIDU_APISTORE_API_VALUE);

        boolean result = false;// 查询数据的结果

        do {

            final String newsUrl = ApiStore.getFinanceNewsUrl(pageCp);
            final String content = NetworkHelper.getWebContent(newsUrl, header, ApiStore.SHOW_API_CHARSET);
            final Status status = fromJSON(content);

//            Log.d("Print-News-While", pageCp + ", " + status + " ============================");

            if (1 == page) {
                if (Status.refreshSuccess == status) {//下拉刷新成功
                    result = true;
                    break;
                } else if (Status.networkError == status || Status.queryFailure == status) {// 下拉刷新失败
                    break;
                } else {

                    // 为了确保下拉刷新得到的数据和本地已经下载到数据相互衔接
                    // 需要找到nid与本地最新数据nid相同的那条数据，这样才算
                    // 一次下拉刷新成功
                    ++pageCp;
                }
            } else {// 不是下拉刷新则跳出循环
                result = Status.querySuccess == status;
                break;
            }

        } while (true);

        return result;
    }

    private Status fromJSON(String content) {
        Status flag = Status.querySuccess;
        try {
            JSONObject data = new JSONObject(content);

            final String resBodyKey = "showapi_res_body";
            if (!data.has(resBodyKey)) {
                return Status.networkError;
            }

            JSONObject resBody = data.optJSONObject(resBodyKey);
            JSONObject pageBean = resBody.optJSONObject("pagebean");
            JSONArray contentList = pageBean.optJSONArray("contentlist");

            NEWS_PAGES = pageBean.optInt("allPages");

            final int contentSize = contentList.length();
            if (0 < contentSize) {
                for (int i = 0; i < contentSize; ++i) {
                    JSONObject newsObj = contentList.optJSONObject(i);
//                    Log.d("Print-News-Json", newsObj.toString());

                    if (1 == page) {

                        // TODO 下拉刷新 not be tested
                        final String nid = newsObj.optString("nid");
                        News latestNews = news.get(0);
//                        Log.d("Print-News-Foreach", nid + " , " + latestNews.getNid() + ", " + nid.equals(latestNews.getNid()));
                        if (nid.equals(latestNews.getNid())) {
                            news.addAll(0, cache);
                            cache.clear();
                            flag = Status.refreshSuccess;
                            break;
                        } else {
                            cache.add(new News(newsObj));
                        }

                    } else {
                        news.add(new News(newsObj));
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            flag = Status.queryFailure;
        }

        return flag;
    }

    private enum Status {
        networkError,// 网络错误，获取数据失败
        refreshSuccess,// 下拉刷新成功（要加载）
        querySuccess,// 加载数据成功（上拉加载数据）
        queryFailure// 加载数据失败
    }

    private int page;
    private ArrayList<News> news;
    private static int NEWS_PAGES;// 新闻的总页数（每页约20条数据）
    private static ArrayList<News> cache;// 存储下拉刷新得到的数据
}
