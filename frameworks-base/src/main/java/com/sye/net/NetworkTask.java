package com.sye.net;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * *****************************************************************************************
 * Created by super.dragon  on 12/11/2018 17:55
 * <p>
 * project undefined
 * <p>
 * version 1.0.1
 * *****************************************************************************************
 */
public class NetworkTask {

    private final PriorityBlockingQueue<Runnable> priorityBlockingQueue = new PriorityBlockingQueue(15);
    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 10, 180L, TimeUnit.SECONDS, priorityBlockingQueue);

    public enum ContentType {
        JSON("application/json"),
        XML("application/xml"),
        HTML("text/html"),
        FORM_DATA("multipart/form-data");

        private final String type;

        ContentType(String type) {
            this.type = type;
        }

        public final String type() {
            return this.type;
        }
    }


    public void su() {
        executor.submit(new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    private static final String BOUNDARY = "----ApplicationFormBoundaryV29ybGQgQ29ubmVjdA";

}
