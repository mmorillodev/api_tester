package com.example.nescara.myapplication.activities;


import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import com.example.nescara.myapplication.adapters.FragmentsAdapter;
import com.example.nescara.myapplication.R;
import com.example.nescara.myapplication.database.HistoryDAO;
import com.example.nescara.myapplication.fragments.HistoryFragment;
import com.example.nescara.myapplication.fragments.ReqFragment;
import com.example.nescara.myapplication.interfaces.OnRedoRequestListener;

public class MainActivity extends AppCompatActivity implements OnRedoRequestListener {

    private FragmentsAdapter mFragmentsrAdapter;
    private ReqFragment reqFragment;
    private HistoryFragment historyFragment;
    private TabLayout tabLayout;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the two
        // primary sections of the activity.

        reqFragment = new ReqFragment();
        historyFragment = new HistoryFragment();
        mFragmentsrAdapter = new FragmentsAdapter(getSupportFragmentManager());
        mFragmentsrAdapter.addFragment(reqFragment);
        mFragmentsrAdapter.addFragment(historyFragment);

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mFragmentsrAdapter);

        tabLayout = findViewById(R.id.tabs);



        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
                if(tab.getPosition() == 1 && historyFragment.getCount() == 0){
                    Snackbar.make(mViewPager, "No data!", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    @Override
    public void onRedoRequest(Bundle bundle) {
        View view = reqFragment.getView();
        if(view != null) {
            EditText editUrl = view.findViewById(R.id.editURL);
            EditText editHeader = view.findViewById(R.id.editHeader1);
            EditText editParam = view.findViewById(R.id.editParam1);
            Spinner spinnerMethod = view.findViewById(R.id.methods);
            try {
                editUrl.setText(bundle.getString("url"));
                editHeader.setText((bundle.getString("headers").equals("null") ? "" : bundle.getString("headers")));
                editParam.setText((bundle.getString("params").equals("null") ? "" : bundle.getString("params")));
                spinnerMethod.setSelection(ReqFragment.translateMethod(bundle.getString("method")));
                tabLayout.getTabAt(0).select();
            } catch(NullPointerException e){}
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.optDellAll:
                deleteAll();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setCancelable(true)
                .setTitle("Do you want to leave the app?")
                .setPositiveButton("Confirm", (dialog, which) -> finish())
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    public void deleteAll(){
        if(historyFragment.getCount() > 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setTitle("Delete all data?");
            builder.setMessage("It cannot be undone");
            builder.setPositiveButton("Confirm", (dialog, which) -> {
                new HistoryDAO(this).delete();
                historyFragment.setHistory();
            });
            builder.setNegativeButton(android.R.string.cancel, null);
            builder.show();
        }
        else
            Snackbar.make(mViewPager, "There is no data to be deleted.", Snackbar.LENGTH_SHORT).show();
    }
}
