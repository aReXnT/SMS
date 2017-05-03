package com.arexnt.sms.utils;

import android.os.AsyncTask;
import android.os.Build;



public class TaskUtils {

    @SafeVarargs
    public static <Params, Progress, Result> void execute(AsyncTask<Params, Progress, Result> task,
                                                          Params... params) {
        if (Build.VERSION.SDK_INT >= 11) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        } else {
            task.execute(params);
        }
    }
}
