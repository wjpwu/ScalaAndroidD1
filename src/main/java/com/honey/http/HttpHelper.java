package com.honey.http;

import android.net.Proxy;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Aaron
 * Date: 13-7-8
 * Time: 下午12:15
 * To change this template use File | Settings | File Templates.
 */
public class HttpHelper {


    private static HttpClient httpRedirect = httpClient(true);
    private static HttpClient httpNoRedirect = httpClient(false);
    private static HttpHelper h = null;

    private HttpHelper()
    {
    }

    public static HttpHelper shareHttp(){
        synchronized (h){
            if(h == null)
                h = new HttpHelper();
            return h;
        }
   }

    public static HttpClient httpClient(boolean redirectFlag)
    {
        BasicHttpParams basichttpparams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(basichttpparams, 45000);
        HttpConnectionParams.setSoTimeout(basichttpparams, 30000);
        HttpConnectionParams.setSocketBufferSize(basichttpparams, 8192);
        HttpClientParams.setRedirecting(basichttpparams, redirectFlag);
        DefaultHttpClient defaulthttpclient = new DefaultHttpClient(basichttpparams);
        return defaulthttpclient;

//        try {
//            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
//            InputStream inputstream = null;//TODO
//            keystore.load(inputstream, "password".toCharArray());
//            inputstream.close();
//            SSLSocketFactory sslsocketfactory = new SSLSocketFactory(keystore);
//            Scheme scheme = new Scheme("https", sslsocketfactory, 443);
//            defaulthttpclient.getConnectionManager().getSchemeRegistry().register(scheme);
//            return defaulthttpclient;
//        }
//        catch(KeyStoreException keystoreexception) { }
//        catch(NoSuchAlgorithmException nosuchalgorithmexception) { }
//        catch(CertificateException certificateexception) { }
//        catch(IOException ioexception) { }
//        catch(KeyManagementException keymanagementexception) { }
//        catch(UnrecoverableKeyException unrecoverablekeyexception) { }
    }


//    public boolean netWorkStatus()
//    {
//        ConnectivityManager cManager = (ConnectivityManager) App.shareContext().getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo networkinfo = cManager.getActiveNetworkInfo();
//        if(networkinfo == null || !networkinfo.isAvailable())
//            return false;
//        else if(networkinfo.getTypeName() != null && networkinfo.getTypeName().toUpperCase().equals("I@KBK"))
//        {
//            String s = Proxy.getDefaultHost();
//            if(s != null)
//                return true;
//        }
//        return true;
//    }

    public final byte[] httpGetWithParam(String url, String str)
            throws Exception
    {
        String s11 = str.trim();
        if(s11.startsWith("?") || s11.startsWith("&"))
            s11 = s11.substring(1);
        String s12 = "&";
        if(!url.contains("?"))
            s12 = "?";
        String s3 = (new StringBuilder(String.valueOf(url))).append(s12).append(s11).toString();

        HttpHost httphost = new HttpHost(url);
        HttpHost httphost1 = new HttpHost(Proxy.getDefaultHost(), Proxy.getDefaultPort());
        HttpClient httpclient = httpRedirect;
        httpclient.getParams().setParameter("http.route.default-proxy", httphost1);
        HttpResponse httpresponse = httpclient.execute(httphost, new HttpGet(s3));
        int status = httpresponse.getStatusLine().getStatusCode();
        return EntityUtils.toByteArray(httpresponse.getEntity());
    }

    public final byte[] httpPostWithEntity(String url, HttpEntity httpentity)
            throws Exception
    {
        HttpClient httpclient = httpNoRedirect;
        HttpHost httphost = new HttpHost(url);
        HttpHost httphost1 = new HttpHost(Proxy.getDefaultHost(), Proxy.getDefaultPort());
        httpclient.getParams().setParameter("http.route.default-proxy", httphost1);
        HttpPost httppost = new HttpPost(url);
        httppost.setEntity(httpentity);
        HttpResponse httpresponse = httpclient.execute(httphost, httppost);
        int status = httpresponse.getStatusLine().getStatusCode();
        return EntityUtils.toByteArray(httpresponse.getEntity());
    }

    public byte[] httpPostWithParams(String url, List<NameValuePair> params)
            throws Exception
    {
        HttpClient client = httpNoRedirect;
        HttpPost post = new HttpPost(url);
        UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
        HttpHost httphost1 = new HttpHost(Proxy.getDefaultHost(), Proxy.getDefaultPort());
        client.getParams().setParameter("http.route.default-proxy", httphost1);
        post.setEntity(ent);
        HttpResponse responsePOST = client.execute(post);
        return EntityUtils.toByteArray(responsePOST.getEntity());
    }

//    public byte[] httpPostWithFile(String url, String pathToFile)
//            throws Exception
//    {
//        HttpClient client = new DefaultHttpClient();
//        HttpPost post = new HttpPost(url);
//        FileBody bin = new FileBody(pathToFile);
//        MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
//        reqEntity.addPart("myFile", bin);
//        post.setEntity(reqEntity);
//        HttpResponse response = client.execute(post);
//        HttpEntity resEntity = response.getEntity();
//        return EntityUtils.toByteArray(responsePOST.getEntity());
//    }

}
