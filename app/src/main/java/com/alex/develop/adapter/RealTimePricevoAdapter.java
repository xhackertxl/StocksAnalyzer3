package com.alex.develop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.alex.develop.stockanalyzer.R;
import org.easystogu.sina.common.RealTimePriceVO;
import java.util.List;

/**
 * Created by Administrator on 2015-11-06.
 */
public class RealTimePricevoAdapter extends BaseAdapter {
    private  List<RealTimePriceVO> mlist ;
    private  Context mContext ;

    public RealTimePricevoAdapter(Context mContext , List<RealTimePriceVO> list ) {
        super();
        this.mlist = list;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mlist.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.realtimepricevo,null);
        TextView lastClose = (TextView)convertView.findViewById(R.id.lastClose);
        TextView open = (TextView)convertView.findViewById(R.id.open);
        TextView high = (TextView)convertView.findViewById(R.id.high);
        TextView current = (TextView)convertView.findViewById(R.id.current);
        TextView low = (TextView)convertView.findViewById(R.id.low);
        TextView range = (TextView)convertView.findViewById(R.id.range);
        RealTimePriceVO _realtime = mlist.get(position);
        lastClose.setText(((Double) _realtime.lastClose).toString());
        open.setText(((Double) _realtime.open).toString());
        high.setText(((Double) _realtime.high).toString());
        current.setText(((Double) _realtime.current).toString());
        low.setText(((Double) _realtime.low).toString());
        range.setText((_realtime.diffRange(_realtime.high, _realtime.low, _realtime.lastClose) ).toString());

        return convertView;
    }
}
