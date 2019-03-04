package com.example.nescara.myapplication.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.nescara.myapplication.R;
import com.example.nescara.myapplication.data.Request;
import com.example.nescara.myapplication.interfaces.OnDeleteRequestListener;
import com.example.nescara.myapplication.interfaces.OnRedoRequestListener;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder> {
    private List<Request> requests;
    private Context context;
    private OnRedoRequestListener listenerRedo;
    private OnDeleteRequestListener listenerDelete;
    private Typeface tfr;
    private Typeface tfb;

    public RecyclerViewAdapter(Context context){
        this.context = context;
        requests = new ArrayList<>();
        tfr = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Regular.ttf");
        tfb = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Bold.ttf");
    }

    public RecyclerViewAdapter(Context context, List<Request> requests){
        this(context);
        this.requests = requests;
    }

    public RecyclerViewAdapter(Context context, List<Request> requests, OnRedoRequestListener listenerRedo, OnDeleteRequestListener listenerDelete){
        this(context, requests);
        this.listenerRedo = listenerRedo;
        this.listenerDelete = listenerDelete;
    }

    public void addRequest(Request request){
        requests.add(request);
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.simple_display_info_layout, viewGroup, false);
        RecyclerViewHolder holder = new RecyclerViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int i) {
        Request request = getItem(i);

//        view.setBackgroundColor(context.getResources().getColor((position % 2 == 0 ? R.color.lightOrange : R.color.white)));

        holder.url.setText((request.getUrl().length() > 33 ? request.getUrl().substring(0,30) + "..." : request.getUrl()));
        holder.url.setTypeface(tfb);

        holder.headers.setText((request.getHeaders().length() > 28 ? request.getHeaders().substring(0,25) + "..." : request.getHeaders()));
        holder.headers.setTypeface(tfr);

        holder.params.setText((request.getParams().length() > 20 ? request.getParams().substring(0,27) + "...": request.getParams()));
        holder.params.setTypeface(tfr);

        holder.method.setText(request.getMethod());
        holder.method.setTextColor(context.getColor((request.isSuccess_flg() ? R.color.success : R.color.error)));
        holder.method.setTypeface(tfr);

        holder.btnRedo.setOnClickListener(view_ -> {
            Bundle datas = new Bundle();
            datas.putString("url", request.getUrl());
            datas.putString("headers", request.getHeaders());
            datas.putString("params", request.getParams());
            datas.putString("method", request.getMethod());

            listenerRedo.onRedoRequest(datas);
        });

        holder.btnDelete.setOnClickListener(view_ -> listenerDelete.delete(request.getId()));
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    public Request getItem(int position){
        return requests.get(position);
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder{
        public TextView url;
        public TextView headers;
        public TextView params;
        public TextView method;
        public ImageButton btnRedo;
        public ImageButton btnDelete;

        public RecyclerViewHolder(@NonNull View view) {
            super(view);
            url = view.findViewById(R.id.txtUrl);
            headers = view.findViewById(R.id.txtHeaders);
            params = view.findViewById(R.id.txtParams);
            method = view.findViewById(R.id.txtMethod);
            btnRedo = view.findViewById(R.id.btnRedo);
            btnDelete = view.findViewById(R.id.btnDelete);
        }
    }
}