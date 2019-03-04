package com.example.nescara.myapplication.fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nescara.myapplication.data.Request;
import com.example.nescara.myapplication.default_folder.Connection;
import com.example.nescara.myapplication.database.HistoryDAO;
import com.example.nescara.myapplication.R;

public class ReqFragment extends Fragment {
    private Button btnExec;
    private TextView txtStatus;
    private EditText editURL;
    private Spinner methods;
    private LinearLayout layoutParams;
    private LinearLayout layoutHeaders;
    private EditText editParam1;
    private EditText editHeader1;
    private int countParam;
    private int countHeader;
    private Connection con;
    private String params;
    private String headers;
    private View view;
    private Typeface tfb;
    private Typeface tfr;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.req_fragment, container, false);

        btnExec = view.findViewById(R.id.btnExec);
        Button btnAddHeader = view.findViewById(R.id.addHeader);
        Button btnAddParam = view.findViewById(R.id.addParam);
        Button btnRemHeader = view.findViewById(R.id.remHeader);
        Button btnRemParam = view.findViewById(R.id.remParam);
        Button btnClear = view.findViewById(R.id.btnClear);
        txtStatus = view.findViewById(R.id.txtStatus);
        editURL = view.findViewById(R.id.editURL);
        methods = view.findViewById(R.id.methods);
        layoutParams = view.findViewById(R.id.aditionalParams);
        layoutHeaders = view.findViewById(R.id.aditionalHeaders);
        editParam1 = view.findViewById(R.id.editParam1);
        editHeader1 = view.findViewById(R.id.editHeader1);
        TextView txtHeaders = view.findViewById(R.id.txtHeaders);
        TextView txtParams = view.findViewById(R.id.txtParams);

        tfb = Typeface.createFromAsset(getContext().getAssets(), "fonts/OpenSans-Bold.ttf");
        tfr = Typeface.createFromAsset(getContext().getAssets(), "fonts/OpenSans-Regular.ttf");

        txtParams.setTypeface(tfb);
        txtHeaders.setTypeface(tfb);
        editURL.setTypeface(tfr);
        editHeader1.setTypeface(tfr);
        editParam1.setTypeface(tfr);
        btnExec.setTypeface(tfb);
        btnClear.setTypeface(tfb);

        countParam = 2;
        countHeader = 2;

        params = null;
        headers = null;

        btnRemParam.setOnClickListener(view_ -> remEditParam());
        btnRemHeader.setOnClickListener(view_ -> remEditHeader());
        btnAddParam.setOnClickListener(view_ -> addEditParam());
        btnAddHeader.setOnClickListener(view_ -> addEditHeader());
        btnExec.setOnClickListener(view_ -> exec());
        btnClear.setOnClickListener(view_ -> clear());

        return view;
    }

    public void exec(){
        String url = editURL.getText().toString().trim();
        ConnectivityManager cm = (ConnectivityManager)  getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = null;
        if (cm != null) {
            info = cm.getActiveNetworkInfo();
        }
        if((info == null) || !info.isConnected())
            Snackbar.make(getView(), R.string.no_internet, Snackbar.LENGTH_LONG).show();
        else if(url.length() <= 0)
            Toast.makeText(getContext(), R.string.no_url, Toast.LENGTH_SHORT).show();
        else {
            exec(url);
//            Toast.makeText(getBaseContext(), "Executado com sucesso!", Toast.LENGTH_SHORT).show();
        }
    }

    public void exec(final String url){
        new Thread(() -> {
            getActivity().runOnUiThread(() -> {
                btnExec.setEnabled(false);
                txtStatus.setText(R.string.processing);
            });
            byte method = translateMethod(String.valueOf(methods.getSelectedItem()));
            con = new Connection(url, method);
            if(editHeader1.length() > 0){
                setHeaders();
            }
            if(method == Connection.POST && editParam1.length() > 0){
                setParameters();
            }
            final String resp = con.fireRequest(true);
            getActivity().runOnUiThread(() -> {
                if(resp.length() > 0) {
                    txtStatus.setText(resp);
                    txtStatus.setOnLongClickListener(v -> {execCopy(); return true;});
                }
                else {
                    Toast.makeText(getContext(), getString(R.string.no_data) + " " + url, Toast.LENGTH_LONG).show();
                    txtStatus.setText("");
                }
                sendToDB();
                btnExec.setEnabled(true);
            });
        }).start();
    }

    private void execCopy(){
        ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(getContext().CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("content", txtStatus.getText().toString());
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(getContext(), R.string.copied, Toast.LENGTH_SHORT).show();
    }


    public static byte translateMethod(String method){
        switch (method){
            case "GET":
                return Connection.GET;
            case "POST":
                return Connection.POST;
            case "DELETE":
                return Connection.DELETE;
            case "PUT":
                return Connection.PUT;
            case "OPTIONS":
                return Connection.OPTIONS;
            case "TRACE":
                return Connection.TRACE;
            default:
                return 0;
        }
    }

    private void setHeaders() {
        StringBuilder headers = new StringBuilder();
        headers.append(editHeader1.getText().toString()).append(layoutHeaders.getChildCount() > 0 ? "&" : "");
        for(int i = 0; i < layoutHeaders.getChildCount(); i++){
            EditText header = (EditText) layoutHeaders.getChildAt(i);
            headers.append(header.getText().toString()).append(i == layoutHeaders.getChildCount() - 1 ? "" : "&");
        }

        this.headers = headers.toString();
        con.setHeaders(headers.toString());
    }

    public void setParameters(){
        StringBuilder params = new StringBuilder();
        params.append(editParam1.getText().toString()).append(layoutParams.getChildCount() > 0 ? "&" : "");
        for (int i = 0; i < layoutParams.getChildCount(); i++) {
            EditText param = (EditText) layoutParams.getChildAt(i);
            params.append(param.getText().toString()).append(i == layoutParams.getChildCount() - 1 ? "" : "&");
        }

        this.params = params.toString();
        con.setParameters(params.toString());
    }

    public void clear() {
        txtStatus.setText("");
        editURL.setText("");
        editParam1.setText("");
        editHeader1.setText("");
        methods.setSelection(0, true);
        int countH = layoutHeaders.getChildCount();
        int countP = layoutParams.getChildCount();
        for(int i = 0; i < countH; i++){
            layoutHeaders.removeViewAt(0);
            countHeader--;
        }
        for(int i = 0; i < countP; i++){
            layoutParams.removeViewAt(0);
            countParam--;
        }
    }

    public void addEditParam() {
        EditText newParam = new EditText(getContext());
        //Width and height
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        newParam.setHint("Parameter " + countParam);
        newParam.setTypeface(tfr);

        layoutParams.addView(newParam, p);

        countParam++;
    }

    public void addEditHeader() {
        EditText newHeader = new EditText(getContext());
        //Width and height
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        newHeader.setHint("Header " + countHeader);
        newHeader.setTypeface(tfr);
        layoutHeaders.addView(newHeader, p);

        countHeader++;
    }

    public void remEditHeader() {
        if(layoutHeaders.getChildCount() <= 0) return;

        layoutHeaders.removeViewAt(layoutHeaders.getChildCount()-1);
        countHeader--;
    }

    public void remEditParam() {
        if (layoutParams.getChildCount() <= 0) return;

        layoutParams.removeViewAt(layoutParams.getChildCount()-1);
        countParam--;
    }

    public void sendToDB(){
        Request model = new Request();
        model.setUrl(editURL.getText().toString());
        model.setHeaders(headers);
        model.setParams(params);
        model.setMethod((String)methods.getSelectedItem());
        model.setSuccess_flg(con.getHttpCode() >= 200 && con.getHttpCode() < 300);
        HistoryDAO db = new HistoryDAO(getContext());

        db.insert(model);

        params = null;
        headers = null;
    }

    public View getView(){
        return view;
    }
}