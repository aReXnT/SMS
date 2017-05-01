package com.arexnt.sms.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.arexnt.sms.R;
import com.arexnt.sms.SMSApp;
import com.arexnt.sms.model.Message;

/**
 * Created by arexnt on 2017/5/2.
 */

public class ToastService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    @Override
    public int onStartCommand(Intent intent,  int flags, int startId) {
        if (intent != null){
            Bundle bundle = intent.getBundleExtra("bundle");
            Message message = (Message) bundle.getSerializable("message");

            if (message.getCaptchas() != null || !message.getCaptchas().isEmpty()){
                Toast.makeText(SMSApp.getContext(),
                        String.format(getResources().getString(R.string.copy_captcha), message.getCaptchas()),
                        Toast.LENGTH_SHORT);
            }
        }
        return START_STICKY;
    }
}
