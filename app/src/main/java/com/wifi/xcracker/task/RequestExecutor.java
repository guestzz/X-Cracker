package com.wifi.xcracker.task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public enum RequestExecutor {
    //enum枚举，全局单例
    INSTANCE;

    private  ExecutorService mExecutorService;
    RequestExecutor(){
        mExecutorService=Executors.newSingleThreadExecutor();
    }

    /**
     * 执行一个请求
     * @param request
     */
    public void execute(Request request ,HttpListener httpListener){
        mExecutorService.execute(new RequestTask(request, httpListener));
    }
}
