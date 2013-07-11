package com.honey.http;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created with IntelliJ IDEA.
 * User: Aaron
 * Date: 13-6-26
 * Time: 上午9:39
 * To change this template use File | Settings | File Templates.
 */
public class MediatorData extends Observable{

//    public static String wwamzhost = "http://pacific-sands-3422.herokuapp.com/root/";
    public final static String wwamzhost = "http://192.168.0.123:9000/root/";
    public final static String wwhosticon = wwamzhost + "mobileicon/";

    /** Queue for the Activity */
    LinkedBlockingQueue <byte[]> queueConsumer = new LinkedBlockingQueue<byte[]>();
    Map<String,byte[]> iconMapsCache = new HashMap<String,byte[]>();


    public void getHostIcon(final String icon)
    {
        Log.d("get icons","icon name is" + icon);
        String urlString = wwhosticon + icon;
////        HttpUtil.get(urlString, new BinaryHttpResponseHandler() {
////            public void onSuccess(byte[] binaryData) {
////                Log.d("get icons|store data","onSuccess icon name is" + icon);
////                put(icon,binaryData);
////                save(icon,binaryData);
////            };
////            public void onFailure(Throwable arg0,String context) {
////
////            };
////            public void onFinish() {
////                Log.d("get icons|update ui","onFinish icon name is" + icon);
////                setChanged();
////                notifyObservers();
////            };
//        });
    }

    public byte[] getIcon(String icon)
    {
        return iconMapsCache.get(icon);
    }


    private void put (String icon,byte[] data)
    {
        if (icon != null && data !=null)
            iconMapsCache.put(icon,data);
    }

    //TODO
    private void save (String icon,byte[] data)
    {

    }
}
