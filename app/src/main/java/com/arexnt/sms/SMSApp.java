package com.arexnt.sms;

import android.content.Context;

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
        context = getApplicationContext();

    }
    public static Context getContext(){
        return context;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}
