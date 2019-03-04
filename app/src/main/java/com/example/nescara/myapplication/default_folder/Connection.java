package com.example.nescara.myapplication.default_folder;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

public class Connection {
    private URL url;
    private HttpURLConnection connection;
    private HttpsURLConnection secureConnection;
    private boolean isSecure;
    private byte method;
    private int httpCode;

    public static final byte POST = 1;
    public static final byte GET = 0;
    public static final byte DELETE = 2;
    public static final byte PUT = 3;
    public static final byte OPTIONS = 4;
    public static final byte TRACE = 5;

    public Connection(String url, byte method) {
        //DEBUGs
//        Log.d("URL", url);
        //
        this.method = method;
        setHttpCode(0);
        if(!url.contains("http://") && !url.contains("https://")) url = "http://" + url;

        isSecure = isSecureConnection(url);
        try {
            this.url = new URL(url);

            if(isSecure)
                createSecureConnection();

            else
                createConnection();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch(NoSuchAlgorithmException ne) {
            ne.printStackTrace();
        }
        catch(KeyManagementException ke) {
            ke.printStackTrace();
        }
    }

    private void createConnection() {
//        Log.d("connection type", "normal connection");
        try {
            connection = (HttpURLConnection) this.url.openConnection();
            connection.setRequestMethod(translateMethod(method));
            connection.setDoInput(true);

            if(method != GET)
                connection.setDoOutput(true);

        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    private void createSecureConnection() throws NoSuchAlgorithmException, KeyManagementException{
//        Log.d("Connection type", "secure connection");
        try {
            secureConnection = (HttpsURLConnection) url.openConnection();
            secureConnection.setRequestMethod(translateMethod(method));
            secureConnection.setDoInput(true);

            SSLContext ssl = SSLContext.getInstance("TLSv1.2");
            ssl.init(null, null, new SecureRandom());

            secureConnection.setSSLSocketFactory(ssl.getSocketFactory());

            if(method != GET)
                secureConnection.setDoOutput(true);

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setHeaders(String headers) {
        if(isSecure) {
            setSecureHeaders(headers);
            return;
        }
//        Log.d("setHeader type", "normal connection");

        //DEBUG
        String[] headerArr;
        for(String header : headers.split("&")) {
            headerArr = explode(header, '=');
            //DEBUG
//			Log.d("Header", headerArr[0] + ":" + headerArr[1]);
            if(headerArr[0] == null || headerArr[1] == null) return;
            connection.setRequestProperty(headerArr[0], headerArr[1]);
        }
    }

    private void setSecureHeaders(String headers) {
//        Log.d("setHeader type", "secure connection");
        String[] headerArr;
        //DEBUG
//        Log.d("Headers", headers);
        for(String header : headers.split("&")) {
            headerArr = explode(header, '=');
            //DEBUG
//            Log.d("Header", headerArr[0] + ":" + headerArr[1]);
            if(headerArr[0] == null || headerArr[1] == null) return;
            secureConnection.setRequestProperty(headerArr[0], headerArr[1]);

        }
    }

    public void setParameters(String params) {
        if(isSecure) {
            setSecureParameters(params);
            return;
        }
//        Log.d("setParameters type", "normal connection");
//        Log.d("Parameters", params);
        try {
            OutputStream os = connection.getOutputStream();
            os.write(params.getBytes());
            os.flush();
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    private void setSecureParameters(String params) {
//        Log.d("setParameters type", "secure connection");
//        Log.d("Parameters", params);
        try {
            OutputStream os = secureConnection.getOutputStream();
            os.write(params.getBytes());
            os.flush();
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    public String fireRequest(boolean getResponseBody) {
        if(isSecure) {
            return fireSecureRequest(getResponseBody);
        }
//        Log.d("fireRequest type", "normal connection");
        String response = "";
        int responseCode;
        try {
            responseCode = connection.getResponseCode();
            setHttpCode(responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                response = (getResponseBody ? getResponseBody(false) : String.valueOf(responseCode));
                if(response.length() > 0) {
                    close();
                    return response;
                }
                response += "success";
            }
            else if(responseCode == HttpURLConnection.HTTP_MOVED_TEMP || responseCode == HttpURLConnection.HTTP_MOVED_PERM) {
                response += "redirected to " + connection.getHeaderField("Location");
            }
            else if(responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                response += "unauthorized";
            }
            else if(responseCode == HttpURLConnection.HTTP_CREATED) {
                response += (getResponseBody ? getResponseBody(true) : String.valueOf(responseCode));
            }
            else if(responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                response += "no_content";
                response += connection.getRequestMethod();
            }
            else {
//                Log.e("error", String.valueOf(responseCode));
                response += "error: " + responseCode;
            }
        } catch (IOException io) {
            io.printStackTrace();
        }
        close();
        return response;
    }

    private String fireSecureRequest(boolean getResponseBody) {
//        Log.d("fireRequest type", "secure connection");
        int responseCode;
        String response = "";
        try {
            responseCode = secureConnection.getResponseCode();
            setHttpCode(responseCode);
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                response = (getResponseBody ? getSecureResponseBody(false) : String.valueOf(responseCode));
                if(response.length() > 0) {
                    close();
                    return response;
                }
                response += "success";
            }
            else if(responseCode == HttpsURLConnection.HTTP_MOVED_TEMP || responseCode == HttpsURLConnection.HTTP_MOVED_PERM) {
                response += "redirected to " + secureConnection.getHeaderField("Location");
            }
            else if(responseCode == HttpsURLConnection.HTTP_UNAUTHORIZED) {
                response += "unauthorized";
            }
            else if(responseCode == HttpsURLConnection.HTTP_CREATED) {
                response += (getResponseBody ? getSecureResponseBody(true) : String.valueOf(responseCode));
            }
            else if(responseCode == HttpsURLConnection.HTTP_NO_CONTENT) {
                response += "no_content";
                response += " " + secureConnection.getRequestMethod();
            }
            else {
                response += "error: " + responseCode;
            }
        } catch (IOException io) {
            io.printStackTrace();
        }
        close();
        return response;
    }

    public String getResponseBody(boolean json) throws IOException {
        String inLine;
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));
        StringBuffer response = new StringBuffer(1000000);

        while((inLine = in.readLine()) != null) {
            response.append(inLine + (json ? "" : "\n"));
        }

        return response.toString();
    }

    private String getSecureResponseBody(boolean json) throws IOException {
        String inLine;
        BufferedReader in = new BufferedReader(new InputStreamReader(secureConnection.getInputStream(), Charset.forName("UTF-8")));
        StringBuffer response = new StringBuffer();

        while((inLine = in.readLine()) != null) {
            response.append(inLine + (json ? "" : "\n"));
        }

        return response.toString();
    }

    private String translateMethod(byte method) {
        switch(method) {
            case POST:
                return "POST";
            case GET:
                return "GET";
            case DELETE:
                return "DELETE";
            case PUT:
                return "PUT";
            case OPTIONS:
                return "OPTIONS";
            case TRACE:
                return "TRACE";
            default:
                return null;
        }
    }

    private String[] explode(String str, char target) {
        String[] arr = new String[2];

        for(int i = 0, j = 0; i < str.length() && j < 2; i++) {
            if(str.charAt(i) == target) {
                arr[0] = str.substring(0, i);
                arr[1] = str.substring((str.charAt(i + 1) == ' ' ? i + 2 : i + 1));
                break;
            }
        }
        return arr;
    }

    private boolean isSecureConnection(String url) {
        return (url.contains("https://"));
    }

    public void close() {
        if(isSecure) {
            secureClose();
            return;
        }
        connection.disconnect();
    }

    private void secureClose() {
        secureConnection.disconnect();
    }

    public int getHttpCode(){
        return httpCode;
    }

    private void setHttpCode(int httpCode){
        this.httpCode = httpCode;
    }

}
