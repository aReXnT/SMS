package com.arexnt.sms.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.arexnt.sms.R;
import com.arexnt.sms.model.Message;
import com.arexnt.sms.utils.ClipboardUtils;
import com.arexnt.sms.utils.NotificationUtils;
import com.arexnt.sms.utils.ToastUtils;


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

            Log.d("pastCaptcha",message.getCaptchas());

            if (message.getCaptchas() != null){
                Log.d("makeToast","yes");
//                Toast.makeText(SMSApp.getContext(),
//                        String.format(getResources().getString(R.string.copy_captcha), message.getCaptchas()),
//                        Toast.LENGTH_SHORT);
                ClipboardUtils.putTextIntoClipboard(ToastService.this, message.getCaptchas());
                ToastUtils.showLong(String.format(getResources().getString(R.string.copy_captcha), message.getCaptchas()));
                NotificationUtils.showMessageInNotificationBar(ToastService.this, message);
            }
        }
        return START_STICKY;
    }
}
