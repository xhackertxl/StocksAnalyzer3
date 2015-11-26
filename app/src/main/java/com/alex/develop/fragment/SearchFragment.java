package com.alex.develop.fragment;

import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.alex.develop.adapter.SearchAdapter;
import com.alex.develop.entity.Stock;
import com.alex.develop.stockanalyzer.Analyzer;
import com.alex.develop.stockanalyzer.CandleActivity;
import com.alex.develop.stockanalyzer.R;
import com.alex.develop.ui.StockKeyboard;

/**
 * Created by alex on 15-9-30.
 */
public class SearchFragment extends Fragment implements SearchAdapter.OnStocksFindListener, StockKeyboard.OnInputTypeChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.search_activity, container, false);

        ActionBar actionBar = getActivity().getActionBar();
        if (null != actionBar) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.action_bar_titile);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        stockSearch = (EditText) view.findViewById(R.id.stockSearch);



        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

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

                mKeyboard.show();
                return true;
            }
        });

        adapter = new SearchAdapter(Analyzer.getSearchStockList());
        adapter.setOnStocksFindListener(this);

        resultList = (ListView) view.findViewById(R.id.resultList);
        resultList.setAdapter(adapter);
        resultList.setTextFilterEnabled(true);
        resultList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                mKeyboard.hide();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });

        resultList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Stock stock = (Stock) adapter.getItem(position);
                stock.search();
                CandleActivity.start(getActivity(), stock.getIndex(), CandleActivity.OTHER_LIST);
            }
        });

        findNothing = (TextView) view.findViewById(R.id.findNothing);

        mKeyboard = (StockKeyboard) view.findViewById(R.id.keyboardView);
        mKeyboard.setOnInputTypeChangeListener(this);
        mKeyboard.setKeyboardLayout(R.xml.symbols, R.xml.qwerty, stockSearch);


        return view;

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
    public void onChanged(com.alex.develop.entity.Enum.InputType inputType) {
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
