package com.chriz.lightingcontrol.lightningSearch;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.chriz.lightingcontrol.R;

/**
 * This activity is used to search for lamps inside the network i.e. gather the lamp's IPs.
 */
public class LightSearchActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private LightSearchAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light_search);

        mRecyclerView = findViewById(R.id.searchRecyclerView);
        mAdapter = new LightSearchAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
