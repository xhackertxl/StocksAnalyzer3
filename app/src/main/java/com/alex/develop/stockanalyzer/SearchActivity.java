package com.alex.develop.stockanalyzer;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.alex.develop.adapter.SearchAdapter;
import com.alex.develop.adapter.SearchAdapter.OnStocksFindListener;
import com.alex.develop.entity.Enum;
import com.alex.develop.entity.Stock;
import com.alex.develop.ui.StockKeyboard;

/**
 * Created by alex on 15-6-15.
 * Search Stocks by EditText
 */
public class SearchActivity extends BaseActivity implements OnStocksFindListener, StockKeyboard.OnInputTypeChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);

        ActionBar actionBar = getActionBar();
        if (null != actionBar) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.action_bar_titile);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        stockSearch = (EditText) findViewById(R.id.stockSearch);

        stockSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                doAfterTextChanged(s);
            }
        });
        stockSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                stockSearch.setInputType(InputType.TYPE_NULL);
                stockSearch.requestFocus();
                //mKeyboard.show();

                return true;
            }
        });

        adapter = new SearchAdapter(Analyzer.getSearchStockList());
        adapter.setOnStocksFindListener(this);

        resultList = (ListView) findViewById(R.id.resultList);
        resultList.setAdapter(adapter);
        resultList.setTextFilterEnabled(true);
        resultList.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                mKeyboard.hide();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {}
        });

        resultList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Stock stock = (Stock) adapter.getItem(position);
                stock.search();
                CandleActivity.start(SearchActivity.this, stock.getIndex(), CandleActivity.OTHER_LIST);
            }
        });
        setResult(Activity.RESULT_OK);

        findNothing = (TextView) findViewById(R.id.findNothing);

        mKeyboard = (StockKeyboard) findViewById(R.id.keyboardView);
        mKeyboard.setOnInputTypeChangeListener(this);
        mKeyboard.setKeyboardLayout(R.xml.symbols, R.xml.qwerty, stockSearch);
    }

    @Override
    public void find(int findSize) {
        if(0 < findSize) {
            findNothing.setVisibility(View.GONE);
        } else {
            if(!stockSearch.getText().toString().isEmpty()) {
                findNothing.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onChanged(Enum.InputType inputType) {
        adapter.setInputType(inputType);
        findNothing.setVisibility(View.GONE);
    }

    private void doAfterTextChanged(CharSequence s) {
        ListAdapter adapter = resultList.getAdapter();
        if(adapter instanceof Filterable) {
            Filter filter = ((Filterable) adapter).getFilter();
            if(null == s || 0 == s.length()) {
                filter.filter(null);
            } else {
                filter.filter(s);
            }
        }
    }

    private ListView resultList;
    private EditText stockSearch;
    private TextView findNothing;
    private SearchAdapter adapter;
    private StockKeyboard mKeyboard;
}
