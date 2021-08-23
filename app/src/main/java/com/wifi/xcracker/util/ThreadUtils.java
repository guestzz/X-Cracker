package com.wifi.xcracker.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadUtils {

    private static ExecutorService sExecutorService = Executors.newSingleThreadExecutor();

    public static void execute(Runnable runnable){
        sExecutorService.execute(runnable);
    }
}
