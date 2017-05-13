package com.arexnt.sms;

import android.content.Context;

import com.squareup.leakcanary.LeakCanary;

import org.litepal.LitePal;
import org.litepal.LitePalApplication;

/**
 * Created by arexnt on 2017/4/21.
 */

public class SMSApp extends LitePalApplication{
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);
        LeakCanary.install(this);
        context = getApplicationContext();
//        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss:SSS");
//        Date curDate = new Date(System.currentTimeMillis());
//        String time = dateFormat.format(curDate);
//        Log.d("start runing time: ",time);


    }
    public static Context getContext(){
        return context;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}
