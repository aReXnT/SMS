package com.arexnt.sms.service;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.arexnt.sms.utils.ClipboardUtils;

/**
 * Created by arexnt on 2017/5/13.
 */

public class ClearReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String captchas = intent.getStringExtra("captchas");
        if (captchas != null) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(
                    Context.NOTIFICATION_SERVICE
            );
            notificationManager.cancel(2013055371);
            ClipboardUtils.putTextIntoClipboard(context, captchas);
        }
    }
}
