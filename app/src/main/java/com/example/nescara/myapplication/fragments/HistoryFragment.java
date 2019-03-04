package com.example.nescara.myapplication.fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nescara.myapplication.adapters.RecyclerViewAdapter;
import com.example.nescara.myapplication.data.Request;
import com.example.nescara.myapplication.database.HistoryDAO;
import com.example.nescara.myapplication.R;
import com.example.nescara.myapplication.interfaces.OnDeleteRequestListener;
import com.example.nescara.myapplication.interfaces.OnRedoRequestListener;

import java.util.List;

public class HistoryFragment extends Fragment implements View.OnScrollChangeListener, OnDeleteRequestListener {
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private LinearLayoutManager layoutManager;
    private LinearLayout fragmentHistory;
    private View view;
    private SwipeRefreshLayout swipeRefresh;
    private HistoryDAO db;
    private OnRedoRequestListener listener;

    private Typeface tfr;
    private Typeface tfb;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(getActivity() instanceof OnRedoRequestListener){
            listener = (OnRedoRequestListener) getActivity();
        }
        else{
            listener = bundle -> {};
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        this.view = view;

        recyclerView = view.findViewById(R.id.recyclerView);
        swipeRefresh = view.findViewById(R.id.refresh);
        fragmentHistory = view.findViewById(R.id.fragment_history);

        tfb = Typeface.createFromAsset(getContext().getAssets(), "fonts/OpenSans-Bold.ttf");
        tfr = Typeface.createFromAsset(getContext().getAssets(), "fonts/OpenSans-Regular.ttf");

        db = new HistoryDAO(view.getContext());
        setHistory();

//        recyclerView.setOnItemLongClickListener((parent, view_, position, id) -> delete(position));
        recyclerView.setOnScrollChangeListener(this);

        swipeRefresh.setOnRefreshListener(() -> {
            setHistory();
            Toast.makeText(getContext(), "History successfully updated!", Toast.LENGTH_SHORT).show();
            swipeRefresh.setRefreshing(false);
        });
//        btnDelAll.setOnClickListener(v -> deleteAll());

        return view;
    }

    public void setHistory(){
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        List<Request> requests = db.read();
        recyclerViewAdapter = new RecyclerViewAdapter(view.getContext(), requests, listener, this);
        recyclerView.setAdapter(recyclerViewAdapter);

        if(requests.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
        }
        else{
            recyclerView.setVisibility(View.INVISIBLE);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            TextView txtNoData = new TextView(getActivity());
            txtNoData.setText("No data");

            fragmentHistory.addView(txtNoData, params);
        }
    }

    @Override
    public boolean delete(int id){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(true);
        builder.setTitle("Delete data?");
        builder.setPositiveButton("Confirm", (dialog, which) -> {
            db.delete(id);
            setHistory();
        });
        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> {
            return;
        });
        builder.show();
        return true;
    }

    @Override
    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        View c = recyclerView.getChildAt(0);
        if (c == null) {
            return;
        }
        int scrolly = -c.getTop() + recyclerView.getPaddingTop() + layoutManager.findFirstVisibleItemPosition() * c.getHeight();
        if(scrolly == 0)
            swipeRefresh.setEnabled(true);
        else
            swipeRefresh.setEnabled(false);
    }

    public int getCount(){
        return recyclerView.getChildCount();
    }
}
