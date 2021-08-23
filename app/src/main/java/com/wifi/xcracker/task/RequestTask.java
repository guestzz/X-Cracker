package com.wifi.xcracker.task;

import com.wifi.xcracker.urlconnection.URLConnectionFactory;
import com.wifi.xcracker.util.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

public class RequestTask implements Runnable{
    private Request mRequest;
    private HttpListener httpListener;

    public RequestTask(Request request,HttpListener httpListener) {
        this.mRequest = request;
        this.httpListener=httpListener;
    }

    @Override
    public void run() {

        Logger.i("执行请求开始： "+mRequest.toString());
        Exception exception =null;
        int responseCode = -1;
        Map<String, List<String>> responseHeaders = null;
        byte[] responseBody = null;

        String urlStr = mRequest.getUrl();
        RequestMethod method = mRequest.getMethod();

        Logger.i("url:"+urlStr);
        Logger.i("method:"+method);
        HttpURLConnection urlConnection =null;
        try {
            //1.建立连接
            URL url =new URL(urlStr);
            //切换OkHttp和UrlConnection
            urlConnection= URLConnectionFactory.getInstance().openUrl(url);

            //https的处理
            if(urlConnection instanceof HttpsURLConnection){
                HttpsURLConnection httpsURLConnection =(HttpsURLConnection)urlConnection;
                SSLSocketFactory sslSocketFactory=mRequest.getSslSocketFactory();
                if(sslSocketFactory!=null)
                    httpsURLConnection.setSSLSocketFactory(sslSocketFactory);//https证书相关信息
                HostnameVerifier hostnameVerifier = mRequest.getHostnameVerifier();
                if(hostnameVerifier!=null)
                    httpsURLConnection.setHostnameVerifier(hostnameVerifier);//服务器主机认证
            }
            //1.1设置请求头等基础信息
            urlConnection.setRequestMethod(method.value());
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(method.isOutputMethod());
            setHeader(urlConnection,mRequest);




            //2. 发送数据
            if (method.isOutputMethod()){
                OutputStream outputStream = urlConnection.getOutputStream();
                mRequest.onWriteBody(outputStream);

            }

            //3.读取响应
            responseCode = urlConnection.getResponseCode();
            Logger.i("ResponseCode"+responseCode);
            responseHeaders = urlConnection.getHeaderFields();
            if(hasResponseBody(method,responseCode)) {
               InputStream inputStream= getInputStream(urlConnection, responseCode);
                ByteArrayOutputStream arrayOutputStream=new ByteArrayOutputStream();
                int len;
                byte[] buffer = new byte[2048];
                while ((len = inputStream.read(buffer))!=-1){
                    arrayOutputStream.write(buffer,0,len);
                }

                arrayOutputStream.close();
                responseBody = arrayOutputStream.toByteArray();
            }else{
                Logger.i("没有响应包体！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            exception=e;
        }finally {
            if(urlConnection !=null){
                urlConnection.disconnect();
            }
        }


        Response response = new Response(mRequest,responseCode,responseHeaders,responseBody,exception);
        //发送响应数据到主线程
        Message message=new Message(response,httpListener);

        Poster.getInstance().post(message);
    }

    /**
     * 判断是否有响应包体
     * @param method
     * @param responseCode
     * @return
     */
    private boolean hasResponseBody(RequestMethod method,int responseCode){
        return method != RequestMethod.HEAD
                && !(100 <= responseCode && responseCode <200)
                && responseCode != 204 && responseCode != 205
                && !(300<= responseCode && responseCode < 400);

    }


    /**
     * 根据响应码拿到服务器的流
     * @param urlConnection
     * @param responseCode
     * @return
     */
    private InputStream getInputStream(HttpURLConnection urlConnection, int responseCode) throws  IOException{
        InputStream inputStream;
        if(responseCode >= 400){
            inputStream=urlConnection.getErrorStream();
        }else {
            inputStream=urlConnection.getInputStream();
        }
        String contentEncoding = urlConnection.getContentEncoding();
        if (contentEncoding!=null&& contentEncoding.contains("gzip")){
            inputStream=new GZIPInputStream(inputStream);
        }
        return inputStream;
    }

    /**
     * 给URLConnection设置请求头
     * @param urlConnection
     * @param request
     */
    private void  setHeader(HttpURLConnection urlConnection,Request request) throws IOException {
        Map<String,String> requestHeader=request.getRequestHeader();
        //处理ContentType
        String contentType=request.getContentType();
        requestHeader.put("Content-Type",contentType);
        //处理ContentLength
        long contentLength = request.getContentLength();
        //long contentLength = 0;
        requestHeader.put("Content-Length",Long.toString(contentLength));
        for (Map.Entry<String, String> stringStringEntry : requestHeader.entrySet()) {
            String headKey =stringStringEntry.getKey();
            String headValue=stringStringEntry.getValue();
            Logger.d(headKey+"="+headValue);
            urlConnection.setRequestProperty(headKey,headValue);

        }
    }
}
