package com.alex.develop.stockanalyzer;

import android.os.Bundle;

import com.alex.develop.fragment.SearchFragment;

/**
 * Created by alex on 15-9-30.
 */
public class ShowActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_activity);

        getTransaction()
                .add(R.id.root, new SearchFragment()).commit();
    }
}
