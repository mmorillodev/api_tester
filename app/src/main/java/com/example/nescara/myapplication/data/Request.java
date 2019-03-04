package com.example.nescara.myapplication.data;

public class Request {
    private int id;
    private String url;
    private String headers;
    private String method;
    private String params;
    private boolean success_flg;

    public boolean isSuccess_flg() {
        return success_flg;
    }

    public void setSuccess_flg(boolean success_flg) {
        this.success_flg = success_flg;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHeaders() {
        return headers;
    }

    public void setHeaders(String headers) {
        this.headers = headers;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Request{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", headers='" + headers + '\'' +
                ", method='" + method + '\'' +
                ", params='" + params + '\'' +
                ", success_flg=" + success_flg +
                '}';
    }
}
