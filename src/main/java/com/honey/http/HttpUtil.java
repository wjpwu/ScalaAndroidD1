package com.honey.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created with IntelliJ IDEA.
 * User: Aaron
 * Date: 13-6-26
 * Time: 上午10:04
 * To change this template use File | Settings | File Templates.
 */


public class HttpUtil {

//    public final static String BASE_URL = "http://pacific-sands-3422.herokuapp.com/root/";
    public static final String BASE_URL = "http://192.168.0.62:9000/root/";



    private static AsyncHttpClient client = new AsyncHttpClient();
    static
    {
        client.setTimeout(1000);   //设置链接超时，如果不设置，默认为10s
    }

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        Log.d("log url",BASE_URL + relativeUrl);
        return BASE_URL + relativeUrl;
    }
}
