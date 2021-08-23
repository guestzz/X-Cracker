package com.wifi.xcracker.task;

import java.util.List;
import java.util.Map;

public class Response {
    /**
     * 请求对象
     */
    private  Request request;
    public String address;
    /**
     * 服务器响应码
     */
    private  int responseCode;
    /**
     * 服务器响应数据
     */
    private byte[] responseBody;
    /**
     * 服务器响应头
     */
    private Map<String, List<String>> responseHeaders;
    /**
     * 请求过程中发生的错误
     */
    private Exception exception;

    public Response(Request request, int responseCode, Map<String,
            List<String>> responseHeaders, byte[] responseBody, Exception exception) {
        this.request = request;
        this.responseCode = responseCode;
        this.responseBody = responseBody;
        this.responseHeaders = responseHeaders;
        this.exception = exception;
    }

    public Request getRequest() {
        return request;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public Map<String, List<String>> getResponseHeaders() {
        return responseHeaders;
    }

    public byte[] getResponseBody(){
        return responseBody;
    }

    Exception getException() {
        return exception;
    }
}
