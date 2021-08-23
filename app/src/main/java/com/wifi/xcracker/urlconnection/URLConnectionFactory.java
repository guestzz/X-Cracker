package com.wifi.xcracker.urlconnection;

import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.internal.huc.OkHttpURLConnection;

/**
 * 把okhttp转换为urlconnection
 */
public class URLConnectionFactory {
    private static URLConnectionFactory instance;
    public static URLConnectionFactory getInstance(){
        if(instance==null)
            synchronized (URLConnectionFactory.class){
            if(instance == null)
                instance = new URLConnectionFactory();
            }
        return instance;
    }
    private OkHttpClient okHttpClient;
    private URLConnectionFactory (){
        okHttpClient = new OkHttpClient();
    }

    /**
     * 打开url
     * @param url
     * @return
     */
    public HttpURLConnection openUrl(URL url){
        return openUrl(url,null);
    }

    /**
     * 打开url
     * @param url
     * @param proxy http代理设置
     * @return
     */
    public HttpURLConnection openUrl(URL url, Proxy proxy){
        String protocol = url.getProtocol();
        OkHttpClient copy = okHttpClient.newBuilder().proxy(proxy).build();
        if(protocol.equals("http")) return new OkHttpURLConnection(url,copy);
        if(protocol.equals("http")) return new OkHttpURLConnection(url,copy);
        throw new IllegalArgumentException("Unexpected protocol: "+protocol);
    }

}
