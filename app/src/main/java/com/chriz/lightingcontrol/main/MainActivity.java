package com.chriz.lightingcontrol.main;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.chriz.lightingcontrol.lightActivation.ActivateLightActivity;
import com.chriz.lightingcontrol.lightningSearch.LightSearchActivity;
import com.chriz.lightingcontrol.R;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private LightBulbListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.itemBulbListRecyclerView);
        mAdapter = new LightBulbListAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fab = findViewById(R.id.addNewLighbulbFAB);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), LightSearchActivity.class);

                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.activate_light:
                startActivateLightActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startActivateLightActivity() {
        Intent intent = new Intent(getBaseContext(), ActivateLightActivity.class);
        startActivity(intent);
    }
}
