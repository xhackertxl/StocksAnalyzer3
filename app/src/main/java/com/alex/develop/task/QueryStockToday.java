package com.alex.develop.task;

import android.os.AsyncTask;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.alex.develop.entity.ApiStore;
import com.alex.develop.entity.Stock;
import com.alex.develop.stockanalyzer.Analyzer;
import com.alex.develop.stockanalyzer.R;
import com.alex.develop.util.NetworkHelper;

/**
 * Created by alex on 15-7-17.
 * 查询一支或多支股票当日行情
 */

public class QueryStockToday extends AsyncTask<Stock, Void, Void> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Animation anim = AnimationUtils.loadAnimation(Analyzer.getContext(), R.anim.loading_data);
        Analyzer.getMainActivityLoadView().setVisibility(View.VISIBLE);
        Analyzer.getMainActivityLoadView().startAnimation(anim);
    }

    @Override
    protected Void doInBackground(Stock... params) {

        String sinaApiUrl = ApiStore.getSinaTodayUrl(params);
        String data = NetworkHelper.getWebContent(sinaApiUrl, ApiStore.SINA_CHARSET);
        String[] lines = data.split(ApiStore.SBL_SEM);

        int i = 0;
        for (String line : lines) {

            Stock stock = params[i];

            String[] temp = line.substring(11).split(ApiStore.SBL_EQL);
            temp[0] = temp[0].substring(2);
            temp[1] = temp[1].substring(1, temp[1].length()-1);
            String id = temp[0];
            String[] info = temp[1].split(ApiStore.SBL_CMA);
            if (id.equals(stock.getCode())) {
                stock.fromSina(info);
                //TheMainCost.fetchDataFromWeb(stock.getCode(),stock);
            }
            ++i;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Analyzer.getMainActivityLoadView().setVisibility(View.GONE);
        Analyzer.getMainActivityLoadView().clearAnimation();
    }

}
