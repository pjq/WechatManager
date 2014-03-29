
package me.pjq.wechat.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServiceProvider {

    private static ExecutorService executorService = Executors.newFixedThreadPool(1);

    public static ExecutorService getExecutorService() {
        return executorService;
    }
}
