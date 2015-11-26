package com.alex.develop.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alex.develop.adapter.SortableStockTableView;
import com.alex.develop.adapter.StockListTableAdapter;
import com.alex.develop.entity.ApiStore;
import com.alex.develop.entity.Stock;
import com.alex.develop.fragment.service.OptionalService;
import com.alex.develop.fragment.service.OptionalServiceConnection;
import com.alex.develop.stockanalyzer.Analyzer;
import com.alex.develop.stockanalyzer.CandleActivity;
import com.alex.develop.stockanalyzer.R;
import com.alex.develop.task.CollectStockTask;
import com.alex.develop.task.QueryStockToday;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.codecrafters.tableview.listeners.TableDataClickListener;

/**
 * Created by alex on 15-5-22.
 * 股票行情列表
 */
@SuppressLint("NewApi")
public class StockFragment extends BaseFragment implements CompoundButton.OnCheckedChangeListener {
    private final static String TAG = "StockFragment";
    public final static int MSG_SAY_HELLO = 0;

    final Messenger mMessenger = new Messenger(new IncomingHandler());

    public interface OnStockHandleListener {
        void onSelected(int index, int from);

        void onCollected();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mStockHandleListener = (OnStockHandleListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "must implement the OnStockSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.stock_fragment, container, false);

        Bundle bundle = getArguments();
        if (null != bundle) {
            isCollectView = bundle.getBoolean(ARG_IS_COLLECT_VIEW);
            if (isCollectView) {
                stocks = Analyzer.getCollectStockList(true);
            } else {
                stocks = Analyzer.getStockList();
            }
        } else {
            stocks = Analyzer.getStockList();
        }

        RadioButton codeRadio = (RadioButton) view.findViewById(R.id.codeRadio);
        codeRadio.setOnCheckedChangeListener(this);
        RadioButton priceRadio = (RadioButton) view.findViewById(R.id.priceRadio);
        priceRadio.setOnCheckedChangeListener(this);
        RadioButton increaseRadio = (RadioButton) view.findViewById(R.id.increaseRadio);
        increaseRadio.setOnCheckedChangeListener(this);

        final SortableStockTableView stockList = (SortableStockTableView) view.findViewById(R.id.stockList);
        stockListAdapter = new StockListTableAdapter(this.getActivity(), stocks);
        stockList.setDataAdapter(stockListAdapter);

        stockList.addDataClickListener(new TableDataClickListener<Stock>() {
            @Override
            public void onDataClicked(int position, Stock arg1) {
                mStockHandleListener.onSelected(position, isCollectView ? CandleActivity.COLLECT_LIST : CandleActivity.STOCK_LIST);
            }
        });

        list = stockList.getTableDataView();

        list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        list.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                stockListAdapter.selectView(position, checked);

                String count = 0 == stockListAdapter.getSelectedCount() ? "" : " [" + stockListAdapter.getSelectedCount() + "]";
                String text = String.format(act.getString(R.string.contextual_add_collect), count);
                if (isCollectView) {
                    text = String.format(act.getString(R.string.contextual_remove_collect), count);
                }
                title.setText(text);
            }

            @Override
            public boolean onCreateActionMode(final ActionMode mode, Menu menu) {

                View view = View.inflate(act, R.layout.collect_contextual_layout, null);
                title = (TextView) view.findViewById(R.id.title);
                ImageButton cancelBtn = (ImageButton) view.findViewById(R.id.cancel);
                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null != mode) {
                            stockListAdapter.removeSelection();
                            mode.finish();
                        }
                    }
                });
                mode.setCustomView(view);

                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                if (0 < stockListAdapter.getSelectedCount()) {

                    int collect = isCollectView ? 0 : 1;

                    new CollectStockTask(collect) {
                        @Override
                        protected void onPostExecute(Boolean aBoolean) {
                            stockListAdapter.removeSelection();
                            super.onPostExecute(aBoolean);
                            mStockHandleListener.onCollected();
                        }
                    }.execute(stockListAdapter.getSelectedStocks());
                }
            }

            private TextView title;
        });

        list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (SCROLL_STATE_IDLE == scrollState) {
                    queryStockToday();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                queryStart = firstVisibleItem;
                queryStop = firstVisibleItem + visibleItemCount;

                if (flag && 0 < visibleItemCount) {
                    queryStockToday();
                    flag = false;
                }
            }

            private boolean flag = true;
        });


        if (null != bundle) {
            isCollectView = bundle.getBoolean(ARG_IS_COLLECT_VIEW);
            if (isCollectView) {

            } else {
                bindService();
            }
        } else {
            bindService();
        }
        bindService();
        return view;
    }
    private  MsgReceiver  msgReceiver;

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                queryStockToday();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void bindService()
    {
        Intent intent = new Intent(this.getActivity(), OptionalService.class);
        // 绑定Service
        getActivity().getApplicationContext().bindService(intent, opSC, Context.BIND_AUTO_CREATE);

        //动态注册广播接收器
        msgReceiver = new MsgReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Analyzer.STOCK_UPDATE);
        getActivity().registerReceiver(msgReceiver, intentFilter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Activity.RESULT_OK == resultCode) {
            if (REQUEST_SEARCH_STOCK == requestCode && isCollectView) {
                updateCollectStockList();
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.codeRadio:
                break;
            case R.id.priceRadio:
                break;
            case R.id.increaseRadio:
                break;
        }
    }

    public void updateCollectStockList() {
        if (isCollectView) {
            stocks = Analyzer.getCollectStockList(true);
            stockListAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 查询当前View可见的所有股票的行情数据
     */
    private void queryStockToday() {
        Map<String, Stock> stockMap = Analyzer.getStockListMap();
        List<Stock> temp = new ArrayList<>();
        for (int i = 0; i < queryStop - queryStart; ++i) {
            View v = list.getChildAt(i);
            Stock stock = null;
            if (null != v) {
                String code = ((TextView) v.findViewById(R.id.stockCode)).getText().toString();
                stock = stockMap.get(code);
            } else {
                stock = stocks.get(i);
            }
            long stamp = System.currentTimeMillis();

            if (ApiStore.SINA_REFRESH_INTERVAL < stamp - stock.getStamp()) {//5秒内不重复查询
                if (!stock.getTime().startsWith("15")) {// 15:00:00以后不重复查询
                    temp.add(stock);
                }
            }
        }

        Stock[] stocks = temp.toArray(new Stock[temp.size()]);

        if (0 == stocks.length) {
            return;
        }

        new QueryStockToday() {
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (null != stockListAdapter) {
                    stockListAdapter.notifyDataSetChanged();
                }
            }
        }.execute(stocks);
    }

    OptionalServiceConnection opSC = new OptionalServiceConnection();

    /**
     * 判断有没有绑定Service
     */
    boolean mBound;

    @Override
    public void onStop() {
        super.onStop();
        // 解绑
        if (mBound) {
            getActivity().unbindService(opSC);
            mBound = false;
        }
    }


    /**
     * 在Service处理Activity传过来消息的Handler
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SAY_HELLO:
                    Toast.makeText(getActivity().getApplicationContext(), "hello!", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }


    /**
     * 广播接收器
     *
     * @author len
     */
    public class MsgReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //拿到进度，更新UI
            int msg = intent.getIntExtra(Analyzer.STOCK_UPDATE, 0);
            switch (msg)
            {
               case 1 :
                   //stockListAdapter.notifyDataSetChanged();
                break;
            }
        }
    }


    private int queryStart;
    private int queryStop;
    private boolean isCollectView;
    private List<Stock> stocks;// 自选股列表
    private ListView list;
    private StockListTableAdapter stockListAdapter;
    private OnStockHandleListener mStockHandleListener;

    public static final String ARG_IS_COLLECT_VIEW = "collect";
    public static final int REQUEST_SEARCH_STOCK = 0X3531;
}