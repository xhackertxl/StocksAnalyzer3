package com.alex.develop.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.alex.develop.stockanalyzer.R;

import org.easystogu.config.FileConfigurationService;
import org.easystogu.sina.common.RealTimePriceVO;
import org.easystogu.sina.helper.SinaDataDownloadHelper;

import java.util.List;

/**
 * Created by alex on 15-8-20.
 * 持仓明细
 */
public class PositionFragment extends BaseFragment {
    ListView listView = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.position_fragment, container, false);
        listView = (ListView) view.findViewById(R.id.date1);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                listView.setAdapter(new RealTimePricevoAdapter(PositionFragment.this.getActivity(),printRealTimeOutput()));
//            }
//        }).start();
        //listView.setAdapter(new RealTimePricevoAdapter(PositionFragment.this.getActivity(), printRealTimeOutput()));
        return view;
    }
    public List<RealTimePriceVO> printRealTimeOutput() {
        FileConfigurationService configure = FileConfigurationService
                .getInstance();
        SinaDataDownloadHelper ins = new SinaDataDownloadHelper();
        String strList = configure.getString("realtime.display.stock.list")
                + "," + configure.getString("analyse.select.stock.list");
        return  ins.fetchDataFromWeb(strList);
    }
}
